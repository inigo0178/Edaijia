package cn.edaijia.android.client.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.model.TitleDetail;

public class AboutAdapter extends BaseAdapter {
	
	/** 关于e代驾   或 分享**/
	private ArrayList<TitleDetail> titleDetails;
	private LayoutInflater inflater;
	public AboutAdapter(){}
	public AboutAdapter(Context context,ArrayList<TitleDetail> titleDetails){
		this.titleDetails = titleDetails;
		inflater = LayoutInflater.from(context);
		
	}
	@Override
	public int getCount() {
		
		return titleDetails.size();
	}

	@Override
	public Object getItem(int position) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHandler mHandler;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.common_about, null);
			mHandler = new ViewHandler();
			convertView.setTag(mHandler);
		}else{
			mHandler = (ViewHandler) convertView.getTag();
		}
		TitleDetail details = titleDetails.get(position);
		
		mHandler.selecttype_title = (TextView) convertView.findViewById(R.id.selecttype_title);
		mHandler.selecttype_text = (TextView) convertView.findViewById(R.id.selecttype_text);
		mHandler.selecttype_text.setText(details.getRightInfo());
		mHandler.selecttype_title.setText(details.getLeftInfo());
		
		return convertView;
	}
	private  static class ViewHandler{
		TextView selecttype_title;
		TextView selecttype_text;
	}
}
