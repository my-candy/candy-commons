package com.candy.commons.sequence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.candy.commons.sequence.number.NumberCalculate;

public class CacheSequence implements Sequence {

    private static final Log log = LogFactory.getLog(DefaultSequence.class);
    
    private static final String SEQUENCE_NAME_PREFIX = "sequence.";

    private int blockSize;
    private String startValue;

    private RedisTemplate<String,Object> redisTemplate;
    // 进制
    private int hexNum = 36;

    public CacheSequence() {
        this.blockSize = 1;
        this.startValue = "000000";
    }

    /**
     * param name 根据名称获取序号
     */
    public synchronized String get(String name) {
        try {
            String value = incrementBlockSize(SEQUENCE_NAME_PREFIX+name,this.blockSize);
            return value;
        }catch (Exception e){
        		log.error("获取序列错误",e);
            throw new RuntimeException("获取序列错误");
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
     * @param size
     * 自增的步长
     * @throws Exception
     * @return
     */
    private String incrementBlockSize(String sequenceName,int size) throws Exception {
        //自增之后的数
        Long value = redisTemplate.opsForValue().increment(sequenceName,Long.parseLong(size+""));
        //进制转换
        String numStr = NumberCalculate.ten2N(value+ "", hexNum);
        numStr = addZeroForNum(numStr,this.startValue.length());
        return numStr;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setHexNum(int hexNum) {
        this.hexNum = hexNum;
    }

}
