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

public class WeatherWidgetV5_time extends AppWidgetProvider{
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
	private static Bitmap temp_line ;
	private static Bitmap srcBitmap ;
	private static Bitmap weather_num_time_bt ;
	private static Bitmap weather_city_bt ;
	
	private static final float ScaleX  = 0.83f ;
    private static String lastCondition;
    private static boolean isChange;
    private static Bitmap hourBitmap;
    private static boolean isNext=false;
    private static Handler handler=new Handler();
    private static Runnable setProviderRunnable;
    private static long toastLastShowTime;
    private static RemoteViews updateViews;
    private static Bitmap hour1;
    private static Bitmap hour2;
    private static Bitmap minute1;
    private static Bitmap minute2;

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for (int i = 0; i < appWidgetIds.length; i++) {
		    RemoteViews updateView = lewaBuildUpdate(context, appWidgetIds[i],false,true,true);
		    appWidgetManager.updateAppWidget(appWidgetIds[i], updateView);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	

	


    @Override
	public void onEnabled(Context context) {
       
		updateTimeAlarm(context);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName(
				"com.lewa.weather",
				"com.lewa.weather.receiver.WidgetReceiver"),
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
		SharedPreferences sp=context.getSharedPreferences("weatherLocation", Context.MODE_PRIVATE);
		boolean firstCreate=sp.getBoolean("firstCreate", true);
		if(firstCreate&&OrderUtil.getAutoCity(context).equals("")){
		    WeatherControl wcc=new WeatherControl(context);
            wcc.getLocationAuto(context);
            Editor editor=sp.edit();
            editor.putBoolean("firstCreate", false).commit();
		}
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName(
				"com.lewa.weather",
				"com.lewa.weather.receiver.WidgetReceiver"),
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}
	@Override
	public void onReceive(final Context context, Intent intent) {
		ComponentName provider = new ComponentName(context,context.getPackageName()+ ".WeatherWidgetV5_time");
		AppWidgetManager gm = AppWidgetManager.getInstance(context);
		int[] appWidgetIds;
		appWidgetIds = gm.getAppWidgetIds(provider);
		final int N = appWidgetIds.length;
		if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
            updateTimeAlarm(context);
        }
		if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
		   WeatherControl.setWeatherUpdateTask(context, 60);
		}
		super.onReceive(context, intent);
	}

	private static synchronized RemoteViews lewaBuildUpdate(Context context, int i,boolean isRefresh,boolean isTimeUpdate,boolean isDateUpdate) {
        // TODO Auto-generated method stub
	   
	  	Date date=new Date();
        boolean bool = DateFormat.is24HourFormat(context);
        if(!isStartWithone(context, date, bool)){
            updateViews=new RemoteViews(context.getPackageName(), R.layout.widget_v5_time);
        }else{
        	updateViews=new RemoteViews(context.getPackageName(), R.layout.widget_v5_time_1);
        }
//       updateWeather(updateViews, context,isWeatherUpdate);
       updateTime(updateViews, context, date, bool,isTimeUpdate);
       updateDate(updateViews, context,isDateUpdate);
       
        Intent  launchIntent = new Intent(context, LewaWeather.class);
        PendingIntent weatherPendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
        launchIntent = new Intent(Intent.ACTION_MAIN, null);
        launchIntent.addCategory(Intent.CATEGORY_DESK_DOCK);
        launchIntent.setClassName("com.android.deskclock", "com.android.deskclock.AlarmClock");
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        weatherPendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
        updateViews.setOnClickPendingIntent(R.id.v5_widget_time_container, weatherPendingIntent);
        launchIntent.setClassName("com.when.android.calendar365", "com.when.android.calendar365.CalendarMain");
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        weatherPendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
        updateViews.setOnClickPendingIntent(R.id.v5_widget_date, weatherPendingIntent);
        updateViews.setOnClickPendingIntent(R.id.v5_widget_week_ll, weatherPendingIntent);
        return updateViews;
    }
	private static void drawHour(Context context,
            RemoteViews remoteViews, Date date, boolean b24Hour,boolean isTimeUpdate) {
        int i = date.getHours();
        int j = 0;
        int k = 0;
        if (b24Hour) {
            j = i / 10;
            k = i % 10;
            remoteViews.setViewVisibility(R.id.lewa_widget_moment, View.GONE);
        } else {
            remoteViews.setViewVisibility(R.id.lewa_widget_moment, View.VISIBLE);
            if(i>=0&&i<12){
                remoteViews.setTextViewText(R.id.lewa_widget_moment,context.getResources().getString(R.string.AM));
            }else if(i>=12){
                remoteViews.setTextViewText(R.id.lewa_widget_moment,context.getResources().getString(R.string.PM));
            }
            if ((i == 0) || (i == 12)) {
                j = 1;
                k = 2;
            }else{
                if ((i > 0) && (i < 12)) {
                    j = i / 10;
                    k = i % 10;
                }else{
                    j = (i -12) / 10;
                    k = (i -12) % 10;   
                }   
            }
        }
        if(hour1==null||isTimeUpdate){
        	WeatherControl.recyleBitmap(hour1);
        	hour1=getHourAndMinuteBitmap(context, String.valueOf(j)) ;
        }
        remoteViews.setImageViewBitmap(R.id.v5_widget_time_hour1,hour1);
        if(hour2==null||isTimeUpdate){
        	WeatherControl.recyleBitmap(hour2);
        	hour2=getHourAndMinuteBitmap(context, String.valueOf(k));
        }
        remoteViews.setImageViewBitmap(R.id.v5_widget_time_hour2, hour2);
    }
	
	private static boolean isStartWithone(Context context, Date date, boolean b24Hour){
		int i = date.getHours();
        int j = 0;
        if (b24Hour) {
            j = i / 10;
        } else {
            if ((i == 0) || (i == 12)) {
                j = 1;
            }else{
                if ((i > 0) && (i < 12)) {
                    j = i / 10;
                }else{
                    j = (i -12) / 10;
                }   
            }
        }
        if(j==1){
        	return true;
        }else{
        	return false;
        }
	}
	
	private static  int getClockNumberResourceId(int resID) {
        return R.drawable.num_0 + resID;
    }

    private static int getDateNumberResourceId(int resID) {
        return R.drawable.num_0 + resID;
    }

    private static void drawMinute(Context context,
            RemoteViews remoteViews, Date date,boolean isTimeUpdate) {
        int i = date.getMinutes();
        int j = i / 10;
        int k = i % 10;
        if(minute1==null||isTimeUpdate){
        	WeatherControl.recyleBitmap(minute1);
        	minute1=getHourAndMinuteBitmap(context, String.valueOf(j));
        }
        remoteViews.setImageViewBitmap(R.id.v5_widget_time_minute1,minute1);
        if(minute2==null||isTimeUpdate){
        	WeatherControl.recyleBitmap(minute2);
        	minute2=getHourAndMinuteBitmap(context, String.valueOf(k));
        }
        remoteViews.setImageViewBitmap(R.id.v5_widget_time_minute2,minute2 );
    }
	
	

	private static Bitmap getConditionFrom(String condition, Context context){
		final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
		Paint paint = new Paint();
		paint.setTextSize(25*scale);
		paint.setColor(Color.parseColor("#3a3a3a"));
		paint.setAntiAlias(true);
		float tempratureRangeWidth = paint.measureText(condition);
		int translateY = (int) (32*scale + 0.5f);
		Bitmap bitmap = Bitmap.createBitmap((int)(tempratureRangeWidth + 0.5f), (int)(40*scale + 0.5f), Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(bitmap);

		canvasTemp.drawText(condition, 0, translateY, paint);
		return bitmap;
	}
	
	public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId,boolean isTimeUpdate,boolean isDateUpdate) {
		
			RemoteViews updateView =lewaBuildUpdate(context, appWidgetId,false,isTimeUpdate,isDateUpdate);
			appWidgetManager.updateAppWidget(appWidgetId, updateView);
	}
	
	
	public static boolean updateWidgets(Context context,boolean isTimeUpdate,boolean isDateUpdate) {
		ComponentName provider = new ComponentName(context, context.getPackageName()+".WeatherWidgetV5_time");
        AppWidgetManager gm = AppWidgetManager.getInstance(context);
        int[] appWidgetIds;
        appWidgetIds = gm.getAppWidgetIds(provider);
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
    		WeatherWidgetV5_time.updateAppWidget(context, gm, appWidgetIds[i],isTimeUpdate,isDateUpdate);
        }	
        return N > 0;
	}

	
	private void updateTimeAlarm(Context context){
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    	Intent intent = new Intent("com.when.action.UPDATE_WIDGET_ALARM");
        PendingIntent sender = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    	am.setRepeating(AlarmManager.RTC, 0, 60*1000, sender);
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
                WeatherControl wc=new WeatherControl(context.get());
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
                boolean isLocation=false;
                if(cityCode_default.contains("true"))
                    isLocation=true;
                cityCode_default=WeatherControl.buildCityCode(cityCode_default);
                if (!wc.updateWeatherData(context.get(), cityCode_default, provinceCn_default,
                        citycn_default, System.currentTimeMillis(),isLocation)) {
                    return false;
                } else {
                    boolean alreadySet = weatherLocation.getBoolean("alreadySet", false);
                    if(!alreadySet)
                        WeatherControl.setWeatherUpdateTask(context.get(),60);
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
                ComponentName provider = new ComponentName(context.get(),context.get().getPackageName()+ ".WeatherWidgetV5_time");
                AppWidgetManager gm = AppWidgetManager.getInstance(context.get());
                int[] appWidgetIds;
                appWidgetIds = gm.getAppWidgetIds(provider);
                for(int i=0;i<appWidgetIds.length;i++){
                    RemoteViews views=lewaBuildUpdate(context.get(), appWidgetIds[i], false,true,true);
                    gm.updateAppWidget(appWidgetIds[i], views);
                }
                
            }else{
                Intent intent=new Intent("com.lewa.weather.refresh");
                context.get().sendBroadcast(intent);
            } 
            super.onPostExecute(result);
        }

    }
	
	private static Bitmap getHourAndMinuteBitmap(Context context, String  text) {
		int shadow_radius=context.getResources().getInteger(R.integer.v5_widget_time_shadow_radius);
		int shadow_dx=context.getResources().getInteger(R.integer.v5_widget_time_shadow_dx);
		int shadow_dy=context.getResources().getInteger(R.integer.v5_widget_time_shadow_dy);
		final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
		Paint paint = new Paint();
		paint.setTextSize(context.getResources().getInteger(R.integer.v5_widget_hour_text_size));
		paint.setColor(Color.WHITE);
		Typeface face = Typeface.createFromAsset (context.getAssets() , "NeoSans-Light.otf");
		paint.setTypeface(face);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(false);
		paint.setShadowLayer(shadow_radius,shadow_dx, shadow_dy, Color.DKGRAY);
		paint.setShadowLayer(shadow_radius,shadow_dx, shadow_dy, Color.DKGRAY);
		paint.setFilterBitmap(true);
		int translateY = (int) (63*scale + 0.5f);
		float tempratureRangeWidth = paint.measureText(text);
		Bitmap bitmap = Bitmap.createBitmap((int)(tempratureRangeWidth + 0.5f), (int)(67*scale + 0.5f), Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(bitmap);
		canvasTemp.drawText(text, 0, translateY, paint);
		return bitmap;
	}
	
	private static Bitmap createTextBitmap(Context context, String text,int size,int transy,boolean isSetShadow) {
		final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
		Paint paint = new Paint();
		Rect rect = new Rect();              
		paint.getTextBounds(text, 0, text.length(), rect);
		paint.setTextSize(size);
		paint.setColor(Color.WHITE);
		Typeface face = Typeface.createFromAsset (context.getAssets() , "NeoSans-Light.otf");
		paint.setTypeface(face);
		paint.setAntiAlias(true);
		
		if(isSetShadow)
			paint.setShadowLayer(5, 1, 2, Color.DKGRAY);
		int translateY = (int) (transy*scale + 0.5f);
		
		float tempratureRangeWidth =paint.measureText(text);
		Bitmap bitmap = Bitmap.createBitmap((int)(tempratureRangeWidth + 0.5f), (int)(20*scale + 0.5f), Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(bitmap);
		canvasTemp.drawText(text, 0, translateY, paint);
		return bitmap;
	}
	
	public static  int getFontHeight(float fontSize)  
	{  
	    Paint paint = new Paint();  
	    paint.setTextSize(fontSize);  
	    FontMetrics fm = paint.getFontMetrics();  
	    return (int) Math.ceil(fm.descent - fm.top)+2;  
	}
	
	public static void updateTime(RemoteViews updateViews,Context context,Date date,boolean bool,boolean isTimeUpdate){
	        drawHour(context, updateViews, date, bool,isTimeUpdate);
	        drawMinute(context, updateViews, date,isTimeUpdate);
	}
	
	public static void updateDate(RemoteViews updateViews,Context context,boolean isDateUpdate){
		    Calendar calendar=Calendar.getInstance();
		    int mounth=calendar.get(Calendar.MONTH)+1; 
	        int day=calendar.get(Calendar.DAY_OF_MONTH);
	        int dayofweek=calendar.get(Calendar.DAY_OF_WEEK);
	        int v5_widget_solar_text_size=context.getResources().getInteger(R.integer.v5_widget_solar_text_size);
	        int v5_widget_lunar_text_size=context.getResources().getInteger(R.integer.v5_widget_lunar_text_size);
	        int transy=context.getResources().getInteger(R.integer.v5_widget_transy);
	        int solar_transy=context.getResources().getInteger(R.integer.v5_widget_solar_transy);
	        String weekdayString=DataFormatControl.dayOfWeekV5(context, dayofweek);
	        LunarItem item=new LunarItem(calendar);
	        String lunarString=item.getChinaCurrentMonthAndDayString();
	        StringBuilder builder=new StringBuilder();
	        if(String.valueOf(mounth).length()<=1)
	        	builder.append(0);
	        String dayString=String.valueOf(day);
	        if(dayString.length()<=1)
	        	dayString="0"+day;
	        builder.append(mounth+"."+dayString);
	        if(solarBitmap==null||isDateUpdate){
	        	WeatherControl.recyleBitmap(solarBitmap);
	        	solarBitmap=WeatherControl.createTextBitmap(context, builder.toString(), v5_widget_solar_text_size,solar_transy,true);
	        }
	        updateViews.setImageViewBitmap(R.id.v5_widget_date_solar, solarBitmap);
	        if(WeatherControl.isLanguageZhCn()||WeatherControl.isLanguageZhTw()){
		        if(lunarBitmap==null||isDateUpdate){
		        	WeatherControl.recyleBitmap(lunarBitmap);
		        	lunarBitmap=WeatherControl.createTextBitmap(context, context.getString(R.string.lunar)+lunarString, v5_widget_lunar_text_size,transy,true);
		        }
		        updateViews.setImageViewBitmap(R.id.v5_widget_date_lunar, lunarBitmap);
	        }
	        if(weekBitmap==null||isDateUpdate){
	        	WeatherControl.recyleBitmap(weekBitmap);
	        	weekBitmap=WeatherControl.createTextBitmap(context, weekdayString, v5_widget_lunar_text_size,transy,true);
	        }
	        updateViews.setImageViewBitmap(R.id.v5_widget_week,weekBitmap );
	}
	
	private static Bitmap solarBitmap;
	private static Bitmap lunarBitmap;
	private static Bitmap weekBitmap;
    private static Bitmap AQIBitmap;
    private static Bitmap tempBitmap;
    private static Bitmap cityBitmap;
}
