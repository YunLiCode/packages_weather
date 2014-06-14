package com.lewa.weather;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnCloseListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lewa.weather.adapters.AddCityAdapter;
import com.lewa.weather.adapters.HotCityAdapter;
import com.lewa.weather.adapters.SearchCityAdapter;
import com.lewa.weather.control.NetworkControl;
import com.lewa.weather.entity.City;
import com.lewa.weather.entity.CityEntity;
import com.lewa.weather.entity.WeatherControl;
import com.lewa.weather.provider.LewaDbHelper;
import com.lewa.weather.R;

public class SelectCityActivity extends Activity implements OnItemClickListener{

	private static List<City> cities = null;

	private Button mTitleButton;
	private Button mClearButton;

	private ImageView mLocateButton;

	private EditText mQueryText;

	private ListView mListVew;
	
	private HotCityAdapter adapter1;
	private SearchCityAdapter adapter2;
	
	private TextView mTitleText;
	private String mSearchStr;
	private InputMethodManager inputMethodManager;
	private long toastLastShowTime;
	 private BroadcastReceiver receiver=new BroadcastReceiver() {
	        
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            // TODO Auto-generated method stub
	         String action=intent.getAction();
	         if(action.equals("com.lewa.weather.locate")){
//	             finish();
	             if(dialog!=null&&dialog.isShowing())
	                 dialog.cancel();
	                 Intent intent2=new Intent(getApplicationContext(), LewaWeatherSetting.class);
	                 startActivity(intent2);
	                 finish();
	         }
	        }
	    };
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_main);
		dbHelper = new LewaDbHelper(getApplicationContext());
		dbHelper.createDataBase(getApplicationContext());
		SharedPreferences sp=this.getSharedPreferences(WeatherControl.WEATHER_SHAREDPREFS_COMMON, Context.MODE_PRIVATE);
		layout = sp.getInt("layout",0 );
		location_country = sp.getString(WeatherControl.LOCATION_COUNTRY, "");
		initUI();
		setActionBar();
		IntentFilter filter=new IntentFilter();
        filter.addAction("com.lewa.weather.locate");
        registerReceiver(receiver, filter);
		cityCount = getIntent().getIntExtra("count", 0);
		if(inputMethodManager==null)
            inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	}
	
	private void setActionBar(){
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);   
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customActionBarView = inflater.inflate(R.layout.search_action_bar, null);        
        actionBar.setTitle(" ");
        
        
        mSearchView = (SearchView) customActionBarView.findViewById(R.id.search_view);
        mSearchView.setVisibility(View.VISIBLE);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setQueryHint(getString(R.string.entercity));
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(mQueryTextListener);
        mSearchView.setOnCloseListener(new OnCloseListener() {
            
            @Override
            public boolean onClose() {
                // TODO Auto-generated method stub
                return true;
            }
        });
        mSearchView.onActionViewExpanded();
//        mSearchView.setQuery(null, true);
//        mSearchView.setFocusableInTouchMode(true);
        int searchViewWidth = this.getResources().getDimensionPixelSize(
                R.dimen.search_view_width);
        if (searchViewWidth == 0) {
            searchViewWidth = LayoutParams.MATCH_PARENT;
        }
        actionBar.setCustomView(customActionBarView, new LayoutParams(
                searchViewWidth, LayoutParams.WRAP_CONTENT));
