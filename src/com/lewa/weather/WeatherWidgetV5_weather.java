package com.lewa.weather;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.R.anim;
import android.R.integer;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.lewa.weather.control.NetworkControl;
import com.lewa.weather.entity.CityEntity;
import com.lewa.weather.entity.DataFormatControl;
import com.lewa.weather.entity.LunarItem;
import com.lewa.weather.entity.OrderUtil;
import com.lewa.weather.entity.WeatherControl;
import com.lewa.weather.entity.WeatherCurrentCondition;
import com.lewa.weather.entity.WeatherForecastCondition;
import com.lewa.weather.entity.WeatherService;
import com.lewa.weather.entity.WeatherSet;
import com.lewa.weather.R;
import com.lewa.weather.R.dimen;
import com.lewa.weather.R.drawable;
import com.lewa.weather.R.id;
import com.lewa.weather.R.layout;
import com.lewa.weather.R.string;

public class WeatherWidgetV5_weather extends AppWidgetProvider {
	private final static String MAIN_PACKAGE_NAME = "com.when.android.calendar365";
	private static Map<String, String> lunarHoliday = new HashMap<String, String>();
	private static Map<String, String> solarHoliday = new HashMap<String, String>();
	private static WeatherSet weatherSet;
	private static WeatherSet ws;
	private static WeatherCurrentCondition wcc;
	private static WeatherForecastCondition wfc;

	private static boolean time_temp_layout = true;
	private static boolean city_temp_layout = false;

	private static SharedPreferences weatherLocation;
	private static String citycn;
	private static String condition;

	// jiangyulong 2012-12-29
	private static Bitmap temp_line;
	private static Bitmap srcBitmap;
	private static Bitmap weather_num_time_bt;
	private static Bitmap weather_city_bt;

