package com.lewa.weather.provider;

import com.lewa.weather.entity.WeatherControl;
import com.lewa.weather.entity.WeatherCurrentCondition;
import com.lewa.weather.entity.WeatherSet;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDbHelper extends SQLiteOpenHelper {
    private static final String DB_TABLE = "DefaultCityWeather";
    private static final String DB_CREATE = "create table " + DB_TABLE
            +" (_id" + " integer primary key autoincrement , " + 
            "city" + " varchar, "+ 
            "citycode" + " varchar, " + 
            "temperature" + " varchar, " + 
            "range" + " varchar, "+ 
            "sort" + " integer, "+ 
            "humidity" + " varchar, "+
            "pmcondition" + " varchar, "+
            "windCondition" + " varchar, " + "condition" + " varchar);";
    private static final int DB_VERSION = 3;
    private Context context;
    private SQLiteDatabase database;
    private WeatherCurrentCondition condition;

    public MyDbHelper(Context context, String name) {
        super(context, name, null, DB_VERSION);
        this.context=context;
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(DB_CREATE);
        WeatherSet weatherSet = WeatherControl.getDefaultWeatherData(context);
        if (weatherSet != null)
            condition = weatherSet.getWeatherCurrentCondition();
        if (weatherSet != null && condition != null) {
            Log.i("wangliqiang", "insert");
            ContentValues values = new ContentValues();
            values.put("city", weatherSet.getCity());
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
            values.put("range", weatherSet.getWeatherForecastConditions()
                    .get(0).getTemperature());
            db.insert(DB_TABLE, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }

}
