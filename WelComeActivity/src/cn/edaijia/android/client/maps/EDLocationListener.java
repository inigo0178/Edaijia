package cn.edaijia.android.client.maps;

import com.baidu.location.BDLocation;

/***
 * 
 * @author CaoZheng
 * @category 位置变化回调接口
 *
 */
public interface EDLocationListener {
	
    /** 获取GPS信息失败*/
	public void onFailed();
	
	/**获取GPS成功*/
	public void onGetLocation(BDLocation location);
	
	/**当前调用是否收取GPS信息，该接口为地图模块使用，在地图切换到后台后，也需要更新GPS信息。*/
	public boolean isReceivedLocation();
}
