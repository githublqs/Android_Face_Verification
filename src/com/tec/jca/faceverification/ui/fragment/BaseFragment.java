package com.tec.jca.faceverification.ui.fragment;

import com.tec.jca.faceverification.ui.activity.LogIntf;

import android.support.v4.app.Fragment;
import android.util.Log;

public abstract class BaseFragment extends Fragment implements LogIntf{
	//ÉúÃüÖÜÆÚ¸ú×Ù
	protected void logLifeSycle(String logMessage){
		Log.d(getLogTag(), logMessage);
	}
	
	

}
