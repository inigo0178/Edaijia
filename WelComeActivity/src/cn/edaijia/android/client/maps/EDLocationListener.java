package cn.edaijia.android.client.maps;

import com.baidu.location.BDLocation;

/***
 * 
 * @author CaoZheng
 * @category λ�ñ仯�ص��ӿ�
 *
 */
public interface EDLocationListener {
	
    /** ��ȡGPS��Ϣʧ��*/
	public void onFailed();
	
	/**��ȡGPS�ɹ�*/
	public void onGetLocation(BDLocation location);
	
	/**��ǰ�����Ƿ���ȡGPS��Ϣ���ýӿ�Ϊ��ͼģ��ʹ�ã��ڵ�ͼ�л�����̨��Ҳ��Ҫ����GPS��Ϣ��*/
	public boolean isReceivedLocation();
}
