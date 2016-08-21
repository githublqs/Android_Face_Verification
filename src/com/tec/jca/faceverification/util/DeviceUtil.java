package com.tec.jca.faceverification.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DeviceUtil {

	public static boolean isTablet(Context context) {
	        return (context.getResources().getConfiguration().screenLayout
	                & Configuration.SCREENLAYOUT_SIZE_MASK)
	                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	/**
	 * ÅĞ¶ÏÊÇ·ñÎªÆ½°å
	 * 
	 * @return
	 */
	private boolean isPad(Context context) {
	 WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	 Display display = wm.getDefaultDisplay();
	 // ÆÁÄ»¿í¶È
	 float screenWidth = display.getWidth();
	 // ÆÁÄ»¸ß¶È
	 float screenHeight = display.getHeight();
	 DisplayMetrics dm = new DisplayMetrics();
	 display.getMetrics(dm);
	 double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
	 double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
	 // ÆÁÄ»³ß´ç
	 double screenInches = Math.sqrt(x + y);
	 // ´óÓÚ6³ß´çÔòÎªPad
	 if (screenInches >= 6.0) {
	  return true;
	 }
	 return false;
	}
}
