package com.tec.jca.faceverification.db;

import com.tec.jca.faceverification.FaceVerificationApplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FaceVerificationDatabaseHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "face_verification";
	public static final String TABL_HISTORY = "history";
	public static final String TABL_CARDINFO = "cardInfo";
	private static FaceVerificationDatabaseHelper instance;
	public static FaceVerificationDatabaseHelper getInstance(){
		if(instance==null){
			instance=new FaceVerificationDatabaseHelper(FaceVerificationApplication.getSingleInstance());
		}
		return instance;
	}
	private FaceVerificationDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	private FaceVerificationDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		updateMyDatabase(db,0,DB_VERSION);
	}
	
	private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion <= 1) {
			String sql="CREATE TABLE "+TABL_CARDINFO+" ("
					+"name TEXT NOT NULL, "
					+"sex INTEGER, "
					+"nation INTEGER, "
					+"birthday TEXT NOT NULL," 
					+"address TEXT NOT NULL,"
					+"card_code TEXT PRIMARY KEY,"
					+"fzjg TEXT NOT NULL," 
					+"yrqx_begin TEXT NOT NULL," 
					+"yrqx_end TEXT NOT NULL," 
					+"picPath TEXT NOT NULL" 
					+");";
					db.execSQL(sql);
					sql="CREATE TABLE "+TABL_HISTORY+" ("
						+"_id INTEGER PRIMARY KEY AUTOINCREMENT, "
						+"name TEXT NOT NULL,"
						+"card_code TEXT NOT NULL,"
						+"photoPath TEXT NOT NULL," 
						+"verifyWhen TimeStamp NOT NULL DEFAULT CURRENT_TIMESTAMP, "
						+"similarity TEXT NOT NULL"
						+");";
					db.execSQL(sql);
					
		
		}
		if (oldVersion < 2) {
		 //db.execSQL("ALTER TABLE DRINK ADD COLUMN FAVORITE NUMERIC;");
		}
	}	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		updateMyDatabase(db,oldVersion,newVersion);
	}
}
