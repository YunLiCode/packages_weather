package com.lewa.weather.provider;

import android.provider.BaseColumns;

public class InstanceItemColumns implements BaseColumns{
	public static final int DISPLAY_TYPE_TIME = 0;
	public static final int DISPLAY_TYPE_ICON = 1;
	public static final int DISPLAY_TYPE_STRIKE = 2;
	
	public static final String TABLE_NAME = "instance";
	
	public static final String CONTENT = "content";
	public static final String SUMMARY = "summary";
	public static final String TIME = "time";
	public static final String ICON = "icon";
	public static final String DISPLAY_TYPE = "display_type";
	public static final String TYPE = "type";
	public static final String REFERENCE_ID = "reference_id";
	public static final String CLASSNAME = "classname";
	public static final String RIGHT_ICON = "right_icon";
	public static final String RIGHT_CONTENT = "right_content";
	
	public static final String INDEX_SCHEDULE_TIME = "schedule_time";
}
