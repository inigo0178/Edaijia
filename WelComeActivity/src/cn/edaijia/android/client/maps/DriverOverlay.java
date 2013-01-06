package cn.edaijia.android.client.maps;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import cn.edaijia.android.client.EDriverApp;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.activity.DriverDetails;
import cn.edaijia.android.client.util.Logger;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
/**
 * ��ͼ������	�������λ�� �ܱ�˾��λ��
 * @author CaoZheng		email:caozheng_119@163.com
 * @create time:2012-8-10 10:07
 * --------------------last update:----------------- 
 * coder:                            update
 * time: Copyright (c) YDJ corporation All Rights Reserved. 
 * INFORMATION
 */
public class DriverOverlay extends ItemizedOverlay<OverlayItem> {
	
	/** ���� **/
	private static final String CLASS_NAME = DriverOverlay.class.getName();
	
	/** �����ͼ�������еı�Ƕ��� **/
	private List<OverlayItem> marks;

	/** ������ **/
	private Context mContext;

	/** �����Ϣ���� **/
	List<DriverRecord> data = null;

	/** ͼƬ��Դ **/
	Drawable marker;
	
	private LayoutInflater mInflater;
	
	private View view;
	
	private LinearLayout driver = null;
	
	private TextView driName = null;
	
	private RatingBar driRating = null;
	
	private float coefficient = 0;
	
	private Logger mLogger = Logger.createLogger("DriverOverlay");
	
	/** ���캯�� **/
	public DriverOverlay(Context context, MapView mapview, List<DriverRecord> dataList) {
		super(null);
		this.mContext = context;
		this.data = dataList;
		this.coefficient = EDriverApp.getMarkCoefficient();
		mInflater = LayoutInflater.from(mContext);
		updataDriverMarks(this.data);
	}

	@Override
	protected OverlayItem createItem(int index) {
		mLogger.d(CLASS_NAME + ".createItem ---------->>>");
		mLogger.d(CLASS_NAME + ".createItem index="+index);

		// Overlay����
		DriverRecord record = this.data.get(index);
		GeoPoint geoPoint = new GeoPoint((int)(record.getLatitude()*1E6), (int)(record.getLongtitude()*1E6));
		OverlayItem overlayItem = new OverlayItem(geoPoint, "", "");
		
		// OverlayͼƬ����
//		marker = mMapView.getContext().getResources().getDrawable(R.drawable.mark);
		
		view = mInflater.inflate(R.layout.test, null);
		driver = (LinearLayout) view.findViewById(R.id.driver_location);
		driName = (TextView) view.findViewById(R.id.driver_location_name);
		driRating = (RatingBar) view.findViewById(R.id.driver_location_ratingBar);
		
		
		if ("0".equals(record.getState())) {
			driver.setBackgroundResource(R.drawable.search_annotation_green);
		} else {
			driver.setBackgroundResource(R.drawable.search_annotation_red);
		}
		
		driName.setTextSize(17 * coefficient);
		driName.setText(record.getName());	
		driRating.setNumStars(Integer.parseInt(record.getLevel()));
		driRating.setRating(Float.parseFloat(record.getLevel()));
		
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, (int)(view.getMeasuredWidth() * coefficient), (int)(view.getMeasuredHeight() * coefficient));
//		view.layout(0, 0, 300, 130);
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		
		Drawable mark = new BitmapDrawable(bitmap);
		
//		overlayItem.setMarker(boundCenterBottom(marker));
		overlayItem.setMarker(boundCenterBottom(mark));
		
		//add
		marks.add(overlayItem);
		mLogger.d(CLASS_NAME + ".createItem <<<----------");
		return marks.get(index);
	}

	@Override
	public int size() {
		return this.data.size();
	}
	
	/** ����һ����� */
	public void addOverlayItem(OverlayItem item){
		marks.add(item);
		populate();
	}
	
//	/** ɾ��һ����� */
//	private void deleteOverlayItem(int index){
//		data.remove(index);
//		populate();
//	}
	
	/***
	 * ���¸���˾������Ϣ
	 */
	public void updataDriverMarks(List<DriverRecord> dataList){
		mLogger.d("updataDriverMarks--->");
		this.data = dataList;
		marks = new ArrayList<OverlayItem>();
		populate();
		mLogger.d("<---updataDriverMarks");
	}
	
	//��ͼ��ǵ���¼�
	@Override
	protected boolean onTap(int index) {
		Intent in = new Intent();
		in.setClass(mContext, DriverDetails.class);
		in.putExtra("info", data.get(index));
		mContext.startActivity(in);
		return super.onTap(index);
	}
}
