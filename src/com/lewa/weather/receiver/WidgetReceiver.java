/**
 * 
 * Author: linsiran
 * Date: 2012-10-23下午3:54:19
 */
package com.lewa.weather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * @author linsiran
 *
 */
public class WidgetReceiver extends BroadcastReceiver{

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i("wangliqiang", "action:"+action);
        if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                || action.equals(Intent.ACTION_TIME_CHANGED)
                || action.equals(Intent.ACTION_DATE_CHANGED)||action.equals(Intent.ACTION_TIME_TICK)) {
        }
	}
}
