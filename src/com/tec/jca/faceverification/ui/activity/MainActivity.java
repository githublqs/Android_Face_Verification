package com.tec.jca.faceverification.ui.activity;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import com.tec.jca.faceverification.Consts;
import com.tec.jca.faceverification.FaceVerificationApplication;
import com.tec.jca.faceverification.GloabalVar;
import com.tec.jca.faceverification.MyJavaCameraView;
import com.tec.jca.faceverification.ParamStorage;
import com.tec.jca.faceverification.R;
import com.tec.jca.faceverification.SoundPlayer;
import com.tec.jca.faceverification.db.CardInfoDBManager;
import com.tec.jca.faceverification.db.FaceVerificationDatabaseHelper;
import com.tec.jca.faceverification.domain.CardService;
import com.tec.jca.faceverification.domain.SFRInit.Callback;
import com.tec.jca.faceverification.domain.SFRInitInteractor;
import com.tec.jca.faceverification.entity.CardInfo;
import com.tec.jca.faceverification.util.CardInfoUtil;
import com.tec.jca.faceverification.util.FileUtil;

import FaceRec.SesFaceRec;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends BaseActivity implements  CvCameraViewListener2, OnClickListener {
	public static final String BROADCAST="com.tec.jca.faceverification.ui.MainActivity";
	private float                  mRelativeFaceSize   = 0.4f;
    private int                    mAbsoluteFaceSize   = 0;
	public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;
    private int                    mDetectorType       = JAVA_DETECTOR;
    private CascadeClassifier      mJavaDetector;
    private static final String TAG="MainActivity";
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    private static final int CARD_PHOTO_WIDTH=358;
    private static final int CARD_PHOTO_HEIGHT=441;
    private static final Scalar    CARD_PHOTO_BORDER_COLOR     = new Scalar(255, 0, 0, 255);
    private static final  Point POINT_PHOTO_BORDER_LT=new Point(141, 20);//w=499-141=358//h=461-20=441
    private static final  Point POINT_PHOTO_BORDER_RB=new Point(499, 461);
    
    private static final  Point POINT_PHOTO_HORIZONTAL_LINE_LT=new Point(0, 20);
    private static final  Point POINT_PHOTO_HORIZONTAL_LINE_RT=new Point(640, 20);
    
    private static final  Point POINT_PHOTO_HORIZONTAL_LINE_LB=new Point(0, 20+441);
    private static final  Point POINT_PHOTO_HORIZONTAL_LINE_RB=new Point(640, 20+441);
    
    private static final  Point POINT_PHOTO_VERTICAL_LINE_LT=new Point(141, 0);
    private static final  Point POINT_PHOTO_VERTICAL_LINE_LB=new Point(141, 480);
    
    private static final  Point POINT_PHOTO_VERTICAL_LINE_RT=new Point(141+358, 0);
    private static final  Point POINT_PHOTO_VERTICAL_LINE_RB=new Point(141+358, 480);
	private static final int MIN_ValidFaceCount = 8;
	private MyJavaCameraView surface_view_face;
	private ImageView iv_id_face;
	public Mat mRgba;
	public Mat mGray;
	private EditText et_card_name;
	private EditText et_card_sex;
	private EditText et_card_nation;
	private EditText et_card_birthday;
	private EditText et_card_address;
	private EditText et_card_code;
	private EditText et_card_yrqx_begin;
	private EditText et_card_yrqx_end;
	private EditText et_card_fzjg;
	private LinearLayout ll_tak_verf;
	private LinearLayout ll_current_person_info;
	private ImageView tv_vfr_card_pic;
	private TextView tv_vfr_card_name;
	private TextView tv_vfr_card_code;
	private FrameLayout fl_vfr_result;
	private LinearLayout ll_tak_err;
	private TextView tv_vfr_failure_reason;
	private TextView tv_vfr_failure_advice;
	private LinearLayout ll_vfr_result;
	private ImageView tv_vfr_pic;
	private ImageView tv_vfr_sig; 
	private TextView tv_vfr_similarity;
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==100){
				float similarity=(Float) msg.obj;
				tv_current_similarity.setText(String.format("%1$.4f", similarity));
			}
		};
	};
	private LinearLayout ll_preview_root;
	private TextView tv_vfr_failure_title;
	private SFRInitInteractor sfrInitInteractor;
	private ProgressBar pb_loading;
	private boolean sFRInitFlag;
	private MainActivityReceiver mActivityReceiver;
	public CardInfo cardInfo;
	public static boolean handleBroadCastFlag;
	private Rect[] facesArray;
	private TextView tv_please_place_face;
	private ParamStorage paramStorage;
	private SoundPlayer soundPlayer;
	private TextView tv_current_similarity;
	public static final int REQUEST_CODE_SET_PARAM=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBroadCastFlag=true;
        logLifeSycle("onCreate-------");
        paramStorage=ParamStorage.getInstance(getApplication());
        soundPlayer=new SoundPlayer(this);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
    	setContentView(R.layout.activity_main);
    	setOverflowShowingAlways();//调用该方法
       pb_loading=(ProgressBar) findViewById(R.id.pb_loading);
        showLoading();
		pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
		ll_preview_root=(LinearLayout)findViewById(R.id.ll_preview_root);
		
		setUpcardInfoViews();
		
		//初始化拍照失败和验证结果页面及其子元素
		setUpTakPhtoAndVerifyResultViews();
		iv_id_face=(ImageView) findViewById(R.id.iv_id_face);
		iv_id_face.setOnClickListener(this);
		tv_please_place_face=(TextView)findViewById(R.id.tv_please_place_face);
		tv_current_similarity=(TextView)findViewById(R.id.tv_current_similarity);
		initCameraView();
		sfrInitInteractor=new SFRInitInteractor();
		initThreadHandler();
        sfrInitInteractor.execute(new Callback() {
			@Override
			public void onInitReturn(boolean ret) {
				sFRInitFlag=ret;
				hideLoading();
				if(!ret){
					Toast.makeText(getApplicationContext(), "算法初始化失败！", Toast.LENGTH_SHORT).show();
					finish();
				}else{
					//sendResumeReadCardBroadCast();
					if (surface_view_face != null){
			  			initDetector();
			  		}
				}
			}
		});
        startService(new Intent(MainActivity.this, CardService.class));
    }
  //初始化拍照失败和验证结果页面及其子元素
  	 private void setUpTakPhtoAndVerifyResultViews() {
  		 //左侧身份证信息（仅code，name）+右侧（拍照 失败页面+验证结果页面）
  		 ll_tak_verf=(LinearLayout)findViewById(R.id.ll_tak_verf);
  		//左侧身份证信息（仅code，name）
  		 ll_current_person_info=(LinearLayout)findViewById(R.id.ll_current_person_info);
  		 // 左侧身份证信息（仅code，name）-->身份证照片
  		 tv_vfr_card_pic=(ImageView)findViewById(R.id.tv_vfr_card_pic);
  		// 左侧身份证信息（仅code，name）-->姓名
  		 tv_vfr_card_name=(TextView)findViewById(R.id.tv_vfr_card_name);
  		// 左侧身份证信息（仅code，name）-->身份证号码
  		 tv_vfr_card_code=(TextView)findViewById(R.id.tv_vfr_card_code);
  		 //右侧（拍照 失败页面+验证结果页面）
  		 fl_vfr_result=(FrameLayout)findViewById(R.id.fl_vfr_result);
  		//右侧（拍照 失败页面+验证结果页面）-->拍照 失败页面
  		 ll_tak_err=(LinearLayout)findViewById(R.id.ll_tak_err);
  		//右侧（拍照 失败页面+验证结果页面）-->拍照 失败页面-->标题
  		 tv_vfr_failure_title=(TextView)findViewById(R.id.tv_vfr_failure_title);
  		//右侧（拍照 失败页面+验证结果页面）-->拍照 失败页面-->原因
  		 tv_vfr_failure_reason=(TextView)findViewById(R.id.tv_vfr_failure_reason);
  		 //右侧（拍照 失败页面+验证结果页面）-->拍照 失败页面-->建议
  		 tv_vfr_failure_advice=(TextView)findViewById(R.id.tv_vfr_failure_advice);
  		 
  		//右侧（拍照 失败页面+验证结果页面）-->验证结果页面
  		 ll_vfr_result=(LinearLayout)findViewById(R.id.ll_vfr_result);
  		//右侧（拍照 失败页面+验证结果页面）-->验证结果页面-->当前拍照照片
  		 tv_vfr_pic=(ImageView)findViewById(R.id.tv_vfr_pic);
  		//右侧（拍照 失败页面+验证结果页面）-->验证结果页面-->成功失败标记图片
  		 tv_vfr_sig=(ImageView)findViewById(R.id.tv_vfr_sig);
  		//右侧（拍照 失败页面+验证结果页面）-->验证结果页面-->品质分值
  		 tv_vfr_similarity=(TextView)findViewById(R.id.tv_vfr_similarity);
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
  	public void onResume() {
  		super.onResume();
  		logLifeSycle("onResume-------");
  		if (sFRInitFlag&&surface_view_face != null){
  			
  			if(!handleBroadCastFlag){
  				surface_view_face.enableView();
  			}
  		}
  		//sendResumeReadCardBroadCast();
  		registerReceivers();
  	}
/*  //发送广播,继续监听身份证阅读器
  	private void sendResumeReadCardBroadCast(){
  		 sendBroadcast(new Intent().setAction(CardService.BROADCAST)
					.putExtra("command", CardService.CMD_RESUME_READ));
  	}*/
  	@Override
  	public void onPause() {
  		super.onPause();
  		logLifeSycle("onPause-------");
  		unregisterReceiver(mActivityReceiver);
  		//sendPauseReadCardBroadCast();
  		if (surface_view_face != null)
  			disableSurfaceView();
  	}
  	//发送广播,暂停监听身份证阅读器
  	/*private void sendPauseReadCardBroadCast(){
  		 sendBroadcast(new Intent().setAction(CardService.BROADCAST)
					.putExtra("command", CardService.CMD_PAUSE_READ));
  	}*/
  	
  	private void initCameraView() {
		surface_view_face =(MyJavaCameraView) findViewById(R.id.fd_activity_surface_view);
		surface_view_face.setCameraIndex(paramStorage.getCamera_front_back()==0?
				CameraBridgeViewBase.CAMERA_ID_FRONT:CameraBridgeViewBase.CAMERA_ID_BACK);
		surface_view_face.setMaxFrameSize(640, 480);
		surface_view_face.setCvCameraViewListener(this);
		surface_view_face.disableFpsMeter();
	}

	private void initDetector(){
        File mCascadeFile = new File(GloabalVar.cascadeDir, "lbpcascade_frontalface.xml");
        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        if (mJavaDetector.empty()) {
            mJavaDetector = null;
        }
    }
	private int validFrameCount;
    private void faceDetect(){
    	if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }
        MatOfRect faces = new MatOfRect();
		if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null){//.submat(roi)
            	double mSize_w_h=358*0.4;
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, 
                		  new Size(mSize_w_h, mSize_w_h), new Size(358,441));
            }
        }
		if(isToatalTimout()){
			if(!verifyIng){
				sendToatalTimoutMessage();
				return;
			}
		}
        facesArray = faces.toArray();
        boolean isPreFaceInRectTemp=isPreFaceInRect;
        if(facesArray.length==1){
        	//如果人脸在相框区域内,且人脸足够够大时
        	if(facesArray[0].tl().x>141
        			&&facesArray[0].br().x<499
        			&&facesArray[0].tl().y>20
        			&&facesArray[0].br().y<461){
        		if(!isPreFaceInRect){
        			faceInRect_start_time=System.currentTimeMillis();
        			isPreFaceInRect=true;
        		}
        		validFrameCount++;
        		if(!isFaceInRectTimeOut()){
        			if((!verifyIng)&&System.currentTimeMillis()-faceInRect_start_time>500
        					&&validFrameCount>Consts.PARAM_MIN_VALID_FRAME_COUNT&&
        					(pre_verifyResult_code==-1||pre_verifyResult_code==MSG_VER_FAIL_LESS_THAN_MIN_SIMILARITY)){
        				Log.i("sendVerMessage", "----------------------");
        				//发送验证消息
            			sendVerMessage(mRgba.submat(roi).clone());
        			}
        		}
        		Core.rectangle(mRgba, facesArray[0].tl(), facesArray[0].br(), FACE_RECT_COLOR, 1);
        	}else{
        		validFrameCount=0;
        		if(System.currentTimeMillis()-faceInRect_start_time>500){
        			if(isPreFaceInRect){
            			if(System.currentTimeMillis()-faceNotInRect_start_time<50){
                    		if(!verifyIng){
                    			soundPlayer.playFaceInRect();	
                    		}
                    	}
            		}
        			isPreFaceInRect=false;
        		}
        		
        	}
        }else{
        	validFrameCount=0;
        	if(System.currentTimeMillis()-faceInRect_start_time>500){
        		if(isPreFaceInRect){
        			if(System.currentTimeMillis()-faceNotInRect_start_time<50){
                		if(!verifyIng){
                			soundPlayer.playFaceInRect();	
                		}
                	}
        		}
    			isPreFaceInRect=false;
    		}
        }
        //如果不在验证过程中，且在框框外待的时间超过1s就提醒用户
    	if(isPreFaceInRectTemp==true&&(!isPreFaceInRect)){
    		faceNotInRect_start_time=System.currentTimeMillis();
    	}
    	if(System.currentTimeMillis()-faceNotInRect_start_time>2000){
    		if(!verifyIng&&!isPreFaceInRect){
    			soundPlayer.playFaceInRect();	
    		}
    		faceNotInRect_start_time=System.currentTimeMillis();
    	}
    }
  //话轮照片区域线
  		private void cardPhotoBorderOnFrame(Mat mat){
  			//width=358px
  			//height=441px
  			//640-358=282
  			//480-440=40
  			//LEFT:141,TOP:20
  			//if(handleBroadCastFlag==false){
  				if(mat!=null&&mat.cols()==640&&mat.rows()==480){
  		  			//上横线
  		  			Core.line(mat,POINT_PHOTO_HORIZONTAL_LINE_LT, POINT_PHOTO_HORIZONTAL_LINE_RT,CARD_PHOTO_BORDER_COLOR,1);
  		  			//下横线
  		  			Core.line(mat,POINT_PHOTO_HORIZONTAL_LINE_LB, POINT_PHOTO_HORIZONTAL_LINE_RB,CARD_PHOTO_BORDER_COLOR,1);
  		  			//左竖线
  		  	
  		  			Core.line(mat,POINT_PHOTO_VERTICAL_LINE_LT, POINT_PHOTO_VERTICAL_LINE_LB,CARD_PHOTO_BORDER_COLOR,1);
  		  			//右竖线
  		  			Core.line(mat,POINT_PHOTO_VERTICAL_LINE_RT, POINT_PHOTO_VERTICAL_LINE_RB,CARD_PHOTO_BORDER_COLOR,1);
  	  			}
  		}
    /*
     * 裁切当前拍照照片并保存
     */
  	Rect roi=new Rect((int)POINT_PHOTO_BORDER_LT.x,(int) POINT_PHOTO_BORDER_LT.y,CARD_PHOTO_WIDTH,CARD_PHOTO_HEIGHT);
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    
    public void hideLoading() {
    	if(pb_loading!=null)
    		pb_loading.setVisibility(View.GONE);
      }

     public void showLoading() {
    	 if(pb_loading!=null)
    		 pb_loading.setVisibility(View.VISIBLE);
    }
   
     
 
  
     @Override
    protected void onStop() {
    	super.onStop();
    	
    	//Activity不可见
    	//第一种情况，正在处理（拍照前获取，验证阶段，结果显示阶段）
    	//拍照前获取 直接disable摄像头
    	//验证阶段   
    	//结果显示阶段 中途被打断 不处理
    	logLifeSycle("onStop-------");

    }
     
     @Override
    protected void onStart() {
    	super.onStart();
    	logLifeSycle("onStart-------");
    	
    }
     @Override
    protected void onRestart() {
    	super.onRestart();
    	logLifeSycle("onRestart-------");
    }
     @Override
    protected void onDestroy() {
    	super.onDestroy();
    	SesFaceRec.loadSuccess=false;
    	logLifeSycle("onDestroy-------");
    	handlerThread.quitSafely();
    	Intent it=new Intent(this, CardService.class);
    	handleBroadCastFlag=false;
		stopService(it);
    }
    @SuppressLint("NewApi")
	public void registerReceivers(){
    	if(mActivityReceiver==null){
    		 mActivityReceiver = new MainActivityReceiver();
    	}
  		IntentFilter filter = new IntentFilter();// 创建IntentFilter对象
  		// 注册一个广播，用于接收Activity传送过来的命令，控制Service的行为，如：发送数据，停止服务等
  		filter.addAction(BROADCAST);
  		// 注册Broadcast Receiver
  		registerReceiver(mActivityReceiver, filter);
     
    }

	
	//显示验证 结果，通过还是不通过
		private void showVerifyResultBySimilarity(String ImagePath2,float similarity) {
			 //摄像头预览以及身份证详细信息页面
			 ll_preview_root.setVisibility(View.GONE);
			//左侧身份证信息（仅code，name）+右侧（拍照 失败页面+验证结果页面）
			 ll_tak_verf.setVisibility(View.VISIBLE);
			//右侧（拍照 失败页面+验证结果页面）
			 fl_vfr_result.setVisibility(View.VISIBLE);
			//右侧（拍照 失败页面+验证结果页面）-->拍照 失败页面
			 ll_tak_err.setVisibility(View.GONE);
			//右侧（拍照 失败页面+验证结果页面）-->验证结果页面
			 ll_vfr_result.setVisibility(View.VISIBLE);
			 
			 
			 
			 Bitmap bm = BitmapFactory.decodeFile(cardInfo.getPicPath());
			 // 左侧身份证信息（仅code，name）-->身份证照片
	  		 tv_vfr_card_pic.setImageBitmap(bm);
	  		// 左侧身份证信息（仅code，name）-->姓名
	  		 tv_vfr_card_name.setText(cardInfo.getName());
	  		// 左侧身份证信息（仅code，name）-->身份证号码
	  		 tv_vfr_card_code.setText(cardInfo.getCard_code());
	  		 
	  		 
			bm = BitmapFactory.decodeFile(ImagePath2);
	  		 
			tv_vfr_pic.setImageBitmap(bm);

			//验证分值达到
			if(similarity>paramStorage.getMin_similarity()){
				tv_vfr_sig.setImageResource(R.drawable.yes);
				tv_vfr_similarity.setBackgroundResource(R.color.green_color);
				tv_vfr_similarity.setText(String.format("%1$.4f", similarity)+"(验证通过)");
			}else{
				tv_vfr_sig.setImageResource(R.drawable.no);
				tv_vfr_similarity.setBackgroundResource(R.color.red_color);
	
				tv_vfr_similarity.setText(String.format("%1$.4f", similarity)+"(验证不通过)");
			}
		}
		

		//是否刚刚验证通过
		/*private boolean verifyOkJustNow(String card_code,int minutes){
		
			boolean b=false;
			SQLiteDatabase db = FaceVerificationDatabaseHelper.getInstance().getReadableDatabase();  
			Cursor cusor=db.query(FaceVerificationDatabaseHelper.TABL_HISTORY, new String[]{"verifyWhen"}, "card_code =? ", new String[]{card_code}, null, null, null,"_id DESC LIMIT 1");
			if(cusor.moveToFirst()){
				String verifyWhen =cusor.getString(0);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				Date date = null;
				long diff=0;
				try {
					date = format.parse(verifyWhen);
					diff = new Date().getTime()-date.getTime();
					b=diff/1000<=minutes;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			cusor.close();
			db.close();
			return b;
		}
		*/
		 //拍照失败
		 private void onTakPhotoFalure(final String title,final String reason,final String advice){
			// getString(R.string.tak_photo_failure)
			 //"保存照片失败"
			//显示失败页面
			 disableSurfaceView();
			//显示失败页面
			showTakPhotoFalureViews(title,reason,advice);
			//2秒钟后
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					//进入预览拍照界面继续
					//showPreviewViews();
					showrPreviewAndClearCardInfoViews();
					pre_verifyResult_code=-1;
					handleBroadCastFlag=true;
				}
			},2000);
		 }
		//显示失败页面
		 private void showFalureViews(String title,String reason,String advice){
			 //摄像头预览以及身份证详细信息页面
			 ll_preview_root.setVisibility(View.GONE);
			//左侧身份证信息（仅code，name）+右侧（拍照 失败页面+验证结果页面）
			 ll_tak_verf.setVisibility(View.VISIBLE);
			//右侧（拍照 失败页面+验证结果页面）
			 fl_vfr_result.setVisibility(View.VISIBLE);
			//右侧（拍照 失败页面+验证结果页面）-->拍照 失败页面
			 ll_tak_err.setVisibility(View.VISIBLE);
			//右侧（拍照 失败页面+验证结果页面）-->验证结果页面
			 ll_vfr_result.setVisibility(View.GONE);
			 
			 updateTakPhotoFalureViews( title, reason, advice);
		 }
		 private void showTakPhotoFalureViews(String title,String reason,String advice){
			 showFalureViews(title, reason, advice);
		 }
		 private void updateTakPhotoFalureViews(String title,String reason,String advice){
			 //右侧（拍照 失败页面+验证结果页面）-->拍照 失败页面-->标题
			 tv_vfr_failure_title.setText(title);
			//右侧（拍照 失败页面+验证结果页面）-->拍照 失败页面-->原因
			 tv_vfr_failure_reason.setText(reason);
			 //右侧（拍照 失败页面+验证结果页面）-->拍照 失败页面-->建议
			 tv_vfr_failure_advice.setText(advice);
		 }
		//显示预览及身份证详细页面
		 private void showPreviewViews(){
			 //摄像头预览以及身份证详细信息页面
			 ll_preview_root.setVisibility(View.VISIBLE);
			//左侧身份证信息（仅code，name）+右侧（拍照 失败页面+验证结果页面）
			 ll_tak_verf.setVisibility(View.GONE);
		 }
		//显示预览及身份证详细页面,并且清空左侧的身份证信息
		 private void showrPreviewAndClearCardInfoViews(){
			 tv_current_similarity.setText("");
			 showPreviewViews();
			 //清空左侧的身份证信息
			 clearCardInfoViews();
		 }
		 
		 private void updateCardInfoToViews(CardInfo cardInfo,int code) {
			 	if(cardInfo!=null){
					et_card_name.setText(cardInfo.getName());
					et_card_sex.setText(cardInfo.getSex()==1?"男":"女");
					et_card_nation.setText(CardInfoUtil.decodeNation(cardInfo.getNation()));
					et_card_fzjg.setText(cardInfo.getFzjg());
					et_card_code.setText(cardInfo.getCard_code());
					et_card_address.setText(cardInfo.getAddress());
					et_card_yrqx_begin.setText(cardInfo.getYrqx_begin());
					et_card_yrqx_end.setText(cardInfo.getYrqx_end());
					et_card_birthday.setText(cardInfo.getBirthday());
					if(code==1){
						Bitmap bmp_pic = BitmapFactory.decodeFile(cardInfo.getPicPath());
						iv_id_face.setImageBitmap(bmp_pic);
					}else {
						iv_id_face.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.face));
					}
			 	}
			}

		//清空左侧的身份证信息
		 private void clearCardInfoViews(){
			 et_card_name.setText("");
			et_card_sex.setText("");
			et_card_nation.setText("");
			et_card_fzjg.setText("");
			et_card_code.setText("");
			et_card_address.setText("");
			et_card_yrqx_begin.setText("");
			et_card_yrqx_end.setText("");
			et_card_birthday.setText("");
			iv_id_face.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.face));
		 }
		 @Override
			public void onCameraViewStarted(int width, int height) {
		        mGray = new Mat();
		        mRgba = new Mat();
		     
			}
			@Override
			public void onCameraViewStopped() {
				if (mGray != null) {
					mGray.release();
				}
				if (mRgba != null) {
					mRgba.release();
				}
			}
			private long preFrameTime;
			@Override
			public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
				long ct=System.currentTimeMillis();
				if(ct-preFrameTime>50){
					
					mGray = inputFrame.gray();
					mRgba = inputFrame.rgba();
					//人脸检测
					faceDetect();
					//画相框
					cardPhotoBorderOnFrame(mRgba);
					preFrameTime=ct;
				}
				return mRgba;
			}
			private class MainActivityReceiver extends BroadcastReceiver {
				@Override
				public void onReceive(Context context, Intent intent) {
					if (intent.getAction().equals(BROADCAST)) {
						Bundle bundle = intent.getExtras();
						int cmd = bundle.getInt("command");// 获取Extra信息
						switch (cmd) {
						case 1:
							if(sFRInitFlag){
								int code=bundle.getInt("code");
								if(code==1){
									Object cardInfoObject =bundle.getSerializable("cardInfo");
									if(cardInfoObject!=null){
										Log.d("MainActivityReceiver", "--------------");
										if(handleBroadCastFlag){
											handleBroadCastFlag=false;
											MainActivity.this.cardInfo=(CardInfo)cardInfoObject;
											updateCardInfoToViews(cardInfo, code);
											toatalTimout_start_time=System.currentTimeMillis();
											surface_view_face.enableView();
										      //播放 人脸放框里...
										    //soundPlayer.playFaceInRect();
											tv_please_place_face.setVisibility(View.VISIBLE);
											tv_current_similarity.setText("");
											Log.i("notifyReadCard", cardInfo.toString());
										}
									}
								}
							}
							break;
						case 2:
							boolean connected=bundle.getBoolean("connected");
							String content=connected?"已连接身份证阅读器":"未连接身份证阅读器";
							Toast.makeText(getApplicationContext(),content , Toast.LENGTH_LONG).show();
							break;
						default:
							break;
						}
					}
				}
		}
		@Override
		public void onBackPressed() {
			logLifeSycle("onBackPressed-------");
			if(sfrInitInteractor.isExecuting()){
				return;
			}
			if(verifyIng){
				return;
			}
			super.onBackPressed();
		}
		
		//监听Home键，不能拦截，在onSaveInstanceState之前触发
		@Override
		public void onUserInteraction() {
			super.onUserInteraction();
		}
		private void disableSurfaceView(){
			surface_view_face.disableView();
			Canvas canvas = surface_view_face.getHolder().lockCanvas();
            if (canvas != null) {
                canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
                surface_view_face.getHolder().unlockCanvasAndPost(canvas);
            }
		}
		@Override
		public String getLogTag() {
			return TAG;
		}
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_id_face://进入设置页面
				Intent intent=new Intent(this, SetAndHistoryActivity.class);
				startActivityForResult(intent, REQUEST_CODE_SET_PARAM);
				break;
			default:
				break;
			}
		}
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
			case RESULT_OK:
				if(requestCode==REQUEST_CODE_SET_PARAM){
					if(data!=null&&data.hasExtra("switchCamera")){
						Bundle b=data.getExtras(); //data为B中回传的Intent
						boolean switchCamera=b.getBoolean("switchCamera");
						if(switchCamera
								&&paramStorage.getCamera_front_back()
								!=(surface_view_face.getCameraIndex()==CameraBridgeViewBase.CAMERA_ID_FRONT?0:1)){
							//切换摄像头
							surface_view_face.setCameraIndex(paramStorage.getCamera_front_back()==0?
									CameraBridgeViewBase.CAMERA_ID_FRONT:CameraBridgeViewBase.CAMERA_ID_BACK);
						}
					}
				}
			
			break;
			default:
			break;
			}
		}
		//验证通过
		 HandlerThread handlerThread = new HandlerThread("verify");
		 
		 Handler threadHandler;  
		 private void initThreadHandler(){
			 handlerThread.start();
			 Looper looper = handlerThread.getLooper();
			 threadHandler = new Handler(looper, new Handler.Callback() {  
		            @Override  
		            public boolean handleMessage(Message msg) {  
		            
		               return false; //外层的handleMessage() 继续执行  
		            }  
		        }) {
				 float similarity;
				 String ImagePath2;
				  void sendVerResultMessage(int arg1,String ImagePath2){
					  Message msg=Message.obtain(mainVerifyHandler);
	  					msg.arg1=arg1;
	  					if(msg.arg1==MSG_VER_SUCCESS||msg.arg1==MSG_VER_FAIL_LESS_THAN_MIN_SIMILARITY){
	  						//msg.obj=bitmap;
	  						msg.getData().putString("ImagePath2", ImagePath2);
	  						msg.getData().putFloat("similarity", similarity);
	  					}
	  					msg.sendToTarget();
				  }
				  long prePlayVeringTime;
		            @Override  
		            public void handleMessage(Message msg) {
		            	verifyIng=true;
		            	File f=null;
		            	Bitmap bm = Bitmap.createBitmap(358, 441, Config.ARGB_8888);
		            	try {
		            		if(pre_verifyResult_code==-1||pre_verifyResult_code==MSG_VER_FAIL_LESS_THAN_MIN_SIMILARITY){
			            		if(System.currentTimeMillis()-prePlayVeringTime>2000){
			            			soundPlayer.play(1);
			            			prePlayVeringTime=System.currentTimeMillis();
			            		}
				            	verifyResult_code=MSG_VER_FAIL_OTHER;
				                //保存图片
				            	Mat bd = (Mat) msg.obj;
				        		/*int w = bd.cols();
				        		int h = bd.rows();*/
				        		//bm = Bitmap.createBitmap(w, h, Config.ARGB_8888);
				        		Utils.matToBitmap(bd, bm);
				        		String fileName = cardInfo.getName()+"_"+cardInfo.getCard_code()+"_"+System.currentTimeMillis()+".png";
				        		ImagePath2=GloabalVar.takPicDir + File.separator + fileName;
				        		f = new File(ImagePath2);
				        		boolean ok = FileUtil.saveBitmap(bm, GloabalVar.takPicDir,fileName);
				        		if(!ok){
				        			deleteFile(ImagePath2);
				        			verifyResult_code=MSG_VER_FAIL_SAVE_PIC;
				        			
				        		}else{
				        			//保存照片成功，开始验证
				        			SesFaceRec sfr = FaceVerificationApplication.getSingleInstance().getSesFaceRec();
							    	similarity = sfr.sfsGetFaceSimilarityByPath(cardInfo.getPicPath(), -1, ImagePath2, -1);
							    	Log.i("similarity","similarity:::::::"+similarity);
							    	//分值达到了，将验证通过的结果保存
							    	if(similarity>paramStorage.getMin_similarity()){
										//将身份证信息存入数据库
							    		CardInfoDBManager.saveCardInfoToDb(cardInfo);
							    		CardInfoDBManager.saveVerifyHistory(cardInfo,similarity,ImagePath2);
							    		CardInfoDBManager.persistRecentHistory(Consts.PARAM_PERSIST_RECENT_RECORDS);
							    		verifyResult_code=MSG_VER_SUCCESS;
							    	}else{
							    		verifyResult_code=MSG_VER_FAIL_LESS_THAN_MIN_SIMILARITY;
							    	}
				        		}
				        		try {
				        			bd.release();
								} catch (Exception e) {
								}
				        		
				                pre_verifyResult_code=verifyResult_code;
				               
			            	}
						} catch (Exception e) {
							
							verifyResult_code=MSG_VER_FAIL_OTHER;
							pre_verifyResult_code=verifyResult_code;
						}
		            	//如果现在人脸
		            	 if(verifyResult_code==MSG_VER_SUCCESS){
		            		 //发送成功消息
		            		 sendVerResultMessage(MSG_VER_SUCCESS,ImagePath2);
		            	 }else if(verifyResult_code==MSG_VER_FAIL_LESS_THAN_MIN_SIMILARITY){
		            		 Message msgg=Message.obtain(mHandler);
		            		 msgg.what=100;
		            		 msgg.obj=similarity;
		            		 msgg.sendToTarget();
		            		 
		            		 
		            		 if(isPreFaceInRect){
	            				if(isFaceInRectTimeOut()){
	            					//发送验证失败消息
	            					sendVerResultMessage(MSG_VER_FAIL_LESS_THAN_MIN_SIMILARITY,ImagePath2);
	            				}else{
	            					//不发送消息, 继续等待验证
	            					 try {
			            				 f.delete();
									} catch (Exception e) {
									}
			            		
	            				}
		            		}else{
		            			 try {
		            				 f.delete();
								} catch (Exception e) {
								}
		            		}
		            		
			            		
		            	 }else if(verifyResult_code==MSG_VER_FAIL_SAVE_PIC){
		            		//发送保存图片失败消息
		            		 sendVerResultMessage(MSG_VER_FAIL_SAVE_PIC,null);
		            	 }else if(verifyResult_code==MSG_VER_FAIL_OTHER){
		            		//发送其他消息
		            		 sendVerResultMessage(MSG_VER_FAIL_OTHER,null);
		            	 }
		            	 verifyIng=false;
		            } 
		        };  
		        
		        
		 }
		 private boolean verifyIng;
		 private int verifyResult_code=MSG_VER_FAIL_OTHER;
		 private int pre_verifyResult_code=-1;
		 private boolean isPreFaceInRect;
		 private long toatalTimout_start_time;
		 private long faceInRect_start_time;
		 private long faceNotInRect_start_time;
		 
		 private boolean isToatalTimout(){
			return System.currentTimeMillis()-toatalTimout_start_time>paramStorage.getTimeout_tak_pic()*1000;
		 }
		 private boolean isFaceInRectTimeOut(){
			return System.currentTimeMillis()-faceInRect_start_time>Consts.Max_FaceInRectTimeOut*1000;
		 }
		 private static final int MSG_VER_SUCCESS=0;
		 private static final int MSG_VER_FAIL_LESS_THAN_MIN_SIMILARITY=1;
		 private static final int MSG_VER_FAIL_SAVE_PIC=2;
		 private static final int MSG_VER_FAIL_OTHER=3;
		 private static final int MSG_VER_FAIL_TOTAL_TIMEOUT=4;
		 void sendToatalTimoutMessage(){
			  Message msg=Message.obtain(mainVerifyHandler);
					msg.arg1=MSG_VER_FAIL_TOTAL_TIMEOUT;
					msg.sendToTarget();
		  }
		 void sendVerMessage(Mat mat){
			  Message msg=Message.obtain(threadHandler);
					msg.obj=mat;
					msg.sendToTarget();
		  }
		 private Handler  mainVerifyHandler =new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.arg1) {
				case MSG_VER_FAIL_SAVE_PIC:
					soundPlayer.playPZSB();
					onTakPhotoFalure(getString(R.string.tak_photo_failure),
							"保存照片失败","");
					break;
				case MSG_VER_FAIL_OTHER:
					soundPlayer.playPZSB();
					onTakPhotoFalure(getString(R.string.tak_photo_failure),
							"未知异常","");
					break;
					
				case MSG_VER_SUCCESS:
				case MSG_VER_FAIL_LESS_THAN_MIN_SIMILARITY:
					onVerSuccessOrFail(msg.getData().getFloat("similarity"),msg.getData().getString("ImagePath2"));
					break;
				case MSG_VER_FAIL_TOTAL_TIMEOUT:
					soundPlayer.playPZSB();
					onTakPhotoFalure(getString(R.string.ver_time_out),
							"验证超时","");
					break;
				default:
					break;
				}
			}; 
		 };
		 private void onVerSuccessOrFail(float similarity,String ImagePath2){
			//等两秒中
				disableSurfaceView();
				if(similarity>paramStorage.getMin_similarity()){
					soundPlayer.playYZTG();
				}else{
					soundPlayer.playYZBTG();
				}
				showVerifyResultBySimilarity(ImagePath2,similarity);
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						showrPreviewAndClearCardInfoViews();
						pre_verifyResult_code=-1;
						handleBroadCastFlag=true;
					}
				}, 2000);
		 }
}