	private static final float ScaleX = 0.83f;
	private static final String ANIMATION_WIDAGET = "com.when.android.calendar365.lewa.weather.UPDATE_WIDGET_NEXT_LAYOUT";
	private static final String UPDATE_WIDAGET = "com.lewa.weather.UPDATE_WIDGET_NEXT_LAYOUT";
	private static String lastCondition;
	private static boolean isChange;
	private static Bitmap hourBitmap;
	private static boolean isNext = false;
	private static Handler handler = new Handler();
	private static Runnable setProviderRunnable;
	private static Runnable updateWidgetRunnable;
	private static long toastLastShowTime;
//	private static RemoteViews updateViews;
	private static Bitmap hour1;
	private static Bitmap hour2;
	private static Bitmap minute1;
	private static Bitmap minute2;

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for (int i = 0; i < appWidgetIds.length; i++) {
			RemoteViews updateView = lewaBuildUpdate(context, appWidgetIds[i],
					false, true, false);
			appWidgetManager.updateAppWidget(appWidgetIds[i], updateView);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {

		updateTimeAlarm(context);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName("com.lewa.weather",
				"com.lewa.weather.receiver.WidgetReceiver"),
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
		SharedPreferences sp = context.getSharedPreferences("weatherLocation",
				Context.MODE_PRIVATE);
		boolean firstCreate = sp.getBoolean("firstCreate", true);
		if (firstCreate && OrderUtil.getAutoCity(context).equals("")) {
			WeatherControl wcc = new WeatherControl(context);
			wcc.getLocationAuto(context);
			Editor editor = sp.edit();
			editor.putBoolean("firstCreate", false).commit();
		}
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName("com.lewa.weather",
				"com.lewa.weather.receiver.WidgetReceiver"),
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		ComponentName provider = new ComponentName(context,
				context.getPackageName() + ".WeatherWidgetV5_weather");
		final AppWidgetManager gm = AppWidgetManager.getInstance(context);
		final int[] appWidgetIds;
		appWidgetIds = gm.getAppWidgetIds(provider);
		final int N = appWidgetIds.length;
		if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
			updateTimeAlarm(context);
		}

		if (intent.getAction().equals("com.lewa.weather.widget.refresh")) {
			if (WeatherControl.isWiFiActive(context)
					|| WeatherControl.IsConnection(context)) {
				Boolean isRefresh = intent.getBooleanExtra("isRefresh", true);
				new WeatherTask(context).execute();
				for (int i = 0; i < appWidgetIds.length; i++) {
					RemoteViews views = lewaBuildUpdate(context,
							appWidgetIds[i], isRefresh, true, false);
					gm.updateAppWidget(appWidgetIds[i], views);
				}
			} else {
				if (System.currentTimeMillis() - toastLastShowTime > 5000) {
					toastLastShowTime = System.currentTimeMillis();
					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.pls_check_network_status_string),
							Toast.LENGTH_SHORT).show();
				}
			}
			return;
		}
		if (UPDATE_WIDAGET.equals(intent.getAction())) {
			if (setProviderRunnable == null) {
				setProviderRunnable = new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						OrderUtil.setProvider(context);
						context.sendBroadcast(new Intent(
								"com.lewa.weather.refresh"));
					}
				};
			}
			
			if(updateWidgetRunnable==null){
				updateWidgetRunnable=new Runnable() {
					public void run() {
						int id=intent.getIntExtra("id", appWidgetIds[0]);
						RemoteViews views = lewaBuildUpdate(context, id, false, true, false);
						gm.updateAppWidget(id, views);
					}
				};
			}
			handler.removeCallbacks(setProviderRunnable);
			handler.removeCallbacks(updateWidgetRunnable);
			if (condition != null)
				lastCondition = condition;
			updateNextCity(context, gm,
					intent.getIntExtra("id", appWidgetIds[0]));
			handler.postDelayed(updateWidgetRunnable, 1500);
			new Thread() {
				public void run() {
					handler.postDelayed(setProviderRunnable, 1500);
				};
			}.start();
		}
		if (!UPDATE_WIDAGET.equals(intent.getAction())) {
			for (int i = 0; i < N; i++) {
				try {
					WeatherWidgetV5_weather.updateAppWidget(context, gm,
							appWidgetIds[i], true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			WeatherControl.setWeatherUpdateTask(context, 60);
		}
		super.onReceive(context, intent);
	}

	private void updateNextCity(Context context, AppWidgetManager gm, int id) {
		OrderUtil.resetOrder(context);
		RemoteViews views = null;
		views = lewaBuildUpdate(context, id, false, true, true);
		gm.updateAppWidget(id, views);
	}

	public static void setImageViewBg(RemoteViews views, int resid) {
		views.setImageViewResource(R.id.lewa_widget_bg_second, resid);
	}

	private static synchronized RemoteViews lewaBuildUpdate(Context context,
			int i, boolean isRefresh, boolean isWeatherUpdate,
			boolean isUpdateNext) {
		// TODO Auto-generated method stub
		RemoteViews updateViews;
		Date date = new Date();
		boolean bool = DateFormat.is24HourFormat(context);
		updateViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_v5_weather);
		updateViews.removeAllViews(R.id.v5_widget_weather_before);
		if (isUpdateNext) {
			updateViews.removeAllViews(R.id.v5_widget_icon_ll);
			updateViews.removeAllViews(R.id.v5_widget_AQI_ll);
			RemoteViews view_add = new RemoteViews(context.getPackageName(),
					R.layout.widget_v5_icon_next);
			updateViews.addView(R.id.v5_widget_icon_ll, view_add);
			view_add = new RemoteViews(context.getPackageName(),
					R.layout.widget_v5_pm_next);
			updateViews.addView(R.id.v5_widget_AQI_ll, view_add);
			view_add = new RemoteViews(context.getPackageName(),
					R.layout.widget_v5_before_next);
			updateViews.addView(R.id.v5_widget_weather_before, view_add);
		}
		
		boolean isCityNull=updateWeather(updateViews, context, isWeatherUpdate, isUpdateNext);
		Intent launchIntent = new Intent();
		launchIntent.setComponent(new ComponentName(context,
				WeatherWidgetV5_weather.class));
		launchIntent.setAction(UPDATE_WIDAGET);
		launchIntent.putExtra("id", i);
		PendingIntent weatherPendingIntent = PendingIntent.getBroadcast(
				context, i, launchIntent, 0);
		updateViews.setOnClickPendingIntent(R.id.v5_widget_city_container,
				weatherPendingIntent);
		if(!isCityNull){
			updateViews.setOnClickPendingIntent(R.id.v5_widget_temp_container,
					weatherPendingIntent);
			updateViews.setOnClickPendingIntent(R.id.v5_widget_AQI_ll,
					weatherPendingIntent);
		}

		launchIntent = new Intent(context, LewaWeather.class);
		weatherPendingIntent = PendingIntent.getActivity(context, 0,
				launchIntent, 0);
		updateViews.setOnClickPendingIntent(R.id.v5_widget_weather_icon,
				weatherPendingIntent);
		if(isCityNull){
			updateViews.setOnClickPendingIntent(R.id.v5_widget_temp_container,
					weatherPendingIntent);
			updateViews.setOnClickPendingIntent(R.id.v5_widget_AQI_ll,
					weatherPendingIntent);
		}
		return updateViews;
	}

	public static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			boolean isWeatherUpdate) {

		RemoteViews updateView = lewaBuildUpdate(context, appWidgetId, false,
				isWeatherUpdate, false);
		appWidgetManager.updateAppWidget(appWidgetId, updateView);
	}

	public static boolean updateWidgets(Context context, boolean isWeatherUpdate) {
		ComponentName provider = new ComponentName(context,
				context.getPackageName() + ".WeatherWidgetV5_weather");
		AppWidgetManager gm = AppWidgetManager.getInstance(context);
		int[] appWidgetIds;
		appWidgetIds = gm.getAppWidgetIds(provider);
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			WeatherWidgetV5_weather.updateAppWidget(context, gm,
					appWidgetIds[i], isWeatherUpdate);
		}
		return N > 0;
	}

	private void updateTimeAlarm(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent("com.when.action.UPDATE_WIDGET_ALARM");
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		am.setRepeating(AlarmManager.RTC, 0, 60 * 1000, sender);
	}

	class WeatherTask extends AsyncTask<Context, Void, Boolean> {
		private WeakReference<Context> context;
		private WeatherSet weatherSet;
		private String citycode;

		public WeatherTask(Context context) {
			this.context = new WeakReference<Context>(context);
		}

		@Override
		protected final Boolean doInBackground(Context... params) {
			try {
				WeatherControl wc = new WeatherControl(context.get());
				final SharedPreferences weatherLocation = context.get()
						.getSharedPreferences(
								WeatherControl.WEATHER_LOCATION_SETTING,
								Context.MODE_WORLD_READABLE);
				String citycn_default = weatherLocation.getString(
						WeatherControl.WEATHER_CITY_DEFAULT, "");
				String provinceCn_default = weatherLocation.getString(
						WeatherControl.WEATHER_PROVINCE_DEFAULT, "");
				String cityCode_default = weatherLocation.getString(
						WeatherControl.WEATHER_CITY_CODE_DEFAULT, "");
				boolean isLocation = false;
				if (cityCode_default.contains("true"))
					isLocation = true;
				cityCode_default = WeatherControl
						.buildCityCode(cityCode_default);
				if (!wc.updateWeatherData(context.get(), cityCode_default,
						provinceCn_default, citycn_default,
						System.currentTimeMillis(), isLocation)) {
					return false;
				} else {
					boolean alreadySet = weatherLocation.getBoolean(
							"alreadySet", false);
					if (!alreadySet)
						WeatherControl.setWeatherUpdateTask(context.get(), 60);
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected final void onPostExecute(Boolean result) {
			if (!result) {
				ComponentName provider = new ComponentName(context.get(),
						context.get().getPackageName()
								+ ".WeatherWidgetV5_weather");
				AppWidgetManager gm = AppWidgetManager.getInstance(context
						.get());
				int[] appWidgetIds;
				appWidgetIds = gm.getAppWidgetIds(provider);
				for (int i = 0; i < appWidgetIds.length; i++) {
					RemoteViews views = lewaBuildUpdate(context.get(),
							appWidgetIds[i], false, true, false);
					gm.updateAppWidget(appWidgetIds[i], views);
				}

			} else {
				Intent intent = new Intent("com.lewa.weather.refresh");
				context.get().sendBroadcast(intent);
			}
			super.onPostExecute(result);
		}

	}


	public static int getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top) + 2;
	}

	public static boolean updateWeather(RemoteViews updateViews, Context context,
			boolean isWeatherUpdate, boolean isUpdateNext) {
		boolean isLocate = false;
		weatherLocation = context.getSharedPreferences(
				WeatherControl.WEATHER_LOCATION_SETTING1,
				Context.MODE_WORLD_READABLE);
		citycn = weatherLocation.getString(WeatherControl.WEATHER_CITY_DEFAULT,
				"");
		String cityCode = weatherLocation.getString(
				WeatherControl.WEATHER_CITY_CODE_DEFAULT, "");
		if (cityCode.contains("true"))
			isLocate = true;
		if (cityCode.contains("|"))
			cityCode = cityCode.substring(0, cityCode.lastIndexOf("|"));
		String temperatureRange = "";
		condition = context.getResources().getString(R.string.weather_no_data);
		Map<String, WeatherSet> map = WeatherControl.loadWeatherData(context);
		if (null == map)
			map = new HashMap<String, WeatherSet>();
		int temp_size = context.getResources().getInteger(
				R.integer.v5_widget_weather_temp_size);
		int temp_transy = context.getResources().getInteger(
				R.integer.v5_widget_weather_temp_transy);
		int city_size = context.getResources().getInteger(
				R.integer.v5_widget_weather_city_size);
		int city_transy = context.getResources().getInteger(
				R.integer.v5_widget_weather_city_transy);
		int pm_size = context.getResources().getInteger(
				R.integer.v5_widget_weather_pm_size);
		int pm_transy = context.getResources().getInteger(
				R.integer.v5_widget_weather_pm_transy);
		int nonet_transy=context.getResources().getInteger(
				R.integer.v5_widget_nonet_text_transy);
		int nonet_text_size=context.getResources().getInteger(
				R.integer.v5_widget_nonet_text_size);
		if (citycn.length() < 1) {
			if (map.size() > 0) {
				OrderUtil.updateDefault1(context);
			}
		}
		weatherSet = map.get(cityCode + "|" + isLocate);
		if (weatherSet != null
				&& weatherSet.getWeatherCurrentCondition() != null) {
			WeatherCurrentCondition wcc = weatherSet
					.getWeatherCurrentCondition();
			condition = (TextUtils.isEmpty(wcc.getConditionCN()) ? TextUtils.isEmpty(wcc.getCondition())?context.getResources().getString(R.string.weather_unknown):wcc.getCondition() : wcc
                    .getConditionCN());
			if (lastCondition == null)
				lastCondition = condition;
			if (isUpdateNext) {
				String imageString_before = WeatherControl
						.getImageString(lastCondition);
				if (!imageString_before.equals("")) {
					int imgImageId_before = context.getResources()
							.getIdentifier(
									"v5_widget_icon_" + imageString_before,
									"drawable", "com.lewa.weather");
					updateViews.setImageViewResource(
							R.id.v5_widget_weather_icon_before,
							imgImageId_before);
				}
				if (!WeatherControl.isBitmapNull(tempBitmap)) {
				    WeatherControl.recyleBitmap(lastTempBitmap);
				    lastTempBitmap=tempBitmap;
					updateViews.setImageViewBitmap(
							R.id.v5_widget_temp_range_before, tempBitmap);
				}
				if (!isLastPmNull && !WeatherControl.isBitmapNull(AQIBitmap)) {
				    WeatherControl.recyleBitmap(lastAQIBitmap);
					lastAQIBitmap=AQIBitmap;
					updateViews.setImageViewBitmap(R.id.v5_widget_AQI_before,
							AQIBitmap);
				}
				if(!isLastTempNull){
					updateViews.setImageViewResource(R.id.v5_widget_temp_degree_before,R.drawable.v5_widget_degree);
				}
			}else{
			    WeatherControl.recyleBitmap(tempBitmap);
			    WeatherControl.recyleBitmap(AQIBitmap);
			}
			lastCondition = condition;
			String imageString = WeatherControl.getImageString(condition);
			if (!imageString.equals("")) {
				int imgImageId = context.getResources().getIdentifier(
						"v5_widget_icon_" + imageString, "drawable",
						"com.lewa.weather");
				updateViews.setImageViewResource(R.id.v5_widget_weather_icon,
						imgImageId);
			}
			String windCondition;
			if(WeatherControl.isLanguageEnUs()){
				windCondition=wcc.getWindDirection();
			}else{
			    windCondition = wcc.getWindCondition();
				windCondition=WeatherControl.removeBlank(windCondition);
			}
			if (windCondition != null) {
				updateViews.setViewVisibility(R.id.v5_widget_AQI, View.VISIBLE);
				if (WeatherControl.isBitmapNull(AQIBitmap)|| isWeatherUpdate) {
					AQIBitmap = WeatherControl.createTextBitmap(context,
							windCondition, pm_size, pm_transy, true);
				}
				isLastPmNull = false;
				updateViews.setImageViewBitmap(R.id.v5_widget_AQI, AQIBitmap);
			} else {
				updateViews.setViewVisibility(R.id.v5_widget_AQI, View.GONE);
				isLastPmNull = true;
			}

			String range = weatherSet.getWeatherForecastConditions().get(0)
					.getTemperature();

			if (range != null && range.contains("~")) {
				int index = range.indexOf('~');
				range = range.substring(0, range.length() - 1);
				String beforeTemp = range.substring(0, index - 1);
				String afterTemp = range.substring(index + 1);
				if (!TextUtils.isEmpty(beforeTemp)
						&& !TextUtils.isEmpty(afterTemp)) {
					int v1 = Integer.parseInt(beforeTemp);
					int v2 = Integer.parseInt(afterTemp);
					if (v1 < v2) {
						int tp = v1;
						v1 = v2;
						v2 = tp;
					}
					if (WeatherControl.isBitmapNull(tempBitmap) || isWeatherUpdate) {
						tempBitmap = WeatherControl.createTextBitmap(context,
								v2 + "-" + v1, temp_size, temp_transy, true);
						updateViews.setViewVisibility(R.id.v5_widget_temp_degree, View.VISIBLE);
						updateViews.setImageViewResource(R.id.v5_widget_temp_degree,R.drawable.v5_widget_degree);
					}
				}else{
					tempBitmap = WeatherControl.createTextBitmap(context,
							context.getResources().getString(R.string.no_weather_data), temp_size, temp_transy, true);
				}
				updateViews.setViewVisibility(R.id.v5_widget_temp_range, View.VISIBLE);
			}else{
				if (WeatherControl.isBitmapNull(tempBitmap) || isWeatherUpdate) {
					tempBitmap = WeatherControl.createTextBitmap(context,
							context.getResources().getString(R.string.weather_no_data), temp_size, temp_transy, true);
				}
				updateViews.setViewVisibility(R.id.v5_widget_temp_degree, View.GONE);
			}
			updateViews.setImageViewBitmap(R.id.v5_widget_temp_range,
					tempBitmap);
			String cityCn = weatherSet.getCityCn();
			cityCn = WeatherControl.removeBrackets(cityCn);
			cityCn=WeatherControl.buildCityName(cityCn);
			if (cityCn != null) {
				if (WeatherControl.isBitmapNull(cityBitmap)  || isWeatherUpdate) {
					WeatherControl.recyleBitmap(cityBitmap);
					cityBitmap = WeatherControl.createTextBitmap(context,
							cityCn, city_size, city_transy, false);
				}
				updateViews.setImageViewBitmap(R.id.v5_widget_city, cityBitmap);
				updateViews.setViewVisibility(R.id.v5_widget_city_container,
						View.VISIBLE);
			}
			isLastTempNull=false;
		} else if (TextUtils.isEmpty(cityCode)) {
			isLastTempNull=true;
			updateViews.setImageViewResource(R.id.v5_widget_weather_icon,
					R.drawable.v5_widget_icon_default);
			String no_location = context.getResources().getString(
					R.string.v5_nolocation);
			String add_city = context.getResources().getString(
					R.string.v5_add_city);
			updateViews.setImageViewBitmap(R.id.v5_widget_temp_range,
					WeatherControl.createTextBitmap(context, no_location,nonet_text_size,
							nonet_transy, true));
			updateViews.setImageViewBitmap(R.id.v5_widget_AQI, WeatherControl
					.createTextBitmap(context, add_city, nonet_text_size, nonet_transy, true));
			updateViews.setViewVisibility(R.id.v5_widget_city_container,
					View.GONE);
			updateViews.setViewVisibility(R.id.v5_widget_temp_degree, View.GONE);
		} else {
			updateViews.setImageViewResource(R.id.v5_widget_weather_icon,
					R.drawable.v5_widget_icon_default);
			String no_net = context.getResources().getString(R.string.no_net);
			String no_weather_data = context.getResources().getString(
					R.string.no_weather_data);
			updateViews.setImageViewBitmap(R.id.v5_widget_temp_range,
					WeatherControl.createTextBitmap(context, no_net, nonet_text_size, nonet_transy,
							true));
			updateViews.setImageViewBitmap(R.id.v5_widget_AQI, WeatherControl
					.createTextBitmap(context, no_weather_data, nonet_text_size, nonet_transy, true));
			citycn = WeatherControl.removeBrackets(citycn);
			citycn=WeatherControl.buildCityName(citycn);
			if (citycn != null) {
				if (cityBitmap == null || isWeatherUpdate) {
					WeatherControl.recyleBitmap(cityBitmap);
					cityBitmap = WeatherControl.createTextBitmap(context,
							citycn, city_size, city_transy, false);
				}
				updateViews.setViewVisibility(R.id.v5_widget_city_container,
						View.VISIBLE);
				updateViews.setImageViewBitmap(R.id.v5_widget_city, cityBitmap);
			}
			isLastTempNull=true;
			updateViews.setImageViewBitmap(
						R.id.v5_widget_temp_range_before, null);
			updateViews.setImageViewBitmap(R.id.v5_widget_AQI_before,
						null);
			WeatherControl.recyleBitmap(AQIBitmap);
			WeatherControl.recyleBitmap(tempBitmap);
			updateViews.setViewVisibility(R.id.v5_widget_temp_degree, View.GONE);
		}
		if (isLocate) {
			updateViews.setViewVisibility(R.id.v5_widget_location_icon,
					View.VISIBLE);
		} else {
			updateViews.setViewVisibility(R.id.v5_widget_location_icon,
					View.GONE);
		}
		return TextUtils.isEmpty(cityCode);
	}

	private static Bitmap AQIBitmap;
	private static Bitmap tempBitmap;
	private static Bitmap cityBitmap;
	private static Bitmap lastAQIBitmap;
	private static Bitmap lastTempBitmap;
	private static boolean isLastPmNull = false;
	private static boolean isLastTempNull=false;
}
