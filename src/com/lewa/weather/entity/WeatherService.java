package com.lewa.weather.entity;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import com.lewa.weather.control.NetworkControl;
import com.lewa.weather.provider.LewaDbHelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

public class WeatherService extends Service implements Runnable{
	
	private static final String TAG = "lewaWeatherService";
	// 控制线程运行的变量
	private static long startCount = 0;
	private static boolean isThreadRunning = false;
    private boolean isGpsEnable;
    private boolean isFirstLocate=false;
	private SharedPreferences weatherLocation;
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "=====> WeatherService.onBind");
		return null;
	}
	

	public class LocalBinder extends Binder{
		WeatherService getService(){
			return WeatherService.this;
		}
	}
	
	@Override
	public boolean onUnbind(Intent i){
		Log.i(TAG, "=====> WeatherService.onUnbind");
		return false;		
	}
	
	@Override
	public void onRebind(Intent i){
		Log.i(TAG, "=====> WeatherService.onRebind");
	}
	
	@Override
	public void onCreate(){
		Log.i(TAG, "=====> WeatherService.onCreate:" + System.currentTimeMillis());
	      isGpsEnable = WeatherControl.correctCellInfo(this);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Log.i(TAG, "=====> Start RiliWeatherService CurrentTime:" + System.currentTimeMillis());
		
		if(!isThreadRunning)
		{			
			try{
				if(intent != null && WeatherControl.WEATHER_UPDATE_ACTION.equals(intent.getAction()))
				{
					startCount++;
					Log.i(TAG, "=====> No." + startCount + " Start RiliWeatherService");
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			Log.i(TAG, "#################### Run a new RiliWeatherService");
			isThreadRunning = true;
			weatherLocation = (SharedPreferences)getSharedPreferences("weatherLocation", Context.MODE_WORLD_READABLE);
			String location_country=weatherLocation.getString(WeatherControl.LOCATION_COUNTRY, "");
			String automatic_citycode=weatherLocation.getString("automatic", "");
			if(TextUtils.isEmpty(location_country)&&TextUtils.isEmpty(automatic_citycode)){
				LewaDbHelper dbHelper = new LewaDbHelper(getApplicationContext());
				dbHelper.createDataBase(getApplicationContext());
				WeatherControl wc=new WeatherControl(this);
				wc.getLocationAuto(this);
				isFirstLocate=true;
			}
			new Thread(this).start();
		} else {
			Log.i(TAG, "#################### RiliWeatherService is already running!");
		}
		
	}
	
	@Override
	public void onDestroy(){
		Log.i(TAG, "=====> WeatherService.onDestroy");
	}
	
	@Override
	public void run(){
//	    Looper.prepare();
	    if(isGpsEnable){
	        try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
            }
	        WeatherControl.releaseCorrectListener();
	    }
	    boolean isAllSuccess=true;
		SharedPreferences allCities = (SharedPreferences) this.getSharedPreferences("all_city", Context.MODE_WORLD_READABLE);
		Map<String, WeatherSet> map = WeatherControl.loadWeatherData(this);
		Set<String> sets=allCities.getAll().keySet();
		for(String citycode:sets){
		     if(citycode.equals(""))
		         continue;
		    WeatherSet weatherSet=null;
		    if(citycode.contains("|")){
		       weatherSet=map.get(citycode);
		    }else{
		        weatherSet=map.get(citycode+"|false");
		    }
		    if(weatherSet==null)
		        weatherSet=map.get(citycode+"|true");
		    if(WeatherControl.isAbleToDoUpdateAction(this,weatherSet)){
		            int count = 0;
		            WeatherControl wc = new WeatherControl(this);
		            boolean isLocation=false;
                    if(citycode.contains("true")){
                         String locateCity=OrderUtil.getLocateCityCN(this, citycode);
                         locateCity=WeatherControl.buildCityCode(locateCity);
                         String cityString=WeatherControl.localeAddress(this);
                         if(cityString!=null&&locateCity!=null&&!cityString.contains(locateCity)){
                             wc.updateWeatherData(this, null, null, cityString, null,true);
                             continue;
                         }
                         isLocation=true;
                    }
		            citycode=WeatherControl.buildCityCode(citycode);
		            if(citycode.length() > 1 && !wc.updateWeatherData(this,citycode,null,null,System.currentTimeMillis(),isLocation))
		            {
		                isAllSuccess=false;
		            }
		    }
		}
		int repeat_count=weatherLocation.getInt("repeat_count", 0);
		if(!isAllSuccess&&Math.pow(2, repeat_count)<60&&NetworkControl.getNetworkState(this)){
		    WeatherControl.setWeatherUpdateTask(this,(int) Math.pow(2, repeat_count));
		    weatherLocation.edit().putInt("repeat_count", repeat_count+1).commit();
		}else{
		    weatherLocation.edit().putInt("repeat_count",0).commit();
		    weatherLocation.edit().putBoolean("alreadySet", false).commit();
		    WeatherControl.setWeatherUpdateTask(this,60);
		}
		Intent intent=new Intent("com.lewa.weather.refresh");
		this.sendBroadcast(intent);
		if(!isFirstLocate){
			 if(WeatherControl.isCitiesShouldUpdate(LewaDbHelper.HOT_CITIES_DB, this))
				   	WeatherControl.updateCityFromServer(this, WeatherControl.HOT_CITIES_URL, LewaDbHelper.HOT_CITIES_DB);
			 if(WeatherControl.isCitiesShouldUpdate(LewaDbHelper.ALL_CITIES_DB, this))
				   	WeatherControl.updateCityFromServer(this, WeatherControl.ALL_CITIES_URL, LewaDbHelper.ALL_CITIES_DB);
		 }
		isThreadRunning = false;
		stopSelf();
	}
}
