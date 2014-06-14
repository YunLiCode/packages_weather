package com.lewa.weather.provider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

import com.lewa.weather.entity.WeatherControl;
import com.lewa.weather.entity.WeatherSet;

public class WeatherProvider extends WhenProvider {

	public static String AUTHORITY = "com.lewa.weather";
	
	@Override
	public AbsIconToolAdapter getIconToolAdapter() {
		return null;
	}

	@Override
	public AbsInstanceAdapter getInstanceAdapter() {
		return new InstanceAdapter(this.getContext());
	}

	public static final long fetchToday() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}
	
	class WeatherInstance {
		String wind;
		String temp;
		String cond;
		String location;
		String id;
		long time;
		
		public void setTime(long time) {
			this.time = time;
		}
		
		public long getTime() {
			return time;
		}
		
		public String getWind() {
			return wind;
		}
		public void setWind(String wind) {
			this.wind = wind;
		}
		public String getTemp() {
			return temp;
		}
		public void setTemp(String temp) {
			this.temp = temp;
		}
		public String getCond() {
			return cond;
		}
		public void setCond(String cond) {
			this.cond = cond;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
	}
	
	class InstanceAdapter extends AbsInstanceAdapter {

		Context context;
		long today;
		List<WeatherInstance> data;
		
		private boolean hasData(long when) {
			
			SharedPreferences sp1 = context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING, Context.MODE_PRIVATE);
			
			if (!sp1.getBoolean(WeatherControl.WEATHER_SHOW, true)) {
				return false;
			}
			SharedPreferences sp = context.getSharedPreferences("order", Context.MODE_PRIVATE);
			Map<String, ?> all = sp.getAll();
			String sel = "";
			long min = Long.MAX_VALUE;
			
			for (Map.Entry<String, ?> e : all.entrySet()) {
				long or = (Long)e.getValue();
				
				if (or < min) {
					min = or;
					sel = e.getKey();
				}
			}
			System.out.println("looking for : " + when);
			Map<String, WeatherSet> map = WeatherControl.loadWeatherData(context);
			data = new ArrayList<WeatherInstance>();
			for (WeatherSet set : map.values()) {

				if (set == null) {
					continue;
				}
				if (!set.getCityCode().equals(sel)) {
					continue;
				}
				WeatherInstance ins = new WeatherInstance();
				ins.setLocation(set.getCityCn());
				ins.setId(set.getCityCode());
				ins.setTime(when);
				if (when == today) {
					ins.setWind(set.getWeatherCurrentCondition().getWindCondition());
					ins.setTemp(set.getWeatherCurrentCondition().getTemperature());
					ins.setCond(set.getWeatherCurrentCondition().getCondition());
				} else{
					int id = (int)((when - today) / 86400000L) - 1;
					ins.setWind("");
					ins.setTemp(set.getWeatherForecastConditions().get(id).getTemperature());
					ins.setCond(set.getWeatherForecastConditions().get(id).getCondition());
				}
				data.add(ins);
			}
			return data.size() > 0;
		}
		
		public InstanceAdapter(Context context) {
			super(context);
			today = fetchToday();
			this.context = context;
		}

		@Override
		public String getContent(int arg0) {
			WeatherInstance ins = data.get(arg0);			
			
			String s = ins.getCond();
			if (!ins.getTemp().contains("暂")) s += " " + ins.getTemp();
			if (!ins.getWind().contains("暂")) s += " " + ins.getWind();
			
			if (s.contains("暂")) {
				s = "暂无数据";
			}
			return s;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public String getIcon(int arg0) {
			return "#ff00ff";
		}

		@Override
		public String getSummary(int arg0) {
			WeatherInstance ins = data.get(arg0);
			return ins.getLocation();
		}

		@Override
		public String getTime(int arg0) {
			return "" + data.get(arg0).getTime() + " 全天";
		}

		@Override
		public boolean loadLastSegment(String arg0) {
			
			long when = Long.parseLong(arg0);
			
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(when);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			when = c.getTimeInMillis();
			
			if (when > today + 86400000L * 5 || when < today) {
				return false;
			}
			return hasData(when);
		}

		@Override
		public String getPackageName(int position) {
			// TODO Auto-generated method stub
			return "com.lewa.weather.LewaWeather";
		}
		
		
	}
}
