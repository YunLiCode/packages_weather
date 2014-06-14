package com.lewa.weather;



import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.lewa.weather.entity.WeatherControl;
import com.lewa.weather.provider.LewaDbHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class TickUpdate extends BroadcastReceiver{
  private static final String TAG = "TickUpdate";
  private static TickUpdate tickUpdate;

  @Override
  public void onReceive( final Context context, Intent intent ){
      String action=intent.getAction();
      boolean isWeatherUpdate=true;
      boolean isTimeUpdate=true;
      boolean isDateUpdate=true;
      if(action.equals(Intent.ACTION_SCREEN_OFF)){
          WeatherControl wcc=new WeatherControl(context);
          wcc.releaseLocationManager();
          return;
      } else if(action.equals("com.lewa.pond.push")){
          Bundle extras = intent.getExtras();
          String packageName=extras.getString("lewaApplicationId");
          String message=extras.getString("lewaApplicationMessage");
          if(packageName.equals(context.getPackageName())){
              try {
                JSONObject json=new JSONObject(message);
                String updateType=json.getString(WeatherControl.WEATHER_UPDATE);
                if(updateType.equals(WeatherControl.HOT_CITIES_UPDATE)){
                    new Thread(){
                        public void run() {
                            WeatherControl.updateCityFromServer(context, WeatherControl.HOT_CITIES_URL, LewaDbHelper.HOT_CITIES_DB);
                        };
                    }.start();
                }else if(updateType.equals(WeatherControl.ALL_CITIES_UPDATE)){
                    new Thread(){
                        public void run() {
                            WeatherControl.updateCityFromServer(context, WeatherControl.ALL_CITIES_URL, LewaDbHelper.ALL_CITIES_DB);
                        };
                    }.start();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
          }
          return;
      }else if(action.equals( Intent.ACTION_TIME_TICK)){
    	  isWeatherUpdate=false;
    	  isTimeUpdate=true;
    	  isDateUpdate=true;
      }else if(action.equals( Intent.ACTION_DATE_CHANGED)||action.equals( Intent.ACTION_TIMEZONE_CHANGED)||action.equals( Intent.ACTION_TIME_CHANGED )){
    	  isWeatherUpdate=true;
    	  isTimeUpdate=true;
    	  isDateUpdate=true;
      }else if(action.equals(Intent.ACTION_LOCALE_CHANGED)){
    	  new Thread(){
    		  public void run() {
    			  if(WeatherControl.isWiFiActive(context)||WeatherControl.IsConnection(context)){
    				   WeatherControl wc=new WeatherControl(context);
    				   wc.updateAllWeathers(context);
    				   if(WeatherControl.isCitiesShouldUpdate(LewaDbHelper.HOT_CITIES_DB, context))
    					   	WeatherControl.updateCityFromServer(context, WeatherControl.HOT_CITIES_URL, LewaDbHelper.HOT_CITIES_DB);
    			 }
    	  	};
    	  }.start();
      }else if(action.equals("android.intent.action.killProcess")){
    	  isWeatherUpdate=true;
    	  isTimeUpdate=true;
    	  isDateUpdate=true;
      }
      WeatherWidgetV5_time.updateWidgets(context, isTimeUpdate, isDateUpdate);
      if(isWeatherUpdate)
    	  WeatherWidgetV5_weather.updateWidgets(context, isWeatherUpdate);
  }
  
  /***
   * ע�ἰȡ�� ʱ�Ӹ���  BroadcastReceiver
   * @param context
   */
  public static void register( Context context ){
    synchronized( TickUpdate.class ){
      if( tickUpdate == null ){
        tickUpdate = new TickUpdate();
        IntentFilter filter = new IntentFilter( Intent.ACTION_TIME_TICK );//时间更新
        filter.addAction( Intent.ACTION_DATE_CHANGED );//日期更新
        filter.addAction( Intent.ACTION_TIME_CHANGED );
        filter.addAction( Intent.ACTION_TIMEZONE_CHANGED );//时区变更
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
//        filter.addAction("com.lewa.pond.push");  暂时去掉push更新
        filter.addAction("android.intent.action.killProcess");
        context.registerReceiver( tickUpdate, filter );
      }
    }
  }
  
  public static void unregister( Context context ){
    synchronized( TickUpdate.class ){
      if( tickUpdate != null ){
        context.unregisterReceiver( tickUpdate );
        tickUpdate = null;
      }
    }
  }
}
