package cn.edaijia.android.client.maps;

import java.util.ArrayList;

import android.content.Context;
import cn.edaijia.android.client.util.Logger;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
/**
 * 位置服务管理类	提供定位,POI搜索服务
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
		option.setOpenGps(true);								//打开gps
		option.setCoorType("bd09ll");							//设置坐标类型为bd09ll-百度经纬度坐标系	gcj02-国测局经纬度坐标系	bd09百度墨卡托坐标系
		option.setPriority(LocationClientOption.NetWorkFirst);		//设置GPS优先
		option.setProdName("Edaijia");							//设置产品线名称
		//		option.setScanSpan(1000);								//定时定位,<1000为执行一次,>1000为定时定位
		option.disableCache(true);								//禁用缓存定位

		//		option.setPoiNumber(5);									//poi搜索返回个数
		//		option.setPoiDistance(300);								//poi搜索范围
		//		option.setPoiExtraInfo(true);							//是否需要poi结果的电话地址等信息
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

	/** 停止定位服务 **/
	public void stopLocService(){
		if (mLocationClient != null) {
			this.mLocationClient.stop();
			this.mLocationClient = null;
		}
	}

	/** 开始定位服务 **/
	public void startLocService(){
		this.mLocationClient.start();
	}

	/** 设置位置连接属性 **/
	public void setLocationClient(){
		this.mLocationClient = new LocationClient(mContext);
		this.mLocationClient.setLocOption(option);
		this.mLocationClient.registerLocationListener(LocManager.this);
		this.mLocationClient.start();
		mLogger.d("new mLocationClient------>"+this.mLocationClient);
	}

	/** 手动请求定位 **/
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