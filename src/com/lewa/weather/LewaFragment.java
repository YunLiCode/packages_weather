package com.lewa.weather;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

import com.lewa.weather.entity.WeatherControl;
import com.lewa.weather.entity.WeatherCurrentCondition;
import com.lewa.weather.entity.WeatherForecastCondition;
import com.lewa.weather.entity.WeatherSet;
import com.lewa.weather.R;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LewaFragment extends Fragment {
//    private TextView lewa_weather_city;
//    private TextView lewa_weather_air;
    private ImageView lewa_weather_img;
    private TextView lewa_weather_temp;
    private MarqueeTextView lewa_weather_weather;
    private TextView lewa_weather_other_temp;
    private MarqueeTextView lewa_weather_other_wind;
    private WeatherSet weatherSet;
    private WeatherCurrentCondition wcc;
    private int positon;
    private RelativeLayout lewa_weather_current;
    private ImageView lewa_weather_icon;
    private TextView lewa_weather_pubDate;
//    private ForeCastAdapter foreCastAdapter;
    private Context context;
    private WeatherControl wc;
    private String cityCode;
    private static final int INCREASE_ALPHA=0;
    private static final int DECREASE_ALPHA=1;
    private float alpha=0.3f;
    private String city;
    private String pm;
    private String pmCondition;
    private Animation fade_out_anim;
    private Animation fade_in_anim;
    public String getPmCondition() {
        return pmCondition;
    }

    public String getCity() {
        if(weatherSet!=null){
          return weatherSet.getCityCn();
        }
        return null;
    }
    
    public String getProvience(){
        return weatherSet.getProvinceCn();
    }
    
    public String getCityCode() {
        return weatherSet.getCityCode();
    }

    public String getPm() {
        return pm;
    }

    private Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case INCREASE_ALPHA:
                alpha+=0.05f;
                lewa_weather_current.setAlpha(alpha);
                if(alpha<1.0f){
                    sendEmptyMessage(INCREASE_ALPHA);
                }   
                break;
            case DECREASE_ALPHA:
                alpha-=0.01f;
                lewa_weather_current.setAlpha(alpha);
                if(alpha>0.5f){
                   sendEmptyMessage(DECREASE_ALPHA);
                }
                break;
            default:
                break;
            }
        };
    };
    private ImageView lewa_weather_refresh;
    
    public LewaFragment (WeatherSet weatherSet,int position,Context context){
        this.weatherSet=weatherSet;
        this.positon=position;
        this.context=context;
        wc = new WeatherControl(context);
        cityCode = weatherSet.getCityCode();
    }
    
    

    public LewaFragment() {
        super();
    }

    public void setWeatherSet(WeatherSet weatherSet) {
        this.weatherSet = weatherSet;
    }
    
    
    public WeatherSet getWeatherSet() {
        return weatherSet;
    }

    public int getPositon() {
        return positon;
    }


    public void setPositon(int positon) {
        this.positon = positon;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view=inflater.inflate(R.layout.lewa_fragment, null);
//        GridView lewa_weather_forecast=(GridView) view.findViewById(R.id.lewa_weather_forecast);
        lewa_weather_current = (RelativeLayout) view.findViewById(R.id.lewa_weather_current);
        lewa_weather_icon = (ImageView) view.findViewById(R.id.lewa_weather_second);
//        lewa_weather_city = (TextView) view.findViewById(R.id.lewa_weather_city);
//        lewa_weather_air = (TextView) view.findViewById(R.id.lewa_weather_air);
        lewa_weather_img = (ImageView) view.findViewById(R.id.lewa_weather_second);
        lewa_weather_temp = (TextView) view.findViewById(R.id.lewa_weather_temp);
        lewa_weather_weather = (MarqueeTextView) view.findViewById(R.id.lewa_weather_weather);
        lewa_weather_other_temp = (TextView) view.findViewById(R.id.lewa_weather_other_temp);
        lewa_weather_other_wind = (MarqueeTextView) view.findViewById(R.id.lewa_weather_other_wind);
        lewa_weather_pubDate = (TextView) view.findViewById(R.id.lewa_weather_pubDate);
        fade_out_anim=AnimationUtils.loadAnimation(getActivity(),R.anim.fade_out);
        fade_in_anim=AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        fade_in_anim.setDuration(3000);
        fade_out_anim.setAnimationListener(new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                mHandler.postDelayed(new Runnable() {
                    
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setData();
                        lewa_weather_current.startAnimation(fade_in_anim);
                    }
                }, 70);
            }
        });
        setData();
