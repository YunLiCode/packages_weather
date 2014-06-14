package com.lewa.weather.provider;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lewa.weather.entity.OrderUtil;
import com.lewa.weather.entity.WeatherControl;
import com.lewa.weather.entity.WeatherCurrentCondition;
import com.lewa.weather.entity.WeatherSet;
import com.lewa.weather.R;
import com.when.android.calendar365.tools.util.AbsIconToolAdapter;
import com.when.android.calendar365.tools.util.AbsInstanceAdapter;
import com.when.android.calendar365.tools.util.WhenProvider;

public class CalendarProvider extends WhenProvider {
 
    @Override
    public AbsIconToolAdapter getIconToolAdapter() {
        // TODO Auto-generated method stub
        return new IconAdapter(getContext());
    }

    @Override
    public AbsInstanceAdapter getInstanceAdapter() {
        // TODO Auto-generated method stub
        return new InstanceAdapter(getContext());
    }
    
    class IconAdapter extends AbsIconToolAdapter {
        Calendar calendar = Calendar.getInstance();

        public IconAdapter(Context context) {
            super(context);
        }

        @Override
        public String getClassName(int position) {
            return "com.lewa.weather.LewaWeather";
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 1;
        }

        @Override
        public String getData(int position) {
           
            return null;
        }

        @Override
        public String getImgUrl(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getName(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getRsid(int arg0) {
            // TODO Auto-generated method stub
            return -1;
        }

        @Override
        public boolean loadLastSegment(String segment) {
            // TODO Auto-generated method stub
            return false;
        }
    }
    
    class InstanceAdapter extends AbsInstanceAdapter {
        Calendar calendar = Calendar.getInstance();

        public InstanceAdapter(Context context) {
            super(context);
        }

        @Override
        public String getContent(int position) {
            WeatherSet weatherSet=WeatherControl.getDefaultWeatherData(getContext());
            if(weatherSet!=null&&weatherSet.getWeatherCurrentCondition()!=null)
            {
                WeatherCurrentCondition currentCondition=weatherSet.getWeatherCurrentCondition();
                String conditon=currentCondition.getCondition();
                String temp=currentCondition.getTemperature();
                String windString=currentCondition.getWindCondition();
                StringBuilder builder=new StringBuilder();
                if(conditon!=null)
                    builder.append(conditon);
                if(temp!=null)
                    builder.append(temp);
                if(windString!=null)
                    builder.append(windString);
                return builder.toString();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public String getIcon(int arg0) {
            return "#000000";
        }

        @Override
        public String getSummary(int arg0) {
            
            return OrderUtil.getDefaultCity(getContext());
        }

        @Override
        public String getTime(int arg0) {
            return "";
        }

        @Override
        public boolean loadLastSegment(String segment) {
            calendar.setTimeInMillis(Long.parseLong(segment));
            return true;
        }
        
    }

}
