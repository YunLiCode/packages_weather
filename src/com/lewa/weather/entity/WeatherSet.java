package com.lewa.weather.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Combines one WeatherCurrentCondition with a List of
 * WeatherForecastConditions.
 */
public class WeatherSet implements Serializable {

	// ===========================================================
	// Fields
	// ===========================================================

	/**
	 * 
	 */
	private static final long serialVersionUID = -2806664543770279559L;
	private WeatherCurrentCondition myCurrentCondition = null;
	private ArrayList<WeatherForecastCondition> myForecastConditions = new ArrayList<WeatherForecastCondition>(5);
	private String city;
	private String longitude;
	private String latitude;
	private String postal_code;
	private String forecast_date;
	private long currentMillis;
	private String provinceCn = "";
	private String cityCn = "";
	private String cityCode = "";
	private long addcurrentMillis;
	private String expires;
	private boolean isLocate;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public long getAddcurrentMillis() {
		return addcurrentMillis;
	}

	public void setAddcurrentMillis(long addcurrentMillis) {
		this.addcurrentMillis = addcurrentMillis;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public long getCurrentMillis() {
		return currentMillis;
	}

	public String getProvinceCn() {
		return provinceCn;
	}

	public void setProvinceCn(String provinceCn) {
		this.provinceCn = provinceCn;
	}

	public String getCityCn() {
		return cityCn;
	}

	public void setCityCn(String cityCn) {
		this.cityCn = cityCn;
	}

	public void setCurrentMillis(long currentMillis) {
		this.currentMillis = currentMillis;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}

	public String getForecast_date() {
		return forecast_date;
	}

	public void setForecast_date(String forecast_date) {
		this.forecast_date = forecast_date;
	}

	public WeatherCurrentCondition getWeatherCurrentCondition() {
		return myCurrentCondition;
	}

	public void setWeatherCurrentCondition(WeatherCurrentCondition myCurrentWeather) {
		this.myCurrentCondition = myCurrentWeather;
	}

	public ArrayList<WeatherForecastCondition> getWeatherForecastConditions() {
		return this.myForecastConditions;
	}

	public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public WeatherForecastCondition getLastWeatherForecastCondition() {
		return this.myForecastConditions.get(this.myForecastConditions.size() - 1);
	}

    public boolean isLocate() {
        return isLocate;
    }

    public void setLocate(boolean isLocate) {
        this.isLocate = isLocate;
    }
    
}