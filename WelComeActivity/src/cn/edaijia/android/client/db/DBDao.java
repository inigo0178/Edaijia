package cn.edaijia.android.client.db;

import java.util.ArrayList;

import cn.edaijia.android.client.maps.DriverRecord;
import cn.edaijia.android.client.model.AppContentInfo;

public interface DBDao {
	/** 保存app**/
	public void saveDBApp(String expireAt , String content);
	
	/** 查询app **/
	public AppContentInfo queryDBApp();
	
	/** 更新app **/
	
	public void updateDBApp(String expireAt , String content);
	
	/** 拨打电话记录存入数据库 **/
	public void saveMyDrivers(String phone ,String driverlist);
	
	/** 查询电话记录 **/
	public ArrayList<DriverRecord> getDriverList();
	
	/** 上传通话记录  **/
	public void saveCalllog(UploadInfo fail);
}
