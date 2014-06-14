package com.lewa.weather.provider;

import java.util.ArrayList;
import java.util.List;

import com.lewa.weather.entity.City;
import com.lewa.weather.entity.WeatherControl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LewaDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="com.lewa.weather";
    public static final String ALL_CITIES_DB="all_cities";
    public static final String HOT_CITIES_DB="hot_cities";
    private Context mContext;
    private static SQLiteDatabase db;
    public LewaDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }
    
    public void createDataBase(Context context){
        Boolean isExists=WeatherControl.checkDataBase();
        boolean isSuccess=false;
        if(!isExists){
            this.getReadableDatabase();
            isSuccess=WeatherControl.copyDataBase(context);
            if(!isSuccess){
                isSuccess=WeatherControl.copyDataBase(context);//if copy fail,try again
            }
        }
    }
    
    public synchronized List<City> getHotCities(){
        SQLiteDatabase sqLiteDatabase=getSqLiteDatabase();
        checkDBIsLocked(sqLiteDatabase);
        List<City> cities=new ArrayList<City>();
        Cursor cursor=null;
        try {
            cursor = sqLiteDatabase.query(HOT_CITIES_DB, null, null, null, null, null, null);
            cursor.moveToFirst();
            cursor.getString(2);
        } catch (Exception e) {
        	db.close();
            return cities;
        }
        if(cursor!=null&&sqLiteDatabase.isOpen()&&!cursor.isClosed()&&cursor.moveToFirst()){
           while (!cursor.isAfterLast()) {
               City city=new City();
               city.setCity_id(cursor.getString(0));
               city.setName(cursor.getString(1));
               try {
				city.setName_en(cursor.getString(2));
			   }  catch (Exception e) {
			   }
               cities.add(city);
               cursor.moveToNext();
           } 
        }
        if(cursor!=null)
            cursor.close();
        return cities;
    }
    
    
    
    public synchronized List<City> searchCityByName(String column,String filter){
        List<City> cities=new ArrayList<City>();
        if(filter==null)
        	 return cities;
    	if(filter.contains("'"))
    		filter=filter.replace("'", "\''");
        SQLiteDatabase sqLiteDatabase=getSqLiteDatabase();
        checkDBIsLocked(sqLiteDatabase);
        StringBuilder where=new StringBuilder();
        where.append(column+" like '"+filter+"%'");
        if(filter.equalsIgnoreCase("xian")){
        	where.append(" or name_en='Xi\''an'");
        }else if(filter.equalsIgnoreCase("jinan")){
        	where.append(" or name_en='Ji\''nan'");
        }
        Cursor cursor=null;
        try {
            cursor = sqLiteDatabase.query(ALL_CITIES_DB, null, where.toString(), null, null, null, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if(mContext!=null)
                WeatherControl.copyDataBase(mContext);
        }
        if(cursor!=null&&sqLiteDatabase.isOpen()&&!cursor.isClosed()&&cursor.moveToFirst()){
           while (!cursor.isAfterLast()) {
               City city=new City();
               city.setCity_id(cursor.getString(0));
               city.setName(cursor.getString(1));
               city.setName_en(cursor.getString(2));
               cities.add(city);
               cursor.moveToNext();
           } 
        }
        if(cursor!=null)
            cursor.close();
        return cities;
    }
    
    public synchronized String getCityInfo(String table,String getColumn,String filteColumn,String filter){
        if(mContext!=null)
            createDataBase(mContext);
        SQLiteDatabase sqLiteDatabase=getSqLiteDatabase();
        checkDBIsLocked(sqLiteDatabase);
        String getString="";
        Cursor cursor=null;
        try {
            cursor = sqLiteDatabase.query(table, new String[]{getColumn}, filteColumn+"=?", new String[]{filter}, null, null, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if(mContext!=null)
                WeatherControl.copyDataBase(mContext);
        }
        if(cursor!=null&&sqLiteDatabase.isOpen()&&!cursor.isClosed()&&cursor.moveToFirst()){
            getString=cursor.getString(0);
        }
        if(cursor!=null)
            cursor.close();
        return getString;
    }
    
    public synchronized void clearData(String table){
        SQLiteDatabase sqLiteDatabase=getSqLiteDatabase();
        checkDBIsLocked(sqLiteDatabase);
        sqLiteDatabase.delete(table, null, null);
    }
    
    public synchronized SQLiteDatabase getSqLiteDatabase(){
        if(db==null||!db.isOpen()){
            db=this.getWritableDatabase();
        }
        return db;
    }
    
    public void closeDb(){
        if(db!=null)
            db.close();
    }
    public void creatCityDB(SQLiteDatabase db,String table){
    	db.execSQL("create table if not exists "+table+"(city_id ,name ,name_en varchar)");
    }
    
    public void checkDBIsLocked(SQLiteDatabase db){
 	   if(db!=null){
 		   if(db.isDbLockedByOtherThreads()||db.isDbLockedByCurrentThread()){
 			   	try {
 					Thread.sleep(2000);
 				} catch (InterruptedException e) {
 					// TODO Auto-generated catch block
 					e.printStackTrace();
 				}
 		   }
 	   }
    }

}
