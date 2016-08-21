package com.tec.jca.faceverification;

import org.opencv.android.JavaCameraView;

import com.tec.jca.faceverification.util.FlashlightUtil;

import android.content.Context;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;

public class MyJavaCameraView extends JavaCameraView {
	
	public MyJavaCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public int getCameraIndex(){
		return mCameraIndex;
	}
	public void  turnLightOn(){
			FlashlightUtil.turnLightOn(mCamera);
	}
	public void  turnLightOff(){
			FlashlightUtil.turnLightOff(mCamera);
	}


	

}