//        ImageView lewa_weather_setting=(ImageView) view.findViewById(R.id.lewa_weather_setting);
//        lewa_weather_refresh = (ImageView) view.findViewById(R.id.lewa_weather_refresh);
//        lewa_weather_setting.setOnClickListener(new OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), LewaWeatherSetting.class);
//                startActivity(intent);
//            }
//        });
//        lewa_weather_refresh.setOnClickListener(new OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                new WeatherTask(context).execute();
//            }
//        });
        return view;
    }


    public void setData() {
        if(getActivity()==null)
            return;
        SharedPreferences weatherLocation = getActivity().getSharedPreferences(
                WeatherControl.WEATHER_LOCATION_SETTING,
                Context.MODE_WORLD_READABLE);
        String citycn_default = weatherLocation.getString(
                WeatherControl.WEATHER_CITY_DEFAULT, "");
         city = null == weatherSet ? citycn_default : weatherSet
                .getCityCn();
//        lewa_weather_city.setText(citycn);
        if (weatherSet != null && null != weatherSet.getWeatherCurrentCondition()){
            wcc = weatherSet.getWeatherCurrentCondition();
            String temperature = wcc.getTemperature();
            String condition = (wcc.getCondition().equals("") ? getResources().getString(R.string.weather_unknown) : wcc
                    .getCondition());
            if (temperature.contains(getResources().getString(R.string.temporary))) {
                lewa_weather_temp.setText(getActivity().getString(R.string.weather_unknown));
            } else {
                lewa_weather_temp.setText(temperature.substring(0, temperature.length() - 1) + "℃");
            }
            lewa_weather_weather.setText(condition);
            pm=wcc.getPm25();
            pmCondition=wcc.getPmcondition();
//            if(PM25!=null){
//                lewa_weather_air.setText("PM2.5: "+PM25+"("+PM25condition+")");
//            }
            String pubTime=wcc.getPubTime();
            if(pubTime!=null){
                int dif=WeatherControl.getTimeDif(context,pubTime);
                String time;
                if(!pubTime.endsWith(":")){
                    time=pubTime.substring(pubTime.indexOf(" "));
                }else{
                    time=pubTime.substring(pubTime.indexOf(" "), pubTime.lastIndexOf(":"));
                }
                if(dif==0){
                    lewa_weather_pubDate.setText(getResources().getString(R.string.publish_time)+" : "+context.getString(R.string.jintian)+time);
                }else if(dif==1){
                    lewa_weather_pubDate.setText(getResources().getString(R.string.publish_time)+" : "+context.getString(R.string.zuotian)+time);
                }else{
                    lewa_weather_pubDate.setText(getResources().getString(R.string.publish_time)+":"+pubTime);
                }
            }else{
                lewa_weather_pubDate.setText(getResources().getString(R.string.publish_time)+" : "+context.getString(R.string.unknown));
            }
            String conditionCN=wcc.getConditionCN();
            if(TextUtils.isEmpty(conditionCN))
            	conditionCN=wcc.getCondition();
            String imageString=WeatherControl.getImageString(conditionCN);
            int imageId=getResources().getIdentifier("lewa_icon_app_"+imageString, "drawable", "com.lewa.weather");
            lewa_weather_icon.setImageResource(imageId);
            String range = weatherSet.getWeatherForecastConditions()
                    .get(0).getTemperature();
            StringBuilder builder=new StringBuilder();
            if(range!=null&&range.contains("~")){
                int index = range.indexOf('~');
                range = range.substring(0, range.length() - 1);
                String beforeTemp=range.substring(0, index - 1);
                String afterTemp=range.substring(index + 1);
                if(!TextUtils.isEmpty(beforeTemp)&&!TextUtils.isEmpty(afterTemp)){
                    int v1 = Integer.parseInt(beforeTemp);
                    int v2 = Integer.parseInt(afterTemp);
                    if (v1 < v2) {
                        int tp = v1;
                        v1 = v2;
                        v2 = tp;
                    }
//                    builder.append(v2+"/"+v1+"℃");
                    lewa_weather_other_temp.setText(v2+"/"+v1+"℃ ");
                    if (temperature.contains(getResources().getString(R.string.temporary))) {
                        lewa_weather_temp.setText((v2+v1)/2+"℃ ");
                    } 
                }
            }
            if(wcc.getWindCondition()!=null&&!wcc.getWindCondition().contains(getResources().getString(R.string.temporary)))
                builder.append("  "+wcc.getWindCondition());
            if(wcc.getShidu()!=null&&!wcc.getShidu().contains(getResources().getString(R.string.temporary)))
               builder.append("  "+getResources().getString(R.string.shidu)+":"+wcc.getShidu());
            lewa_weather_other_wind.setText(builder.toString());
        }else{
            lewa_weather_other_temp.setText(getResources().getString(R.string.nodata));
        }
    }
    
    
    
  
    
    public void setAlpha(){
//        Log.i("wangliqiang", "alpha:"+alpha);
//        if(alpha>=1.0f){
//            mHandler.sendEmptyMessage(DECREASE_ALPHA);
//            Log.i("wangliqiang", "DECREASE_ALPHA");
//        }else{
            alpha=0.3f;
            mHandler.sendEmptyMessage(INCREASE_ALPHA);
//            Log.i("wangliqiang", "INCREASE_ALPHA");
//        }
    }
    
    public void setAlphaUnRegular(){
       alpha=0.3f;
       lewa_weather_current.setAlpha(alpha);
    }
    
    public void setAlphaRegular(){
        alpha=1.0f;
        lewa_weather_current.setAlpha(alpha);
     }
    public void startRereshAnim(){
        lewa_weather_current.startAnimation(fade_out_anim);
    }
    
//    public void setGridAdapter(GridView view){
//        foreCastAdapter = new ForeCastAdapter();
//        view.setAdapter(foreCastAdapter);
//    }
    
    
}
