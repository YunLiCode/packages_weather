package com.lewa.weather.entity;

import java.io.Serializable;

/**
 * Holds the information between the <forecast_conditions>-tag of what the
 * Google Weather API returned.
 */
public class WeatherForecastCondition implements Serializable {

	// ===========================================================
	// Fields
	// ===========================================================

	/**
	 * 
	 */
	private static final long serialVersionUID = 728279195801433743L;
	private String dayofWeek = null;
	private Integer tempMin = null;
	private Integer tempMax = null;
	private String iconURL = null;
	private String iconName = null;
	private String condition = null;
	private String temperature = null;
	private int iconID = 0;
	private String conditionCN=null;

	// ===========================================================
	// Constructors
	// ===========================================================

	public int getIconID() {
		return iconID;
	}

	public void setIconID(int iconID) {
		this.iconID = iconID;
	}

	public WeatherForecastCondition() {

	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public String getDayofWeek() {
		return dayofWeek;
	}

	public void setDayofWeek(String dayofWeek) {
		this.dayofWeek = dayofWeek;
	}

	public Integer getTempMinCelsius() {
		return tempMin;
	}

	public void setTempMinCelsius(Integer tempMin) {
		this.tempMin = tempMin;
	}

	public Integer getTempMaxCelsius() {
		return tempMax;
	}

	public void setTempMaxCelsius(Integer tempMax) {
		this.tempMax = tempMax;
	}

	public String getIconURL() {
		return iconURL;
	}

	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getConditionCN() {
		return conditionCN;
	}

	public void setConditionCN(String conditionCN) {
		this.conditionCN = conditionCN;
	}
	
	
}