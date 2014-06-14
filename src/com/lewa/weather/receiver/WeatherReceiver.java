package com.lewa.weather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.lewa.weather.control.NetworkControl;
import com.lewa.weather.entity.WeatherControl;
import com.lewa.weather.entity.WeatherService;

public class WeatherReceiver extends BroadcastReceiver {
	private static final String TAG = "RiliWeatherReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "====> WeatherReceiver.onReceive Action=" + intent.getAction());
		 
		if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
			SharedPreferences weatherLocation = (SharedPreferences) context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING, Context.MODE_WORLD_READABLE);
			boolean autoUpdate = weatherLocation.getBoolean(WeatherControl.WEATHER_AUTO_UPDATE, true);
			if(autoUpdate && NetworkControl.getNetworkState(context)) {
				doUpdate(context);
			}
		}
		else if(intent.getAction().equals(WeatherControl.WEATHER_UPDATE_ACTION) && NetworkControl.getNetworkState(context)) 
		{ 
			doUpdate(context);
		}
	}
	
	private void doUpdate(Context context) {
		context.startService(new Intent(context, WeatherService.class));
	}

}
