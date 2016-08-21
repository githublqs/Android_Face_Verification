package com.ivsign.android.IDCReader;

import android.os.Environment;

public class IDCReaderSDK {
	
	private static final String TAG = "unpack";
	
	public IDCReaderSDK()
	{
	
	}
	public static int Init(String workPath)
	{
		
		return wltInit(workPath);
	}
	public static int unpack(byte[] wltdata, byte[] licdata)
	{
		return wltGetBMP(wltdata, licdata);
	}

	// native functin interface
    public static native int wltInit(String workPath);
    
    public static native int wltGetBMP(byte[] wltdata, byte[] licdata);
    
    /* this is used to load the 'wltdecode' library on application
     */
    static {
    	
        System.loadLibrary("wltdecode");
    }
}
