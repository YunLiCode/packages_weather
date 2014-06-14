package com.lewa.weather.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.R.integer;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lewa.weather.LewaWeather;
import com.lewa.weather.control.NetworkControl;
import com.lewa.weather.provider.LewaDbHelper;
import com.lewa.weather.R;
//import lewa.bi.BIAgent;

public class WeatherControl {

	public static final String LAST_UPDATE_TIME = "lastUpdateTime";
	public static final String WEATHER_AUTO_UPDATE = "weatherAutoUpdate";
	public static final String WEATHER_PROVINCE_CODE = "weatherProvinceCode";
	public static final String WEATHER_CITY = "weatherCity";
	public static final String WEATHER_CITY_CODE = "weatherCityCode";
	public static final String WEATHER_CITY_DEFAULT = "weatherCity_default";
	public static final String WEATHER_CITY_CODE_DEFAULT = "weatherCityCode_default";
	public static final String WEATHER_UPDATE_ROUND = "weatherUpdateRound";
	public static final String WEATHER_SHOW = "showInMain";
	public static final String ALREADY_SET = "alreadySet";
	public static final String WEATHER_PROVINCE = "weatherProvince";
	public static final String WEATHER_PROVINCE_DEFAULT = "weatherProvince_default";
	public static final String UPDATE_ROUND_SELECTED = "updateRoundSelected";
	public static final String CITY_SELECTED = "citySelected";
	public static final String PROVINCE_SELECTED = "provinceSelected";
	
	public static final String WEATHER_LOCATION_SETTING = "weatherLocation";
	public static final String WEATHER_LOCATION_SETTING1 = "weatherLocation1";
	public static final String LOCAL_CITY = "localCity";
	public static final String WEATHER_CURRENT_TEMPERATURE = "weatherCurrentTemperature";
	public static final String WEATHER_CURRENT_CONDITION = "weatherCurrentCondition";
	public static final String WEATHER_CURRENT_WIND_CONDITION = "weatherCurrentWindCondition";
	public static final String WEATHER_CURRENT_ICON_NAME = "weatherIconName";
	public static final String WEATHER_CURRENT_CITY = "weatherCurrentCity";
	
	public static final String WEATHER_CURRENT = "weatherCurrent";
	public static final String WEATHER_CUR_CITY_CODE = "weatherCurrentCityCode";
	
	private static final String SHORT_LONGITUDE = "ShortLongitude";
	private static final String SHORT_LATITUDE = "ShortLatitude";
	private static final String LONGITUDE = "Longitude";
	private static final String LATITUDE = "Latitude";
	public static final String WEATHER_UPDATE_ACTION = "com.lfan.action.WEATHER_UPDATE";
	public static final int WEATHER_SUNSHINE=0;
	public static final int WEATHER_SHADE=2;
	public static final int WEATHER_CLOUDY=1;
	public static final int WEATHER_FOG=3;
	public static final int WEATHER_HAZE=4;
	public static final int WEATHER_SHOWER=5;
	public static final int WEATHER_LIGHT_RAIN=6;
	public static final int WEATHER_MODE_RAIN=7;
	public static final int WEATHER_HEAVRY_RAIN=8;
	public static final int WEATHER_THUNDER_RAIN=9;
	public static final int WEATHER_TORRENT_RAIN=10;
	public static final int WEATHER_RAIN_SNOW=11;
	public static final int WEATHER_SNOW_SHOWER=12;
	public static final int WEATHER_LIGHT_SNOW=13;
	public static final int WEATHER_MODE_SNOW=14;
	public static final int WEATHER_HEAVRY_SNOW=15;
	public static final int WEATHER_TORRENT_SNOW=16;
	public static final int WEATHER_RAIN_HAIL=17;
	public static final int WEATHER_HAIL=18;
	
	public static final int PM_GOOD=0;
	public static final int PM_LIGHT=1;
	public static final int PM_SERIOUS=2;
    public static final String WEATHER_UPDATE="weather_update";
	public static final String HOT_CITIES_UPDATE="hot_cities";
	public static final String ALL_CITIES_UPDATE="all_cities";
	
	
	private Context context;
	private Spinner spiProvince = null;
	private Spinner spiCity = null;
	private Spinner spiHour = null;
	private LinearLayout chkAutoUpdate = null;
	private TextView offTextView = null;
	private TextView onTextView = null;
	
	private LinearLayout chkAutoUpdateShow = null;
	private TextView offTextViewShow = null;
	private TextView onTextViewShow = null;
	
		
	private ArrayAdapter<String> provinceAdpter = null;
	private ArrayAdapter<String> cityAdpter = null;
	private int provinceSelected = 0;
	private int citySelected = 0;
	private int updateRoundSelected = 0;
	private Boolean autoUpdate = true;
	private Boolean isAutoUpdateSelected = false;
	private Boolean isShowSelected = false;

	private static LocationManager mgr;

	private static final String BASE_URL = "http://api.lewaos.com/thinkpage/trends";				//六天预报
	private static final String BASE_NOW_URL = "http://api.lewaos.com/thinkpage/day";	//实时天气
	private static final String WEATHERAPI_WEATHER_URL = "http://weather.365rili.com/weatherapi/weather_get_cn.php?citycode=";
	private static final String EXTENSION = ".html";
	public static final String WEATHER_TAG = "RiliWeather";
	private static final String WEATHER_DATA_FILE = "weather.dat";
	private static final String SHANGHAI_WEATHER_DATA_FILE = "shanghaiweather.dat";
	private static String bestMethod;
    public  static final String ALL_CITIES_URL="http://api.lewaos.com/thinkpage/get_all_city";
    public static final String HOT_CITIES_URL="http://api.lewaos.com/thinkpage/get_hot_city";

	private static Map<String, Integer> locationMap = new HashMap<String, Integer>();
	
	private String language = Locale.getDefault().getCountry();
	public String[] PROVINCE;
	public String[] PROVINCE_CODE;
	public String[][] CITY;
	public String[][] CITY_CODE;
	public static final String[] UPDATE_ROUND = {"0.5", "1", "2", "3", "4", "6", "8", "12", "24"};
	private final static String MAIN_PACKAGE_NAME = "com.when.android.calendar365";
	
	
	public WeatherControl(Context context) {
		this.context = context;
//		getCityData();
	}

