package com.lewa.weather.adapters;

import java.util.List;

import com.lewa.weather.entity.City;

import android.R.integer;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;

public abstract class AddCityAdapter extends BaseAdapter {

	protected int column;
	protected OnClickListener onSelectCityListener = null;
	protected  List<City> cities;

	public abstract void update(List<String[]> citys);
	public abstract String getName();
	
	public void setOnSelectCityListener(OnClickListener onSelectCityListener) {
		this.onSelectCityListener = onSelectCityListener;
	}
	

	public int getColumn() {
		return column;
	}
	
	public City getCity(int postion){
	    if(cities!=null&&postion<cities.size())
	        return cities.get(postion);
	    return null;
	}
}
