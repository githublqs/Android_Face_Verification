package com.tec.jca.faceverification.domain;
import java.io.File;
import java.io.IOException;

import FaceRec.SesFaceRec;
import android.os.Environment;

import com.tec.jca.faceverification.Consts;
import com.tec.jca.faceverification.FaceVerificationApplication;
import com.tec.jca.faceverification.GloabalVar;
import com.tec.jca.faceverification.ParamStorage;
import com.tec.jca.faceverification.executer.Executor;
import com.tec.jca.faceverification.executer.Interactor;
import com.tec.jca.faceverification.executer.MainThread;
import com.tec.jca.faceverification.executer.MainThreadImpl;
import com.tec.jca.faceverification.executer.ThreadExecuter;
import com.tec.jca.faceverification.util.SDCardSizeUtil;

public class SFRInitInteractor implements  Interactor,SFRInit{
	
	
	private Callback callback;
	private final Executor executor=ThreadExecuter.getInstance();
	private final MainThread mainThread=MainThreadImpl.getInstance();
	@Override
	public void execute( Callback callback) {
	    validateArguments(callback);
	    this.callback = callback;
	   
	    
	    this.executor.run(this);
	    

	}
	private boolean isExecute;
	public boolean isExecuting(){
		return isExecute;
	}

	@Override
	public void run() {
		isExecute=true;
		FaceVerificationApplication app = FaceVerificationApplication.getSingleInstance();

		int countFile=0;
		File dir = new File(GloabalVar.FaceData_Dir);
		if(dir.exists()){
			File []files = new File(GloabalVar.FaceData_Dir).listFiles();
			for(File fileIndex:files){
				if(!fileIndex.isDirectory()){
					countFile++;
				}
			}
			
			
		
		}
		if(countFile!=6){
			if(!SDCardSizeUtil.isAvaiableSpace(Consts.MIN_AvaiableSpace)){
				mainThread.post(new Runnable() {
		    	      @Override public void run() {
		    	    	  if(callback!=null){
		    	    		  callback.onInitReturn(false);
		    	    	  }
		    	      }
		    	    });
				return;
			}
		}
		
		try {
				FaceVerificationApplication.copyAssetsToSDcard(app,countFile!=6);
			
		} catch (IOException e) {
			e.printStackTrace();
			mainThread.post(new Runnable() {
	    	      @Override public void run() {
	    	    	  if(callback!=null){
	    	    		  callback.onInitReturn(false);
	    	    	  }
	    	      }
	    	    });
			return;
		}
		SesFaceRec sfr = app.getSesFaceRec();
    	sfr.init(GloabalVar.FaceData_Dir);
    	final boolean result = sfr.loadSuccess;
    	mainThread.post(new Runnable() {
    	      @Override public void run() {
    	    	if(callback!=null){
    	    		callback.onInitReturn(result);	
    	    	}
    	      }
    	    });
    	isExecute=false;
	}
	private void validateArguments( Callback callback) {
	    
	    if (callback == null) {
	      throw new IllegalArgumentException("Callback parameter can't be null");
	    }
	  }
}