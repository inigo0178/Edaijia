package cn.edaijia.android.client.db;

import java.util.ArrayList;

import cn.edaijia.android.client.maps.DriverRecord;
import cn.edaijia.android.client.model.AppContentInfo;

public interface DBDao {
	/** ����app**/
	public void saveDBApp(String expireAt , String content);
	
	/** ��ѯapp **/
	public AppContentInfo queryDBApp();
	
	/** ����app **/
	
	public void updateDBApp(String expireAt , String content);
	
	/** ����绰��¼�������ݿ� **/
	public void saveMyDrivers(String phone ,String driverlist);
	
	/** ��ѯ�绰��¼ **/
	public ArrayList<DriverRecord> getDriverList();
	
	/** �ϴ�ͨ����¼  **/
	public void saveCalllog(UploadInfo fail);
}
