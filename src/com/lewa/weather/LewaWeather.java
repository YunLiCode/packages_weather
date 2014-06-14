package com.lewa.weather;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//import com.lewa.weather.LewaFragment.ViewHolder;
import com.lewa.weather.adapters.Rotate3dAnimation;
import com.lewa.weather.entity.DataFormatControl;
import com.lewa.weather.entity.OrderUtil;
import com.lewa.weather.entity.WeatherControl;
import com.lewa.weather.entity.WeatherCurrentCondition;
import com.lewa.weather.entity.WeatherForecastCondition;
import com.lewa.weather.entity.WeatherService;
import com.lewa.weather.entity.WeatherSet;
import com.lewa.weather.view.MyGridView;
import com.lewa.weather.R;
import com.lewa.weather.R.anim;
import com.lewa.weather.R.dimen;
import com.lewa.weather.R.drawable;
import com.lewa.weather.R.id;
import com.lewa.weather.R.layout;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore.Video;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.AndroidCharacter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;


public class LewaWeather extends Activity {
    public static final int REQUEST_NEW_CITY=0;
    public static final int RESULT_NEW_CITY=1;
    private ViewPager viewPager;
    private  static LewaFragment[] fragments;
    private TabPageAdapter tabPageAdapter;
    private ImageView[] imageViews;
    private int currtItem;
    private Map<String, WeatherSet> weathers;
    private static int size;
    public static boolean isSizeChanged;
    private int scrollX=-1;
    public static boolean isSetAlpha;
    public static final int UPDATE_BG=0;
    public static final int UPDATE_CITY=1;
    float alpha=0.5f;
    private float cityAlpha;
    private boolean isScrolling;
    private static boolean isSetbg;
    private boolean isFirst=true;
    private String lastCondition;
    private String city;
    private String pm;
    private String pmCondition;
    private LewaFragment curFragment;
    private int lastScrollPix=-1;
    private int tranlateX;
    private int firstPosition=-1;
    private int curPosition;
    private boolean isAfterScroll;
    private int lastX=0;
    private long startTime=-1;
    private long lastShowTime;
    private Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case UPDATE_BG:
            alpha+=0.02f;
            lewa_weather_imgbg_first.setAlpha(alpha);
            if(alpha<1.0f){
                mHandler.sendEmptyMessage(UPDATE_BG);
            }
            break;
            case UPDATE_CITY:
            cityAlpha+=0.02f;
            lewa_weather_air.setAlpha(cityAlpha);
            lewa_weather_city.setAlpha(cityAlpha);
            if(cityAlpha<1.0f){
                mHandler.sendEmptyMessage(UPDATE_CITY);
            }
            break;
            default:
                break;
            }
        };
    };
    private BroadcastReceiver receiver=new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, final Intent intent) {
            // TODO Auto-generated method stub
          String action=intent.getAction();
          if(action.equals("com.lewa.weather.locate")){
              if(fragmentList!=null&&fragmentList.size()>0){
                  fragmentTransaction = fragmentManager
                          .beginTransaction();
                  for(int i=0;i<fragmentList.size();i++){
                      fragmentTransaction.remove(fragmentList.get(i));
                  }
                  fragmentTransaction.commitAllowingStateLoss();
                  fragmentManager.executePendingTransactions();
                  initView();
            }
          }else if(action.equals("com.lewa.weather.refresh")){
              refresh(context);
          }else if(action.equals(Intent.ACTION_LOCALE_CHANGED)){
             
              finish();
          }
        }
    };
    private FragmentTransaction fragmentTransaction;
    public static List<LewaFragment> fragmentList;
    private FragmentManager fragmentManager;
    private LinearLayout guidemark;
    private int windowWidth;
    private ImageView lewa_weather_imgbg_first;
    private ImageView lewa_weather_imgbg_second;
    private MarqueeTextView lewa_weather_city;
    private TextView lewa_weather_air;
    private MyGridView lewa_weather_forecast;
    private ForeCastAdapter adapter;
    private WeatherControl wc;
    private ImageView lewa_weather_refresh;
    private SharedPreferences sp;
    private boolean isFirstOpen;
    private ImageView lewa_grid_line;
    private ImageView lewa_weather_pm_icon;
    private ImageView locate_icon;
    private TextView lewa_weather_AQ;
 
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lewa_weather_main);
        getActionBar().hide();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.lewa.weather.locate");
        filter.addAction("com.lewa.weather.refresh");
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        registerReceiver(receiver, filter);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        windowWidth = metric.widthPixels;
        sp = getSharedPreferences("weatherLocation", Context.MODE_PRIVATE);
        Boolean alreadySet= sp.getBoolean("alreadySet", false);
        if(!alreadySet)
            WeatherControl.setWeatherUpdateTask(getApplicationContext(),60);
        isFirstOpen = sp.getBoolean("isFirstOpen", true);
        if(isFirstOpen&&OrderUtil.getAutoCity(getApplicationContext()).equals("")){
            wc = new WeatherControl(getApplicationContext());
            wc.getLocationAuto(getApplicationContext());
            Editor editor=sp.edit();
            editor.putBoolean("isFirstOpen", false);
            editor.commit();
        }
        initView();
    }


    
     
    
    public void initView(){
        viewPager = (ViewPager) findViewById(R.id.mviewpager);
        guidemark = (LinearLayout) findViewById(R.id.guidemark);
        lewa_weather_imgbg_first = (ImageView) findViewById(R.id.lewa_weather_imgbg_first);
        lewa_weather_imgbg_second = (ImageView) findViewById(R.id.lewa_weather_imgbg_second);
        lewa_weather_city = (MarqueeTextView) findViewById(R.id.lewa_weather_city);
        lewa_weather_air = (TextView) findViewById(R.id.lewa_weather_air);
        lewa_weather_pm_icon = (ImageView) findViewById(R.id.lewa_weather_pm_icon);
        lewa_weather_forecast = (MyGridView) findViewById(R.id.lewa_weather_forecast);
        lewa_weather_refresh = (ImageView) findViewById(R.id.lewa_weather_refresh);
        lewa_grid_line = (ImageView) findViewById(R.id.lewa_grid_line);
        locate_icon = (ImageView)findViewById(R.id.locate_icon);
        lewa_weather_AQ=(TextView)findViewById(R.id.lewa_weather_AQ);
        if(wc==null)
            wc= new WeatherControl(LewaWeather.this);
        lewa_weather_refresh.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
              // TODO Auto-generated method stub
              if(WeatherControl.isWiFiActive(getApplicationContext())||WeatherControl.IsConnection(getApplicationContext())){
                  new WeatherTask(LewaWeather.this,curFragment).execute();
                  lewa_weather_refresh.setClickable(false);
              }else{
                  if(System.currentTimeMillis()-lastShowTime>2000){
                      Toast.makeText(getApplicationContext(),getResources().getString(R.string.pls_check_network_status_string), 0).show();
                      lastShowTime=System.currentTimeMillis();
                  }
              }
          }
      });
        
        ImageView lewa_weather_setting=(ImageView)findViewById(R.id.lewa_weather_setting);
        lewa_weather_setting.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
              // TODO Auto-generated method stub
              Intent intent = new Intent();
              intent.setClass(LewaWeather.this, LewaWeatherSetting.class);
              startActivity(intent);
              finish();
          }
      });
        
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager
                .beginTransaction();
        weathers = WeatherControl.loadWeatherData(this);
        if (null == weathers || weathers.size() == 0) {
            Intent intent = new Intent();
            intent.setClass(this, LewaWeatherSetting.class);
            startActivity(intent);
            finish();
            return;
        }
        fragments=new LewaFragment[weathers.size()];
        fragmentList = new ArrayList<LewaFragment>();
        size=weathers.size();
        List<WeatherSet> temp=new ArrayList<WeatherSet>(weathers.values());
        final Map<String, ?> order = OrderUtil.getAll(this);
        
        Collections.sort(temp, new Comparator<WeatherSet>() {

            @Override
            public int compare(WeatherSet o1, WeatherSet o2) {
                String cityCodeO1=o1.isLocate()?o1.getCityCode()+"|true":o1.getCityCode();
                String cityCodeO2=o2.isLocate()?o2.getCityCode()+"|true":o2.getCityCode();
                if(order.get(cityCodeO1)==null||order.get(cityCodeO2)==null){
                    return -1;
                }else{
                    long d = (Long)order.get(cityCodeO1) -  (Long)order.get(cityCodeO2) ;
                    return d < 0 ? -1 : 1;
                }
            }
        });
        for(int i=0;i<size;i++){
            fragments[i] = (LewaFragment)fragmentManager
                    .findFragmentByTag("index"+ i);
            if(fragments[i]==null){
                fragments[i]=new LewaFragment(temp.get(i),i,LewaWeather.this);
            }
//            fragments[i].setGridView(lewa_weather_forecast);
            fragmentList.add(fragments[i]);
            if(!fragments[i].isAdded())
              fragmentTransaction.add(R.id.mviewpager, fragments[i], "index"+i);
            if(i!=0)
                fragmentTransaction.hide(fragments[i]);
        }
        fragmentTransaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
        if(tabPageAdapter==null)
            tabPageAdapter=new TabPageAdapter(this);
        viewPager.setAdapter(tabPageAdapter);
        viewPager.setOnPageChangeListener(tabPageAdapter);
