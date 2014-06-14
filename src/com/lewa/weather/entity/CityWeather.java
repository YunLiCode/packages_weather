package com.lewa.weather.entity;

public class CityWeather {
	String cityName;
	String temp;
	int weather;
	long order;
	String cityCode;
	WeatherSet set;
	boolean isLocate;
	
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public int getWeather() {
		return weather;
	}
	public void setWeather(int weather) {
		this.weather = weather;
	}
	public long getOrder() {
		return order;
	}
	public void setOrder(long order) {
		this.order = order;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public void setTag(WeatherSet set) {
		this.set = set;
	}
	public WeatherSet getTag() {
		return set;
	}
    public boolean isLocate() {
        return isLocate;
    }
    public void setLocate(boolean isLocate) {
        this.isLocate = isLocate;
    }
	
}
