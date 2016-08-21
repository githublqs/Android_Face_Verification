package com.tec.jca.faceverification.db;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.DownloadManager.Query;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tec.jca.faceverification.GloabalVar;
import com.tec.jca.faceverification.entity.CardInfo;
import com.tec.jca.faceverification.entity.VerifyHistory;
import com.tec.jca.faceverification.util.FileUtil;
public class CardInfoDBManager {
	 //保存验证成功记录
	 public static void saveVerifyHistory(CardInfo cardInfo,float similarity,String photoPath){
		 try {
			 SQLiteDatabase db = FaceVerificationDatabaseHelper.getInstance().getWritableDatabase();
				ContentValues values=new ContentValues();
				values.put("name", cardInfo.getName());
				values.put("card_code", cardInfo.getCard_code());
				values.put("photoPath", photoPath);
				values.put("verifyWhen", System.currentTimeMillis());
				values.put("similarity",similarity+"");
				db.insert(FaceVerificationDatabaseHelper.TABL_HISTORY, null, values);
				db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	 }
	 
	 public static void saveCardInfoToDb(CardInfo cardInfo){
			SQLiteDatabase db = FaceVerificationDatabaseHelper.getInstance().getWritableDatabase();  
			Cursor cusor=db.query(FaceVerificationDatabaseHelper.TABL_CARDINFO, new String[]{"card_code"}, "card_code =? ", new String[]{cardInfo.getCard_code()}, null, null, null);
			boolean haveCardInfo=cusor.moveToFirst();
			ContentValues values=new ContentValues();
			values.put("name", cardInfo.getName());
			values.put("nation", cardInfo.getNation());
			values.put("sex", cardInfo.getSex());
			values.put("birthday", cardInfo.getBirthday());
			values.put("address", cardInfo.getAddress());
			values.put("card_code", cardInfo.getCard_code());
			values.put("fzjg", cardInfo.getFzjg());
			values.put("yrqx_begin", cardInfo.getYrqx_begin());
			values.put("yrqx_end", cardInfo.getYrqx_end());		
			//String picPath= GloabalVar.takPicDir+"/"+cardInfo.getName()+"_"+cardInfo.getCard_code()+".bmp";
			String targetFileName=cardInfo.getName()+"_"+cardInfo.getCard_code()+".bmp";
			try {
				FileUtil.copyF(cardInfo.getPicPath(),GloabalVar.takPicDir, targetFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			values.put("picPath" ,GloabalVar.takPicDir+File.separator+ targetFileName);
			if(haveCardInfo){
				db.update(FaceVerificationDatabaseHelper.TABL_CARDINFO, values, "card_code =? ", new String[]{cardInfo.getCard_code()});
			}else{
				db.insert(FaceVerificationDatabaseHelper.TABL_CARDINFO, null, values);
			}
			cusor.close();
			db.close();
		}
	 //保留最近的100条
	public static void persistRecentHistory(int num) {
		List<String> persistCardCodes=new ArrayList<String>();
		
		SQLiteDatabase db = FaceVerificationDatabaseHelper.getInstance().getWritableDatabase();
		Cursor cursor = db.query(FaceVerificationDatabaseHelper.TABL_HISTORY, new String[]{"card_code","photoPath","_id"}, 
				null, null, null, null, " _id desc ");
		int count=0;
		while(cursor.moveToNext())
		{
			count++;
			
			if(count>num){
				File photoPathFile= new File(cursor.getString(1));
				if(photoPathFile.isFile()&&photoPathFile.exists()){
					try {
						photoPathFile.delete();	
					} catch (Exception e) {
					}
				}
				deleteCardInfoAndPic(cursor,db,persistCardCodes);
				db.delete(FaceVerificationDatabaseHelper.TABL_HISTORY, "_id = ?", new String[]{cursor.getInt(2)+""});
			}else{
				persistCardCodes.add(cursor.getString(0));
			}
		}
		
		cursor.close();
		db.close();
	}

	private static void deleteCardInfoAndPic(Cursor cursor,SQLiteDatabase db,List<String> persistCardCodes) {
		String cardCode=cursor.getString(0);
		Cursor cc = db.query(FaceVerificationDatabaseHelper.TABL_CARDINFO,new String[]{"picPath","card_code"},"card_code = ? ", new String[]{cardCode}, null, null, null);
		while(cc.moveToNext())
		{	boolean bDelete=true;
			for (String persistCardCode : persistCardCodes) {
				if(cc.getString(1).equals(persistCardCode)){
					bDelete=false;
					break;
				}
			}
			if(bDelete){
				File picPathFile= new File(cc.getString(0));
				if(picPathFile.isFile()&&picPathFile.exists()){
					try {
						
						picPathFile.delete();	
					} catch (Exception e) {
					}
				}
				db.delete(FaceVerificationDatabaseHelper.TABL_CARDINFO, "card_code = ? ", new String[]{cardCode});
			}
			
		}
		cc.close();
		
		
	}
	
	public static CardInfo getCardInfo(String card_code){
		SQLiteDatabase db =null;
		Cursor cursor=null;
		CardInfo cardInfo=null;
		try {
			db = FaceVerificationDatabaseHelper.getInstance().getReadableDatabase();
			cursor = db.query(FaceVerificationDatabaseHelper.TABL_CARDINFO, new String[]{
					"name","sex","nation",
					"birthday","address","card_code","fzjg",
					"yrqx_begin","yrqx_end","picPath"}, 
					"card_code = ? ", new String[]{card_code}, null, null, null);
			if(cursor.moveToFirst()){
				cardInfo=new CardInfo();
				cardInfo.setName(cursor.getString(0));
				cardInfo.setSex(cursor.getInt(1));
				cardInfo.setNation(cursor.getInt(2));
				cardInfo.setBirthday(cursor.getString(3));
				cardInfo.setAddress(cursor.getString(4));
				cardInfo.setCard_code(cursor.getString(5));
				cardInfo.setFzjg(cursor.getString(6));
				cardInfo.setYrqx_begin(cursor.getString(7));
				cardInfo.setYrqx_end(cursor.getString(8));
				cardInfo.setPicPath(cursor.getString(9));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(cursor!=null){
					cursor.close();
				}
				if(db!=null){
					db.close();
				}
			} catch (Exception e) {
			}
		}
		
		return cardInfo;
	}
}
