package com.lewa.weather.entity;

import java.lang.ref.WeakReference;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.lewa.weather.LewaWeatherSetting;
import com.lewa.weather.R;

public class WeatherTask2 extends AsyncTask<Object, Void, Integer> {
	ProgressDialog pdialog;
	private String citycode;
	private WeakReference<Context> context;
	Toast toast;
	
	public WeatherTask2(Context context, Toast toast) {
		this.context = new WeakReference<Context>(context);
		this.toast = toast;
	}
	
	@Override
	protected final void onPreExecute() {
		pdialog = new ProgressDialog(context.get());
		pdialog.setTitle(context.get().getResources().getString(R.string.app_name));
		pdialog.setMessage(context.get().getResources().getString(R.string.weather_weather_waiting));
		pdialog.setCancelable(true);
		pdialog.setIndeterminate(true);
		pdialog.show();
		pdialog.setCancelable(true);
		super.onPreExecute();
	}

	@Override
	protected final Integer doInBackground(Object... params) {
		
		try {
			final String citycn = (String) params[0];
			final String provincecn = (String) params[1];
			final String citycode = (String) params[2];
			Map<String,WeatherSet> weathers = (Map<String, WeatherSet>) params[3];
			boolean isLocate=(Boolean) params[4];
			
			if (null != weathers && null != weathers.get(citycode+"|"+isLocate)) {
				return 2;
			}

			this.citycode = citycode;
			final WeatherControl wc = new WeatherControl(context.get());
			boolean bool = false;
			try {
				bool = wc.updateWeatherData(context.get(), citycode, provincecn,
						citycn,System.currentTimeMillis(),false);
			} catch (Exception e) {
				e.printStackTrace();
				return 1;
			}
			if (bool)
				return 0;
			else {
				final WeatherSet model = new WeatherSet();
				model.setCityCode(citycode);
				model.setProvinceCn(provincecn);
				model.setCityCn(citycn);
				model.setCurrentMillis(System.currentTimeMillis());
				if (wc.addWeatherData(model,false))
					return 1;
				else
					return 3;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		}
	}

	@Override
	protected final void onPostExecute(Integer result) {
		pdialog.cancel();
		if(context.get()!=null){
    		Activity activity=(Activity)context.get();
            if(activity.isFinishing())
                return;
        }
		if (null == result || result == 1) {
			if (context.get() != null) {
				Builder dialog = new AlertDialog.Builder(context.get())
						.setMessage(R.string.weather_weather_get_failed)
						.setNegativeButton(R.string.weather_close,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(
											DialogInterface dialog,
											int which) {
										((LewaWeatherSetting)context.get()).updateData(citycode);
									}

								});
			    dialog.show();
				toast.cancel();
				toast.setText(context.get().getString(R.string.weather_cityadd_ok));
				toast.show();
			}
		} else if (result == 2) {
			toast.cancel();
			toast.setText(context.get().getString(R.string.weather_cityadd_error_1));
			toast.show();
			((LewaWeatherSetting)context.get()).updateData(citycode);
		} else if (result == 3) {
			toast.cancel();
			toast.setText(context.get().getString(R.string.weather_cityadd_error));
			toast.show();
			((LewaWeatherSetting)context.get()).updateData(null);
		} else if (result == 0) {
			if (context.get() != null) {
				((LewaWeatherSetting)context.get()).updateData(citycode);
				toast.cancel();
				toast.setText(context.get().getString(R.string.weather_cityadd_ok));
				toast.show();
			}
		}
		super.onPostExecute(result);
	}

}
