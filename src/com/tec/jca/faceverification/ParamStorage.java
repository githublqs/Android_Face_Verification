package com.tec.jca.faceverification;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
public class ParamStorage {
	private static ParamStorage instance;
	public static ParamStorage getInstance(Application app){
		if(instance==null){
			instance=new ParamStorage(app);
		}
		return instance;
	}
	private Application app;
	private int camera_front_back;
	private int timeout_tak_pic;
	private float min_similarity;
	private boolean use_flashlight;
	private boolean tak_pic_playsoud;
	private boolean has_hint_sound;
	private SharedPreferences sps;
	private ParamStorage(Application app){
		this.app=app;
		sps = app.getSharedPreferences(Consts.PARAM_FILE_NAME,Context.MODE_PRIVATE);
		loadParam();
	}
	private void loadParam(){
		SharedPreferences sps = app.getSharedPreferences(Consts.PARAM_FILE_NAME,Context.MODE_PRIVATE);
		setCamera_front_back(sps.getInt("camera_front_back", 0));
		setTimeout_tak_pic(sps.getInt("timeout_tak_pic", 32));
		setMin_similarity(sps.getFloat("min_similarity", Consts.PARAM_MIN_SIMILARITY));
		setUse_flashlight(sps.getBoolean("use_flashlight", false));
		setTak_pic_playsoud(sps.getBoolean("tak_pic_playsoud", true));
		setHas_hint_sound(sps.getBoolean("has_hint_sound", true));
		
	
		
	}
	public int getCamera_front_back() {
		return camera_front_back;
	}
	public void setCamera_front_back(int camera_front_back) {
		this.camera_front_back = camera_front_back;
	}
	public int getTimeout_tak_pic() {
		return timeout_tak_pic;
	}
	public void setTimeout_tak_pic(int timeout_tak_pic) {
		this.timeout_tak_pic = timeout_tak_pic;
	}
	public float getMin_similarity() {
		return min_similarity;
	}
	public void setMin_similarity(float min_similarity) {
		this.min_similarity = min_similarity;
	}
	public boolean isUse_flashlight() {
		return use_flashlight;
	}
	public void setUse_flashlight(boolean use_flashlight) {
		this.use_flashlight = use_flashlight;
	}
	public boolean isHas_hint_sound() {
		return has_hint_sound;
	}
	public void setHas_hint_sound(boolean has_hint_sound) {
		this.has_hint_sound = has_hint_sound;
	}
	public boolean isTak_pic_playsoud() {
		return tak_pic_playsoud;
	}
	public void setTak_pic_playsoud(boolean tak_pic_playsoud) {
		this.tak_pic_playsoud = tak_pic_playsoud;
	}
	public void saveSetParams(int camera_front_back,
			int timeout_tak_pic,float min_similarity,boolean use_flashlight,boolean tak_pic_playsoud,
			boolean has_hint_sound){
		Editor edit = sps.edit();
		edit.putInt("camera_front_back", camera_front_back);
		edit.putInt("timeout_tak_pic", timeout_tak_pic);
		edit.putFloat("min_similarity", min_similarity);
		edit.putBoolean("use_flashlight", use_flashlight);
		edit.putBoolean("tak_pic_playsoud", tak_pic_playsoud);
		edit.putBoolean("has_hint_sound", has_hint_sound);
		edit.commit();
		loadParam();
	}
	
	
	
	
}
