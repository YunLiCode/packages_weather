package com.lewa.weather.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.lewa.weather.provider.LewaDbHelper;
import com.lewa.weather.provider.MyDbHelper;
import com.lewa.weather.R.integer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.lewa.weather.R;

public class OrderUtil {
	private static final String SP_NAME = "order";

	public static final long getOrder(Context context, String cityCode) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);

		return sp.getLong(cityCode, -1);
	}

	public static final void setOrder(Context context, String cityCode,
			long order) {
		System.out.println("order : seted");
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putLong(cityCode, order).commit();
		updateDefault(context);
	}
	
	public static long getAutoOrder(Context context){
        SharedPreferences sp=context.getSharedPreferences("weatherLocation", Context.MODE_PRIVATE);
	    return sp.getLong("autoOrder", 0);   
	}
	
	public static String getAutoCity(Context context){
        SharedPreferences sp=context.getSharedPreferences("weatherLocation", Context.MODE_PRIVATE);
        return sp.getString("automatic", "");   
    }
	
	
	public static void setAutoOrder(Context context,long order){
        SharedPreferences sp=context.getSharedPreferences("weatherLocation", Context.MODE_PRIVATE);
        Editor editor=sp.edit();
        editor.putLong("autoOrder", order).commit();
    }
	
	public static Boolean getIsAutoOrderSetted(Context context){
        SharedPreferences sp=context.getSharedPreferences("weatherLocation", Context.MODE_PRIVATE);
        return sp.getBoolean("autoOrderSeted", false);
    }
	
	public static void setIsAutoOrderSetted(Context context,Boolean isSetted){
        SharedPreferences sp=context.getSharedPreferences("weatherLocation", Context.MODE_PRIVATE);
        Editor editor=sp.edit();
        editor.putBoolean("autoOrderSeted", isSetted).commit();
    }
	
	public static void removeAutoOrder(Context context){
        SharedPreferences sp=context.getSharedPreferences("weatherLocation", Context.MODE_PRIVATE);
        Editor editor=sp.edit();
        editor.remove("autoOrder").commit();
        editor.remove("automatic").commit();
    }
	
	
	public static final void setOrderLite(Context context, String cityCode,
			long order) {
		System.out.println("order : seted");
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putLong(cityCode, order).commit();
	}

	public static final void clear(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
		sp.edit().clear().commit();
	}

	public static final Map<String, ?> getAll(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
		return sp.getAll();
	}

	public static final void remove(Context context, String cityCode) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
		long order=getOrder(context, cityCode);
		Map<String, ?> orderMap=sp.getAll();
		for (Map.Entry<String, ?> e : orderMap.entrySet()) {
		    Long v = (Long)e.getValue();
		    if(v>order){
		        sp.edit().putLong(e.getKey(), v-1).commit();
		    }
		}
		sp.edit().remove(cityCode).commit();
		updateDefault(context);
		updateDefault1(context);
	}

	public static long nextOrder(Context context) {
		Map<String, ?> map = getAll(context);
		  if(map.size()<=0){
	            WeatherSet weatherSet=WeatherControl.getDefaultWeatherData(context);
	            if(weatherSet==null)
	                return 0;
	            WeatherCurrentCondition condition=weatherSet.getWeatherCurrentCondition();
	            if(weatherSet!=null&&condition!=null){
	                MyDbHelper helper=new MyDbHelper(context, "defaultweather");
	                SQLiteDatabase database=helper.getWritableDatabase();
	                database.delete("defaultcityweather", null, null);
	                ContentValues values=new ContentValues();
	                values.put("city",weatherSet.getCityCn());
	                values.put("citycode", weatherSet.getCityCode());
	                values.put("temperature", condition.getTemperature());
	                values.put("windCondition", condition.getWindCondition());
	                String conString=condition.getCondition();
	                values.put("condition", conString);
	                if(conString!=null&&conString.contains("转")){
	                    String firString=conString.substring(0,conString.indexOf("转"));
	                    String secString=conString.substring(conString.indexOf("转")+1);
	                    int firIndex=WeatherControl.getWeather(firString);
	                    int secIndex=WeatherControl.getWeather(secString);
	                    if(firIndex>secIndex){
	                        values.put("sort", firIndex);
	                    }else{
	                        values.put("sort", secIndex);
	                    }
	                }else{
	                    int index=WeatherControl.getWeather(conString);
	                    values.put("sort", index);
	                }
	                values.put("range", weatherSet.getWeatherForecastConditions().get(0).getTemperature());
	                database.insert("defaultcityweather", null, values);
	                database.close();
	                helper.close();
	                context.getContentResolver().notifyChange(Uri.parse("content://com.lewa.weather"), null);
	            }
	        }
		long max = -1;
		for (Map.Entry<String, ?> e : map.entrySet()) {
			Long v = (Long)e.getValue();
			
			if (v > max) {
				max = v;
			}
		}
		return max+1;
	}
	
	public static void resetOrder(Context context){
	    Map<String, ?> map = getAll(context);
	    boolean hasRepeat=false;
	    SharedPreferences sp = context.getSharedPreferences(SP_NAME,
                Context.MODE_PRIVATE);
	    for(long i=0;i<map.size();i++){
	        if(!map.containsValue(i)){
	            List<Long> list=new ArrayList<Long>();
	            for (Map.Entry<String, ?> e : map.entrySet()) {
	                Long value=(Long)e.getValue();
	                if(!list.contains(value)){
	                    list.add(value);
	                }else{
	                    hasRepeat=true;
	                    sp.edit().putLong(e.getKey(), i).commit();
	                }
	            }
	        }
	    }
	    if(hasRepeat)
	        map=getAll(context);
	    for (Map.Entry<String, ?> e : map.entrySet()) {
            Long v = (Long)e.getValue();
            if(v==0){
                sp.edit().putLong(e.getKey(), map.size()-1).commit();
            }else{
                sp.edit().putLong(e.getKey(), v-1).commit();
            }
        }
	    updateDefaultNext(context);
        
	}
	
	public static void updateDefault(Context context) {
		Map<String, ?> map = getAll(context);
		String cityCode = null;
		long min = Long.MAX_VALUE;
		for (Map.Entry<String, ?> e : map.entrySet()) {
			Long v = (Long)e.getValue();
			
			if (v < min) {
				min = v;
				cityCode = e.getKey();
			}
		}
		SharedPreferences sp = context.getSharedPreferences(WeatherControl.WEATHER_CURRENT, Context.MODE_PRIVATE);
		LewaDbHelper dbHelper =new LewaDbHelper(context);
//		String defaultCity = sp.getString(WeatherControl.WEATHER_CUR_CITY_CODE, "");
		
		Editor e = sp.edit();
		if (cityCode != null) {
		    String buildCityCode=WeatherControl.buildCityCode(cityCode);
            String city=dbHelper.getCityInfo(LewaDbHelper.ALL_CITIES_DB, "name", "city_id", buildCityCode);
			
			e.putString(WeatherControl.WEATHER_CUR_CITY_CODE, cityCode);
			e.commit();
			
//			if (!defaultCity.equals(cityCode)) {
				
				e = context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING, Context.MODE_PRIVATE).edit();
				e.putString(WeatherControl.WEATHER_CITY_DEFAULT, city);
			    e.putString(WeatherControl.WEATHER_CITY_CODE_DEFAULT, cityCode);
				e.commit();
				
				e = context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING1, Context.MODE_PRIVATE).edit();
				e.putString(WeatherControl.WEATHER_CITY_DEFAULT, city);
				e.putString(WeatherControl.WEATHER_CITY_CODE_DEFAULT, cityCode);
				e.commit();
				
				Intent intent = new Intent("com.when.android.calendar365.lewa.weather.WEATHER_NUMBER_TIME_CITY_CHANGE_CITY");
				intent.putExtra("main", true);
				context.sendBroadcast(intent);
