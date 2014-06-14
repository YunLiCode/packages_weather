package com.lewa.weather.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class TimeWidgetReceiver extends BroadcastReceiver {
	boolean isScreenOn = true;
	boolean hasWidget = true;
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("The service receiver");
//		if (!intent.getAction().equals("android.intent.action.BOOT_COMPLETED")
//				&& !intent.getAction().equals("com.when.action.Restart_Service")) {
//			return;
//		}
//
//		Intent service = new Intent(context, WidgetTimeService.class);
//		ComponentName cpName = context.startService(service);
		final Context context1 = context;
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			isScreenOn = false;
		}
		else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			isScreenOn = true;
		}
		if (isScreenOn) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					hasWidget = true;
					if (!hasWidget) {
						AlarmManager am = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
				    	Intent intent = new Intent("com.when.action.UPDATE_WIDGET_ALARM");
				        PendingIntent sender = PendingIntent.getBroadcast(
				        		context1, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				        am.cancel(sender);
					}
				}
			}).start();
		}
	}

}
