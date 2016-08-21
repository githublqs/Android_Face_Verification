package com.tec.jca.faceverification;
import java.io.File;
import java.io.IOException;

import com.tec.jca.faceverification.db.FaceVerificationDatabaseHelper;
import com.tec.jca.faceverification.util.FileUtil;
import com.tec.jca.faceverification.util.UnzipFromAssets;

import FaceRec.SesFaceRec;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
public class FaceVerificationApplication extends Application {
	private static SesFaceRec sesFaceRec;
	private FaceVerificationDatabaseHelper dbHelper;
	private static FaceVerificationApplication singleInstance;
	public static FaceVerificationApplication getSingleInstance(){
		return singleInstance;
	}
	public static void copyAssetsToSDcard(Context context,boolean unzip) throws IOException{
		UnzipFromAssets.unZip(context, "base.dat",false, GloabalVar.wltlibDir, true);
		UnzipFromAssets.unZip(context, "license.lic",false, GloabalVar.wltlibDir, true);
		UnzipFromAssets.unZip(context, "lbpcascade_frontalface.xml",false, GloabalVar.cascadeDir, true);
		if(unzip){
			UnzipFromAssets.unZip(context, "FaceData.zip",true ,GloabalVar.FaceData_Dir, false);
		}
	}
	@Override
	public void onCreate() {
		super.onCreate();
		initGloabalVars(this);
		//FileUtil.copyRaws(getApplicationContext());
		sesFaceRec=new SesFaceRec();
		singleInstance=this;
	}
	
	public SesFaceRec getSesFaceRec(){
		return sesFaceRec;
	}
	private void initGloabalVars(Context context){
		 GloabalVar.FaceData_Dir=context.getExternalFilesDir("FaceData").toString();
		 GloabalVar.takPicDir = Environment.getExternalStorageDirectory()+ File.separator + "face_rec_Pic";
		 /*File takPicDirFile = new File(GloabalVar.takPicDir);
		 if(takPicDirFile.isFile()){
			 takPicDirFile.delete();
		 }*/
		 //takPicDirFile.mkdirs();
		 GloabalVar.wltlibDir = context.getExternalFilesDir("wltlib").toString();
		 GloabalVar.cascadeDir= context.getExternalFilesDir("cascadeDir").toString();
	}
}
