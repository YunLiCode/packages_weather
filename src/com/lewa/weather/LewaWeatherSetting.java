package com.lewa.weather;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.lewa.weather.entity.CityWeather;
import com.lewa.weather.entity.OrderUtil;
import com.lewa.weather.entity.WeatherControl;
import com.lewa.weather.entity.WeatherSet;
import com.lewa.weather.entity.WeatherTask2;
import com.lewa.weather.view.DraggableListView;
import com.lewa.weather.R;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class LewaWeatherSetting extends ListActivity {
    
    private DraggableListView city_list;
    public static final int RESULT_ORDER_CHANGED = 9;
    private BroadcastReceiver receiver=new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action=intent.getAction();
            if(action.equals("com.lewa.weather.locate")){
                weathers = WeatherControl.loadWeatherData(LewaWeatherSetting.this);
                cityAdapter = new CityAdapter(LewaWeatherSetting.this, weathers);
                city_list.setAdapter(cityAdapter);
                city_list.invalidate();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setTitle(getString(R.string.city_set));
        setContentView(R.layout.lewa_setting);
        init();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.lewa.weather.locate");
        registerReceiver(receiver, filter);
        super.onCreate(savedInstanceState);
    }

    private void init() {
        city_list = (DraggableListView) getListView();
        addcity_bt = (Button) findViewById(R.id.addcity);
        del_city_bt = (ImageButton) findViewById(R.id.del_city);
        city_list.setButtons(addcity_bt, del_city_bt);
        city_list.setCacheColorHint(0);
        city_list.setDropListener(mDropListener);
        city_list.setLongClickable(false);
        weathers = WeatherControl.loadWeatherData(LewaWeatherSetting.this);
        cityAdapter = new CityAdapter(LewaWeatherSetting.this, weathers);
//        city_list.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
//        city_list.setAdapter(cityAdapter);
        setListAdapter(cityAdapter);
        if (cityAdapter.getCount() == 0) {
            Intent intent = new Intent();
            intent.setClass(LewaWeatherSetting.this, SelectCityActivity.class);
            startActivityForResult(intent,LewaWeather.REQUEST_NEW_CITY);
        } 
        addcity_bt.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent=new Intent(getApplicationContext(), SelectCityActivity.class);
                intent.putExtra("count", cityAdapter.getCount());
                startActivityForResult(intent, LewaWeather.REQUEST_NEW_CITY);
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
        case LewaWeather.REQUEST_NEW_CITY:
            if (resultCode == LewaWeather.RESULT_NEW_CITY) {
//              setResult(resultCode, data);
//              Intent intent = new Intent();
//              intent.setClass(this, Weather.class);
//              intent.putExtras(data);
//              startActivity(intent);
//              finish();
                
                String provinceCN = data.getStringExtra("province");
                String cityCN = data.getStringExtra("city");
                String cityCode = data.getStringExtra("cityCode");
                
                new WeatherTask2(LewaWeatherSetting.this, Toast.makeText(LewaWeatherSetting.this, "", Toast.LENGTH_SHORT)).execute(cityCN, provinceCN,
                        cityCode, weathers,false);
                
            } else {
                weathers = WeatherControl.loadWeatherData(LewaWeatherSetting.this);
                cityAdapter = new CityAdapter(LewaWeatherSetting.this, weathers);
//                city_list.setAdapter(cityAdapter);
                setListAdapter(cityAdapter);
                if (cityAdapter.getCount() == 0) {
                    finish();
                }
            }
            break;

        default:
            break;
        }
    }
    
    private DraggableListView.DropListener mDropListener = new DraggableListView.DropListener() {
        public void drop(final int from, final int to) {
            if(to==-1){
                cityAdapter.deleteItem(from);
            }else{
                cityAdapter.swap(from, to);
            }
            cityAdapter.notifyDataSetChanged();
            if (from != to) {
                setResult(RESULT_ORDER_CHANGED);
                sendBroadcast(new Intent("when.action.instance.update"));
            }
        }
    };
    private List<CityWeather> list;
    private CityAdapter cityAdapter;
    private Button addcity_bt;
    private ImageButton del_city_bt;
    private Map<String, WeatherSet> weathers;
    
    private class CityAdapter extends BaseAdapter{
        private Context context;
        private Map<String, WeatherSet> weathers;
        public CityAdapter(Context context, Map<String, WeatherSet> weathers){
            this.context=context;
            this.weathers=weathers;
            list = new ArrayList<CityWeather>();
            Map<String, ?> order = OrderUtil.getAll(LewaWeatherSetting.this);
            int i = 0;
//            for (WeatherSet set : weathers.values()) {
//                CityWeather cw = new CityWeather();
//                cw.setCityName(set.getCityCn());
////                cw.setOrder(i++);
//                cw.setCityCode(set.getCityCode());
//                cw.setOrder((Long)order.get(set.getCityCode()));
//                list.add(cw);
//            }
            for(Entry<String, WeatherSet> entry:weathers.entrySet()){
                WeatherSet set=entry.getValue();
                String key=entry.getKey();
                CityWeather cw = new CityWeather();
                String cityCn=set.getCityCn();
                cityCn=WeatherControl.removeBrackets(cityCn);
                if(cityCn!=null)
                    cw.setCityName(cityCn);
//                cw.setOrder(i++);
                cw.setCityCode(set.getCityCode());
                if(key.contains("true")){
                    cw.setLocate(true);
                    String code=set.getCityCode()+"|true";
                    if(order.get(code)!=null)
                        cw.setOrder((Long)order.get(code));
                }else{
                    if(order.get(set.getCityCode())!=null)
                        cw.setOrder((Long)order.get(set.getCityCode()));
                }
                cw.setTag(set);
                list.add(cw);
            }
            Collections.sort(list, new Comparator<CityWeather>() {

                @Override
                public int compare(CityWeather o1, CityWeather o2) {
                    long v = o1.getOrder() - o2.getOrder();
                    return v < 0 ? -1 : 1;
                }
            });
        }
        public void deleteItem(int from) {
            // TODO Auto-generated method stub
            CityWeather cWeather=list.get(from);
           if( WeatherControl.deleteCity(context, cWeather.getTag(), cWeather.getCityCode())){
               Toast.makeText(context,getResources().getString(R.string.del_success), 0).show();
           }else{
               Toast.makeText(context, getResources().getString(R.string.del_fail), 0).show();
           };
            if(cWeather.isLocate()){
                weathers.remove(cWeather.getCityCode()+"|true");
                OrderUtil.remove(context, cWeather.getCityCode()+"|true");
                OrderUtil.removeAutoOrder(context);
             }else{
                weathers.remove(cWeather.getCityCode()+"|false");
                OrderUtil.remove(context, cWeather.getCityCode());
             }
            list.remove(from);
//            LewaWeather.fragmentList.remove(from);
//            LewaWeather.setSize(LewaWeather.fragmentList.size());
            if(LewaWeather.fragmentList==null)
                return;
            for(int i=0;i<LewaWeather.fragmentList.size();i++){
                LewaFragment fragment=LewaWeather.fragmentList.get(i);
                if(fragment.getPositon()>from){
                    fragment.setPositon(fragment.getPositon()-1);
                }
            }
//            Intent intent=new Intent("com.lewa.player.deleteItem");
//            intent.putExtra("position", from);
//            sendBroadcast(intent);
        }
        
        public void swap(int from, int to) {
            // TODO Auto-generated method stub
            if (from >= getCount() || to >= getCount()) {
                return;
            }
            CityWeather itemFrom = (CityWeather)getItem(from);

            list.remove(from);
            
            OrderUtil.clear(context);
            add(to, itemFrom);
            SharedPreferences sp=context.getSharedPreferences("weatherLocation", Context.MODE_PRIVATE);
            String autoCityCode=sp.getString("automatic", "");
            for (int i = 0; i < getCount(); i++) {
                CityWeather item = (CityWeather)getItem(i);
                if(autoCityCode.equals(item.getCityCode())){
                    if(!OrderUtil.getIsAutoOrderSetted(context))
                        OrderUtil.setIsAutoOrderSetted(context,true);
                    OrderUtil.setAutoOrder(context, i);
                }
                item.setOrder(i);
                if(item.isLocate()){
                    OrderUtil.setOrderLite(context, item.getCityCode()+"|true", i);
                }else{
                    OrderUtil.setOrderLite(context, item.getCityCode(), i);
                }
            }
            OrderUtil.updateDefault(context);
//            if(LewaWeather.fragmentList==null)
//                return;
//            if(from<to){
//                for(int i=0;i<LewaWeather.fragmentList.size();i++){
//                    LewaFragment tempFragment=LewaWeather.fragmentList.get(i);
//                    if(tempFragment.getPositon()>from&&tempFragment.getPositon()<=to){
//                        tempFragment.setPositon(tempFragment.getPositon()-1);
//                    }else if(tempFragment.getPositon()==from){
//                        tempFragment.setPositon(to);
//                    }
//                }
//            }else {
//                for(int i=0;i<LewaWeather.fragmentList.size();i++){
//                    LewaFragment tempFragment=LewaWeather.fragmentList.get(i);
//                    if(tempFragment.getPositon()<from&&tempFragment.getPositon()>=to){
//                        tempFragment.setPositon(tempFragment.getPositon()+1);
//                    }else if(tempFragment.getPositon()==from){
//                        tempFragment.setPositon(to);
//                    }
//                }
//            }
        }
        
        public void add(int to, CityWeather item) {
            list.add(to, item);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            convertView=View.inflate(context, R.layout.lewa_city_item, null);
            TextView lewa_city=(TextView) convertView.findViewById(R.id.lewa_city);
            CityWeather cw=list.get(position);
            if(cw.isLocate()){
                lewa_city.setText(list.get(position).getCityName()+"("+getResources().getString(R.string.auto_location)+")");
            }else{
                lewa_city.setText(list.get(position).getCityName());
             }
            return convertView;
        }
        
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
        case android.R.id.home: {
            Intent intent = new Intent();
            intent.setClass(this, LewaWeather.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
           }
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void updateData(String citycode) {
        weathers = WeatherControl.loadWeatherData(LewaWeatherSetting.this);
        cityAdapter = new CityAdapter(LewaWeatherSetting.this, weathers);
//        city_list.setAdapter(cityAdapter);
        setListAdapter(cityAdapter);
        if (cityAdapter.getCount() == 0) {
            Intent intent = new Intent();
            intent.setClass(LewaWeatherSetting.this, SelectCityActivity.class);
            startActivityForResult(intent,LewaWeather.REQUEST_NEW_CITY);
        } 
        
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.setClass(this, LewaWeather.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
