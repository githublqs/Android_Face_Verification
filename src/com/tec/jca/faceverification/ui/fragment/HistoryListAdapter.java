package com.tec.jca.faceverification.ui.fragment;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.tec.jca.faceverification.R;
import com.tec.jca.faceverification.entity.VerifyHistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
public class HistoryListAdapter extends BaseAdapter{
	private Context mContext;
	private List<VerifyHistory> list;
	private LayoutInflater mInflater;
	private SimpleDateFormat format;
	public HistoryListAdapter(Context context,List<VerifyHistory> list){
		this.mContext=context;
		this.list=list;
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		mInflater = LayoutInflater.from(mContext);
	}
	

	@Override
	public int getCount() {
		
		return list==null?0:list.size();
	}
	@Override
	public VerifyHistory getItem(int position) {
		return list==null?null:list.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh=null;
		if(null==convertView){
			vh=new ViewHolder();
			convertView=mInflater.inflate(R.layout.layout_list_item_history, null);
			vh.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
			vh.tv_card_code=(TextView) convertView.findViewById(R.id.tv_card_code);
			vh.tv_similarity=(TextView) convertView.findViewById(R.id.tv_similarity);
			vh.tv_verifyWhen=(TextView) convertView.findViewById(R.id.tv_verifyWhen);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder) convertView.getTag();
		}
		VerifyHistory verifyHistory=getItem(position);
		vh.tv_name.setText(verifyHistory.getName());
		vh.tv_card_code.setText(verifyHistory.getCard_code());
		float sim = Float.parseFloat(verifyHistory.getSimilarity());
		vh.tv_similarity.setText(String.format("%1$.4f",sim ));
		try {
			vh.tv_verifyWhen.setText(format.format(new Date(Long.parseLong(verifyHistory.getVerifyWhen()))));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertView;
	}
	private class ViewHolder{
		TextView tv_name;
		TextView tv_card_code;
		TextView tv_similarity;
		TextView tv_verifyWhen;
	}
}
