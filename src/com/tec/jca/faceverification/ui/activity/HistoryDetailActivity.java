package com.tec.jca.faceverification.ui.activity;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tec.jca.faceverification.R;
import com.tec.jca.faceverification.db.CardInfoDBManager;
import com.tec.jca.faceverification.entity.CardInfo;
import com.tec.jca.faceverification.ui.activity.BaseActivity;
import com.tec.jca.faceverification.util.CardInfoUtil;
public class HistoryDetailActivity extends BaseActivity {
	private ImageView iv_pic;
	private ImageView iv_id_face;
	private EditText et_card_name;
	private EditText et_card_sex;
	private EditText et_card_nation;
	private EditText et_card_birthday;
	private EditText et_card_address;
	private EditText et_card_code;
	private EditText et_card_fzjg;
	private EditText et_card_yrqx_begin;
	private EditText et_card_yrqx_end;
	private TextView tv_similarity;
	private TextView tv_verify_time;
	private SimpleDateFormat format;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fullScreenAndShowActionBackButton();
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		setContentView(R.layout.activity_history_detail);
		tv_similarity=(TextView)findViewById(R.id.tv_similarity);
		tv_verify_time=(TextView)findViewById(R.id.tv_verify_time);
		iv_pic=(ImageView)findViewById(R.id.iv_pic);
		setUpcardInfoViews();
		updateViews(getIntent());
	}
	private void updateViews(Intent intent) {
		if(intent!=null){
			if(intent.hasExtra("card_code")&&intent.hasExtra("photoPath")
					&&intent.hasExtra("similarity")
							&&intent.hasExtra("verify_time")){
				String similarity = intent.getStringExtra("similarity");
				String verify_time = intent.getStringExtra("verify_time");
				float sim = Float.parseFloat(similarity);
				tv_similarity.setText(String.format("%1$.4f",sim ));
				try {
					tv_verify_time.setText(format.format(new Date(Long.parseLong(verify_time))));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String photoPath=intent.getStringExtra("photoPath");
				try {
					Bitmap photoPathBitmap = BitmapFactory.decodeFile(photoPath);
					iv_pic.setImageBitmap(photoPathBitmap);	
				} catch (Exception e) {
				}
				String card_code=intent.getStringExtra("card_code");
				CardInfo card_info = CardInfoDBManager.getCardInfo(card_code);
				if(card_info!=null){
					try {///storage/emulated/0/face_rec_Pic¡ı«‡À…            _430525198706067495.bmp
						Bitmap picPathBitmap = BitmapFactory.decodeFile(card_info.getPicPath());
						iv_id_face.setImageBitmap(picPathBitmap);	
					} catch (Exception e) {
					}
					et_card_name.setText(card_info.getName());
					et_card_sex.setText(card_info.getSex()==1?"ƒ–":"≈Æ");
					et_card_nation.setText(CardInfoUtil.decodeNation(card_info.getNation()));
					et_card_birthday.setText(buildDayString(card_info.getBirthday()));
					et_card_address.setText(card_info.getAddress());
					et_card_code.setText(card_info.getCard_code());
					et_card_fzjg.setText(card_info.getFzjg());
					et_card_yrqx_begin.setText(buildDayString(card_info.getYrqx_begin()));
					et_card_yrqx_end.setText(buildDayString(card_info.getYrqx_end()));
				}
			}
		}
	}
	private String buildDayString(String srcStr){
		try {
			String year = srcStr.substring(0, 4);
			String month=srcStr.substring(4, 6);
			String day=srcStr.substring(6, 8);
			String spliter="-";
			return year+spliter+month+spliter+day;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	private void setUpcardInfoViews() {
 		 iv_id_face=(ImageView)findViewById(R.id.iv_id_face);
 		 et_card_name=(EditText)findViewById(R.id.et_card_name);
 		 et_card_sex=(EditText)findViewById(R.id.et_card_sex);
 		 et_card_nation=(EditText)findViewById(R.id.et_card_nation);
 		 et_card_birthday=(EditText)findViewById(R.id.et_card_birthday);
 		 et_card_address=(EditText)findViewById(R.id.et_card_address);
 		 et_card_code=(EditText)findViewById(R.id.et_card_code);
 		 et_card_fzjg=(EditText)findViewById(R.id.et_card_fzjg);
 		 et_card_yrqx_begin=(EditText)findViewById(R.id.et_card_yrqx_begin);
 		 et_card_yrqx_end=(EditText)findViewById(R.id.et_card_yrqx_end); 
 	}
	@Override
	public String getLogTag() {
		return "HistoryDetailActivity";
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  if(item.getItemId()==android.R.id.home){
			  onBackPressed();
		  }
		return super.onOptionsItemSelected(item);
	}
}