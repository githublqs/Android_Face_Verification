package com.tec.jca.faceverification.executer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;

public class MainThreadImpl implements MainThread {

	 private Handler handler;

	  private MainThreadImpl() {
	    this.handler = new Handler(Looper.getMainLooper());
	  }

	
		
	private static MainThreadImpl instance;

	public static MainThreadImpl getInstance() {
		if (instance == null) {
			instance = new MainThreadImpl();
		}

		return instance;
	}
	  public void post(Runnable runnable) {
	    handler.post(runnable);
	  }

}
