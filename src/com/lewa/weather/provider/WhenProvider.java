package com.lewa.weather.provider;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public abstract class WhenProvider extends ContentProvider{
	public static String AUTHORITY = "com.lewa.weather";
	public static final String PATH_INSTANCE = "instance/#";
	public static final String PATH_ICON = "icon/#";
	
	private static final int INSTANCE = 1;
	private static final int ICON = 2;
	
	private static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	
	AbsInstanceAdapter instanceAdapter;
	AbsIconToolAdapter iconToolAdapter;
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		instanceAdapter = getInstanceAdapter();
		iconToolAdapter = getIconToolAdapter();
		MatrixCursor cursor = null;
		ApplicationInfo appInfo;
		try {
			appInfo = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
			this.URI_MATCHER.addURI(getAuthority(), PATH_INSTANCE, WhenProvider.INSTANCE);
			this.URI_MATCHER.addURI(getAuthority(), PATH_ICON, WhenProvider.ICON);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		switch (this.URI_MATCHER.match(uri)) {
		case INSTANCE:
			try {
				if (instanceAdapter != null) {
					if (instanceAdapter.loadLastSegment(uri.getLastPathSegment())) {
						cursor = genInstanceCursor();
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			break;
			
		case ICON:
			try {
				if (iconToolAdapter != null) {
					if (iconToolAdapter.loadLastSegment(uri.getLastPathSegment())) {
						cursor = genIconToolCursor();
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			break;
		default:
		}
		
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 生成返回的对应InstanceItem的Cursor
	 * @return cursor
	 */
	private MatrixCursor genInstanceCursor() {
		String[] columns = new String[]{
				InstanceItemColumns._ID,
				InstanceItemColumns.CLASSNAME,
				InstanceItemColumns.CONTENT,
				InstanceItemColumns.SUMMARY,
				InstanceItemColumns.DISPLAY_TYPE,
				InstanceItemColumns.ICON,
				InstanceItemColumns.REFERENCE_ID,
				InstanceItemColumns.RIGHT_CONTENT,
				InstanceItemColumns.RIGHT_ICON,
				InstanceItemColumns.TIME,
				InstanceItemColumns.TYPE };
		MatrixCursor cursor = new MatrixCursor(columns);
		
		List<Object> list = new ArrayList<Object>();
		int count = instanceAdapter.getCount();
		for (int i=0; i<count; i++) {
			list.clear();
			list.add(0);
			list.add(instanceAdapter.getPackageName(i));
			list.add(instanceAdapter.getContent(i));
			list.add(instanceAdapter.getSummary(i));
			list.add(InstanceItemColumns.DISPLAY_TYPE_ICON);
			list.add(instanceAdapter.getIcon(i));
			list.add(instanceAdapter.getReferenceId(i));
			list.add(instanceAdapter.getRightContent(i));
			list.add(instanceAdapter.getRightIcon(i));
			list.add(instanceAdapter.getTime(i));
			list.add(2);
				
			cursor.addRow(list);
		}
		
		return cursor;
	}
	
	/**
	 * 
	 * Author: linsiran
	 * Date: 2012-4-23下午04:39:52
	 */
	private MatrixCursor genIconToolCursor() {
		String[] columns = new String[]{
				IconToolColumns.CLASS_NAME,
				IconToolColumns.DATA, 
				IconToolColumns.IMG_URL, 
				IconToolColumns.NAME, 
				IconToolColumns.PACKAGE_NAME,
				IconToolColumns.RSID };
		MatrixCursor cursor = new MatrixCursor(columns);
		
		List<Object> list = new ArrayList<Object>();
		int count = iconToolAdapter.getCount();
		for (int i=0; i<count; i++) {
			list.clear();
			list.add(iconToolAdapter.getClassName(i));
			list.add(iconToolAdapter.getData(i));
			list.add(iconToolAdapter.getImgUrl(i));
			list.add(iconToolAdapter.getName(i));
			list.add(iconToolAdapter.getPackageName(i));
			list.add(iconToolAdapter.getRsid(i));
				
			cursor.addRow(list);
		}
		
		return cursor;
	}
	
	public abstract AbsInstanceAdapter getInstanceAdapter();
	
	public abstract AbsIconToolAdapter getIconToolAdapter();
	
	public String getAuthority() {
		return AUTHORITY;
	}
	
}