//        try {
//            Field mField = ViewPager.class.getDeclaredField("mScroller");               
//            mField.setAccessible(true);     
//            mScroller = new FixedSpeedScroller(viewPager.getContext(), new AccelerateInterpolator());          
//            mField.set(viewPager, mScroller);
//            
//        } catch (IllegalArgumentException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }  
//        Log.i("wangliqiang", "onCreate");
        lastCondition=null;
        setBackGround(fragmentList.get(0).getWeatherSet());
        adapter = new ForeCastAdapter();
        adapter.setWeatherSet(temp);
//        DisplayMetrics metric = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metric);
//        int width = metric.widthPixels;
        LayoutParams params=new LayoutParams(windowWidth*fragmentList.size(),LayoutParams.MATCH_PARENT);
        params.gravity=Gravity.BOTTOM|Gravity.CENTER;
        lewa_weather_forecast.setLayoutParams(params);
        lewa_weather_forecast.setColumnWidth(windowWidth/5);
        lewa_weather_forecast.setAdapter(adapter);
        lewa_weather_forecast.setNumColumns(adapter.getCount());
        curFragment=fragmentList.get(0);
        imageViews=new ImageView[size];
        guidemark.removeAllViews();
        for(int i=0;i<size;i++){
            imageViews[i]=new ImageView(this);
            if(i==currtItem){
                imageViews[i].setImageResource(R.drawable.point_select);
            }else{
                imageViews[i].setImageResource(R.drawable.point_unselect);
            }
            imageViews[i].setPadding(1, 10, 1,10);
            guidemark.addView(imageViews[i]);
        }
        
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        viewPager.setCurrentItem(0);
        lewa_weather_forecast.scrollTo(0, 0);
        setCityAndPM(curFragment);
        tabPageAdapter.notifyDataSetChanged();
