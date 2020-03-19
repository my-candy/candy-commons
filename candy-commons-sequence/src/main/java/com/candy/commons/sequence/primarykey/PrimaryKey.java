package com.candy.commons.sequence.primarykey;
/**
 * 得到主键的接口
 * @author server
 *
 */
public interface PrimaryKey {

	/**
	 * 得到主键
	 * @return
	 */
	public String getPrimaryKey();
	
	/**
	 * 序列的key的前缀
	 * @param tenantNum
	 * @return
	 */
	public String getPrimaryKey(String tenantNum, String sysCode) ;

	/**
	 * 取序列的key
	 * @param key
	 * @return
	 */
	String getPrimaryKey(String key);
	
}



 