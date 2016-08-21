package com.tec.jca.faceverification.ui.activity;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.WindowManager;

public abstract class BaseActivity extends ActionBarActivity implements LogIntf{
	
	//生命周期跟踪
	protected void logLifeSycle(String logMessage){
		Log.d(getLogTag(), logMessage);
	}
	/**
     * 解决overflow按钮的显示情况和手机的硬件情况是有关系的，
     * 如果手机没有物理Menu键的话，overflow按钮就可以显示，
     * 如果有物理Menu键的话，overflow按钮就不会显示出来
     */
     protected void setOverflowShowingAlways() { 
	     try { 
	     ViewConfiguration config = ViewConfiguration.get(this); 
	     Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey"); 
	     menuKeyField.setAccessible(true); 
	     menuKeyField.setBoolean(config, false); 
	     } catch (Exception e) { 
	     e.printStackTrace(); 
	     } 
     }
     //全屏，并且显示actionbar上的后退按钮
     protected void fullScreenAndShowActionBackButton(){
    	 this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
 		getSupportActionBar().setDisplayShowHomeEnabled(true);
 		getSupportActionBar().setHomeButtonEnabled(true);
 		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     }
     
     
	
}
