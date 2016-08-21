package com.tec.jca.faceverification.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.tec.jca.faceverification.GloabalVar;
import com.tec.jca.faceverification.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class FileUtil {
	public static boolean saveBitmap(Bitmap bm, String targetDir,String targetFileName) {
		boolean ret = false;
		try {
			File targetDirFile = new File(targetDir);
			if(!targetDirFile.exists()){
				targetDirFile.mkdirs();
			}
	        FileOutputStream out = new FileOutputStream(targetDir+File.separator+targetFileName);
			bm.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			ret=true;

		} catch (Exception e) {
			Log.e("error", e.getMessage());
		
		}
		return ret;
	}
	/*public static void copyRawF(Context context,File fDir,int idR,String rFileName) throws IOException{
    	InputStream is = context.getResources().openRawResource(idR);
        File f = new File(fDir, rFileName);
        FileOutputStream os = new FileOutputStream(f);
        copyF(is,os);
    }*/
	public static void copyF(String sourceFilePath,String targetDir,String targetFileName)throws IOException{
		InputStream is = new FileInputStream(sourceFilePath);
		File targetDirFile = new File(targetDir);
		if(!targetDirFile.exists()){
			targetDirFile.mkdirs();
		}
        FileOutputStream os = new FileOutputStream(targetDir+File.separator+targetFileName);
        copyF(is,os);
	}
	public static void copyF(InputStream is,OutputStream os) throws IOException{
		 byte[] buffer = new byte[4096];
	        int bytesRead;
	        while ((bytesRead = is.read(buffer)) != -1) {
	            os.write(buffer, 0, bytesRead);
	        }
	        is.close();
	        os.close();
	}
	
	/*public static void copyRaws(Context context){
		Map<Integer,String> map=new HashMap<Integer,String>();
		map.put(R.raw.base,"base.dat");
		map.put(R.raw.license,"license.lic");
		copyRawsTo(context,GloabalVar.wltlibDir,map);
		map.clear();
		map.put(R.raw.lbpcascade_frontalface,"lbpcascade_frontalface.xml");
		copyRawsTo(context,GloabalVar.cascadeDir,map);
	}
	public static void copyRawsTo(Context context,String dir,Map<Integer,String> rawIdsMap){
    	try {
    		File fDir = new File(dir);
        	//文件夹不存在就拷贝raw文件
    		
        	fDir.mkdirs();
        		
    		for(Map.Entry<Integer, String> entry:rawIdsMap.entrySet()){ 
    			copyRawF(context,fDir,entry.getKey(),entry.getValue());
    		}
        	         
        	
    	
        	
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }*/
	
	
}
