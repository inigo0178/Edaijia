package cn.edaijia.android.client.db;

public class UploadInfo {
	
	private String latitude;
	
	private String longtitude;
	
	/** 客户端版本号 **/
	private String version;
	
	/** 型号 **/
	private String device;
	
	/** 手机号 **/
	private String phone;
	
	/** 呼叫司机ID **/
	private String driver_id;
	
	/** IMEI **/
	private String udid;
	
	/** 操作系统 **/
	private String os;
	
	/** 呼叫时间 **/
	private String call_time;
	
	/** 分辨率 **/
	private String resolution;
	
	
	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getLatitude() {
		return latitude;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getDevice() {
		return device;
	}
	
	public void setDevice(String device) {
		this.device = device;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getDriver_id() {
		return driver_id;
	}
	
	public void setDriver_id(String driver_id) {
		this.driver_id = driver_id;
	}
	
	public String getUdid() {
		return udid;
	}
	
	public void setUdid(String udid) {
		this.udid = udid;
	}
	
	public String getOs() {
		return os;
	}
	
	public void setOs(String os) {
		this.os = os;
	}
	
	public String getCall_time() {
		return call_time;
	}
	
	public void setCall_time(String call_time) {
		this.call_time = call_time;
	}
}