//			}
		} else {
			e = context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING, Context.MODE_PRIVATE).edit();
			e.putString(WeatherControl.WEATHER_CITY_DEFAULT, "");
			e.putString(WeatherControl.WEATHER_CITY_CODE_DEFAULT, "");
			e.putString(WeatherControl.WEATHER_PROVINCE_DEFAULT, "");
			e.commit();
			
			e = context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING1, Context.MODE_PRIVATE).edit();
			e.putString(WeatherControl.WEATHER_CITY_DEFAULT, "");
			e.putString(WeatherControl.WEATHER_CITY_CODE_DEFAULT, "");
			e.putString(WeatherControl.WEATHER_PROVINCE_DEFAULT, "");
			e.commit();
			Intent intent = new Intent("com.when.android.calendar365.lewa.weather.WEATHER_NUMBER_TIME_CITY_CHANGE_CITY");
			intent.putExtra("main", true);
			context.sendBroadcast(intent);
		}
		setProvider(context);
	}

    public static void setProvider(Context context) {
        WeatherSet weatherSet=WeatherControl.getDefaultWeatherData(context);
        if(weatherSet==null){
            MyDbHelper helper=new MyDbHelper(context, "defaultweather");
            SQLiteDatabase database=helper.getWritableDatabase();
            database.delete("defaultcityweather", null, null);
            database.close();
            helper.close();
            return;
        }
        WeatherCurrentCondition condition=weatherSet.getWeatherCurrentCondition();
        if(weatherSet!=null&&condition!=null){
            MyDbHelper helper=new MyDbHelper(context, "defaultweather");
            SQLiteDatabase database=helper.getWritableDatabase();
            database.delete("defaultcityweather", null, null);
            ContentValues values=new ContentValues();
            values.put("city",weatherSet.getCityCn());
            values.put("citycode", weatherSet.getCityCode());
            WeatherCurrentCondition currentCondition=weatherSet.getWeatherCurrentCondition();
            if(currentCondition!=null){
                String humidity=currentCondition.getShidu();
                if(!TextUtils.isEmpty(humidity))
                    values.put("humidity", humidity);
                String pmCondition=currentCondition.getPmcondition();
                if(!TextUtils.isEmpty(pmCondition))
                    values.put("pmcondition", pmCondition);
            }
            String temperature=condition.getTemperature();
            String range=weatherSet.getWeatherForecastConditions().get(0).getTemperature();
            if (temperature!=null&&!temperature.contains(context.getResources().getString(R.string.temporary))){
                    values.put("temperature",temperature);
            }else{
                if(range!=null&&range.contains("~")){
                    int index = range.indexOf('~');
                    range = range.substring(0, range.length() - 1);
                    String beforeTemp=range.substring(0, index - 1);
                    String afterTemp=range.substring(index + 1);
                    if(!TextUtils.isEmpty(beforeTemp)&&!TextUtils.isEmpty(afterTemp)){
                        int v1 = Integer.parseInt(beforeTemp);
                        int v2 = Integer.parseInt(afterTemp);
                        if (v1 < v2) {
                            int tp = v1;
                            v1 = v2;
                            v2 = tp;
                        }
                        values.put("temperature",(v2+v1)/2+"℃");
                    }
                }
            }
            values.put("windCondition", condition.getWindCondition());
            String conString=condition.getCondition();
            values.put("condition", conString);
            String conditonCN=condition.getConditionCN();
            if(conditonCN!=null&&conditonCN.contains("转")){
                String firString=conditonCN.substring(0,conditonCN.indexOf("转"));
                String secString=conditonCN.substring(conditonCN.indexOf("转")+1);
                int firIndex=WeatherControl.getWeather(firString);
                int secIndex=WeatherControl.getWeather(secString);
                if(firIndex>secIndex){
                    values.put("sort", firIndex);
                }else{
                    values.put("sort", secIndex);
                }
            }else{
                int index=WeatherControl.getWeather(conditonCN);
                values.put("sort", index);
            }
            values.put("range",range );
            database.insert("defaultcityweather", null, values);
            database.close();
            helper.close();
            context.getContentResolver().notifyChange(Uri.parse("content://com.lewa.weather"), null);
        }
    }
	
	public static String getDefaultCity(Context context){
	    SharedPreferences sp=context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING, Context.MODE_PRIVATE);
	    return sp.getString(WeatherControl.WEATHER_CITY_DEFAULT, "");
	}
	
	public static void updateDefaultNext(Context context) {
        Map<String, ?> map = getAll(context);
        String cityCode = null;
        long min = Long.MAX_VALUE;
        for (Map.Entry<String, ?> e : map.entrySet()) {
            Long v = (Long)e.getValue();
            
            if (v < min) {
                min = v;
                cityCode = e.getKey();
            }
        }
        SharedPreferences sp = context.getSharedPreferences(WeatherControl.WEATHER_CURRENT, Context.MODE_PRIVATE);
        
        String defaultCity = sp.getString(WeatherControl.WEATHER_CUR_CITY_CODE, "");
        LewaDbHelper dbHelper=new LewaDbHelper(context);
        Editor e = sp.edit();
        if (cityCode != null) {
            String buildCityCode=WeatherControl.buildCityCode(cityCode);
            String city=dbHelper.getCityInfo(LewaDbHelper.ALL_CITIES_DB, "name", "city_id", buildCityCode);
            e.putString(WeatherControl.WEATHER_CUR_CITY_CODE, cityCode);
            e.commit();
            
            if (!defaultCity.equals(cityCode)) {
                
                e = context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING, Context.MODE_PRIVATE).edit();
                e.putString(WeatherControl.WEATHER_CITY_DEFAULT, city);
                e.putString(WeatherControl.WEATHER_CITY_CODE_DEFAULT, cityCode);
//                e.putString(WeatherControl.WEATHER_PROVINCE_DEFAULT, province);
                e.commit();
                
                e = context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING1, Context.MODE_PRIVATE).edit();
                e.putString(WeatherControl.WEATHER_CITY_DEFAULT, city);
                e.putString(WeatherControl.WEATHER_CITY_CODE_DEFAULT, cityCode);
//                e.putString(WeatherControl.WEATHER_PROVINCE_DEFAULT, province);
                e.commit();
            }
        } else {
            e = context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING, Context.MODE_PRIVATE).edit();
            e.putString(WeatherControl.WEATHER_CITY_DEFAULT, "");
            e.putString(WeatherControl.WEATHER_CITY_CODE_DEFAULT, "");
            e.putString(WeatherControl.WEATHER_PROVINCE_DEFAULT, "");
            e.commit();
            
            e = context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING1, Context.MODE_PRIVATE).edit();
            e.putString(WeatherControl.WEATHER_CITY_DEFAULT, "");
            e.putString(WeatherControl.WEATHER_CITY_CODE_DEFAULT, "");
            e.putString(WeatherControl.WEATHER_PROVINCE_DEFAULT, "");
            e.commit();
        }
    }
	
	
	public static void updateDefault1(Context context) {
		Map<String, ?> map = getAll(context);
		String cityCode = null;
		long min = Long.MAX_VALUE;
		LewaDbHelper dbHelper=new LewaDbHelper(context);
		for (Map.Entry<String, ?> e : map.entrySet()) {
			Long v = (Long)e.getValue();
			
			if (v < min) {
				min = v;
				cityCode = e.getKey();
//				 if(cityCode.contains("true"))
//	                  cityCode=cityCode.substring(0, cityCode.lastIndexOf("|"));
			}
		}
		SharedPreferences sp = context.getSharedPreferences(WeatherControl.WEATHER_CURRENT, Context.MODE_PRIVATE);
		
		String defaultCity = sp.getString(WeatherControl.WEATHER_CUR_CITY_CODE, "");
		
		Editor e = sp.edit();
		if (cityCode != null) {
			String buildCityCode=WeatherControl.buildCityCode(cityCode);
            String city=dbHelper.getCityInfo(LewaDbHelper.ALL_CITIES_DB, "name", "city_id", buildCityCode);
			
			e.putString(WeatherControl.WEATHER_CUR_CITY_CODE, cityCode);
			e.commit();
			
//			if (!defaultCity.equals(cityCode)) {
				
				e = context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING, Context.MODE_PRIVATE).edit();
				e.putString(WeatherControl.WEATHER_CITY_DEFAULT, city);
				e.putString(WeatherControl.WEATHER_CITY_CODE_DEFAULT, cityCode);
				e.commit();
				
				e = context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING1, Context.MODE_PRIVATE).edit();
				e.putString(WeatherControl.WEATHER_CITY_DEFAULT, city);
				e.putString(WeatherControl.WEATHER_CITY_CODE_DEFAULT, cityCode);
				e.commit();
				
				Intent intent = new Intent("com.when.android.calendar365.lewa.weather.WEATHER_NUMBER_TIME_CITY_CHANGE_CITY");
				intent.putExtra("main", true);
				context.sendBroadcast(intent);
//			}
		} else {
			e = context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING, Context.MODE_PRIVATE).edit();
			e.putString(WeatherControl.WEATHER_CITY_DEFAULT, "");
			e.putString(WeatherControl.WEATHER_CITY_CODE_DEFAULT, "");
			e.putString(WeatherControl.WEATHER_PROVINCE_DEFAULT, "");
			e.commit();
			
			e = context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING1, Context.MODE_PRIVATE).edit();
			e.putString(WeatherControl.WEATHER_CITY_DEFAULT, "");
			e.putString(WeatherControl.WEATHER_CITY_CODE_DEFAULT, "");
			e.putString(WeatherControl.WEATHER_PROVINCE_DEFAULT, "");
			e.commit();
			Intent intent = new Intent("com.when.android.calendar365.lewa.weather.WEATHER_NUMBER_TIME_CITY_CHANGE_CITY");
			intent.putExtra("main", true);
			context.sendBroadcast(intent);
		}
	}
	
	public static String getLocateCityCN(Context context,String key){
	    SharedPreferences sp=context.getSharedPreferences("all_city", Context.MODE_PRIVATE);
	    return sp.getString(key, "");
	}

}
