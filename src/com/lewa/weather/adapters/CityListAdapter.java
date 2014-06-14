package com.lewa.weather.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lewa.weather.entity.CityWeather;
import com.lewa.weather.entity.OrderUtil;
import com.lewa.weather.entity.WeatherControl;
import com.lewa.weather.entity.WeatherSet;
import com.lewa.weather.R;

public class CityListAdapter extends ArrayAdapter<CityWeather> {
	List<CityWeather> list = new ArrayList<CityWeather>();
	LayoutInflater inflater;
	Context context;

	public CityListAdapter(Context context, Map<String, WeatherSet> data) {
		super(context, R.layout.weather_city_list_item, new ArrayList<CityWeather>());

		Map<String, ?> order = OrderUtil.getAll(context);
		int i = 0;
		for (WeatherSet set : data.values()) {
			CityWeather cw = new CityWeather();
			cw.setCityName(set.getCityCn());
			cw.setOrder(i++);
			if (set.getWeatherForecastConditions().size() == 0) {
				cw.setTemp("暂无数据");
			} else {
				cw.setTemp(set
						.getWeatherForecastConditions()
						.get(0)
						.getTemperature()
						.replace("℃",
								context.getResources().getString(R.string.weather_du)));
			}
			cw.setCityCode(set.getCityCode());
			cw.setOrder((Long)order.get(set.getCityCode()));
			if (set.getWeatherCurrentCondition() != null)
			cw.setWeather(WeatherControl.getIconId(context, "weather_" + set
					.getWeatherCurrentCondition().getIconName()));
			list.add(cw);
		}
		Collections.sort(list, new Comparator<CityWeather>() {

			@Override
			public int compare(CityWeather o1, CityWeather o2) {
				long v = o1.getOrder() - o2.getOrder();
				return v < 0 ? -1 : 1;
			}
		});
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	public void notifyDataSetChanged(List<CityWeather> list) {
		this.list = list;
		super.notifyDataSetChanged();
	}

	@Override
	public CityWeather getItem(int position) {
		return list.get(position);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.weather_city_list_item, null);

			viewHolder = new ViewHolder();

			viewHolder.city = (TextView) convertView.findViewById(R.id.cityTxt);
			viewHolder.temp = (TextView) convertView.findViewById(R.id.temp);

			viewHolder.delete = (ImageView) convertView
					.findViewById(R.id.delete);
			viewHolder.weather = (ImageView) convertView
					.findViewById(R.id.weather);
			viewHolder.grabber = (ImageView) convertView
					.findViewById(R.id.grabber);

			
			viewHolder.delete.setOnClickListener(onDeleteListener);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		CityWeather cw = getItem(position);

		viewHolder.delete.setTag(cw);
		viewHolder.city.setText(cw.getCityName());

		viewHolder.temp.setText(cw.getTemp());
		viewHolder.weather.setImageResource(cw.getWeather());

		convertView.setBackgroundDrawable(context.getResources().getDrawable(
				chooseColor(position)));
		return convertView;
	}

	private OnClickListener onDeleteListener;
	
	public void setOnDeleteListener(OnClickListener l) {
		this.onDeleteListener = l;
	}
	
	private int chooseColor(int position) {

		System.out.println("chooseColor:" + getCount());
		if (getCount() == 1) {
			System.out.println("only one");
			return R.drawable.weather_single;
		}
		
		System.out.println("dammmmmm");
		if (position == 0) {
			
			return R.drawable.weather_city_list_top;
		}
		
		
		if (position == getCount() - 1) {
			if (position % 2 == 0) {
				return R.drawable.weather_city_down_white;
			}
			return R.drawable.weather_city_list_down_gray;
		}

		if (position % 2 == 0) {
			return R.drawable.weather_city_list_single;
		}
		return R.drawable.weather_city_list_single_gray;
	}

	class ViewHolder {
		TextView city;
		ImageView delete;
		ImageView weather;
		// ImageView flag;
		ImageView grabber;
		TextView temp;

	}

	public void add(int to, CityWeather item) {
		list.add(to, item);
	}

	public void swap(int from, int to) {
		
		if (from >= getCount() || to >= getCount()) {
			return;
		}
		CityWeather itemFrom = (CityWeather)getItem(from);

		list.remove(from);
		
		OrderUtil.clear(context);
		add(to, itemFrom);
		for (int i = 0; i < getCount(); i++) {
			CityWeather item = (CityWeather)getItem(i);
			item.setOrder(i + 1);
			OrderUtil.setOrderLite(context, item.getCityCode(), i + 1);
		}
		OrderUtil.updateDefault(context);
	}

	public void removeByCode(String cityCode) {
		for (int i = 0; i < getCount(); i++) {
			CityWeather c = (CityWeather)getItem(i);
			
			if (c.getCityCode().equals(cityCode)) {
				list.remove(i);
				break;
			}
		}
	}

}