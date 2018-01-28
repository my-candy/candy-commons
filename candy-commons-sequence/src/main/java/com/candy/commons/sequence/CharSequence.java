package com.candy.commons.sequence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.candy.commons.sequence.number.NumberCalculate;

public class CharSequence implements Sequence {
	private static final Log log = LogFactory.getLog(DefaultSequence.class);
	private int blockSize;
	private String startValue;
	private static final String GET_SQL = "SELECT ID FROM SEQUENCE_VALUE WHERE NAME = ?";
	private static final String NEW_SQL = "INSERT INTO SEQUENCE_VALUE (ID,NAME) VALUES (?,?)";
	private static final String UPDATE_SQL = "UPDATE SEQUENCE_VALUE SET ID = ?  WHERE NAME = ? AND ID = ?";
	private Map<String, Queue> queueMap;
	private JdbcTemplate jdbcTemplate;
	// 进制
	private int hexNum = 36;

	public CharSequence() {
		this.blockSize = 5;
		this.startValue = "000000";
		this.queueMap = new HashMap<String, Queue>();
	}

	/**
	 * 得到下一个序列
	 * @param sequenceName
	 * @param queue
	 * @return
	 * @throws Exception
	 */
	private boolean getNextBlock(String sequenceName, Queue queue) throws Exception {
		//
		String value = getPersistenceValue(sequenceName);
		// 如果value为空说明数据库中没有这条序列
		if (value == null) {
			try {
				// 在数据库中添加名字为sequenceName的一条序列
				value = newPersistenceValue(sequenceName);
			} catch (Exception e) {
				// 如果抛异常说明已经添加过名字为sequenceName的序列了。
				log.error("newPersistenceValue error!");
				// 调用获取当前序列的方法
				value = getPersistenceValue(sequenceName);
			}
		}

		// 如果在更新序列时候没有更新到，有可能其他的的并发更新了，这时候需要重新取
		boolean b = saveValue(value, sequenceName) == 1;
		if (b) {
			queue.setCurrentValue(value);
			queue.setEndValue(incrementBlockSize(value, this.blockSize));
		}
		return b;
	}

	/**
	 * param name 根据名称获取序号
	 */
	public synchronized String get(String name) {
		try {
			Queue queue = (Queue) this.queueMap.get(name);
			// 如果queue为空代表第一次取数
			if (queue == null) {
				// 初始化queue包括startValue（开始的数字） blockSize（要缓存的最大数）
				queue = new Queue(this.startValue, incrementBlockSize(this.startValue, this.blockSize));
				this.queueMap.put(name, queue);
			} else if (comparedSize(queue.currentValue, queue.endValue)) {
				queue.setCurrentValue(incrementBlockSize(queue.currentValue, 1));
				return queue.currentValue;
			}

			// 如果发生异常循环的乐观锁循环的次数，目前以缓存的数量为循环次数
			for (int i = 0; i < this.blockSize; i++) {
				// 如果没有发生异常，将取到的序列的值递增1
				if (getNextBlock(name, queue)) {
					queue.setCurrentValue(incrementBlockSize(queue.currentValue, 1));
					return queue.currentValue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new RuntimeException("No more value.");
	}

	private boolean comparedSize(String currentValue, String endValue) {
		// 判断currentCodes是否小于endValue，小于则返回true
		boolean result = false;
		byte[] currentCodes = currentValue.getBytes();
		byte[] endCodes = endValue.getBytes();
		int len = currentCodes.length;
		for (int i = 0; i < len; ++i) {
			if (currentCodes[i] < endCodes[i]) {
				result = true;
				break;
			} else if (currentCodes[i] == endCodes[i]) {
				continue;
			} else {
				result = false;
				break;
			}
		}
		return result;
	}

	private int saveValue(String value, String sequenceName) {
		try {
			String newValue = incrementBlockSize(value, this.blockSize);
			int i = jdbcTemplate.update(UPDATE_SQL, newValue, sequenceName, value);
			return i;
		} catch (Exception e) {
			log.error("newPersistenceValue error!", e);
			throw new RuntimeException("newPersistenceValue error!", e);
		}
	}

	public static String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);// 左补0
				// sb.append(str).append("0");//右补0
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}

	/**
	 * @param value
	 * 要做自增的数
	 * @param size
	 * 自增的步长
	 * @throws Exception
	 * @return
	 */
	private String incrementBlockSize(String value, int size) throws Exception {
		String oldValue = value;
		String numStr = NumberCalculate.n2ten(value, hexNum);
		long num = Long.parseLong(numStr);
		num += size;
		numStr = NumberCalculate.ten2N(num + "", hexNum);
		numStr = addZeroForNum(numStr,oldValue.length());
		return numStr;
	}

	/**
	 * get currentValue from DB by sequenceName
	 * @param sequenceName
	 * @return
	 */
	private String getPersistenceValue(String sequenceName) {
		try {
			// get sequenceName from DB
			List<Map<String, Object>> mapList = jdbcTemplate.queryForList(GET_SQL, sequenceName);
			// 如果结果不为空说明之前已经添加过序列。
			if (mapList != null && mapList.size() == 1) {
				String localString = mapList.get(0).get("ID").toString();
				return localString;
			}
		} catch (Exception e) {
			log.error("getPersistenceValue error! 在查询序列的时候报错了!", e);
			throw new RuntimeException("getPersistenceValue error! 在查询序列的时候报错了!", e);
		}
		// 如果返回null说明没有在数据库中添加过序列
		return null;
	}

	/**
	 * creat currentValue to db. key is sequenceName
	 * @param sequenceName
	 * @return
	 */
	private String newPersistenceValue(String sequenceName) {
		try {
			jdbcTemplate.update(NEW_SQL, this.startValue, sequenceName);
		} catch (Exception e) {
			log.error("newPersistenceValue error!", e);
			throw new RuntimeException("newPersistenceValue error!", e);
		}
		return this.startValue;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public void setStartValue(String startValue) {
		this.startValue = startValue;
	}

	static class Queue {
		// 当前值
		private String currentValue;
		// 结束
		private String endValue;

		Queue(String currentValue, String endValue) {
			this.currentValue = currentValue;
			this.endValue = endValue;
		}

		public void setCurrentValue(String currentValue) {
			this.currentValue = currentValue;
		}

		public void setEndValue(String endValue) {
			this.endValue = endValue;
		}
	}

	public int getHexNum() {
		return hexNum;
	}

	public void setHexNum(int hexNum) {
		this.hexNum = hexNum;
	}
}
