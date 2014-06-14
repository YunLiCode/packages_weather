package com.lewa.weather.entity;

import com.lewa.weather.entity.GpsTask.GpsData;


public interface GpsTaskCallBack {

	public void gpsConnected(GpsData gpsdata);
	
	public void gpsConnectedTimeOut();
	
}
