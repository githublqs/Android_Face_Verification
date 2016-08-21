package com.tec.jca.faceverification.ui.activity;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.WindowManager;

public abstract class BaseActivity extends ActionBarActivity implements LogIntf{
	
	//�������ڸ���
	protected void logLifeSycle(String logMessage){
		Log.d(getLogTag(), logMessage);
	}
	/**
     * ���overflow��ť����ʾ������ֻ���Ӳ��������й�ϵ�ģ�
     * ����ֻ�û������Menu���Ļ���overflow��ť�Ϳ�����ʾ��
     * ���������Menu���Ļ���overflow��ť�Ͳ�����ʾ����
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
     //ȫ����������ʾactionbar�ϵĺ��˰�ť
     protected void fullScreenAndShowActionBackButton(){
    	 this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
 		getSupportActionBar().setDisplayShowHomeEnabled(true);
 		getSupportActionBar().setHomeButtonEnabled(true);
 		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     }
     
     
	
}