//        new Thread(){
//            public void run() {
//                WeatherControl.localeAddress(getApplicationContext());
//            };
//        }.start();
        super.onResume();
    }
    
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        if(wc!=null){
            wc.releaseLocationManager();
        }
        super.onStop();
    }
    
    private class TabPageAdapter extends PagerAdapter implements OnPageChangeListener{
        private FragmentManager fragmentManager;
        private FragmentTransaction mCurTransaction = null;

        public TabPageAdapter(Activity activity){
            fragmentManager=activity.getFragmentManager();
        }
        public Fragment getFragment(int position){
            if(position>=0&&position<fragmentList.size()){
                for(int i=0;i<fragmentList.size();i++)
                {   
                    if(position==fragmentList.get(i).getPositon()){
                        if(position==0)
                            fragmentList.get(i).setAlphaRegular();
                        return fragmentList.get(i);
                   }
                }
            }
            throw new IllegalArgumentException("position: " + position);
        }
        @Override
        public synchronized void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public synchronized void onPageScrolled(int position, float positionOffset, final int positionOffsetPixels) {
            // TODO Auto-generated method stub
//            if(position==0&&!isSetAlpha&&size>1){
//                fragmentList.get(1).setAlphaUnRegular();
//            }
            if(firstPosition==-1)
                firstPosition=position;
            if(scrollX==-1)
                scrollX=positionOffsetPixels;
            if(size>1){
                final int x=viewPager.getScrollX();
                if(lastX!=x&&x>=0){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lewa_weather_forecast.scrollTo(x, 0);
                        }
                    }, 100);
                }
                lastX=x;
              }
        }

        @Override  
        public void onPageSelected(final int arg0) {
//            Log.i("wangliqiang","onPageSelected");
            curPosition=arg0;
//            if(arg0==0){
//                lewa_weather_forecast.scrollTo(windowWidth, 0);
//            }else if(arg0==getCount()-1){
//                lewa_weather_forecast.scrollTo((arg0-1)*windowWidth, 0);
//            }
//            lewa_weather_forecast.scrollTo(arg0*windowWidth, 0);
            // TODO Auto-generated method stub
            fragmentList.get(arg0).setAlphaRegular();
            if(arg0==1)
            {
                isSetAlpha=true;
            }
            if((arg0-1)>=0){
                fragmentList.get(arg0-1).setAlphaUnRegular();
            }
            if((arg0+1)<getCount()){
                fragmentList.get(arg0+1).setAlphaUnRegular();
            }
//            WeatherSet weatherSet=fragmentList.get(arg0).getWeatherSet();
//            setBackGround(weatherSet);
//            if(!isSetbg){
               LewaFragment fragment=fragmentList.get(arg0);
               curFragment=fragment;
               WeatherSet weatherSet=fragment.getWeatherSet();
               setBackGround(weatherSet);
               setCityAndPM(fragment);
//               startAnmation(fragment);
//               fragment.setGridAdapter(lewa_weather_forecast);
               
//            }
//            isSetbg=false;
            scrollX=-1;
            if(size>1){
                ImageView currentview=(ImageView) guidemark.getChildAt(currtItem);
                if(currentview!=null)
                    currentview.setImageResource(R.drawable.point_unselect);
                ImageView view=(ImageView) guidemark.getChildAt(arg0);
                if(view!=null)
                    view.setImageResource(R.drawable.point_select);
                currtItem = arg0;
            }
        }