	/*
	 *  获取用户位置
	 */
	public static Boolean getUserLocation(Context context) {
		mgr = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Location location;
		try {
			// 获取最佳数据源
			Criteria criteria = new Criteria();
			bestMethod = mgr.getBestProvider(criteria, true);
			location = mgr.getLastKnownLocation(bestMethod);

			if (location != null) {
				locationMap.put(LATITUDE, Integer.parseInt((new BigDecimal(
						location.getLatitude() * 1000000).setScale(0,
						BigDecimal.ROUND_HALF_UP)).toString()));
				locationMap.put(LONGITUDE, Integer.parseInt((new BigDecimal(
						location.getLongitude() * 1000000).setScale(0,
						BigDecimal.ROUND_HALF_UP)).toString()));
				locationMap.put(SHORT_LATITUDE, Integer.parseInt((new BigDecimal(
						location.getLatitude()).setScale(1,
						BigDecimal.ROUND_HALF_UP)).toString()));
				locationMap.put(SHORT_LONGITUDE, Integer.parseInt((new BigDecimal(
						location.getLongitude()).setScale(1,
						BigDecimal.ROUND_HALF_UP)).toString()));
				
				return true;
			}
		} catch (NumberFormatException e) {
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		return false;
	}
	

	/*
	 *  获取天气信息
	 *  useLocation 为true时则使用城市配置，为false时则采用定位方式
	 */
	public static WeatherSet getData(Context context, Boolean useSetting,String cityCode,String provinceCN,String cityCN) {
		
		String httpUrl = BASE_URL;
		String httpNowUrl = BASE_NOW_URL;
//		String cityCode = null;
//		String provinceCN = null;
//		String cityCN = null;
		WeatherSet ws = null;
		boolean getSuccess = false;
		URLConnection urlConnection = null;
		
		
		if(useSetting)
		{	
		    if(cityCode!=null){
		        httpUrl += "?city_code="+cityCode;
			    httpNowUrl +="?city_code="+ cityCode;
			}else if(cityCN!=null){
			    if(cityCN.contains("市"))
			        cityCN=cityCN.substring(0, cityCN.lastIndexOf("市"));
			    httpUrl += "?city_name="+cityCN;
                httpNowUrl +="?city_name="+ cityCN;
			}
		    if(mLocation!=null&&!TextUtils.isEmpty(cellInfo))
            {
                httpUrl+="&gps="+mLocation.getLatitude()+","+mLocation.getLongitude()+"&cellInfo="+cellInfo;
                httpNowUrl+="&gps="+mLocation.getLatitude()+","+mLocation.getLongitude()+"&cellInfo="+cellInfo;
            }
			try {
				String cont = "";
				JSONObject json;
				JSONObject weatherInfoObj;
				Calendar now = Calendar.getInstance();
				String dayOfWeek = "";
		        
				WeatherCurrentCondition wcc = new WeatherCurrentCondition();
				WeatherForecastCondition[] wfc = new WeatherForecastCondition[6];
				
				for(int i = 0; i < wfc.length; i++) {
					wfc[i] = new WeatherForecastCondition();
				}
				// 获取实时天气
				HttpGet request = new HttpGet(httpNowUrl);
				String user_agent=buildUserAgent(context);
				if(user_agent!=null&&!user_agent.equals(""))
				    request.addHeader("User-Agent",user_agent);
				request.addHeader("accept-language", getLanguageHeader());
				HttpClient httpClient = NetworkControl.getHttpClient(context);
				HttpResponse response = httpClient.execute(request);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && response.getEntity() != null) {
					cont = EntityUtils.toString(response.getEntity(), "UTF8");
				}
				String expires=response.getFirstHeader("Expires").getValue();
				json = new JSONObject(cont);
				weatherInfoObj = json.getJSONObject("result");
				if(weatherInfoObj.has("weatherinfo")){
				    weatherInfoObj=weatherInfoObj.getJSONObject("weatherinfo");
				}
				dayOfWeek = DataFormatControl.DayOfWeekDisplay(now.get(Calendar.DAY_OF_WEEK ));
				wcc.setDayofWeek(dayOfWeek);
				wcc.setTemperature(weatherInfoObj.getString("temp") + "℃");
				wcc.setWindCondition(weatherInfoObj.getString("WD") + " " + weatherInfoObj.getString("WS"));
				wcc.setShidu(weatherInfoObj.getString("SD"));
				wcc.setWindDirection(weatherInfoObj.getString("WD"));
				if(weatherInfoObj.has("PM25")){
    				wcc.setPm25(weatherInfoObj.getString("PM25"));
    				wcc.setPmcondition(weatherInfoObj.getString("PM25Text"));
				}
				int mounth=now.get(Calendar.MONTH)+1; 
				int day=now.get(Calendar.DAY_OF_MONTH);
				wcc.setPubTime(mounth+"月"+day+"日  "+weatherInfoObj.getString("time"));
				
				// 获取六天预报
				request = new HttpGet(httpUrl);
				request.addHeader("accept-language", getLanguageHeader());
				httpClient = NetworkControl.getHttpClient(context);
				response = httpClient.execute(request);
				cont = "";
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && response.getEntity() != null) {
					cont = EntityUtils.toString(response.getEntity(), "UTF8");
				}
				json = new JSONObject(cont);
				weatherInfoObj = json.getJSONObject("result").getJSONObject("weatherinfo");
				wcc.setCondition(weatherInfoObj.getString("weather1"));
				wcc.setConditionCN(weatherInfoObj.getString("weather1_cn"));
				for(int i = 0; i < 6; i++) {
					wfc[i].setDayofWeek("" + i);
    				if(weatherInfoObj.has("weather" + (i + 1))){
    				    String weather=weatherInfoObj.getString("weather" + (i + 1));
    				    if(!TextUtils.isEmpty(weather)){
    				        wfc[i].setCondition(weather);
    				     }else{
    				         wfc[i].setCondition(context.getString(R.string.sunshine));
    				     }
    				}else{
    				    wfc[i].setCondition(context.getString(R.string.sunshine));
    				}
    				if(weatherInfoObj.has("weather" + (i + 1)+"_cn")){
    					   String weather_cn=weatherInfoObj.getString("weather" + (i + 1)+"_cn");
    					   if(!TextUtils.isEmpty(weather_cn)){
    						   wfc[i].setConditionCN(weather_cn);
       				       }else{
       				    	   wfc[i].setConditionCN("晴");
       				       }
    				}
    				if(weatherInfoObj.has("temp" + (i + 1))){
    				    wfc[i].setTemperature(weatherInfoObj.getString("temp" + (i + 1)));
					}else{
					    wfc[i].setTemperature(context.getString(R.string.weather_no_data));
					}
					dayOfWeek = DataFormatControl.DayOfWeekDisplay(now.get(Calendar.DAY_OF_WEEK ));
					wfc[i].setDayofWeek(dayOfWeek);
					now.add(Calendar.DATE, 1);
				}

				ws = new WeatherSet();
				if(expires!=null)
				    ws.setExpires(expires);
				ws.setCurrentMillis(System.currentTimeMillis());
				ws.setWeatherCurrentCondition(wcc);
				for(int i = 0; i < wfc.length; i++) {
					ws.getWeatherForecastConditions().add(wfc[i]);
				}
				
				if(provinceCN!=null)
				{
					ws.setProvinceCn(provinceCN);
				}
				
//				if(cityCN!=null)
//				{
//					ws.setCityCn(cityCN);
//				}else {
				    ws.setCityCn(weatherInfoObj.getString("city"));
//				}
				if(cityCode!=null){
				    ws.setCityCode(cityCode);
			    }else{
			        ws.setCityCode(weatherInfoObj.getString("cityid"));
			    }
				
				if(wfc.length > 0 && ws != null) {
					getSuccess = true;
					return ws;
				}
			
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
//		if(!getSuccess) {
//			SAXParserFactory spf;
//			SAXParser sp;
//			XMLReader xr;
//			String language = null;
//			String codeFormat = "UTF8";
//			ws = null;
//			
//			language = Locale.getDefault().getCountry();
////			Log.i(WEATHER_TAG, "Local Language: " + language);
//			
////			if(language.equals("CN"))
////			{
////				language = "zh-cn";
////				codeFormat = "GBK";
////			}
////			else if(language.equals("TW"))
////			{
////				language = "zh-tw";
////				codeFormat = "Big5";
////			}
//	//		else if(language.equals("US"))
//	//		{
//	//			language = "us";
//	//		}
//			
//			if(useSetting)
//			{			
//				httpUrl = httpUrl.replace("zh-cn", language);
//	
//				httpUrl = WEATHERAPI_WEATHER_URL + cityCode;
//			}
//			else if (getUserLocation(context)) {
//				httpUrl += ",,," + locationMap.get(LATITUDE).toString() + ","
//						+ locationMap.get(LONGITUDE).toString();
//			}
//			else
//			{
//				return ws;
//			}
//	
//	
//			try {
//				spf = SAXParserFactory.newInstance();
//				sp = spf.newSAXParser();
//				xr = sp.getXMLReader();
//				
//				InputStreamReader isr;
//				urlConnection = NetworkControl.getHttpURLConnection(context, httpUrl);
//				urlConnection.connect();
//				isr = new InputStreamReader(urlConnection.getInputStream(), codeFormat);
//				
//				GoogleWeatherHandler gwh = new GoogleWeatherHandler();
//				xr.setContentHandler(gwh);
//				xr.parse(new InputSource(isr));
//	
//				ws = gwh.getWeatherSet();
//				
//				Calendar now = Calendar.getInstance();
//				String dayOfWeek = DataFormatControl.DayOfWeekDisplay(now.get(Calendar.DAY_OF_WEEK ));
//				ws.getWeatherCurrentCondition().setDayofWeek(dayOfWeek);
//				ws.getWeatherCurrentCondition().setTemperature(ws.getWeatherCurrentCondition().getTemperature() + "℃");
////				ws.getWeatherCurrentCondition().setTemperature("暂无数据");
////				ws.getWeatherCurrentCondition().setWindCondition("");
//				for(int i = 0, len = ws.getWeatherForecastConditions().size(); i < len; i++) {
//					dayOfWeek = DataFormatControl.DayOfWeekDisplay(now.get(Calendar.DAY_OF_WEEK ));
//					ws.getWeatherForecastConditions().get(i).setDayofWeek(dayOfWeek);
//					now.add(Calendar.DATE, 1);
//				}
//				
//				ws.setCurrentMillis(System.currentTimeMillis());
//				
//				if(!provinceCN.equals(null))
//				{
//					ws.setProvinceCn(provinceCN);
//				}
//				
//				if(!cityCN.equals(null))
//				{
//					ws.setCityCn(cityCN);
//				}
//				
//				ws.setCityCode(cityCode);
//				
//				if(ws.getWeatherCurrentCondition() == null)
//				{
//					ws = null;
//				}
//				
//				return ws;
//	
//			} catch (ParserConfigurationException e) {
//				// TODO Auto-generated catch block
//				//Log.e(WEATHER_TAG, e.getMessage());
//				e.printStackTrace();
//			} catch (SAXException e) {
//				// TODO Auto-generated catch block
//				//Log.e(WEATHER_TAG, e.getMessage());
//				e.printStackTrace();
//			} catch (MalformedURLException e) {
//				//Log.e(WEATHER_TAG, e.getMessage());
//				e.printStackTrace();
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				//Log.e(WEATHER_TAG, e.getMessage());
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				//Log.e(WEATHER_TAG, e.getMessage());
//				e.printStackTrace();
//			} catch (Exception e) {
//				//Log.e(WEATHER_TAG, e.getMessage());
//				e.printStackTrace();
//			}
//		}

		return null;
	}
	
	public boolean addWeatherData(WeatherSet model,boolean isLocation){
		FileOutputStream stream = null;
		ObjectOutputStream objStream = null;
		boolean returnValue = false;
		try{
			if(null!=model){
			
				Map<String, WeatherSet> weathers = loadWeatherData(context);
				if(null==weathers)
					weathers = new HashMap<String, WeatherSet>();
				if(weathers.containsKey(model.getCityCode()+"|"+isLocation)){
					WeatherSet temp = weathers.get(model.getCityCode()+"|"+isLocation);
					model.setCityCode(temp.getCityCode());
					model.setProvinceCn(temp.getProvinceCn());
					model.setCityCn(temp.getCityCn());
					model.setAddcurrentMillis(temp.getAddcurrentMillis());
				}else{
					model.setAddcurrentMillis(System.currentTimeMillis());
					OrderUtil.setOrder(context, model.getCityCode(), OrderUtil.nextOrder(context));
				}
				weathers.put(model.getCityCode()+"|"+isLocation, model);
				stream = context.openFileOutput(WEATHER_DATA_FILE, Context.MODE_PRIVATE);
				objStream = new ObjectOutputStream(stream);
				objStream.writeObject(weathers);
				
//				save(context, weathers);
				returnValue = true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(null!=objStream) objStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(null!=stream) stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return returnValue;
	}

	/*
	 *  更新天气信息并保存
	 */
	public boolean updateWeatherData(Context context,String cityCode,String provinceCN,String cityCN,Long currentTimeMillis,boolean isLocation) {
//		Log.i("wangliqiang", cityCode);
	  
	    if(!NetworkControl.getNetworkState(context))
		{
//			Log.i(WEATHER_TAG, "Do updateWeatherData() --- No Network");
			return false;
		}
		WeatherSet ws = getData(context, true, cityCode, provinceCN, cityCN);
//		  Log.i("wangliqiang", "citycode:"+ws.getCityCode()+"cityCn:"+ws.getCityCn());
		if(ws == null)
		{
//			Log.i(WEATHER_TAG, "Do updateWeatherData() --- No Data");
			return false;
		}
		
		SharedPreferences weatherLocation = (SharedPreferences) context.getSharedPreferences(WEATHER_LOCATION_SETTING, Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = weatherLocation.edit();
		editor.putLong(LAST_UPDATE_TIME, System.currentTimeMillis());
		editor.commit();
		if(isWriteLogToSD){
	        StringBuilder builder=new StringBuilder();
	        builder.append("\n updateWeatherData");
	        if(ws.getCityCn()!=null)
	        	builder.append("  "+ws.getCityCn());
	        writeLogToSDCard(builder.toString(),WEATHER_LOG);
        }
//		Log.i(WEATHER_TAG, "Do updateWeatherData()");
		try
		{		
			if(null!=ws&&null!=ws.getCityCode()&&!ws.getCityCode().equals("")
					&&null!=ws.getCityCn()&&!ws.getCityCn().equals("")){
				Map<String, WeatherSet> weathers = loadWeatherData(context);
				if(null==weathers)
					weathers = new HashMap<String, WeatherSet>();
			
				if(null!=currentTimeMillis){
					ws.setAddcurrentMillis(currentTimeMillis);
				}
				String citycode=ws.getCityCode();
				if(weathers.containsKey(citycode))
				     weathers.remove(citycode);
				if (!weathers.containsKey(ws.getCityCode()+"|"+isLocation)&&!isLocation) {
				    long order=OrderUtil.nextOrder(context);
				    long autoOrder=OrderUtil.getAutoOrder(context);
				    String autoCity=OrderUtil.getAutoCity(context);
				    Boolean isAutoOrderSetted=OrderUtil.getIsAutoOrderSetted(context);
				    if(!isAutoOrderSetted&&!autoCity.equals("")&&autoOrder==0||!isAutoOrderSetted&&!autoCity.equals("")&&autoOrder==order-1){
				      OrderUtil.setOrder(context, ws.getCityCode(),autoOrder);
	                  SharedPreferences sp=context.getSharedPreferences("weatherLocation", Context.MODE_PRIVATE);
	                  String city=sp.getString("automatic", "");
	                  if(!city.equals("")){
	                      OrderUtil.setAutoOrder(context, order);
	                      OrderUtil.setOrder(context, city+"|true",order);
	                   }
				    }else{
				        OrderUtil.setOrder(context, ws.getCityCode(), OrderUtil.nextOrder(context));
				    }
				}else if(isLocation){
				    SharedPreferences sp=context.getSharedPreferences("weatherLocation", Context.MODE_PRIVATE);
				    String automatic=sp.getString("automatic", "");
				    if(!automatic.equals(ws.getCityCode())){
				        weathers.remove(automatic+"|"+isLocation);
				        if(!OrderUtil.getAutoCity(context).equals(""))
				            OrderUtil.remove(context, automatic+"|"+isLocation);
    				    Editor edit=sp.edit();
    				    edit.putString("automatic", ws.getCityCode());
    				    edit.commit();
    				    long order=OrderUtil.nextOrder(context);
    				    OrderUtil.setOrder(context, ws.getCityCode()+"|true",order );
    				    OrderUtil.setAutoOrder(context, order);
    				    sp = context.getSharedPreferences("all_city", Context.MODE_PRIVATE);
    				    editor = sp.edit(); 
    				    editor.remove(automatic+"|"+isLocation);
    				    editor.putString(ws.getCityCode()+"|"+isLocation, ws.getCityCn()+"|"+ws.getProvinceCn());
    				    editor.commit();
    				}
                    ws.setLocate(true);
				}
				if(!isLocation)
				    ws.setLocate(false);
				weathers.put(ws.getCityCode()+"|"+isLocation, ws);
											
				FileOutputStream stream = context.openFileOutput(WEATHER_DATA_FILE, Context.MODE_PRIVATE);
				ObjectOutputStream objStream = new ObjectOutputStream(stream);
				objStream.writeObject(weathers);
				objStream.close();
				stream.close();
//				Log.i(WEATHER_TAG, "Do updateWeatherData() Successful");
				
				sendMessageToWidget(context);
			}else{
//				Log.i(WEATHER_TAG, "Do updateWeatherData() errorful");
			}
			return true;
		}
		catch (FileNotFoundException e)
		{
//			Log.e(WEATHER_TAG, e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e)
		{
//			Log.e(WEATHER_TAG, e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e)
		{
//			Log.e(WEATHER_TAG, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 *  获取本地保存的天气信息
	 */
	public static Map<String,WeatherSet> loadWeatherData(Context context) {
		
		Map<String,WeatherSet> ws = null;
		try
		{
			String filePath = context.getFilesDir() + "/" + WEATHER_DATA_FILE;
			
			if((new File(filePath)).exists()) {

				FileInputStream stream = context.openFileInput(WEATHER_DATA_FILE);
				
				ObjectInputStream objStream = new ObjectInputStream(stream);
				ws = (Map<String,WeatherSet>) objStream.readObject();
				objStream.close();
				stream.close();
				if(ws != null)
				{
					Set<String> wskey = ws.keySet();
					Iterator<String> it = wskey.iterator();
					String key = null;
					WeatherSet weatherset = null;
					while(it.hasNext()){
						key = it.next();
						weatherset = ws.get(key);
						if(null!=weatherset){
							long updateTime = weatherset.getCurrentMillis();
							long now = System.currentTimeMillis();
							if(now - updateTime > 24*3600*1000)
							{
								weatherset.setWeatherCurrentCondition(null);
							}
							ws.put(key, weatherset);//?????
						}
					}
					
				}
			}
			
		}
		catch (FileNotFoundException e)
		{
			//Log.e(WEATHER_TAG, e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e)
		{
			//Log.e(WEATHER_TAG, e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e)
		{
			//Log.e(WEATHER_TAG, e.getMessage());
			e.printStackTrace();
		}
		
		if(ws == null)
		{
			//updateWeatherData();
			ws = new HashMap<String, WeatherSet>();
		}
		
		return ws;
	}
	
	public static String localeAddress(Context context){
	    String cont=null;
	    String city=null;
//	    HttpGet request = new HttpGet("http://api.dev.lewatek.com/weather/get_location?ip=220.181.108.178");
	    String requestUrl="http://api.lewaos.com/thinkpage/get_location";
	    String cellInfo=getCellInfo(context);
	    if(cellInfo!=null)
	        requestUrl=requestUrl+"?cellInfo="+cellInfo;
	    HttpGet request = new HttpGet(requestUrl);
	    String user_agent=buildUserAgent(context);
        if(user_agent!=null&&!user_agent.equals(""))
            request.addHeader("User-Agent",user_agent);
    	request.addHeader("accept-language", getLanguageHeader());
        HttpClient httpClient = NetworkControl.getHttpClient(context);
        try {
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && response.getEntity() != null) {
                cont = EntityUtils.toString(response.getEntity(), "UTF8");
            }else {
            }
            JSONObject json = new JSONObject(cont);
            JSONObject data = json.getJSONObject("result");
            city=data.getString("city");
            if(data.has("country")){
            	String country=data.getString("country");
            	if(country!=null){
            		SharedPreferences sp=context.getSharedPreferences(WEATHER_SHAREDPREFS_COMMON, Context.MODE_PRIVATE);
                	sp.edit().putString(LOCATION_COUNTRY, country).commit();
//                	if(WeatherControl.isCitiesShouldUpdate(LewaDbHelper.HOT_CITIES_DB, context))
//    					  WeatherControl.updateCityFromServer(context, WeatherControl.HOT_CITIES_URL, LewaDbHelper.HOT_CITIES_DB);
            	}
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return city;
	}

	/*
	 *  更新天气信息并保存
	 */
	public static void shanghaiWeatherData(Context context,String cityCode,String provinceCN,String cityCN,Long currentTimeMillis) {
		
		WeatherSet ws = getData(context, true, cityCode, provinceCN, cityCN);
		
//		SharedPreferences weatherLocation = (SharedPreferences) context.getSharedPreferences(WEATHER_LOCATION_SETTING, Context.MODE_WORLD_READABLE);
//		SharedPreferences.Editor editor = weatherLocation.edit();
//		editor.putLong(LAST_UPDATE_TIME, System.currentTimeMillis());
//		editor.commit();
	try
		{				
			if(null!=ws&&null!=ws.getCityCode()&&!ws.getCityCode().equals("")
					&&null!=ws.getProvinceCn()&&!ws.getProvinceCn().equals("")
					&&null!=ws.getCityCn()&&!ws.getCityCn().equals("")){
				Map<String, WeatherSet> weathers = loadshanghaiWeatherData(context);
				if(null==weathers)
					weathers = new HashMap<String, WeatherSet>();
			
				if(null!=currentTimeMillis){
					ws.setAddcurrentMillis(currentTimeMillis);
				}
				if (!weathers.containsKey(ws.getCityCode())) {
					OrderUtil.setOrder(context, ws.getCityCode(), OrderUtil.nextOrder(context));
				}
				weathers.put(ws.getCityCode(), ws);
											
				FileOutputStream stream = context.openFileOutput(SHANGHAI_WEATHER_DATA_FILE, Context.MODE_PRIVATE);
				ObjectOutputStream objStream = new ObjectOutputStream(stream);
				objStream.writeObject(weathers);
				objStream.close();
				stream.close();
				
				sendMessageToWidget(context);
			}else{
			}
		}
		catch (FileNotFoundException e)
		{
//			Log.e(WEATHER_TAG, e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e)
		{
//			Log.e(WEATHER_TAG, e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e)
		{
//			Log.e(WEATHER_TAG, e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*
	 *  获取本地保存的天气信息
	 */
	public static Map<String,WeatherSet> loadshanghaiWeatherData(Context context) {
		
		Map<String,WeatherSet> ws = null;
		try
		{
			String filePath = context.getFilesDir() + "/" + SHANGHAI_WEATHER_DATA_FILE;
			
			if((new File(filePath)).exists()) {

				FileInputStream stream = context.openFileInput(SHANGHAI_WEATHER_DATA_FILE);
				
				ObjectInputStream objStream = new ObjectInputStream(stream);
				ws = (Map<String,WeatherSet>) objStream.readObject();
				objStream.close();
				stream.close();
				if(ws != null)
				{
					Set<String> wskey = ws.keySet();
					Iterator<String> it = wskey.iterator();
					String key = null;
					WeatherSet weatherset = null;
					while(it.hasNext()){
						key = it.next();
						weatherset = ws.get(key);
						if(null!=weatherset){
							long updateTime = weatherset.getCurrentMillis();
							long now = System.currentTimeMillis();
							if(now - updateTime > 24*3600*1000)
							{
								weatherset.setWeatherCurrentCondition(null);
							}
							ws.put(key, weatherset);
						}
					}
					
				}
			}
			
		}
		catch (FileNotFoundException e)
		{
			//Log.e(WEATHER_TAG, e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e)
		{
			//Log.e(WEATHER_TAG, e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e)
		{
			//Log.e(WEATHER_TAG, e.getMessage());
			e.printStackTrace();
		}
		
		if(ws == null)
		{
			//updateWeatherData();
			ws = new HashMap<String, WeatherSet>();
		}
		return ws;
	}
	/*
	 * 设定定时更新天气
	 */
	public static void setWeatherUpdateTask(Context context,int minutes) {
		
		long timeMillis = 0;
		long roundMillis = 0;
		long nextMillis = 0;
		
		SharedPreferences weatherLocation = (SharedPreferences) context.getSharedPreferences(WEATHER_LOCATION_SETTING, Context.MODE_WORLD_READABLE);
			roundMillis = (long)(minutes*60*1000);
			timeMillis = System.currentTimeMillis();
			
			nextMillis = timeMillis + roundMillis;
			
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(WeatherControl.WEATHER_UPDATE_ACTION);
			intent.setClass(context, WeatherService.class);
			PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
			weatherLocation.edit().putBoolean(ALREADY_SET, true).commit();
			
			am.set(AlarmManager.RTC_WAKEUP, nextMillis, pendingIntent);
		
	}
	
	public static void sendMessageToWidget(Context context){
		OrderUtil.updateDefault(context);
		String widgetAction = "com.android.UPDATE_WIDGET";
		context.sendBroadcast(new Intent(widgetAction));
	}
	
	/*
	 * 取消定时任务
	 */
	public static void unsetWeatherUpdateTask(Context context) {
		
		SharedPreferences weatherLocation = (SharedPreferences) context.getSharedPreferences(WEATHER_LOCATION_SETTING, Context.MODE_WORLD_READABLE);
				
		weatherLocation.edit().putBoolean(ALREADY_SET, false).commit();
		
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(WeatherControl.WEATHER_UPDATE_ACTION);
		intent.setClass(context, WeatherService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
		
		am.cancel(pendingIntent);
	}
	
	public static boolean deleteCity(Context context,WeatherSet model,String city_code){
		Map<String, WeatherSet> temp = loadWeatherData(context);
		if(temp.containsKey(city_code+"|false")||temp.containsKey(city_code+"|true")){
			FileOutputStream stream = null;
			try{
			    if(!model.isLocate()){
			        temp.remove(city_code+"|false");
			    }else{
			        temp.remove(city_code+"|true");
			    }
				stream = context.openFileOutput(WEATHER_DATA_FILE, Context.MODE_PRIVATE);
				ObjectOutputStream objStream = new ObjectOutputStream(stream);
				objStream.writeObject(temp);
				objStream.close();
				stream.close();
				save(context, temp);
				return true;
			}catch (Exception e) {
				return false;
			}
		}else if(temp.containsKey(city_code)){
	        try {
                FileOutputStream stream = null;
                temp.remove(city_code);
                stream = context.openFileOutput(WEATHER_DATA_FILE, Context.MODE_PRIVATE);
                ObjectOutputStream objStream = new ObjectOutputStream(stream);
                objStream.writeObject(temp);
                objStream.close();
                stream.close();
                save(context, temp);
                return true;
            } catch (Exception e) {
                return false;
            }
		}else{
			return false;
		}
	}

	private static void save(Context context, Map<String, WeatherSet> weathers) {
		SharedPreferences sp = context.getSharedPreferences("all_city", Context.MODE_PRIVATE);
		Editor editor = sp.edit(); 
		editor.clear();
		for (Map.Entry<String, WeatherSet> entry : weathers.entrySet()) {
			String cityCode = entry.getKey();
			WeatherSet set = entry.getValue();
			String cityCn = set.getCityCn();
			String province = set.getProvinceCn();
			
			editor.putString(cityCode, cityCn + "|" + province);
		}
		editor.commit();
	}
	
	private void updateAutoUpdateSelectionUI() {
		onTextView.setTextColor(Color.parseColor("#ffffff"));
		offTextView.setTextColor(Color.parseColor("#515151"));
		if(isAutoUpdateSelected) {
			chkAutoUpdate.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.weather_button_switch_on));
			offTextView.setVisibility(View.INVISIBLE);
			onTextView.setVisibility(View.INVISIBLE);
		} else {
			chkAutoUpdate.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.weather_button_switch_off));
			offTextView.setVisibility(View.INVISIBLE);
			onTextView.setVisibility(View.INVISIBLE);
		}
		spiHour.setEnabled(isAutoUpdateSelected);
	}
	
	private void updateShowSelectionUI() {
		onTextViewShow.setTextColor(Color.parseColor("#ffffff"));
		offTextViewShow.setTextColor(Color.parseColor("#515151"));
		if(isShowSelected) {
			chkAutoUpdateShow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.weather_button_switch_on));
			offTextViewShow.setVisibility(View.INVISIBLE);//textview显示开关没什么用
			onTextViewShow.setVisibility(View.INVISIBLE);
		} else {
			chkAutoUpdateShow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.weather_button_switch_off));
			offTextViewShow.setVisibility(View.INVISIBLE);
			onTextViewShow.setVisibility(View.INVISIBLE);
		}
	}
	
	public static String getCityTimeIconNameFormatted(String iconName, String preName ,int tag) {
		 
		if(iconName.equals(""))
		{
			iconName = preName + "weather_default";
		}
		else
		{
			if (tag == 0) {//当天
				
				if(iconName.indexOf(".") > 0) {
					iconName = preName + "weather_city_time_" + new String(iconName.substring(0, iconName.lastIndexOf(".")));
				} else {
					iconName = preName + "weather_city_time_" + iconName;
				}
				
			}else {//预测
				if(iconName.indexOf(".") > 0) {
					iconName = preName + "weather_" + new String(iconName.substring(0, iconName.lastIndexOf(".")));
				} else {
					iconName = preName + "weather_" + iconName;
				}
			}	
		}
		return iconName;
	}
	
	public static String getCityTimeIconNameFormatted(String iconName) {
		return getCityTimeIconNameFormatted(iconName, "",0);
	}
	
	public static int getCityTimeIconId(Context context, String iconName, String preName, int tag){
		int id = 0;
		try
		{
			iconName = getCityTimeIconNameFormatted(iconName, preName ,tag);
			id = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return id;
	}
	
	public static int getCityTimeIconId(Context context, String iconName, int tag){
		return getCityTimeIconId(context, iconName, "", tag);
	}
	
	public static String getIconNameFormatted(String iconName, String preName) {
		if(iconName.equals(""))
		{
			iconName = preName + "weather_default";
		}
		else
		{
			if(iconName.indexOf(".") > 0) {
				iconName = preName + "weather_weather_" + new String(iconName.substring(0, iconName.lastIndexOf(".")));
			} else {
				iconName = preName + "weather_weather_" + iconName;
			}
		}
		return iconName;
	}
	
	public static int getIconId(Context context, String iconName, String preName){
		int id = 0;
		try
		{
			iconName = getIconNameFormatted(iconName, preName);
			id = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return id;
	}
	
	public static int getIconId(Context context, String iconName){
		return getIconId(context, iconName, "");
	}
	
	/*
	 * 从CityEntity中获取PROVINCE、PROVINCE_CODE、CITY、CITY_CODE
	 * 
	 * [旧]从strings.xml中获得城市信息
	 * 并分别解析给PROVINCE、PROVINCE_CODE、CITY、CITY_CODE数组
	 */
	public void getCityData(){	
		try
		{
			CityEntity cities = new CityEntity();
			PROVINCE = cities.getProvince(language);
//			PROVINCE_CODE = cities.getProvinceCode(language);
			CITY = cities.getCity(language);
			CITY_CODE = cities.getCityCode(language);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static String getIconName(int id) {
		String iconName = "";
		switch(id) {
		case 0:
			iconName = "sunny";
			break;
		case 1:
			iconName = "mostly_cloudy";
			break;
		case 2:
			iconName = "cloudy";
			break;
		case 3:
			iconName = "rain";
			break;
		case 4:
		case 5:
			iconName = "thunderstorm";
			break;
		case 6:
			iconName = "heavyrain";
			break;
		case 7:
		case 8:
		case 9:
			iconName = "cn_lightrain";
			break;
		case 10:
		case 11:
		case 12:
			iconName = "rain";
			break;
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
			iconName = "snow";
			break;
		case 18:
			iconName = "haze";
			break;
		case 19:
			iconName = "cn_heavyrain";
			break;
		case 20:
			iconName = "dust";
			break;
		case 21:
		case 22:
		case 23:
			iconName = "rain";
			break;
		case 24:
		case 25:
			iconName = "cn_heavyrain";
			break;
		case 26:
		case 27:
		case 28:
			iconName = "snow";
			break;
		case 29:
		case 30:
		case 31:
			iconName = "dust";
			break;
		default:
			iconName = "default";
			break;
		}
		iconName += ".png";
		return iconName;
	}
	
	public static String getCityTimeIconName(int id) {
		String iconName = "";
		switch(id) {
		case 0:
			iconName = "sunny";
			break;
		case 1:
			iconName = "mostly_cloudy";
			break;
		case 2:
			iconName = "cloudy";
			break;
		case 3:
			iconName = "rain";
			break;
		case 4:
			iconName = "thunderstorm";
			break;
		case 5:
			iconName = "heavyrain";
			break;
		case 6:
			iconName = "cn_lightrain";
			break;	
		case 7:
			iconName = "snow";
			break;
		case 8:
			iconName = "haze";
			break;
		case 9:
			iconName = "cn_heavyrain";
			break;
		case 10:
			iconName = "dust";
			break;	
		default:
			iconName = "default";
			break;
		}
		iconName += ".png";
		return iconName;
	}
	public static boolean isAbleToDoUpdateAction(Context context) {
        SharedPreferences weatherLocation = (SharedPreferences) context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING, Context.MODE_WORLD_READABLE);
        float updateRound = weatherLocation.getFloat(WeatherControl.WEATHER_UPDATE_ROUND, (float)4);
        long lastUpdateTime = weatherLocation.getLong(WeatherControl.LAST_UPDATE_TIME, 0);
        Boolean autoUpdate = weatherLocation.getBoolean(WeatherControl.WEATHER_AUTO_UPDATE, true);
        String cityCode = weatherLocation.getString(WeatherControl.WEATHER_CITY_CODE_DEFAULT, "");
        Map<String, WeatherSet> map = WeatherControl.loadWeatherData(context);
        
        boolean isupdate = false;
        WeatherSet model = map.get(cityCode);
        if(null!=model){
            lastUpdateTime = model.getCurrentMillis();
            if(null == model.getWeatherCurrentCondition())
                isupdate = true; 
        }
        
        if(!cityCode.equals("") && autoUpdate && (System.currentTimeMillis() - lastUpdateTime > (long)(updateRound*3600L*1000L) || System.currentTimeMillis() - lastUpdateTime > 24*3600*1000 || isupdate)) {
            return true;
        }
        
        return false;
    }
	
	public static boolean isAbleToDoUpdateAction(Context context,WeatherSet model) {
		SharedPreferences weatherLocation = (SharedPreferences) context.getSharedPreferences(WeatherControl.WEATHER_LOCATION_SETTING, Context.MODE_WORLD_READABLE);
		float updateRound = weatherLocation.getFloat(WeatherControl.WEATHER_UPDATE_ROUND, (float)4);
		long lastUpdateTime = weatherLocation.getLong(WeatherControl.LAST_UPDATE_TIME, 0);
		Boolean autoUpdate = weatherLocation.getBoolean(WeatherControl.WEATHER_AUTO_UPDATE, true);
//		String cityCode = weatherLocation.getString(WeatherControl.WEATHER_CITY_CODE_DEFAULT, "");
//	    Map<String, WeatherSet> map = WeatherControl.loadWeatherData(context);
		
	    boolean isupdate = false;
//	    WeatherSet model = map.get(cityCode);
	    long dif=-1;
	    if(null!=model){
	    	lastUpdateTime = model.getCurrentMillis();
	        String expires=model.getExpires();
	        if(expires!=null){
	            long expiresTime=WeatherControl.parseGmtTime(expires);
	            dif=System.currentTimeMillis()-expiresTime;
	        }
	    	if(null == model.getWeatherCurrentCondition())
	    		isupdate = true; 
	    }
		if(System.currentTimeMillis() - lastUpdateTime >=4*3600*1000 ||dif>=0&&System.currentTimeMillis() - lastUpdateTime >=1800*1000 ||isupdate) {
			return true;
		}
		
		return false;
	}
	 public static WeatherSet getDefaultWeatherData(Context context) {
	        Map<String,WeatherSet> ws = null;
	        try
	        {
	            String filePath = context.getFilesDir() + "/" + WEATHER_DATA_FILE;
	            
	            if((new File(filePath)).exists()) {

	                FileInputStream stream = context.openFileInput(WEATHER_DATA_FILE);
	                ObjectInputStream objStream = new ObjectInputStream(stream);
	                ws = (Map<String,WeatherSet>) objStream.readObject();
	                objStream.close();
	                stream.close();
	                SharedPreferences sharedPreferences=context.getSharedPreferences("weatherLocation", Context.MODE_PRIVATE);
	                String cityCode=sharedPreferences.getString("weatherCityCode_default", "");
	                cityCode=WeatherControl.buildCityCode(cityCode);
	                if(cityCode!=""){
	                   return  ws.get(cityCode+"|false")==null?ws.get(cityCode+"|true"): ws.get(cityCode+"|false");
	                }
	            }
	            
	        }
	        catch (FileNotFoundException e)
	        {
	            e.printStackTrace();
	        }
	        catch (IOException e)
	        {
	            e.printStackTrace();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return null;
	    }
	    
	
	public static boolean isWiFiActive(Context inContext) {
        WifiManager mWifiManager = (WifiManager) inContext
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
            return true;
        } else {
            return false;
        }
    }
	
	public static Boolean IsConnection(Context inContext) {
        ConnectivityManager connec = (ConnectivityManager) inContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED
                || connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING) {
            return true;
        } else if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED
                || connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

	public static void deleteAllCity(Context context) {
		context.deleteFile(WEATHER_DATA_FILE);
		SharedPreferences sp = context.getSharedPreferences(WEATHER_LOCATION_SETTING, Context.MODE_WORLD_READABLE);
		
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
		
		sp = context.getSharedPreferences(WEATHER_LOCATION_SETTING1, Context.MODE_WORLD_READABLE);
		
		editor = sp.edit();
		editor.clear();
		editor.commit();
		
		sp = context.getSharedPreferences(WEATHER_CURRENT, Context.MODE_WORLD_READABLE);
		
		editor = sp.edit();
		editor.clear();
		editor.commit();
		
		OrderUtil.clear(context);
		Intent intent = new Intent("com.when.android.calendar365.lewa.weather.WEATHER_NUMBER_TIME_CITY_CHANGE_CITY");
		intent.putExtra("main", true);
		context.sendBroadcast(intent);
	}
	
	public void getLocationAuto(final Context context) {
		
		if(isWriteLogToSD){
			writeLogToSDCard("\n locate address", WEATHER_LOG);
		}
	    Handler handler=new Handler();
	    Runnable r=null;
	    locationListener = new LocationListener() {
	        
	        @Override
	        public void onStatusChanged(String provider, int status, Bundle extras) {
	            // TODO Auto-generated method stub
	            
	        }
	        
	        @Override
	        public void onProviderEnabled(String provider) {
	            // TODO Auto-generated method stub
	            
	        }
	        
	        @Override
	        public void onProviderDisabled(String provider) {
	            // TODO Auto-generated method stub
	            
	        }
	        
	        @Override
	        public void onLocationChanged(Location location) {
	            // TODO Auto-generated method stub
	            mLocation=location;
	            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent()) {
	                // Since the geocoding API is synchronous and may take a while.  You don't want to lock
	                // up the UI thread.  Invoking reverse geocoding in an AsyncTask.
	                (new ReverseGeocodingTask(context)).execute(new Location[] {location});
	            }
	        }
	    };
        Location location=null;
        if(locationManager==null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(gpsEnabled)
            r=new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    releaseLocationManager();
                    if(mLocation==null){
                        if(isWiFiActive(context)||IsConnection(context)){
                            new LocalizedTask(context).execute();
                        }
                    }
                }
            };
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);  
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW); 
        String providerName = locationManager.getBestProvider(criteria, true);
        if(providerName!=null)
            location=locationManager.getLastKnownLocation(providerName);
        if(location!=null){
            long getTime=location.getTime();
            Calendar cal = Calendar.getInstance();
            int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
            int dstOffset = cal.get(Calendar.DST_OFFSET);
            cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
            long curTime=cal.getTimeInMillis();
            if(curTime-getTime<TWO_HOURS){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent()) {
                    // Since the geocoding API is synchronous and may take a while.  You don't want to lock
                    // up the UI thread.  Invoking reverse geocoding in an AsyncTask.
                    (new ReverseGeocodingTask(context)).execute(new Location[] {location});
                }
            }else if(gpsEnabled){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0, locationListener);
                if(r!=null)
                    handler.postDelayed(r, 5000);
            }else if(isWiFiActive(context)||IsConnection(context)){
                new LocalizedTask(context).execute();
            }
         }else {
           if(gpsEnabled){
               locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
               if(r!=null)
                   handler.postDelayed(r, 5000);
//               new LocalizedTask(context).execute();
           }else if(isWiFiActive(context)||IsConnection(context)){
               new LocalizedTask(context).execute();
           }
         }
    }
	
	public void getLocationAutoWIFI(){
        new LocalizedTask(context).execute();
	}
	    
    public class LocalizedTask extends AsyncTask<Void, Void, String>{
        private Context mContext;
        public LocalizedTask(Context context){
            mContext=context;
        }
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String cityString=WeatherControl.localeAddress(mContext);
            WeatherControl wcc=new WeatherControl(mContext);
            wcc.updateWeatherData(mContext, null, null, cityString, null,true);
            mContext.sendBroadcast(new Intent("com.lewa.weather.locate"));
            return null;
        }
    }
    
    private class ReverseGeocodingTask extends AsyncTask<Location, Void, Void> {
        Context mContext;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected Void doInBackground(Location... params) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

            Location loc = params[0];
            List<Address> addresses = null;
            try {
                // Call the synchronous getFromLocation() method by passing in the lat/long values.
                addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
//                addresses = geocoder.getFromLocation(31.203250000000004,121.59948833333334, 1);
            } catch (IOException e) {
                e.printStackTrace();
                // Update UI field with the exception.
//                Message.obtain(mHandler, UPDATE_ADDRESS, e.toString()).sendToTarget();
            }
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                try {
                    File file=new File(Environment.getExternalStorageDirectory()+"/address.txt");
                    RandomAccessFile file1 =new RandomAccessFile(file,"rwd");
                    file1.writeBytes(address.toString());
                    file1.close();
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                // Format the first line of address (if available), city, and country name.
//                String addressText = String.format("%s, %s, %s",
//                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
//                        address.getLocality(),
//                        address.getCountryName());
                String addressText=address.getLocality();
                if(addressText!=null){
                    WeatherControl wcc=new WeatherControl(mContext);
                    if(addressText.contains("市"))
                        addressText=addressText.substring(0, addressText.lastIndexOf("市"));
                    getCellInfo(mContext);
                    wcc.updateWeatherData(mContext, null, null, addressText, null,true);
                    mLocation=null;
                    mContext.sendBroadcast(new Intent("com.lewa.weather.locate"));
                }
                String country=address.getCountryName();
                if(country!=null){
                	SharedPreferences sp=mContext.getSharedPreferences(WEATHER_SHAREDPREFS_COMMON, Context.MODE_PRIVATE);
                	sp.edit().putString(LOCATION_COUNTRY, country).commit();
                	if(WeatherControl.isCitiesShouldUpdate(LewaDbHelper.HOT_CITIES_DB, context))
  					       WeatherControl.updateCityFromServer(context, WeatherControl.HOT_CITIES_URL, LewaDbHelper.HOT_CITIES_DB);
                }
                    
                // Update the UI via a message handler.
//                Message.obtain(mHandler, UPDATE_ADDRESS, addressText).sendToTarget();
            }
            return null;
        }
    }
    
    public  void releaseLocationManager(){
        if(locationManager!=null&&locationListener!=null)
            locationManager.removeUpdates(locationListener);
    }
    
    public static String getImageString(String condition){
    	if(condition==null)
    		return "";
        if(condition.contains("转")){
//            condition=condition.substring(0,condition.indexOf("转"));
            String firstCondition=condition.substring(0,condition.indexOf("转"));
            String secCondition=condition.substring(condition.indexOf("转")+1);
            int firIndex=WeatherControl.getWeather(firstCondition);
            int secIndex=WeatherControl.getWeather(secCondition);
            if(firIndex>secIndex){
                condition=firstCondition;
            }else{
                condition=secCondition;
            }
        }
        if(condition.contains("云")){
           return "cloudy";
        }else if(condition.contains("雷")){
            return "thunderstorm";
        }else if(condition.contains("雨")||condition.contains("雹")){
            if(condition.contains("雨")&&condition.contains("雹")){
                return "rainandhailstone";
            }else if(condition.contains("雨")){
                if(condition.contains("大雨")){
                    return "heavyrain";
                }else if(condition.contains("小雨")){
                   return "lightrain";
                }else if(condition.contains("阵雨")){
                    return "shower";
                }else if(condition.contains("雪")){
                   return "rainandsnow"; 
                }else{
                    return "heavyrain";
                }
            }else{
                return "hailstone";
            }
        }else if(condition.contains("晴")){
            return "sunshine";
        }else if(condition.contains("雾")||condition.contains("霾")){
            return "fog";
        }else if(condition.contains("阴")){
            return "shade";
        }else if(condition.contains("雪")){
            if(condition.contains("大雪")){
                return "heavysnow";
            }else if(condition.contains("小雪")){
                return "lightsnow";
            }else if(condition.contains("阵雪")){
                return "lightsnow";
            }else{
                return "heavysnow";
            }
        }else{
            return "";
        }
     }
    public static String getBgImageName(String condition,String lastCondition){
        if(condition!=null&&condition.contains("转")){
            String firstCondition=condition.substring(0,condition.indexOf("转"));
            String secCondition=condition.substring(condition.indexOf("转")+1);
            int firIndex=WeatherControl.getWeather(firstCondition);
            int secIndex=WeatherControl.getWeather(secCondition);
            if(firIndex>secIndex){
                condition=firstCondition;
            }else{
                condition=secCondition;
            }
        }else if(condition==null){
        	return "";
        }
        if(lastCondition!=null&&lastCondition.contains("转")){
            String firstCondition=lastCondition.substring(0,lastCondition.indexOf("转"));
            String secCondition=lastCondition.substring(lastCondition.indexOf("转")+1);
            int firIndex=WeatherControl.getWeather(firstCondition);
            int secIndex=WeatherControl.getWeather(secCondition);
            if(firIndex>secIndex){
                lastCondition=firstCondition;
            }else{
                lastCondition=secCondition;
            }
//            lastCondition=lastCondition.substring(0,lastCondition.indexOf("转"));
        }
        if(condition.contains("云")){
            if(lastCondition==null||lastCondition!=null&&!lastCondition.contains("云")){
              return "cloudy";
            }else{
                return "";
            }
        }else if(condition.contains("雷")){
            if(lastCondition==null||lastCondition!=null&&!lastCondition.contains("雷")){
                return "thunder";
            }else{
                return "";
            }
        }else if(condition.contains("雨")||condition.contains("雹")){
            if(lastCondition==null||lastCondition!=null&&!lastCondition.contains("雨")&&!lastCondition.contains("雹")||lastCondition!=null&&lastCondition.contains("雷")){
                return "rain";
            }else{
                return "";
            }
        }else if(condition.contains("晴")){
            if(lastCondition==null||lastCondition!=null&&!lastCondition.contains("晴")){
                return "sunshine";
            }else{
                return "";
            }
        }else if(condition.contains("雾")||condition.contains("霾")){
            if(lastCondition==null||lastCondition!=null&&!lastCondition.contains("雾")&&!lastCondition.contains("霾")){
                return "fog";
            }else{
                return "";
            }
        }else if(condition.contains("阴")){
            if(lastCondition==null||lastCondition!=null&&!lastCondition.contains("阴")){
                return "shade";
            }else{
                return "";
            }
        }else if(condition.contains("雪")){
            if(lastCondition==null||lastCondition!=null&&!lastCondition.contains("雪")){
                return "snow";
            }else{
                return "";
            }
        }else {
            return "";
        }
    }
    public static int getWeather(String condition){
        if(condition==null)
            return WEATHER_SUNSHINE;
        if(condition.contains("晴")){
            return WEATHER_SUNSHINE;
        }else if(condition.contains("阴")){
            return WEATHER_SHADE;
        }else if(condition.contains("云")){
            return WEATHER_CLOUDY;
        }else if(condition.contains("雾")){
            return WEATHER_FOG;
        }else if(condition.contains("霾")){
            return WEATHER_HAZE;
        }else if(condition.contains("雷")){
            return WEATHER_THUNDER_RAIN;
        }else if(condition.contains("雨")){
            if(condition.contains("阵雨")){
                return WEATHER_SHOWER;
            }else if(condition.contains("小雨")){
                return WEATHER_LIGHT_RAIN;
            }else if(condition.contains("中雨")){
                return WEATHER_MODE_RAIN;
            }else if(condition.contains("大雨")){
                return WEATHER_HEAVRY_RAIN;
            }else if(condition.contains("暴雨")){
                return WEATHER_TORRENT_RAIN;
            }else if(condition.contains("雪")){
                return WEATHER_RAIN_SNOW;
            }else if(condition.contains("雹")){
                return WEATHER_RAIN_HAIL;
            }else{
                return WEATHER_MODE_RAIN;
            }
        }else if(condition.contains("雪")){
            if(condition.contains("阵雪")){
                return WEATHER_SNOW_SHOWER;
            }else if(condition.contains("小雪")){
                return WEATHER_LIGHT_SNOW;
            }else if(condition.contains("中雪")){
                return WEATHER_MODE_SNOW;
            }else if(condition.contains("大雪")){
                return WEATHER_HEAVRY_SNOW;
            }else if(condition.contains("暴雪")){
                return WEATHER_TORRENT_SNOW;
            }else{
                return WEATHER_MODE_SNOW;
            }
        }else if(condition.contains("雹")){
            return WEATHER_HAIL;
        }else{
            return WEATHER_SUNSHINE;
        }
    }
    
    public static Date getDate(String expires){
        SimpleDateFormat format=new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss z",Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date=null;
        
        try {
            date = format.parse(expires);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }
    
    public static long parseGmtTime(String gmtTime) {
        try {
            SimpleDateFormat GMT_FORMAT = new SimpleDateFormat(
                    "EEE, d MMM yyyy HH:mm:ss z",
                     Locale.ENGLISH);
            return GMT_FORMAT.parse(gmtTime).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Date getGMT(){
        Date gmt8 = null;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"),Locale.ENGLISH);
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        day.set(Calendar.MONTH, cal.get(Calendar.MONTH));
        day.set(Calendar.DATE, cal.get(Calendar.DATE));
        day.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        day.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        day.set(Calendar.SECOND, cal.get(Calendar.SECOND));
        gmt8 = day.getTime();
        return gmt8;
    }
    
    public static int getTimeDif(Context context,String time){
        Calendar calendar=Calendar.getInstance();
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        String getMonth=time.substring(0, time.indexOf(context.getString(R.string.yue)));
        String getDay=time.substring(time.indexOf(context.getString(R.string.yue))+1, time.indexOf(context.getString(R.string.ri)));
        if(month+1==Integer.valueOf(getMonth)){
            return day-Integer.valueOf(getDay);
        }else{
            return 2;
        }
    }
    
    public static int getPmStatus(String pm){
        if(pm==null)
            return PM_LIGHT;
        int pmValue=Integer.parseInt(pm);
        if(pmValue<100){
            return PM_GOOD;
        }else if(pmValue>=100&&pmValue<200){
            return PM_LIGHT;
        }else if(pmValue>=200){
            return PM_SERIOUS;
        }
        return PM_LIGHT;
    }
    
    public static String buildUserAgent(Context context){
        if (!initUserAgent && context != null) {
            initUserAgent = true;
            StringBuilder s = new StringBuilder(256);
            s.append(USEER_AGENT_PREFIX);
            s.append(" (Android ").append(Build.VERSION.RELEASE);
            String model=Build.MODEL;
            s.append("; Model ").append(model.replace(" ", "_"));
            if (!initLewaVersion) {
                getLewaVersion();
            }
            if (lewaVersion != null && lewaVersion.length() > 0) {
                s.append("; ").append(lewaVersion);
            }
            s.append(") ");
            if (!initAppVersionCode) {
                getAppVersionCode(context);
            }
            if (appVersionCode != null) {
                s.append(context.getPackageName()).append("/").append(appVersionCode);
            }
            if (!initBiClientId) {
                getBiClientId(context);
            }
            if (biClientId != null && biClientId.length() > 0) {
                s.append(" ClientID/").append(biClientId);
            }
            return (userAgent = s.toString());
        }
        return  userAgent ;
     }
    
    public static String getBiClientId(Context context) {
        if (!initBiClientId && context != null) {
            initBiClientId = true;
            // get biclient id by lewa.bi.BIAgent.getBIClientId(Context context) method
            Class<?> demo = null;
            try {
                demo = Class.forName(CLASS_NAME_BIAGENT);
                Method method = demo.getMethod(METHOD_NAME_GET_CLIENT_ID, Context.class);
                return (biClientId = (String)method.invoke(demo.newInstance(), context));
            } catch (Exception e) {
                /**
                 * accept all exception, include ClassNotFoundException, NoSuchMethodException,
                 * InvocationTargetException, NullPointException
                 */
                e.printStackTrace();
            }
        }
        return biClientId;
    }
    
    public static String getAppVersionCode(Context context) {
        if (!initAppVersionCode && context != null) {
            initAppVersionCode = true;
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(context.getPackageName(), 0);
                    if (pi != null) {
                        return (appVersionCode = Integer.toString(pi.versionCode));
                    }
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return appVersionCode;
    }
    
    public static String getLewaVersion() {
        if (!initLewaVersion) {
            initLewaVersion = true;
            // get lewa os version by lewa.os.Build.LEWA_VERSION
            Class<?> demo = null;
            try {
                demo = Class.forName(CLASS_NAME_LEWA_BUILD);
                Field field = demo.getField(FIELD_NAME_LEWA_BUILD_VERSION);
                return lewaVersion = (String)field.get(demo.newInstance());
            } catch (Exception e) {
                /**
                 * accept all exception, include ClassNotFoundException, NoSuchFieldException, InstantiationException,
                 * IllegalArgumentException, IllegalAccessException, NullPointException
                 */
                e.printStackTrace();
            }
        }
        return lewaVersion;
    }
    class CellIDInfo {
        public int cellId;
        public String mobileCountryCode;
        public String mobileNetworkCode;
        public int locationAreaCode;
        public String radioType;

        public CellIDInfo() {
        }
    }
    
    public static String getCellInfo(Context context){
//        if(cellInfo==null){
            cellInfo="";
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
    
            CellLocation loc = tm.getCellLocation();
            if (loc instanceof GsmCellLocation) {
                GsmCellLocation gsmCellLocation= (GsmCellLocation) loc;
                if(gsmCellLocation!=null){
                    int cellId=gsmCellLocation.getCid();
                    int areaCode=gsmCellLocation.getLac();
                    if(cellId==-1&&areaCode==-1){
                        cellInfo="";
                    }else{
                        cellInfo=cellId+"_"+areaCode;
                    }
                }
            } else {
                CdmaCellLocation cdmaCellLocation=(CdmaCellLocation) loc;
                if(cdmaCellLocation!=null){
                    int cellId=cdmaCellLocation.getBaseStationId();
                    int areaCode=cdmaCellLocation.getNetworkId();
                    if(cellId==-1&&areaCode==-1){
                        cellInfo="";
                    }else{
                        cellInfo=cellId+"_"+areaCode;
                    }
                }
             }
//        }
        return cellInfo;
     }
    public static String buildCityCode(String citycode){
        if(citycode!=null&&citycode.contains("|"))
            citycode=citycode.substring(0, citycode.lastIndexOf("|"));
        return citycode;
    }
    public static boolean checkDataBase(){
        SQLiteDatabase db=null;
        try {
            String path=DB_PATH+DB_NAME;
            db=SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(db!=null)
            db.close();
        return db!=null?true:false;
    }
    
    public static boolean copyDataBase(Context context){
        InputStream is=null;
        OutputStream os=null;
        try {
            is=context.getAssets().open("com.lewa.weather");
            String outFileName=DB_PATH+DB_NAME;
            os=new FileOutputStream(outFileName);
            byte[] buffer=new byte[1024];
            int length;
            while((length=is.read(buffer))>0){
                os.write(buffer, 0, length);
            }
            os.flush();
            os.close();
            is.close();
            SharedPreferences sp=context.getSharedPreferences(WEATHER_SHAREDPREFS_COMMON, Context.MODE_PRIVATE);
            sp.edit().putLong(ALL_CITIES_LAST_UPDATE_TIME, System.currentTimeMillis()).commit();
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
           return false;
        } finally{
            try {
                if(is!=null)
                    is.close();
                if(os!=null)
                    os.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public static void updateCityFromServer(Context context,String url,String table){
    	if(url.equals(WeatherControl.HOT_CITIES_URL)){
    		SharedPreferences sp=context.getSharedPreferences(WEATHER_SHAREDPREFS_COMMON, Context.MODE_PRIVATE);
    		String location_country=sp.getString(LOCATION_COUNTRY, "");
    		url=url+"?country="+location_country;
    	}
        HttpGet request = new HttpGet(url);
        HttpClient httpClient = NetworkControl.getHttpClient(context);
        try {
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && response.getEntity() != null) {
                String cont = EntityUtils.toString(response.getEntity(), "UTF8");
                JSONObject json = new JSONObject(cont);
                JSONArray jsonArray=json.getJSONArray("result");
                SharedPreferences sp=context.getSharedPreferences(WEATHER_SHAREDPREFS_COMMON, Context.MODE_PRIVATE);
                if(json.has("layout"))
                	sp.edit().putInt("layout",  json.getInt("layout")).commit();
                LewaDbHelper dbHelper=new LewaDbHelper(context);
                SQLiteDatabase db=dbHelper.getWritableDatabase();
                db.beginTransaction();
                db.delete(table, null, null);
                dbHelper.creatCityDB(db, table);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject cityObj=jsonArray.getJSONObject(i);
                    ContentValues values=new ContentValues();
                    values.put("city_id", cityObj.getString("id"));
                    String name=cityObj.getString("name");
                    values.put("name", name);
                    if(cityObj.has("en"))
                       values.put("name_en", cityObj.getString("en"));
                    db.insert(table, "name", values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                dbHelper.closeDb();
                String expires=response.getFirstHeader("Expires").getValue();
                StringBuilder builder=new StringBuilder();
                if(table.equals(LewaDbHelper.HOT_CITIES_DB)){
                	sp.edit().putLong(HOT_CITIES_LAST_UPDATE_TIME, System.currentTimeMillis()).commit();
                	sp.edit().putString(HOT_CITIES_EXPIRES, expires).commit();
                	if(isWriteLogToSD){
	                	builder.append("\n"+LewaDbHelper.HOT_CITIES_DB);
	                	builder.append("  "+UPDATE);
	                	writeLogToSDCard(builder.toString(), WEATHER_LOG);
                	}
                }else if(table.equals(LewaDbHelper.ALL_CITIES_DB)){
                	sp.edit().putLong(ALL_CITIES_LAST_UPDATE_TIME, System.currentTimeMillis()).commit();
                	sp.edit().putString(ALL_CITIES_EXPIRES, expires).commit();
                	if(isWriteLogToSD){
	                	builder.append("\n"+LewaDbHelper.ALL_CITIES_DB);
	                	builder.append("  "+UPDATE);
	                	writeLogToSDCard(builder.toString(), WEATHER_LOG);
                	}
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * 去除string括号中的字符
     * @param string
     * @return
     */
    public static String removeBrackets(String string){
        if(string!=null&&string.contains("（")){
            int index=string.indexOf("（");
            string =string.substring(0, index);
        }else if(string!=null&&string.contains("(")){
        	 int index=string.indexOf("(");
             string =string.substring(0, index);
        }
        return string;
    }
    public static boolean correctCellInfo(Context context){
        getCellInfo(context);
        if(!TextUtils.isEmpty(cellInfo)){
            mLocation=null;
            if(locationManager==null)
                locationManager=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(gpsEnabled){
                if(correctListener==null)
                    correctListener=new LocationListener() {
                        
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            // TODO Auto-generated method stub
                            
                        }
                        
                        @Override
                        public void onProviderEnabled(String provider) {
                            // TODO Auto-generated method stub
                            
                        }
                        
                        @Override
                        public void onProviderDisabled(String provider) {
                            // TODO Auto-generated method stub
                            
                        }
                        
                        @Override
                        public void onLocationChanged(Location location) {
                            // TODO Auto-generated method stub
                            mLocation=location;
                        }
                    };
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0,correctListener );
                return true;
            }
        }
        return false;
    }
    
    public static void releaseCorrectListener(){
        if(locationManager!=null&&correctListener!=null)
            locationManager.removeUpdates(correctListener);
    }
    public static int dip2px(Context context, float dpValue) { 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int) (dpValue * scale + 0.5f); 
    }
    
    public static void recyleBitmap(Bitmap bitmap){
    	if(bitmap!=null&&!bitmap.isRecycled())
    		bitmap.recycle();
    }
    
    public static Bitmap createTextBitmap(Context context, String text,float size,int transy,boolean isSetShadow) {
		int shadow_radius=context.getResources().getInteger(R.integer.v5_widget_shadow_radius);
		int shadow_dx=context.getResources().getInteger(R.integer.v5_widget_shadow_dx);
		int shadow_dy=context.getResources().getInteger(R.integer.v5_widget_shadow_dy);

		final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
		Paint paint = new Paint();
		Rect rect = new Rect();              
		paint.getTextBounds(text, 0, text.length(), rect);
		paint.setTextSize(size);
		paint.setColor(Color.WHITE);
//		Typeface face = Typeface.createFromAsset (context.getAssets() , "NeoSans-Light.otf");
		Typeface face = Typeface.createFromFile (ANDROID_ROBOTO_FONT_FILE);
		paint.setTypeface(face);
		paint.setAntiAlias(true);
		
		if(isSetShadow)
			paint.setShadowLayer(shadow_radius, shadow_dx, shadow_dy, Color.DKGRAY);
		int translateY = (int) (transy*scale + 0.5f);
		
		float tempratureRangeWidth =paint.measureText(text);
		Bitmap bitmap = Bitmap.createBitmap((int)(tempratureRangeWidth + 0.5f), (int)(20*scale + 0.5f), Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(bitmap);
		canvasTemp.drawText(text, 0, translateY, paint);
		return bitmap;
	}
    
    public static boolean isBitmapNull(Bitmap bitmap){
    	if(bitmap==null||bitmap.isRecycled())
    		return true;
    	return false;
    }
    public static String buildPmCondition(String pmCondition,Context context){
    	if(pmCondition!=null&&pmCondition.length()==1){
    		if(pmCondition.equals(context.getString(R.string.v5_excellent))){
    			 return context.getString(R.string.v5_excellent_build);
    		}else if(pmCondition.equals(context.getString(R.string.v5_good))){
    			return context.getString(R.string.v5_good_build);
    		}
    	}
    	return pmCondition;
    }
    public static String buildCityName(String name){
    	if(TextUtils.isEmpty(name)){
    		return name;
    	}
    	if(WeatherControl.isLanguageEnUs()){
    		if(name.length()>9){
    			name=name.substring(0, 9)+"...";
    		}
    	}else{
    			if(name.length()>4){
    				name=name.substring(0, 3);
    				name=name.concat("...");
    			}else if(name.length()==2){
    				name=" "+name.substring(0, 1)+"  "+name.substring(1)+" ";
    			}
        }
    	return name;
    }
    
     public static String getLanguageHeader(){
    	StringBuilder builder=new StringBuilder();
    	builder.append(Locale.getDefault().getLanguage());
    	builder.append("-");
    	builder.append(Locale.getDefault().getCountry());
    	return builder.toString().toLowerCase();
    }
    
    public static boolean isLanguageZhCn(){
    	String defaultLanguage=getLanguageHeader();
    	return defaultLanguage.equalsIgnoreCase("zh-cn");
    }
    
    public static boolean isLanguageZhTw(){
    	String defaultLanguage=getLanguageHeader();
    	return defaultLanguage.equalsIgnoreCase("zh-tw");
    }
    public static boolean isLanguageEnUs(){
    	String defaultLanguage=getLanguageHeader();
    	return defaultLanguage.equalsIgnoreCase("en-us");
    }
    
    public  void updateAllWeathers(Context context){
    	try {
            final SharedPreferences weatherLocation = context
                    .getSharedPreferences(
                            WeatherControl.WEATHER_LOCATION_SETTING,
                            Context.MODE_PRIVATE);
            SharedPreferences allCities = (SharedPreferences) context.getSharedPreferences("all_city", Context.MODE_PRIVATE);
            Set<String> sets=allCities.getAll().keySet();
            for(String citycode:sets){
                if (citycode.length() > 0) {
                    boolean isLocation=false;
                    if(citycode.contains("true")){
                        String locateCity=OrderUtil.getLocateCityCN(context, citycode);
                        locateCity=WeatherControl.buildCityCode(locateCity);
                        String cityString=WeatherControl.localeAddress(context);
                        if(!cityString.contains(locateCity)){
                            updateWeatherData(context, null, null, cityString, null,true);
                            continue;
                        }
                        isLocation=true;
                    }
                    citycode=WeatherControl.buildCityCode(citycode);
                    updateWeatherData(context, citycode, null,
                            null, System.currentTimeMillis(),isLocation);
                } 
            }
            boolean alreadySet = weatherLocation.getBoolean("alreadySet", false);
            if(alreadySet)
                WeatherControl.unsetWeatherUpdateTask(context);
            WeatherControl.setWeatherUpdateTask(context,60);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean isCitiesShouldUpdate(String tag,Context context){
    	SharedPreferences sp=context.getSharedPreferences(WEATHER_SHAREDPREFS_COMMON, Context.MODE_PRIVATE);
    	long lastUpdateTime=0;
    	String expires="";
    	if(tag.equals(LewaDbHelper.HOT_CITIES_DB)){
    		lastUpdateTime=sp.getLong(HOT_CITIES_LAST_UPDATE_TIME, 0);
    		expires=sp.getString(HOT_CITIES_EXPIRES, "");
    	}else if(tag.equals(LewaDbHelper.ALL_CITIES_DB)){
    		lastUpdateTime=sp.getLong(ALL_CITIES_LAST_UPDATE_TIME, 0);
    		expires=sp.getString(ALL_CITIES_EXPIRES, "");
    	}
    	 if(!TextUtils.isEmpty(expires)){
	            long expiresTime=WeatherControl.parseGmtTime(expires);
	            long dif=System.currentTimeMillis()-expiresTime;
	            if(System.currentTimeMillis()-lastUpdateTime>=ONE_DAY*30||dif>=0&&System.currentTimeMillis()-lastUpdateTime>=ONE_DAY)
	            	return true;
	            return false;
	     }else if(TextUtils.isEmpty(expires)&&lastUpdateTime==0&&System.currentTimeMillis()-lastUpdateTime>=ONE_DAY||TextUtils.isEmpty(expires)&&lastUpdateTime!=0&&System.currentTimeMillis()-lastUpdateTime>=ONE_DAY*15){
	    	 return true;
	     }
    	 return false;
    }
    
    public static boolean isCountryCN(String country){
    	if(country!=null&&country.equalsIgnoreCase("china")||country!=null&&country.equals("中国"))
    		return true;
    	return false;
    }
    public static String removeBlank(String string){
    	if(!TextUtils.isEmpty(string)){
    		string=string.replaceAll(" ", "");
    	}
    	return string;
    }
    
    public static void writeLogToSDCard(String str,String filename){
    	String path=Environment.getExternalStorageDirectory().getAbsolutePath();
    	String state=Environment.getExternalStorageState();
    	if(state.equals(Environment.MEDIA_MOUNTED)){
	    	File file=new File(path+File.separator+filename);
	    	FileOutputStream outputStream=null;
	    	OutputStreamWriter writer=null;
	    	try {
	    	if(!file.exists())
	    		file.createNewFile();
		    	outputStream=new FileOutputStream(file, true);
		    	writer=new OutputStreamWriter(outputStream);
		    	 SimpleDateFormat simpleDateFormat=DataFormatControl.getFormatter();
	             String time=simpleDateFormat.format(new Date());
	             str=str+" "+time;
				writer.write(str);
				writer.close();
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					if(writer!=null)
						writer.close();
					if(outputStream!=null)
						outputStream.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
      }
    }
    
    public static final String MOCK_GPS="mock_gps";
    public static Location mLocation=null;
    private static final long TWO_HOURS = 1000 * 60 * 60 * 2;
    private  static LocationManager locationManager;
    private  static LocationListener locationListener;
    private static boolean               initUserAgent                 = false;
    private static boolean               initBiClientId                = false;
    private static boolean               initAppVersionCode            = false;
    private static boolean               initLewaVersion               = false;
    public static final String           USEER_AGENT_PREFIX            = "LewaApi/1.0-1";
    public static final String           CLASS_NAME_BIAGENT            = "lewa.bi.BIAgent";
    public static final String           METHOD_NAME_GET_CLIENT_ID     = "getBIClientId";
    public static final String           CLASS_NAME_LEWA_BUILD         = "lewa.os.Build";
    public static final String           FIELD_NAME_LEWA_BUILD_VERSION = "LEWA_VERSION";
    private static String                userAgent                     = null;
    /** biclient id defined by lewa.bi.BIAgent.getBIClientId(Context context) method **/
    private static String                biClientId                    = null;
    /** android:versionCode in AndroidManifest.xml **/
    private static String                appVersionCode                = null;
    /** lewa os version defined by lewa.os.Build.LEWA_VERSION **/
    private static String                lewaVersion                   = null;
    private static String                cellInfo                      = null;
    private static String DB_PATH="/data/data/com.lewa.weather/databases/";
    private static String DB_NAME="com.lewa.weather";
    private static LocationListener correctListener;
    private static String HOT_CITIES_LAST_UPDATE_TIME="hot_cities_last_update_time";
    private static String ALL_CITIES_LAST_UPDATE_TIME="all_cities_last_update_time";
    private static String HOT_CITIES_EXPIRES="hot_cities_expires";
    private static String ALL_CITIES_EXPIRES="ALL_cities_expires";
    public static String WEATHER_SHAREDPREFS_COMMON="weatherLocation";
    private static final long ONE_DAY = 1000 * 60 * 60 * 24;
    public static String LOCATION_COUNTRY="location_country";
    public static String UPDATE="update";
    public static String WEATHER_LOG="weather.txt";
    public static boolean isWriteLogToSD=false;
    private static final String ANDROID_ROBOTO_FONT_FILE = "/system/fonts/Roboto-Light.ttf";
}
