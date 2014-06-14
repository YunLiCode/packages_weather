package com.lewa.weather.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.lewa.weather.entity.City;
import com.lewa.weather.entity.WeatherControl;
import com.lewa.weather.entity.WeatherSet;
import com.lewa.weather.R;

public class HotCityAdapter extends AddCityAdapter {

	Context context;
	LayoutInflater inflater;
	public static  int columnNum=3;
//	public static final int TEXT_COLOR = Color.rgb(52, 157, 238);
//	public static final int WHITE_COLOR = Color.argb(255, 240, 241, 242);
//	public static final int GRAY_COLOR = Color.argb(255, 230, 235, 238);
	public HotCityAdapter(Context context, List<City> cities) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		SharedPreferences sp=context.getSharedPreferences(WeatherControl.WEATHER_SHAREDPREFS_COMMON, Context.MODE_PRIVATE);
		int layout=sp.getInt("layout",0 );
		String location_country=sp.getString(WeatherControl.LOCATION_COUNTRY, "");
		if(layout==1&&!WeatherControl.isLanguageEnUs()){
			columnNum=3;
		}else if(layout==2&&WeatherControl.isLanguageZhCn()){
			columnNum=1;
		}else if(WeatherControl.isLanguageZhCn()){
			columnNum=3;
		}else if(WeatherControl.isLanguageEnUs()&&TextUtils.isEmpty(location_country)){
//			columnNum=1;
			cities.clear();
		}else if(WeatherControl.isLanguageEnUs()){
			columnNum=1;
		}
		updateCities(cities);
		this.cities=cities;
	}

	@Override
	public int getCount() {
	    int size=cities.size();
		return ((size-1)%columnNum==0?(size-1)/columnNum:(size-1)/columnNum+1)+1;
	}

	@Override
	public Object getItem(int position) {
		return cities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		List<TextView> views;

		public ViewHolder() {
			views = new ArrayList<TextView>();
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {

			LinearLayout layout = new LinearLayout(context);

			layout.setLayoutParams(new AbsListView.LayoutParams(
					AbsListView.LayoutParams.FILL_PARENT,context.getResources().getInteger(R.integer.hot_grid_height)));
			layout.setOrientation(LinearLayout.HORIZONTAL);

			layout.setWeightSum(1f);
			layout.setGravity(Gravity.CENTER);

			holder = new ViewHolder();

			LayoutParams params = new LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT, 1.0f / column);
			for (int i = 0; i < column; i++) {
					TextView text =(TextView) View.inflate(context, R.layout.lewa_text_item, null);
					if(column==1){
						text.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
						params.setMargins(20, 0, 0, 0);
					}else{
						params.setMargins(10, 5, 10, 5);
						text.setGravity(Gravity.CENTER);
					}
					text.setTextSize(18f);
					layout.addView(text, params);
					if(columnNum==3)
						text.setOnClickListener(onSelectCityListener);
					holder.views.add(text);
			}
			convertView = layout;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		for(int i=0;i<column;i++)
			holder.views.get(i).setVisibility(View.INVISIBLE);
		for (int i = 0; i < column; i++) {
			int realPosition=0;
			if(position!=0){
			     realPosition=(position-1) * column + i+1;
			}
			TextView text = holder.views.get(i);
		    if(realPosition>=0&&realPosition<cities.size()){
    		    City city=cities.get(realPosition);
    			String name=city.getName();
    			String name_en=city.getName_en();
    			if((WeatherControl.isLanguageZhCn()||WeatherControl.isLanguageZhTw())&&name!=null){
    			    text.setText(name);
        			text.setVisibility(View.VISIBLE);
    			}else if(WeatherControl.isLanguageEnUs()&&name_en!=null){
    				 text.setText(name_en);
         			 text.setVisibility(View.VISIBLE);
    			}
    			text.setTag(realPosition);
    			if (city.isAdded()&&column!=1) {
    				text.setBackgroundResource(R.drawable.weather_city_selected);
    			}else{
    				text.setBackgroundDrawable(null);
    			} 
		   }
		   if(realPosition==0)
		   {		
			   if(WeatherControl.isLanguageZhCn()||WeatherControl.isLanguageZhTw())
				   holder.views.get(i).setTextSize(15f);
               SharedPreferences sp =context.getSharedPreferences("weatherLocation",Context.MODE_PRIVATE);
               String automatic=sp.getString("automatic", "");
               if(!automatic.equals("")&&column!=1)
            	   holder.views.get(i).setBackgroundResource(R.drawable.weather_city_selected);
			   return convertView;
		   }else{
			   holder.views.get(0).setTextSize(18f);
		   }
		}
		return convertView;
	}

	public void updateCities(List<City> cities) {
//		this.citys = citys;

		if (cities == null || cities.size() == 0) {
			column = 0;
		} else {
			column = columnNum;
		}

//		selected = new boolean[citys.size()][column];

		Map<String, WeatherSet> data = WeatherControl.loadWeatherData(context);
		if(data==null||data.size()==0){
		    for(City city: cities){
               city.setAdded(false);
            }
		}else{
			ArrayList<String> names=new ArrayList<String>();
    		for (WeatherSet set : data.values()) {
    		    String dataName=set.getCityCn();
    		    names.add(dataName);
    		}
    		for(City city: cities){
    			String cityName=city.getName();
    			if(cityName!=null&&names.contains(cityName)){
    				city.setAdded(true);
    			}else{
    				city.setAdded(false);
    			}
    		}
		}
	}

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "HotCityAdapter";
    }

    @Override
    public void update(List<String[]> citys) {
        // TODO Auto-generated method stub
        
    }
    
    public void clear(){
        this.cities.clear();
    }
}
