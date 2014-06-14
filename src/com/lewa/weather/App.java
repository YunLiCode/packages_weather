package com.lewa.weather;




import com.lewa.weather.entity.WeatherControl;

import android.app.Application;
import android.util.Log;

public class App extends Application{

	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		TickUpdate.register( this );
	}

	/* (non-Javadoc)
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		TickUpdate.unregister( this );
		super.onTerminate();
	}

}
