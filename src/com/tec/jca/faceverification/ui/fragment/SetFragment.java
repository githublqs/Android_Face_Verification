package com.tec.jca.faceverification.ui.fragment;
import java.io.File;

import com.tec.jca.faceverification.FaceVerificationApplication;
import com.tec.jca.faceverification.GloabalVar;
import com.tec.jca.faceverification.ParamStorage;
import com.tec.jca.faceverification.R;
import com.tec.jca.faceverification.util.InputFilterMinMax;
import com.tec.jca.faceverification.util.InputFilterMinMaxDecimal;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
public class SetFragment extends BaseFragment implements OnClickListener  {
	private static final String TAG="SetFragment";
	private EditText et_timeout_tak_pic;
	private EditText et_min_similarity;
	private CheckBox chk_hint_sound;
	private CheckBox chk_tak_pic_playsoud;
	private CheckBox chk_use_flashlight;
	private Spinner spinner_camera_front_back;
	private Button btn_ok;
	private Button btn_cancel;
	private SetFragmentListener listener;
	private boolean switchCamera;
	private ParamStorage paramStorage;
	private TextView tv_tak_pic_save_path;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		logLifeSycle("onAttach-----");
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		logLifeSycle("onCreate-----");
	}
	//没有onRestart
	@Override
	public void onStart() {
		super.onStart();
		logLifeSycle("onStart-----");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		paramStorage=ParamStorage.getInstance(getActivity().getApplication());
		View view = inflater.inflate(R.layout.fragment_set, null);
		
		
		tv_tak_pic_save_path=(TextView)view.findViewById(R.id.et_timeout_tak_pic);//图片保存路劲
		et_timeout_tak_pic=(EditText)view.findViewById(R.id.et_timeout_tak_pic);//拍照超时
		et_timeout_tak_pic.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "100")});
		et_min_similarity=(EditText)view.findViewById(R.id.et_min_similarity);//最低相似度
		et_min_similarity.setFilters(new InputFilter[]{ new InputFilterMinMaxDecimal("0.5", "1.0")});
		spinner_camera_front_back=(Spinner)view.findViewById(R.id.spinner_camera_front_back);//闪光灯
		chk_use_flashlight=(CheckBox)view.findViewById(R.id.chk_use_flashlight);//闪光灯
		chk_tak_pic_playsoud=(CheckBox)view.findViewById(R.id.chk_tak_pic_playsoud);//拍照声音
		chk_hint_sound=(CheckBox)view.findViewById(R.id.chk_hint_sound);//提示音
		
		
		btn_ok=(Button)view.findViewById(R.id.btn_save);//确定
		btn_ok.setOnClickListener(this);
		btn_cancel=(Button)view.findViewById(R.id.btn_cancel);//取消
		btn_cancel.setOnClickListener(this);
		
		setViewsData();
		logLifeSycle("onCreateView-----");
		return view;
	}
	private void setViewsData() {
		tv_tak_pic_save_path.setText(getString(R.string.sdCard)+ File.separator + "face_rec_Pic");
		et_timeout_tak_pic.setText(paramStorage.getTimeout_tak_pic()+"");
		String str_similarity = String.format("%1$.4f", paramStorage.getMin_similarity());
		et_min_similarity.setText(str_similarity);
		spinner_camera_front_back.setSelection(paramStorage.getCamera_front_back());
		chk_use_flashlight.setChecked(paramStorage.isUse_flashlight());
		chk_tak_pic_playsoud.setChecked(paramStorage.isTak_pic_playsoud());
		chk_hint_sound.setChecked(paramStorage.isHas_hint_sound());
		
		
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		super.onViewCreated(view, savedInstanceState);
		logLifeSycle("onViewCreated-----");
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		logLifeSycle("onResume-----");
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		logLifeSycle("onPause-----");
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		logLifeSycle("onStop-----");
	}
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		logLifeSycle("onDetach-----");
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		logLifeSycle("onDestroy-----");
	}
	@Override
	public String getLogTag() {
		return TAG;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_save:
			onBtnSaveClicked(v);
			break;
		case R.id.btn_cancel:
			onBtnCancelClicked(v);
			break;

		default:
			break;
		}
		
	}
	
	private void onBtnSaveClicked(View v){
		saveSetParams();
		if(listener!=null){
			listener.onSaveSet(switchCamera);
		}
		
	}
	private void onBtnCancelClicked(View v){
		if(listener!=null){
			listener.onCancelSet();
		}
	}
	
	public void setListener(SetFragmentListener listener) {
		this.listener = listener;
	}
	public interface  SetFragmentListener{
		public void onCancelSet();
		public void onSaveSet(boolean switchCamera);
	}
	private void saveSetParams(){
		ParamStorage ps = ParamStorage.getInstance(getActivity().getApplication());
		int camera_front_back = spinner_camera_front_back.getSelectedItemPosition();
		switchCamera=ps.getCamera_front_back()!=camera_front_back;
		int timeout_tak_pic =paramStorage.getTimeout_tak_pic();
		try {
			timeout_tak_pic= Integer.parseInt(et_timeout_tak_pic.getText().toString().trim());
		}catch(Exception e){
			
		}
		
		
		float min_similarity = paramStorage.getMin_similarity();
		try {
			min_similarity= Float.parseFloat(et_min_similarity.getText().toString().trim());
		}catch(Exception e){
		}
		boolean use_flashlight = chk_use_flashlight.isChecked();
		boolean tak_pic_playsoud = chk_tak_pic_playsoud.isChecked();
		boolean has_hint_sound = chk_hint_sound.isChecked();
		ps.saveSetParams(
				camera_front_back, 
				timeout_tak_pic, 
				min_similarity, 
				use_flashlight, 
				tak_pic_playsoud, 
				has_hint_sound);
		
	}
}
