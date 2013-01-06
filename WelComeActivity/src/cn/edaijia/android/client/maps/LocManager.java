package cn.edaijia.android.client.maps;

import java.util.ArrayList;

import android.content.Context;
import cn.edaijia.android.client.util.Logger;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
/**
 * λ�÷��������	�ṩ��λ,POI��������
 * @author CaoZheng		email:caozheng_119@163.com
 * @create time:2012-8-10 10:07
 * --------------------last update:----------------- 
 * coder:                            update
 * time: Copyright (c) YDJ corporation All Rights Reserved. 
 * INFORMATION
 */

public class LocManager implements BDLocationListener{

	private Context mContext;

	private LocationClient mLocationClient = null;

	private LocationClientOption option = null;

	private volatile double lat,lont;

	private String addr = null;

	private ArrayList<EDLocationListener> mListenerList;

	private Logger mLogger = Logger.createLogger("LocManager");
	
	public LocManager(){}

	public LocManager(Context con){
		this.mContext = con;
		option = new LocationClientOption();
		option.setOpenGps(true);								//��gps
		option.setCoorType("bd09ll");							//������������Ϊbd09ll-�ٶȾ�γ������ϵ	gcj02-����־�γ������ϵ	bd09�ٶ�ī��������ϵ
		option.setPriority(LocationClientOption.NetWorkFirst);		//����GPS����
		option.setProdName("Edaijia");							//���ò�Ʒ������
		//		option.setScanSpan(1000);								//��ʱ��λ,<1000Ϊִ��һ��,>1000Ϊ��ʱ��λ
		option.disableCache(true);								//���û��涨λ

		//		option.setPoiNumber(5);									//poi�������ظ���
		//		option.setPoiDistance(300);								//poi������Χ
		//		option.setPoiExtraInfo(true);							//�Ƿ���Ҫpoi����ĵ绰��ַ����Ϣ
		setLocationClient();
		
		mListenerList = new ArrayList<EDLocationListener>();
	}

	public void addLocationListener(EDLocationListener listener){
	    if (listener != null && !mListenerList.contains(listener)){
	        this.mListenerList.add(listener);
	    }
	}
	
	public void removeLocationListener(EDLocationListener listener){
	    if (listener != null && !mListenerList.contains(listener)){
            this.mListenerList.remove(listener);
        }
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location == null){
			if (mListenerList != null ){
			    for (EDLocationListener listener : mListenerList){
			        if (listener != null && listener.isReceivedLocation()){
			            listener.onFailed();
			        }
			    }
			}
			return ;
		}
		
		if (location.getLocType() == BDLocation.TypeNetWorkLocation || location.getLocType() == BDLocation.TypeGpsLocation) {
			setLat(location.getLatitude());
			setLont(location.getLongitude());
			setAddr(location.getAddrStr());
			
			if (mListenerList != null ){
                for (EDLocationListener listener : mListenerList){
                    if (listener != null && listener.isReceivedLocation()){
                        listener.onGetLocation(location);
                    }
                }
            }
		} else {
		    if (mListenerList != null ){
                for (EDLocationListener listener : mListenerList){
                    if (listener != null && listener.isReceivedLocation()){
                        listener.onFailed();
                    }
                }
            }
		}
	}

	@Override
	public void onReceivePoi(BDLocation location) {

	}

	/** ֹͣ��λ���� **/
	public void stopLocService(){
		if (mLocationClient != null) {
			this.mLocationClient.stop();
			this.mLocationClient = null;
		}
	}

	/** ��ʼ��λ���� **/
	public void startLocService(){
		this.mLocationClient.start();
	}

	/** ����λ���������� **/
	public void setLocationClient(){
		this.mLocationClient = new LocationClient(mContext);
		this.mLocationClient.setLocOption(option);
		this.mLocationClient.registerLocationListener(LocManager.this);
		this.mLocationClient.start();
		mLogger.d("new mLocationClient------>"+this.mLocationClient);
	}

	/** �ֶ�����λ **/
	public void requestLoc(){
		try {
			mLocationClient.requestLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLont() {
		return lont;
	}

	public void setLont(double lont) {
		this.lont = lont;
	}
}