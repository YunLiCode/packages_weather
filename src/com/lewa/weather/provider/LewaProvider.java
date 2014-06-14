package com.lewa.weather.provider;

import com.lewa.weather.entity.WeatherControl;
import com.lewa.weather.entity.WeatherCurrentCondition;
import com.lewa.weather.entity.WeatherSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class LewaProvider extends ContentProvider {
    private static final String DB_NAME = "defaultweather";  
    private static final String DB_TABLE = "defaultcityweather"; 
    private static final int DB_VERSION = 1;
    private ContentResolver resolver;
    private MyDbHelper dbHelper;
    private Context context; 
    @Override
    public boolean onCreate() {
        dbHelper = new MyDbHelper(getContext(),DB_NAME); 
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sqLiteDatabase=dbHelper.getReadableDatabase();
        return  sqLiteDatabase.query(DB_TABLE, null, null, null, null, null, null);
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        WeatherSet weatherSet=WeatherControl.getDefaultWeatherData(getContext());
        WeatherCurrentCondition condition=weatherSet.getWeatherCurrentCondition();
        if(weatherSet!=null&&condition!=null){
            ContentValues values1=new ContentValues();
            values.put("city",weatherSet.getCity());
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
            sqLiteDatabase.insert(DB_TABLE, null, values1);
        }
        sqLiteDatabase.close();
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        sqLiteDatabase.delete(DB_TABLE, null, null);
        sqLiteDatabase.close();
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
