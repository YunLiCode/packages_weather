package com.lewa.weather.entity;

public class City {
    String city_id;
    String name;
    String name_en;
    boolean isAdded;
    public String getCity_id() {
        return city_id;
    }
    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName_en() {
        return name_en;
    }
    public void setName_en(String name_en) {
        this.name_en = name_en;
    }
    public boolean isAdded() {
        return isAdded;
    }
    public void setAdded(boolean isAdded) {
        this.isAdded = isAdded;
    }
    
    
}
