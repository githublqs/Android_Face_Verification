package com.tec.jca.faceverification.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.tec.jca.faceverification.Consts;
import com.tec.jca.faceverification.R;
import com.tec.jca.faceverification.db.FaceVerificationDatabaseHelper;
import com.tec.jca.faceverification.entity.VerifyHistory;
import com.tec.jca.faceverification.ui.RefreshLayout;
import com.tec.jca.faceverification.ui.RefreshLayout.OnLoadListener;
import com.tec.jca.faceverification.ui.activity.HistoryDetailActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
public class HistoryFragment extends BaseFragment implements OnRefreshListener, OnLoadListener, OnItemClickListener {
	private static final String TAG="HistoryFragment";
	private  int TableRowCount;
	private ListView lv_history;
	private List<VerifyHistory> records;
	private int pageID;
	private HistoryListAdapter historyListAdapter;
	private RefreshLayout mSwipeRefreshWidget;
	private Handler mHandler=new Handler();
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		logLifeSycle("onAttach-----");
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		TableRowCount=Consts.PARAM_PERSIST_RECENT_RECORDS;
		logLifeSycle("onCreate-----");
	}
	private Runnable mRefreshDone = new Runnable() {  
		  
        @Override  
        public void run() {  
        	List<VerifyHistory> data = getFirstPageData();
        	addDataToListView(true, data);
            mSwipeRefreshWidget.setRefreshing(false);  
        }  
    };  
    private Runnable mNextPageDone = new Runnable() {  
        @Override  
        public void run() { 
        	List<VerifyHistory> data = getNextPageData();
        	addDataToListView(false, data);
        	mSwipeRefreshWidget.setLoading(false);  
        }  
    };  
	//Ã»ÓÐonRestart
	@Override
	public void onStart() {
		super.onStart();
		logLifeSycle("onStart-----");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_hostory, null);
		records=new ArrayList<VerifyHistory>();
		historyListAdapter=new HistoryListAdapter(getContext(), records);
		lv_history=(ListView) view.findViewById(R.id.listview);
		lv_history.setOnItemClickListener(this);
		lv_history.setAdapter(historyListAdapter);
		addDataToListView(true, getFirstPageData());
		mSwipeRefreshWidget = (RefreshLayout) view.findViewById(R.id.swipe_layout);  
        mSwipeRefreshWidget.setColorScheme(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);  
        mSwipeRefreshWidget.setOnRefreshListener(this);  
        mSwipeRefreshWidget.setOnLoadListener(this);
        mSwipeRefreshWidget.setProgressViewEndTarget(false, 8);  
		logLifeSycle("onCreateView-----");
		return view;
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
		super.onDestroy();
		logLifeSycle("onDestroy-----");
	}
	@Override
	public String getLogTag() {
		return TAG;
	}
	private List<VerifyHistory> getDataByPage(){
		List<VerifyHistory > list=new ArrayList<VerifyHistory>();
		SQLiteDatabase db = FaceVerificationDatabaseHelper.getInstance().getReadableDatabase();
		ContentValues values= new ContentValues();
		Cursor cursor = db.query(FaceVerificationDatabaseHelper.TABL_HISTORY, new String[]{"name","card_code","photoPath","similarity","verifyWhen"}, 
				null, null, null, null, " _id desc Limit "+String.valueOf(TableRowCount)+ " Offset " +String.valueOf(pageID*TableRowCount));
		while(cursor.moveToNext()){
			list.add(new VerifyHistory(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getLong(4)+""));
		}
		db.close();
		return list;
		
	}
	private void addDataToListView(boolean clear ,List<VerifyHistory> data){
		if(clear){
			records.clear();
		}
		if(data!=null){
			records.addAll(data);
		}
		historyListAdapter.notifyDataSetChanged();
	}
	private List<VerifyHistory> getNextPageData(){
		pageID++;
		return getDataByPage();
	}
	private List<VerifyHistory> getFirstPageData(){
		pageID=0;	
		return getDataByPage();
	}
	@Override
	public void onRefresh() {
		mHandler.removeCallbacks(mRefreshDone);
		mHandler.postDelayed(mRefreshDone,1000);
	}
	@Override
	public void onLoad() {
		mHandler.removeCallbacks(mNextPageDone);
		mHandler.postDelayed(mNextPageDone,1000);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent=new Intent(getActivity(), HistoryDetailActivity.class);
		VerifyHistory verifyHistory=historyListAdapter.getItem(position);
		intent.putExtra("card_code", verifyHistory.getCard_code());
		intent.putExtra("photoPath", verifyHistory.getPhotoPath());
		intent.putExtra("similarity", verifyHistory.getSimilarity());
		intent.putExtra("verify_time",verifyHistory.getVerifyWhen());
		startActivity(intent);
	}
}
