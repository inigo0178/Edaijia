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

public class PriceListAdapter extends BaseAdapter {

	LayoutInflater mInflater;
	private ArrayList<TitleDetail> titleDetails;
	private TitleDetail titleDetail;
	public PriceListAdapter() {
	}
	public PriceListAdapter(Context context,ArrayList<TitleDetail> titleDetails){
		this.titleDetails = titleDetails;
		mInflater = LayoutInflater.from(context);
		
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		
		return false;
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
		
		ViewHandler vHandler ;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.common_price, null);
			vHandler = new ViewHandler();
			vHandler.price_tv_l = (TextView) convertView.findViewById(R.id.price_tv_l);
			vHandler.price_tv_r = (TextView) convertView.findViewById(R.id.price_tv_r);
			
			convertView.setTag(vHandler);
		}else{
			vHandler = (ViewHandler) convertView.getTag();
		}
			titleDetail = titleDetails.get(position);
			vHandler.price_tv_l.setText(titleDetail.getLeftInfo());
			vHandler.price_tv_r.setText(titleDetail.getRightInfo());
			
		return convertView;
	}
	private static class ViewHandler{
		TextView price_tv_l;
		TextView price_tv_r;
	}
}
