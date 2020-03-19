package com.candy.commons.sequence.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.candy.commons.sequence.Sequence;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class DefaultSequence implements Sequence {
	private static final Log log = LogFactory.getLog(DefaultSequence.class);
	private int blockSize;
	private long startValue;
	private static final String GET_SQL = "SELECT ID FROM SEQUENCE_VALUE WHERE NAME = ?";
	private static final String NEW_SQL = "INSERT INTO SEQUENCE_VALUE (ID,NAME) VALUES (?,?)";
	private static final String UPDATE_SQL = "UPDATE SEQUENCE_VALUE SET ID = ?  WHERE NAME = ? AND ID = ?";
	private Map<String, Step> stepMap;
	private JdbcTemplate jdbcTemplate;

	public DefaultSequence() {
		this.blockSize = 5;
		this.startValue = 0L;
		this.stepMap = new HashMap<String, Step>();
	}

	private boolean getNextBlock(String sequenceName, Step step) {
		Long value = getPersistenceValue(sequenceName);
		if (value == null) {
			try {
				value = newPersistenceValue(sequenceName);
			} catch (Exception e) {
				log.error("newPersistenceValue error!");
				value = getPersistenceValue(sequenceName);
			}
		}
		boolean b = saveValue(value.longValue(), sequenceName) == 1;
		if (b) {
			step.setCurrentValue(value.longValue());
			step.setEndValue(value.longValue() + this.blockSize);
		}
		return b;
	}

	public synchronized String get(String sequenceName) {
		Step step = (Step) this.stepMap.get(sequenceName);
		if (step == null) {
			step = new Step(this.startValue, this.startValue + this.blockSize);
			this.stepMap.put(sequenceName, step);
		} else if (step.currentValue < step.endValue) {
			return Long.toString(step.incrementAndGet());
		}

		for (int i = 0; i < this.blockSize; i++) {
			System.out.println("==========================blockSize:"+i);
			if (getNextBlock(sequenceName, step)) {
				return Long.toString(step.incrementAndGet());
			}
		}
		
		throw new RuntimeException("No more value.");
	}

	private int saveValue(long value, String sequenceName) {
		try {
			long newValue = value + this.blockSize;
			int i = jdbcTemplate.update(UPDATE_SQL, newValue, sequenceName, value);
			return i;
		} catch (Exception e) {
			log.error("newPersistenceValue error!", e);
			throw new RuntimeException("newPersistenceValue error!", e);
		}
	}

	private Long getPersistenceValue(String sequenceName) {
		try {
			List<Map<String, Object>> mapList = jdbcTemplate.queryForList(GET_SQL, sequenceName);
			if (mapList!=null && mapList.size() == 1) {
				Long localLong = Long.parseLong(mapList.get(0).get("ID").toString());
				System.out.println("=============================:"+localLong+":====================================");
				return localLong;
			}
		} catch (Exception e) {
			log.error("getPersistenceValue error!", e);
			throw new RuntimeException("getPersistenceValue error!", e);
		} 
		return null;		
	}

	private Long newPersistenceValue(String sequenceName) {
		try {
			jdbcTemplate.update(NEW_SQL, this.startValue, sequenceName);			
		} catch (Exception e) {
			log.error("newPersistenceValue error!", e);
			throw new RuntimeException("newPersistenceValue error!", e);
		} 
		return Long.valueOf(this.startValue);
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public void setStartValue(long startValue) {
		this.startValue = startValue;
	}

	static class Step {
		
		private long currentValue;
		private long endValue;

		Step(long currentValue, long endValue) {
			this.currentValue = currentValue;
			this.endValue = endValue;
		}

		public void setCurrentValue(long currentValue) {
			this.currentValue = currentValue;
		}

		public void setEndValue(long endValue) {
			this.endValue = endValue;
		}

		public long incrementAndGet() {
			return ++this.currentValue;
		}
	}
}