//        private void startAnmation(final LewaFragment fragment) {
//            // TODO Auto-generated method stub
//             Log.i("wangliqiang", "count:"+lewa_weather_forecast.getCount());
//            
//            for(int i=0;i<lewa_weather_forecast.getCount();i++){
//                ImageView view=(ImageView) lewa_weather_forecast.getChildAt(i).findViewById(R.id.lewa_forcast_icon);
//                final float centerX=view.getWidth()/2;
//                final float centerY=view.getHeight()/2;
//                Rotate3dAnimation animation=getAnimation(centerX, centerY, fragment,0,90);
//                view.startAnimation(animation);
//            }
//        }
        
//        public Rotate3dAnimation getAnimation(float centerX,float centerY,final LewaFragment fragment,float FromDegress,float ToDegress){
//            Rotate3dAnimation animation=new Rotate3dAnimation(FromDegress, ToDegress, centerX, centerY, 310f, true);
//            animation.setDuration(500);
//            animation.setFillAfter(true);
//            animation.setInterpolator(new AccelerateInterpolator());
//            animation.setAnimationListener(new AnimationListener() {
//                
//                @Override
//                public void onAnimationStart(Animation animation) {
//                    // TODO Auto-generated method stub
//                    
//                }
//                
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//                    // TODO Auto-generated method stub
//                    
//                }
//                
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    // TODO Auto-generated method stub
//                    adapter.setWeatherSet(fragment.getWeatherSet());
//                    adapter.notifyDataSetChanged();
//                    startAnmationAgain(fragment);
//                }
//
//                private void startAnmationAgain(LewaFragment fragment) {
//                    // TODO Auto-generated method stub
//                    for(int i=0;i<lewa_weather_forecast.getCount();i++){
//                        ImageView view=(ImageView) lewa_weather_forecast.getChildAt(i).findViewById(R.id.lewa_forcast_icon);
//                        float centerX=view.getWidth()/2;
//                        float centerY=view.getHeight()/2;
//                        Rotate3dAnimation animation=new Rotate3dAnimation(-90,0, centerX, centerY, 310f, false);;
//                        animation.setDuration(500);
//                        animation.setFillAfter(true);
//                        animation.setInterpolator(new DecelerateInterpolator());
//                        view.startAnimation(animation);
//                    } 
//                }
//            });
//            return animation;
//        }
        
        
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            // TODO Auto-generated method stub
            if(arg2==null)
                return;
            if (mCurTransaction == null) {
                mCurTransaction = fragmentManager.beginTransaction();
            }
            mCurTransaction.hide((Fragment) arg2);
        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub
            if (mCurTransaction != null) {
                mCurTransaction.commitAllowingStateLoss();
                mCurTransaction = null;
                fragmentManager.executePendingTransactions();
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return fragmentList.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            // TODO Auto-generated method stub
            if (mCurTransaction == null) {
                mCurTransaction = fragmentManager.beginTransaction();
            }
            Fragment f;
            try {
                f = getFragment(arg1);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                return null;
            }
            mCurTransaction.show(f);
            return f;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            if(object == null) {
               return false;
            }
            // TODO Auto-generated method stub
            return ((Fragment) object).getView() == view;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return POSITION_NONE;
        }
    }
    
    private void setCityAndPM(LewaFragment fragment) {
        city = fragment.getCity();
        pm = fragment.getPm();
        pmCondition = fragment.getPmCondition();
        WeatherSet set=fragment.getWeatherSet();
//        cityAlpha=0.2f;
//        lewa_weather_city.setAlpha(cityAlpha);
//        lewa_weather_air.setAlpha(cityAlpha);
        if(city!=null)
        {   if(set.isLocate()){
              locate_icon.setVisibility(View.VISIBLE);
            }else{
              locate_icon.setVisibility(View.GONE);
            }
            city=WeatherControl.removeBrackets(city);
            if(city!=null)
                lewa_weather_city.setText(city);
        }
//        if(pm!=null&&!pm.equals("")){
//            lewa_weather_air.setVisibility(View.VISIBLE);
//            lewa_weather_pm_icon.setVisibility(View.VISIBLE);
//            lewa_weather_air.setText("PM2.5: "+pm);
//            int pmStatus=WeatherControl.getPmStatus(pm);
//            if(pmStatus==WeatherControl.PM_GOOD){
//                 lewa_weather_pm_icon.setImageResource(R.drawable.pm_app_good);
//            }else if(pmStatus==WeatherControl.PM_LIGHT){
//                lewa_weather_pm_icon.setImageResource(R.drawable.pm_app_slight);
//            }else if(pmStatus==WeatherControl.PM_SERIOUS){
//                lewa_weather_pm_icon.setImageResource(R.drawable.pm_app_serious);
//            }
//        }else{
            lewa_weather_air.setVisibility(View.GONE);
            lewa_weather_pm_icon.setVisibility(View.GONE);
//        }
        if(pmCondition!=null&&!pmCondition.equals("")){
            lewa_weather_AQ.setVisibility(View.VISIBLE);
            lewa_weather_AQ.setText(getResources().getString(R.string.AQI)+": "+pmCondition);
        }else{
            lewa_weather_AQ.setVisibility(View.GONE);
        }
        
//        mHandler.sendEmptyMessage(UPDATE_CITY);
         
     }
    
    public static void setSize(int i){
        size=i;
    }
    public void setBackGround(WeatherSet weatherSet){
        if (weatherSet != null && null != weatherSet.getWeatherCurrentCondition()){
            WeatherCurrentCondition wcc = weatherSet.getWeatherCurrentCondition();
            String condition = (TextUtils.isEmpty(wcc.getConditionCN()) ? TextUtils.isEmpty(wcc.getCondition())?getResources().getString(R.string.weather_unknown):wcc.getCondition() : wcc
                    .getConditionCN());
            String imageString=WeatherControl.getBgImageName(condition, lastCondition);
            if(!imageString.equals("")){
                int imageId=getResources().getIdentifier("lewa_"+imageString+"_bg_app", "drawable", "com.lewa.weather");
                setImageViewBg(imageId);
            }
            lastCondition=condition;
        }else{
            setImageViewBg(R.drawable.lewa_sunshine_bg_app);
            lastCondition="";
        }
        
        
//        mHandler.sendEmptyMessage(UPDATE_BG);
    }


    public void setImageViewBg(int resid) {
        if(isFirst){
            lewa_weather_imgbg_second.setBackgroundResource(resid);
            lewa_weather_imgbg_first.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
            lewa_weather_imgbg_second.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
            isFirst=false;
        }else{
            lewa_weather_imgbg_first.setBackgroundResource(resid);
            isFirst=true;
            lewa_weather_imgbg_second.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
            lewa_weather_imgbg_first.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
        }
    }
    
    private class ForeCastAdapter extends BaseAdapter{
        private ArrayList<WeatherForecastCondition> wfclist=new ArrayList<WeatherForecastCondition>();
        private List<WeatherSet> weatherSet;
        private String chineseDayOfWeek;
        public void setWeatherSet(List<WeatherSet> weatherSet) {
            this.weatherSet = weatherSet;
            wfclist.clear();
            Calendar calendar=Calendar.getInstance();
            int day=calendar.get(Calendar.DAY_OF_WEEK );
            int num=day+1;
            if(num>7)
                num=num-7;
            chineseDayOfWeek = DataFormatControl.DayOfWeekDisplay(num);
            for(int i=0;i<weatherSet.size();i++){
                ArrayList<WeatherForecastCondition> forecastConditions=weatherSet.get(i).getWeatherForecastConditions();
                int size=forecastConditions.size();
                if(size==0){
//                    Calendar calendar=Calendar.getInstance();
//                    int day=calendar.get(Calendar.DAY_OF_WEEK );
                    for(int k=0;k<4;k++){
                        WeatherForecastCondition wfc=new WeatherForecastCondition();
                        int n=day+1+k;
                        if(n>7){
                            n=n-7;
                        }
                        String dayofWeek=DataFormatControl.DayOfWeekDisplay(n);
                        wfc.setDayofWeek(dayofWeek);
                        wfc.setCondition(getResources().getString(R.string.sunshine));
                        wfc.setConditionCN("晴");
                        wfc.setTemperature(getResources().getString(R.string.nodata));
                        wfclist.add(wfc);
                    }
                    continue;
                }
                if(size>=6)
                	size=5;
                for(int j=1;j<size;j++){
                    wfclist.add(forecastConditions.get(j));
                }
            }
        }
        
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return wfclist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if(convertView==null){
                holder=new ViewHolder();
                convertView=View.inflate(LewaWeather.this, R.layout.lewa_forecast_item, null);
                holder.lewa_week=(TextView) convertView.findViewById(R.id.lewa_week);
                holder.lewa_forcast_icon=(ImageView) convertView.findViewById(R.id.lewa_forcast_icon);
//                holder.lewa_forcast_condition=(TextView) convertView.findViewById(R.id.lewa_forcast_condition);
                holder.lewa_forcast_temp=(TextView) convertView.findViewById(R.id.lewa_forcast_temp);
                convertView.setTag(holder);
            }else{
                holder=(ViewHolder) convertView.getTag();
            }
            if(wfclist.size()>0){
            WeatherForecastCondition wfc=wfclist.get(position);
            if(wfc.getDayofWeek()!=null&&wfc.getDayofWeek().equals(chineseDayOfWeek)){
                holder.lewa_week.setText(getResources().getString(R.string.tomorrow));
            }else{
                String dayOfWeek=DataFormatControl.DayOfWeekDisplayInternation(LewaWeather.this, wfc.getDayofWeek());
                if(!TextUtils.isEmpty(dayOfWeek))
                    holder.lewa_week.setText(dayOfWeek);
            }
            String condition=wfc.getConditionCN();
            if(TextUtils.isEmpty(condition))
            	condition=wfc.getCondition();
            String range=wfc.getTemperature();
            if(!TextUtils.isEmpty(range)&&range.contains("~")){
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
    //            holder.lewa_forcast_condition.setText(condition);
                    holder.lewa_forcast_temp.setText(v2+"/"+v1+"℃");
                }
            }else if(!TextUtils.isEmpty(range)) {
                holder.lewa_forcast_temp.setText(range);
            }  
            String imageString=WeatherControl.getImageString(condition);
            int imageId=getResources().getIdentifier("lewa_icon_app_forcast_"+imageString, "drawable", "com.lewa.weather");
            holder.lewa_forcast_icon.setImageResource(imageId);
         }
            return convertView;
        }
        
    }
    
    private static class ViewHolder{
        TextView lewa_week;
//        TextView lewa_forcast_condition;
        TextView lewa_forcast_temp;
        ImageView lewa_forcast_icon;
    }
    
  
    
    class WeatherTask extends AsyncTask<Context, Void, Void> {
        private WeakReference<Context> context;
        ProgressDialog pdialog;
        private Animation anim;
        private LewaFragment fragment;
        private WeatherSet weatherSet;
        private String citycode;
        private boolean result=false;
        private boolean isAnimEnd=false;

        public WeatherTask(Context context,LewaFragment fragment) {
            this.context = new WeakReference<Context>(context);
            this.fragment=fragment;
            pdialog = new ProgressDialog(this.context.get());
        }

        @Override
        protected final void onPreExecute() {
//            if(WeatherControl.isWiFiActive(getApplicationContext())||WeatherControl.IsConnection(getApplicationContext())){
                anim = AnimationUtils.loadAnimation(context.get(), R.anim.lewa_rotate);
                isAnimEnd=false;
                anim.setAnimationListener(new AnimationListener() {
                    
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
                        isAnimEnd=true;
                        lewa_weather_refresh.setClickable(true);
                        if(result){
                            if (context != null) {
                                refresh(getApplicationContext());
                            }
                        }
                    }
                });
                lewa_weather_refresh.startAnimation(anim);
//            }
            super.onPreExecute();
        }

        @Override
        protected final Void doInBackground(Context... params) {
           wc.updateAllWeathers(context.get());
            return null;
        }
        @Override
        protected final void onPostExecute(Void result) {
            lewa_weather_refresh.setClickable(true);
            this.result=true;
            if(isAnimEnd){
                if (context != null) {
                    refresh(getApplicationContext());
                }
            }
            super.onPostExecute(result);
        }

    }
    
   @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(receiver);
        super.onDestroy();
    }
   public void refresh(Context context) {
         if(fragmentList==null||fragmentList!=null&&fragmentList.size()<=0)
             return;
         Map<String, WeatherSet> temp = WeatherControl.loadWeatherData(context);
         if (null != temp && temp.size() > 0) {
             Map<String, WeatherSet> weatherMap=WeatherControl.loadWeatherData(context);
            
             List<WeatherSet> temp1=new ArrayList<WeatherSet>(weatherMap.values());
             final Map<String, ?> order = OrderUtil.getAll(LewaWeather.this);
             Collections.sort(temp1, new Comparator<WeatherSet>() {
                 @Override
                 public int compare(WeatherSet o1, WeatherSet o2) {
                     String cityCodeO1=o1.isLocate()?o1.getCityCode()+"|true":o1.getCityCode();
                     String cityCodeO2=o2.isLocate()?o2.getCityCode()+"|true":o2.getCityCode();
                     if(order.get(cityCodeO1)==null||order.get(cityCodeO2)==null){
                         return -1;
                     }else{
                         long d = (Long)order.get(cityCodeO1) -  (Long)order.get(cityCodeO2) ;
                         return d < 0 ? -1 : 1;
                     }
                 }
             });
             // add by luoyongxing, need check.
             int size = Math.min(fragmentList.size(), temp1.size());
             if(fragmentList.size() != temp1.size()){
            	 Log.w("LewaWeather", "fragmentList.size() != weatherMap.values.size(), fragmentList.size()="+fragmentList.size()+", weatherMap.values.size()"+temp1.size());
             }
             // end
             for(int i=0;i<fragmentList.size();i++){
                 LewaFragment fragment=fragmentList.get(i);
                 if(i<temp1.size())
                     fragment.setWeatherSet(temp1.get(i));
                 if(fragment==curFragment){
                     fragment.startRereshAnim();
                 }else{
                     fragment.setData();
                 }
             }
             setCityAndPM(curFragment);
             if(curFragment!=null)
                 setBackGround(curFragment.getWeatherSet());
             adapter.setWeatherSet(temp1);
             adapter.notifyDataSetChanged();
         }
   }   
 
}
