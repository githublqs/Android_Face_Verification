package com.tec.jca.faceverification.ui.activity;
import java.util.List;
import com.tec.jca.faceverification.R;
import com.tec.jca.faceverification.ui.fragment.HistoryFragment;
import com.tec.jca.faceverification.ui.fragment.SetFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
public class SetAndHistoryActivity extends BaseActivity implements SetFragment.SetFragmentListener{
	private static final String TAG="SetAndHistoryActivity";
	private SetFragment setFragment;
	private HistoryFragment historyFragment;
	private FrameLayout fl_container;
	private boolean isTablet;
	private ListView lv_menu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fullScreenAndShowActionBackButton();
		setContentView(R.layout.activity_set_and_history);
		fl_container=(FrameLayout)findViewById(R.id.fl_container);
		isTablet = findViewById(R.id.lv_menu)==null?false:true;
		if(isTablet){
			lv_menu=(ListView)findViewById(R.id.lv_menu);
			lv_menu.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1,new String[]{"设置","历史纪录"}));
			lv_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					onLeftButtonClick(position);
				}
			});
		}
		addFragments();
		logLifeSycle("onCreate----");
	}
	private void addFragments() {
		//在同一个FragmentTransaction 中 add 多个 一并触发生命周期
		//而replace 会替换掉前面的fragment,所以只有replace的那个fragment的证明周期被执行
		//多个 FragmentTransaction 循序排列 互不影响 ;
		//addToBackStack 只打包看待当前FragmentTransaction 中所管理的frament
		//有几个back 几个
		setFragment=new SetFragment();
		setFragment.setListener(this);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.fl_container, setFragment, "frat_set");
		ft.commit();
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		logLifeSycle("onStart----");
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		logLifeSycle("onRestart----");
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		logLifeSycle("onResume----");
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		logLifeSycle("onPause----");
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		logLifeSycle("onStop----");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		logLifeSycle("onDestroy----");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.manage, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(!isTablet){
			Fragment frgt=getVisibleFragment();
			if(frgt!=null){
				if(frgt instanceof SetFragment){
					menu.findItem(R.id.action_go_history).setVisible(true);
					
				}else if(frgt instanceof HistoryFragment){
					menu.findItem(R.id.action_go_history).setVisible(false);
				}
			}
		}else{
			menu.findItem(R.id.action_go_history).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		
		  if(item.getItemId() == R.id.action_go_history){  
			  addHistoryFragment();
		    }  
		  if(item.getItemId()==android.R.id.home){
			  onBackPressed();
		  }
		return super.onOptionsItemSelected(item);
	}
	
	private void addHistoryFragment(){
		if( getSupportFragmentManager().findFragmentByTag("frgt_history")==null){
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			historyFragment=new HistoryFragment();
			ft.replace(R.id.fl_container, historyFragment,"frgt_history");  
			ft.addToBackStack(null);
			ft.commit();
			
		}
	}


	private Fragment getVisibleFragment(){
	    FragmentManager fragmentManager = getSupportFragmentManager();
	    List<Fragment> fragments = fragmentManager.getFragments();
	    for(Fragment fragment : fragments){
	        if(fragment != null && fragment.isVisible())
	            return fragment;
	    }
	    return null;
	}
	@Override
	public String getLogTag() {
		// TODO Auto-generated method stub
		return TAG;
	}
	private void onLeftButtonClick(int id){
		
		if(id==0){
			popBackStack();
			
		}else if(id==1){
			addHistoryFragment();
		}
	}
	private void popBackStack(){
		Fragment f = getVisibleFragment();
		if(f!=null&& f instanceof HistoryFragment){
			getSupportFragmentManager().popBackStack();	
		}
	}
	@Override
	public void onBackPressed() {
		Fragment f = getVisibleFragment();
		if(f!=null&& f instanceof HistoryFragment){
			getSupportFragmentManager().popBackStack();	
			
		}else{
			super.onBackPressed();
		}
		
	}
	@Override
	public void onCancelSet() {
		onBackPressed();
	}
	@Override
	public void onSaveSet(boolean switchCamera) {
		Intent data=new Intent();
		data.putExtra("switchCamera", switchCamera);
		setResult(RESULT_OK, data);
		onBackPressed();
		
	}
}
