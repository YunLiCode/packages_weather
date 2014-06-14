package com.lewa.weather.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.lewa.weather.entity.City;
import com.lewa.weather.entity.CityEntity;
import com.lewa.weather.entity.QuanPin;
import com.lewa.weather.entity.SuoXie;
import com.lewa.weather.entity.WeatherControl;
import com.lewa.weather.entity.WeatherSet;
import com.lewa.weather.provider.LewaDbHelper;
import com.lewa.weather.R;

public class SearchCityAdapter extends AddCityAdapter {

	Context context;
	LayoutInflater inflater;

//	public static final int TEXT_COLOR = Color.rgb(52,157,238);
    private LewaDbHelper dbHelper;
//	public static final int WHITE_COLOR = Color.argb(255, 240, 241, 242);
//	public static final int GRAY_COLOR = Color.argb(255, 230, 235, 238);

	@SuppressWarnings("unchecked")
	public SearchCityAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	    dbHelper = new LewaDbHelper(context);
	}

	@Override
	public int getCount() {
		return cities.size();
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
		TextView view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {

			LinearLayout layout = new LinearLayout(context);

			layout.setLayoutParams(new AbsListView.LayoutParams(
					AbsListView.LayoutParams.FILL_PARENT, context.getResources().getInteger(R.integer.hot_grid_height)));
			layout.setOrientation(LinearLayout.HORIZONTAL);

			holder = new ViewHolder();

			LayoutParams params = new LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.FILL_PARENT);

			params.setMargins(20, 0, 0, 0);

			final TextView text = (TextView) View.inflate(context, R.layout.lewa_text_item, null);
			text.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			text.setTextSize(18f);
			layout.addView(text, params);
			holder.view=text;
//			text.setOnClickListener(onSelectCityListener);
		
//			layout.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					text.performClick();
//				}
//			});
			convertView = layout;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

//		String[] arr = (String[]) getItem(position);
		City city=(City) getItem(position);
		for (int i = 0; i < column; i++) {
			TextView text = holder.view;
			if(WeatherControl.isLanguageZhCn()||WeatherControl.isLanguageZhTw()){
				text.setText(city.getName());
			}else{
				text.setText(city.getName_en());
			}
			text.setTag(position);
//			if (isSelected(position, i)) {
//				text.setBackgroundResource(R.drawable.weather_city_selected);
//			} else {
//				text.setBackgroundColor(Color.TRANSPARENT);
//			}
		}
		return convertView;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;

		if (cities == null || cities.size() == 0) {
			column = 0;
		} else {
			column = cities.size();
		}
	}

	private void filterName(String filter) {
	    List<City> cities=dbHelper.searchCityByName("name",filter);
	    setCities(cities);
	}

	public void filter(String filter) {
		if(filter!=null&&!filter.contains("'")){
			for (char ch : filter.toCharArray()) {
				if (!((ch <= 'z' && ch >= 'a') || (ch <= 'Z' && ch >= 'A'))) {
					filterName(filter);
					return;
				}
			}
		}
		List<String[]> cityres = new ArrayList<String[]>();

		filter = filter.toLowerCase();
		List<City> cities=dbHelper.searchCityByName("name_en",filter);
		setCities(cities);
	}

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "SearchCityAdapter";
    }

    @Override
    public void update(List<String[]> citys) {
        // TODO Auto-generated method stub
        
    }
}
