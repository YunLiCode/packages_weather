package com.lewa.weather.entity;

public class NetType {
	private String apn = "";
	private String proxy = "";
	private String typeName = "";
	private int port = 0;
	private boolean isWap = false;
	
	public String getApn() {
		return apn;
	}
	public void setApn(String apn) {
		this.apn = apn;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getProxy() {
		return proxy;
	}
	public void setProxy(String proxy) {
		this.proxy = proxy;
	}
	public boolean isWap() {
		return isWap;
	}
	public void setWap(boolean isWap) {
		this.isWap = isWap;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
