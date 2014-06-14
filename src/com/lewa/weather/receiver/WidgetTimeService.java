package com.lewa.weather.receiver;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;


public class WidgetTimeService extends Service{
	UpdateReceiver widgetsUpdateReceiver = new UpdateReceiver();
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_TIME_TICK);
		intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
		intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
		intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		this.getBaseContext().registerReceiver(widgetsUpdateReceiver, intentFilter);
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sendBroadcast(new Intent("com.when.android.calendar365.lewa.weather.Restart_Service"));
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	class UpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			updateWidget();
		}
		
	}
	
	private void updateWidget() {
		ComponentName provider = new ComponentName(this.getApplicationContext(), this.getApplicationContext().getPackageName()+".WeatherNumberTimeAndCityWidget4x2");
        AppWidgetManager gm = AppWidgetManager.getInstance(this.getApplicationContext());
        int[] appWidgetIds;
        appWidgetIds = gm.getAppWidgetIds(provider);
        final int N = appWidgetIds.length;
	}
}
