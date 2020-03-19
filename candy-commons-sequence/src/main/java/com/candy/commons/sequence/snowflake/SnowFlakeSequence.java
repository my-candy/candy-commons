package com.candy.commons.sequence.snowflake;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Twitter的分布式自增ID雪花算法snowflake
 * @author MENG
 * @create 2018-08-23 10:21
 **/
public class SnowFlakeSequence {

    private final static Logger LOGGER = LogManager.getLogger(SnowFlakeSequence.class);

    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1577877175000l;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12;

    /**
     * 机器标识占用的位数
     */
    private final static long MACHINE_BIT = 5;   //

    /**
     * 数据中心占用的位数
     */
    private final static long DATACENTER_BIT = 5;

    /**
     * 每一部分的最大值
     */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    /**
     * 数据中心
     */
    private long datacenterId = getDataId();

    /**
     * 机器标识
     */
    private long machineId = getWorkId();

    private SequenceProperty sequenceProperty;
    /**
     * 序列号
     */
    private long sequence = 0L;

    /**
     * 上一次时间戳
     */
    private long lastStmp = -1L;

    public SnowFlakeSequence(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
        LOGGER.debug("datacenterId:"+this.datacenterId+",binaryDatacenterId:"+Long.toBinaryString(this.datacenterId));
        LOGGER.debug("machineId:"+this.machineId+",binaryMachineId:"+Long.toBinaryString(this.machineId));
    }

    public SnowFlakeSequence() {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            LOGGER.error("数据中心ID不能大于"+MAX_DATACENTER_NUM+"或小于0");
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            LOGGER.error("机器ID不能大于"+MAX_MACHINE_NUM+"或小于0");
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }

        if(sequenceProperty!=null&&sequenceProperty.getMachineId()!=null&&sequenceProperty.getDatacenterId()!=null){
            this.datacenterId = sequenceProperty.getDatacenterId();
            this.machineId = sequenceProperty.getMachineId();
        }
        LOGGER.debug("datacenterId:"+this.datacenterId+",binaryDatacenterId:"+Long.toBinaryString(this.datacenterId));
        LOGGER.debug("machineId:"+this.machineId+",binaryMachineId:"+Long.toBinaryString(this.machineId));
    }

    /**
     * 产生下一个ID
     * @return
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            LOGGER.error("时钟向后移动。拒绝生成id");
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }
        LOGGER.debug("datacenterId:"+this.datacenterId+",binaryDatacenterId:"+Long.toBinaryString(this.datacenterId));
        LOGGER.debug("machineId:"+this.machineId+",binaryMachineId:"+Long.toBinaryString(this.machineId));
        lastStmp = currStmp;
        return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
                | datacenterId << DATACENTER_LEFT       //数据中心部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | sequence;                             //序列号部分
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    private static int getHostId(String s,int max){
        byte[] bytes = s.getBytes();
        int sums=0;
        for(int b:bytes){
            sums += b;
        }
        return sums % (max+1);
    };

    private static int getWorkId(){
        int workId = new Random().nextInt(31);
        try {
            workId = getHostId(Inet4Address.getLocalHost().getHostAddress(),31);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        LOGGER.debug("workId:"+workId);
        return workId;
    }

    private static int getDataId(){
        int dataId = new Random().nextInt(31);
        try {
            dataId = getHostId(Inet4Address.getLocalHost().getHostName(),31);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        LOGGER.debug("dataId:"+dataId);
        return dataId;
    }

    public static void main(String[] args) {
        SnowFlakeSequence snowFlake = new SnowFlakeSequence();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            System.out.println(snowFlake.nextId());
            System.out.println(Long.toBinaryString(snowFlake.nextId()));
        }
    }

    public void setSequenceProperty(SequenceProperty sequenceProperty) {
        this.sequenceProperty = sequenceProperty;
    }
}