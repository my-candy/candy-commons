package com.candy.commons.codec.jwt;

public class JwtPayload {
	 
	private String kiss;// token的发行者
	private String sub;// token的题目
	private String aud;// token的客户
	private long exp;// 经常使用的，以数字时间定义失效期，也就是当前时间以后的某个时间本token失效。
	private long nbf;// 定义在此时间之前，JWT不会接受处理。
	private int kiat;// JWT发布时间，能用于决定JWT年龄
	private String jti;// JWT唯一标识. 能用于防止 JWT重复使用，一次只用一个token
	private long val;//有效期毫秒
	
	public String getKiss() {
		return kiss;
	}
	public void setKiss(String kiss) {
		this.kiss = kiss;
	}
	public String getSub() {
		return sub;
	}
	public void setSub(String sub) {
		this.sub = sub;
	}
	public String getAud() {
		return aud;
	}
	public void setAud(String aud) {
		this.aud = aud;
	}
	public long getExp() {
		return exp;
	}
	public void setExp(long exp) {
		this.exp = exp;
	}
	public long getNbf() {
		return nbf;
	}
	public void setNbf(long nbf) {
		this.nbf = nbf;
	}
	public long getKiat() {
		return kiat;
	}
	public void setKiat(int kiat) {
		this.kiat = kiat;
	}
	public String getJti() {
		return jti;
	}
	public void setJti(String jti) {
		this.jti = jti;
	}
	public long getVal() {
		return val;
	}
	public void setVal(long val) {
		this.val = val;
	}

	

}