//        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
	
	SearchView.OnQueryTextListener mQueryTextListener = new SearchView.OnQueryTextListener() {
        public boolean onQueryTextSubmit(String query) {
            return true;
        }
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                   mSearchStr = "*";
                }else {
                    //mEmptyTextView.setVisibility(View.GONE);
                    mSearchStr = newText;
                }
                updateView(newText);
            return false;
        }
    };

	private void updateView(String filter) {
		if(TextUtils.isEmpty(location_country)&&WeatherControl.isLanguageEnUs())
			mTitleText.setVisibility(View.GONE);
		if (filter.length() == 0) {
			mTitleText.setText(getString(R.string.weather_hot_city));
			mListVew.setAdapter(adapter1);
		} else {
			mTitleText.setVisibility(View.VISIBLE);
			mTitleText.setText(getString(R.string.weather_search_city));	
			adapter2.filter(filter);
			mListVew.setAdapter(adapter2);
			mListVew.setOnItemClickListener(this);
		}
	}
	
	private void initUI() {

		mListVew = (ListView) findViewById(R.id.city_list);
		mListVew.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if(inputMethodManager!=null&&SelectCityActivity.this.getCurrentFocus()!=null)
                    inputMethodManager.hideSoftInputFromWindow((SelectCityActivity.this.getCurrentFocus().getWindowToken()),InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
		mTitleText = (TextView)findViewById(R.id.titleText);
		if(TextUtils.isEmpty(location_country)&&WeatherControl.isLanguageEnUs())
			mTitleText.setVisibility(View.GONE);
		if (cities == null||cities.size()<=0) {
		    cities =dbHelper.getHotCities();
		    if(cities.size()==0){
		    	WeatherControl.copyDataBase(getApplicationContext());
				cities =dbHelper.getHotCities();
		    }
		}
		
		adapter1 = new HotCityAdapter(this, cities);
		
		adapter2 = new SearchCityAdapter(this);
//		adapter2.setOnSelectCityListener(onSelectCityListener);
		if(adapter1.columnNum==3){
			mListVew.setDivider(null);
			mTitleText.setGravity(Gravity.CENTER);
			mTitleText.setTextSize(18f);
			adapter1.setOnSelectCityListener(onSelectCityListener);
		}else{
			mListVew.setOnItemClickListener(this);
		}
		mListVew.setAdapter(adapter1);
	}

	OnClickListener onSelectCityListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int pos = (Integer) v.getTag();
		    disposeClick(v,pos);
		}
	};
	private void disposeClick(View v,int position) {
		if(inputMethodManager!=null&&SelectCityActivity.this.getCurrentFocus()!=null)
	        inputMethodManager.hideSoftInputFromWindow((SelectCityActivity.this.getCurrentFocus().getWindowToken()),InputMethodManager.HIDE_NOT_ALWAYS);
	    if(cityCount>=15){
	        if(System.currentTimeMillis()-toastLastShowTime>2000){
		        Toast.makeText(getApplicationContext(),getResources().getString(R.string.sorry_not_insert), 0).show();
		        toastLastShowTime=System.currentTimeMillis();
	        }
	        return;
	    }
		if (v.getTag() != null) {
			AddCityAdapter adapter = (AddCityAdapter)mListVew.getAdapter();
			City city=adapter.getCity(position);
			if(city==null)
			    return;
			String name="";
			if(WeatherControl.isLanguageZhCn()||WeatherControl.isLanguageZhTw()){
			      name=city.getName();
			}else if(WeatherControl.isLanguageEnUs()){
				 name=city.getName_en();
			}
			String cityCode=city.getCity_id();
			if (!city.isAdded()) {
				if(HotCityAdapter.columnNum!=1)
					v.setBackgroundResource(R.drawable.weather_city_selected);
				if(adapter.getName().equals("HotCityAdapter")&&name!=null&&name.equals(getApplicationContext().getString(R.string.auto_location))){
				    if(NetworkControl.getNetworkState(getApplicationContext())){
					    WeatherControl wcc=new WeatherControl(getApplicationContext());
					    wcc.getLocationAuto(getApplicationContext());
					    showDialog(SelectCityActivity.this);
				    }else{
				        Toast.makeText(getApplicationContext(), getResources().getText(R.string.no_net), 0).show();
				    }
				}else{
					if(name!=null&&cityCode!=null){
					    Intent intent = new Intent();
    					intent.putExtra("city", name);
    					intent.putExtra("cityCode", cityCode);
    					setResult(LewaWeather.RESULT_NEW_CITY, intent);
    					SharedPreferences sp = getApplicationContext().getSharedPreferences("all_city", Context.MODE_PRIVATE);
    			        Editor editor = sp.edit(); 
    			        editor.putString(cityCode, name);
    			        editor.commit();
    			        finish();
					}
				}
			}
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // TODO Auto-generated method stub
	    switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	
	public void showDialog(final Context context){
	    AlertDialog.Builder builder=new AlertDialog.Builder(context);
	    builder.setTitle(getResources().getString(R.string.app_name));
	    builder.setMessage(getResources().getString(R.string.location_wait));
	    builder.setNegativeButton(getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
               WeatherControl wc=new WeatherControl(context);
               wc.releaseLocationManager();
               if(WeatherControl.isWiFiActive(context)||WeatherControl.IsConnection(context))
                   wc.getLocationAutoWIFI();
               dialog.cancel();
            }
        } );
	    dialog = builder.create();
	    dialog.show();
	}
	@Override
	protected void onStop() {
	    // TODO Auto-generated method stub
	    WeatherControl wc=new WeatherControl(getApplicationContext());
        wc.releaseLocationManager();
	    super.onStop();
	}
	
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
//        if(adapter1!=null)
//            adapter1.clear();
        if(dbHelper!=null){
            dbHelper.closeDb();
            dbHelper.close();
        }
        unregisterReceiver(receiver);
        super.onDestroy();
    }
    private SearchView mSearchView;
    private int cityCount;
    private AlertDialog dialog;
    private LewaDbHelper dbHelper;

	private int layout;

	private String location_country;
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		disposeClick(view,position);
	}
}