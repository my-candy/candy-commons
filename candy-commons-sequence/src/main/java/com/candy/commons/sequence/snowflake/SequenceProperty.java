package com.candy.commons.sequence.snowflake;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName SequenceProperty
 * @Description 雪花算法主键策略的属性配置
 * @Author quanjj
 * @Time 2020/1/8 12:14
 */
@Component
@ConfigurationProperties(prefix="sequence")
public class SequenceProperty  {

    private Integer datacenterId;

    private Integer machineId;

    public Integer getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(Integer datacenterId) {
        this.datacenterId = datacenterId;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }
}
