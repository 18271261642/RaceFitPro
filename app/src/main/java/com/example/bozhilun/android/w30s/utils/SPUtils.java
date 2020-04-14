package com.example.bozhilun.android.w30s.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * SharedPreferences 工具类<br>
 * 内部已经封装了打印功能,只需要把DEBUG参数改为true即可<br>
 * 如果需要更换tag可以直接更改,默认为KEZHUANG
 * 
 * @author KEZHUANG
 *
 */
public class SPUtils {
	private static final String TAG = "SPUtils";
	
	/**
	 * 保存在手机里面的文件名
	 */
	public final static String SETTING = "sp_race_data";


	public static void putValue(Context context,String key, int value) {
		SharedPreferences.Editor sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
		sp.putInt(key, value);
		sp.commit();
	}
	public static void putValue(Context context,String key, boolean value) {
		SharedPreferences.Editor sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
		sp.putBoolean(key, value);
		sp.commit();
	}
	public static void putValue(Context context,String key, String value) {
		SharedPreferences.Editor sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
		sp.putString(key, value);
		sp.commit();
	}
	public static int getValue(Context context,String key, int defValue) {
		SharedPreferences sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
		int value = sp.getInt(key, defValue);
		return value;
	}
	public static boolean getValue(Context context,String key, boolean defValue) {
		SharedPreferences sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
		boolean value = sp.getBoolean(key, defValue);
		return value;
	}
	public static String getValue(Context context,String key, String defValue) {
		SharedPreferences sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
		String value = sp.getString(key, defValue);
		return value;
	}

}