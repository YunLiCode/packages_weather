package com.lewa.weather.control;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.R;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.lewa.weather.entity.NetType;

public class NetworkControl {	
	public static final String CLIENT_USER_AGENT = "Android365";

	public static boolean getNetworkState(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo(); 
		
		if(info != null)
		{
			return info.isAvailable();
		}
		return false;
	}
    
	public static final NetType getNetType(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager == null) return null; 
		
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if(info == null) return null;
		
		String type = info.getTypeName();
		
		if (type.equalsIgnoreCase("WIFI")) {
			return null;
		} else if(type.equalsIgnoreCase("MOBILE")) {
			String proxyHost = android.net.Proxy.getDefaultHost();   
	        if (proxyHost != null && !proxyHost.equals("")) {  
	        	NetType netType = new NetType();
	            netType.setProxy(proxyHost);
	        	netType.setPort(android.net.Proxy.getDefaultPort());
	        	netType.setWap(true);
	        	Log.v("tag", "WAP Network  proxy=" + proxyHost + "  port=" + android.net.Proxy.getDefaultPort());
	        	return netType;
	        }
		}
		return null;
	}
	
	public static final HttpClient getHttpClient(Context context) {
		HttpClient httpClient = new DefaultHttpClient();
		
		httpClient.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 6*1000);
		httpClient.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, 6*1000);
//		httpClient.getParams().setParameter(HttpProtocolParams.USER_AGENT, CLIENT_USER_AGENT + "/" + context.getString(R.string.channel) + " " + context.getString(R.string.mversion));
		
		// 判断Wap网络并设置代理
		NetType netType = getNetType(context);
		if (netType != null && netType.isWap()) {
			HttpHost proxy = new HttpHost(netType.getProxy(), netType.getPort());
			httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
		}
		
		return httpClient;
	}
	
	public static final HttpURLConnection getHttpURLConnection(Context context, String url) {
		URL realUrl;
		HttpURLConnection conn = null;
		try {
			// 打开和URL之间的连接
			realUrl = new URL(url);
			
			// 判断Wap网络并设置代理
			NetType netType = NetworkControl.getNetType(context);
			if (netType != null && netType.isWap()) {
				Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(netType.getProxy(), netType.getPort()));
				conn = (HttpURLConnection) realUrl.openConnection(proxy);
			} else {
				conn = (HttpURLConnection) realUrl.openConnection();
			}
			if(url.contains("365rili.com"))
				conn.setRequestProperty("user-agent", CLIENT_USER_AGENT + "/" + context.getString(R.string.cancel) + " " + context.getString(R.string.cancel));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return conn;
	}
	
	public static HttpPost getHttpPost(String url)
	{
		HttpPost httpPost = new HttpPost(url);
		//httpPost.setHeader("Connection", "close");
		return httpPost;
	}
	
	public static HttpGet getHttpGet(String url)
	{
		HttpGet httpGet = new HttpGet(url);
		//httpGet.setHeader("Connection", "close");
		return httpGet;
	}
}
