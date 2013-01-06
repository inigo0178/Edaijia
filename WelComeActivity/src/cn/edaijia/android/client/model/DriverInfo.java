package cn.edaijia.android.client.model;

public class DriverInfo {
	/***司机信息***/
	private String driverID;//id
	private String picture;
	private String name;
	private String level;
	private String carCard;
	private String year;
	private String serviceTimes;
	private String highOpinionTimes;
	private String lowOpinionTimes;
	private String phone;
	private String state;
	private String mark;
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public String getDriverID() {
		return driverID;
	}
	public void setDriverID(String driverID) {
		this.driverID = driverID;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getCarCard() {
		return carCard;
	}
	public void setCarCard(String carCard) {
		this.carCard = carCard;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getServiceTimes() {
		return serviceTimes;
	}
	public void setServiceTimes(String serviceTimes) {
		this.serviceTimes = serviceTimes;
	}
	public String getHighOpinionTimes() {
		return highOpinionTimes;
	}
	public void setHighOpinionTimes(String highOpinionTimes) {
		this.highOpinionTimes = highOpinionTimes;
	}
	public String getLowOpinionTimes() {
		return lowOpinionTimes;
	}
	public void setLowOpinionTimes(String lowOpinionTimes) {
		this.lowOpinionTimes = lowOpinionTimes;
	}
	
	@Override
	public String toString(){
		return  new StringBuffer().append(driverID)
			.append(" | "+picture)
			.append(" | "+name)
			.append(" | "+level)
			.append(" | "+carCard)
			.append(" | "+year)
			.append(" | "+serviceTimes)
			.append(" | "+highOpinionTimes)
			.append(" | "+lowOpinionTimes).toString();
			
	}
} 
