package com.candy.commons.core.invocation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class InvocationInfo {
	String sysid;
	String tenantid;
	String userid;
	String callid;
	String token;
	String theme;
	String locale;
	String logints;

	Map<Object, Object> extendAttributes = new HashMap<Object, Object>();

	Map<String, String> parameters = new HashMap<String, String>();

	public Iterator<Map.Entry<String, String>> getSummry() {
		Map<String, String> map = new HashMap<String, String>();
		map.putAll(this.parameters);
		map.put("sysid", this.sysid);
		map.put("token", this.token);
		map.put("tenantid", this.tenantid);
		map.put("userid", this.userid);
		map.put("callid", this.callid);
		map.put("locale", this.locale);
		return map.entrySet().iterator();
	}
}
