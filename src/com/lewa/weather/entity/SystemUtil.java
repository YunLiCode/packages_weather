package com.lewa.weather.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Looper;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.lewa.weather.control.NetworkControl;
import com.lewa.weather.entity.GoogleMapCityDetail.AddressDetails;
import com.lewa.weather.entity.GoogleMapCityDetail.AdministrativeArea;
import com.lewa.weather.entity.GoogleMapCityDetail.Country;
import com.lewa.weather.entity.GoogleMapCityDetail.Placemark;
import com.lewa.weather.entity.GpsTask.GpsData;
import com.lewa.weather.entity.IAddressTask.MLocation;

public class SystemUtil {

	public static boolean isWiFiActive(Context inContext) {
		Context context = inContext.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getTypeName().equals("WIFI")
							&& info[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean GPS_DONE = false;
	public static String GPS_RES = null;

	/**
	 * 通过GPS得到城市名
	 * 
	 * @param context
	 *            一個Activity
	 * @return 城市名
	 */
	@SuppressWarnings("unchecked")
	public String getcityName(final Context context) {

		if (isWiFiActive(context)) {// WIFI Connected
			MLocation location = null;
			try {
				location = new AddressTask(context, IAddressTask.DO_WIFI)
						.doWifiPost();
			} catch (Exception e) {
				return null;
			}

			if (location == null) {
				return null;
			}
			Log.i("FetchCity Via Wifi", location.City);
			if (location.City.endsWith("市")) {
				return location.City.substring(0, location.City.length() - 1);
			}
			return location.City;
		}

		LocationManager alm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {// GPS
																					// opened
			GPS_RES = null;

			Looper.prepare();
			GpsTask gpstask = new GpsTask(context, new GpsTaskCallBack() {

				public void gpsConnectedTimeOut() {
					GPS_DONE = true;
					Looper.myLooper().quit();
				}

				public void gpsConnected(GpsData gpsdata) {
					MLocation location = null;
					try {
						location = new AddressTask(context, IAddressTask.DO_GPS)
								.doGpsPost(gpsdata.getLatitude(),
										gpsdata.getLongitude());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (location != null)
						GPS_RES = location.City;
					GPS_DONE = true;
					Looper.myLooper().quit();
				}

			}, 3000);
			gpstask.execute();
			Looper.loop();

			while (!GPS_DONE) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (GPS_RES == null) {
				return null;
			}

			Log.i("FetchCity Via GPS", GPS_RES);

			if (GPS_RES.endsWith("市")) {
				return GPS_RES.substring(0, GPS_RES.length() - 1);
			}
			return GPS_RES;
		}

		if (!NetworkControl.getNetworkState(context)) {
			return null;
		}
		tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		CellLocation loc = tm.getCellLocation();

		if (loc instanceof GsmCellLocation) {
			String ret = solveGSM((GsmCellLocation) loc);

			return ret;
		} else {
			String ret = solveCDMA((CdmaCellLocation) loc);
			return ret;
		}
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

	private String solveCDMA(CdmaCellLocation location) {
		int sid = location.getSystemId();// 系统标识 mobileNetworkCode
		int bid = location.getBaseStationId();// 基站小区号 cellId
		int nid = location.getNetworkId();// 网络标识 locationAreaCode

		Log.i("sid:", "" + sid);
		Log.i("bid:", "" + bid);
		Log.i("nid:", "" + nid);
		ArrayList<CellIDInfo> CellID = new ArrayList<CellIDInfo>();
		CellIDInfo info = new CellIDInfo();
		info.cellId = bid;
		info.locationAreaCode = nid;
		info.mobileNetworkCode = String.valueOf(sid);
		info.mobileCountryCode = tm.getNetworkOperator().substring(0, 3);
		info.mobileCountryCode = tm.getNetworkOperator().substring(3, 5);
		info.radioType = "cdma";
		CellID.add(info);
		Log.d("cellId:", "" + info.cellId);
		Log.d("locationAreaCode:", "" + info.locationAreaCode);
		Log.d("mobileNetworkCode:", info.mobileNetworkCode);
		Log.d("mobileCountryCode:", info.mobileCountryCode);

		return callGear(CellID);
	}

	private String callGear(ArrayList<CellIDInfo> cellID) {
		if (cellID == null)
			return null;

		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://www.google.com/loc/json");
		JSONObject holder = new JSONObject();

		try {
			holder.put("version", "1.1.0");
			holder.put("host", "maps.google.com");
			holder.put("home_mobile_country_code",
					cellID.get(0).mobileCountryCode);
			holder.put("home_mobile_network_code",
					cellID.get(0).mobileNetworkCode);
			holder.put("radio_type", cellID.get(0).radioType);
			holder.put("request_address", true);
			holder.put("address_language", "zh_CN");

			JSONObject data, current_data;
			JSONArray array = new JSONArray();

			current_data = new JSONObject();
			current_data.put("cell_id", cellID.get(0).cellId);
			current_data.put("location_area_code",
					cellID.get(0).locationAreaCode);
			current_data.put("mobile_country_code",
					cellID.get(0).mobileCountryCode);
			current_data.put("mobile_network_code",
					cellID.get(0).mobileNetworkCode);
			current_data.put("age", 0);
			current_data.put("signal_strength", -60);
			current_data.put("timing_advance", 5555);
			array.put(current_data);

			holder.put("cell_towers", array);

			StringEntity se = new StringEntity(holder.toString());
			Log.e("Location send", holder.toString());
			post.setEntity(se);
			HttpResponse resp = client.execute(post);

			HttpEntity entity = resp.getEntity();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					entity.getContent()));
			StringBuffer sb = new StringBuffer();
			String result = br.readLine();
			while (result != null) {

				sb.append(result);
				result = br.readLine();
			}
			data = new JSONObject(sb.toString());
			data = (JSONObject) data.get("location");
			data = (JSONObject) data.get("address");
			String city = data.getString("city");

			if (city != null && city.endsWith("市")) {
				city = city.substring(0, city.length() - 1);
				return city;
			}
			return city;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String solveGSM(GsmCellLocation s) {

		int cid = s.getCid();
		int lac = s.getLac();
		int mcc = Integer.valueOf(tm.getNetworkOperator().substring(0, 3));
		int mnc = Integer.valueOf(tm.getNetworkOperator().substring(3, 5));

		try {
			JSONObject j = new JSONObject();
			j.put("version", "1.1.0");
			j.put("host", "maps.google.com");
			j.put("request_address", true);
			j.put("radio_type", "gsm");

			if (mcc == 460) {
				j.put("address_language", "zh_CN");
			} else {
				j.put("address_language", "en_US");
			}

			JSONArray ar = new JSONArray();
			JSONObject o = new JSONObject();

			o.put("cell_id", cid);
			o.put("location_area_code", lac);
			o.put("mobile_country_code", mcc);
			o.put("mobile_network_code", mnc);

			ar.put(o);
			j.put("cell_towers", ar);

			DefaultHttpClient client = new DefaultHttpClient();

			HttpPost post = new HttpPost("http://www.google.com/loc/json");
			StringEntity se = new StringEntity(j.toString());
			post.setEntity(se);
			HttpResponse resp = client.execute(post);
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity resEntityGet = resp.getEntity();
				String sourceString = "";
				if (resEntityGet != null)

					sourceString = new String(
							EntityUtils.toString(resEntityGet));

				JSONObject data = new JSONObject(sourceString);
				data = (JSONObject) data.get("location");
				data = (JSONObject) data.get("address");
				String city = data.getString("city");

				if (city != null && city.endsWith("市")) {
					city = city.substring(0, city.length() - 1);
					return city;
				}
				return city;

			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	TelephonyManager tm;

	// 从google获取城市的名字
	private String putCity(String str) {
		GoogleMapCityDetail map = new GoogleMapCityDetail();
		String returnValue = "";
		try {
			JSONObject object = new JSONObject(str);

			JSONArray array = object.getJSONArray("Placemark");
			ArrayList<Placemark> maplist = new ArrayList<Placemark>();

			Placemark placemark = map.new Placemark();

			object = array.getJSONObject(0);

			object = object.getJSONObject("AddressDetails");
			AddressDetails details = map.new AddressDetails();

			object = object.getJSONObject("Country");
			Country country = map.new Country();

			object = object.getJSONObject("AdministrativeArea");
			AdministrativeArea area = map.new AdministrativeArea();
			area.setName(object.getString("AdministrativeAreaName"));

			country.setArea(area);

			details.setCountry(country);

			placemark.setAddressDetails(details);

			maplist.add(placemark);

			map.setList(maplist);

			returnValue = map.getList().get(0).getAddressDetails().getCountry()
					.getArea().getName();

		} catch (JSONException e) {
			e.printStackTrace();
			returnValue = "";
		}

		return returnValue;
	}

	/**
	 * 检查wifi和3G是否打开
	 * */
	public static boolean checkNetworkInfo(Context context) {
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// mobile 3G Data Network
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		// wifi
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();

		// 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
		if (mobile == State.CONNECTED || mobile == State.CONNECTING)
			return true;
		if (wifi == State.CONNECTED || wifi == State.CONNECTING)
			return true;

		return false;
		// startActivity(new
		// Intent(Settings.ACTION_WIRELESS_SETTINGS));//进入无线网络配置界面
		// startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
		// //进入手机中的wifi网络设置界面

	}

}
