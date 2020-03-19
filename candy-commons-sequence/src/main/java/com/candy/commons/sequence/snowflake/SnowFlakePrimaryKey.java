package com.candy.commons.sequence.snowflake;

import com.candy.commons.sequence.primarykey.PrimaryKey;

/**
 * @ClassName SnowFlakePrimaryKey
 * @Description 雪花算法主键
 * @Author quanjj
 * @Time 2020/1/7 16:25
 */
public class SnowFlakePrimaryKey implements PrimaryKey {

    private SnowFlakeSequence snowFlakeSequence;

    @Override
    public String getPrimaryKey() {
        return snowFlakeSequence.nextId()+"";
    }

    @Override
    public String getPrimaryKey(String key) {
        return snowFlakeSequence.nextId()+"";
    }

    @Override
    public String getPrimaryKey(String tenantNum, String sysCode) {
        return snowFlakeSequence.nextId()+""+tenantNum+sysCode;
    }

    public Long getPrimaryKeyLong(){
        return snowFlakeSequence.nextId();
    }

    public void setSnowFlakeSequence(SnowFlakeSequence snowFlakeSequence) {
        this.snowFlakeSequence = snowFlakeSequence;
    }
}
