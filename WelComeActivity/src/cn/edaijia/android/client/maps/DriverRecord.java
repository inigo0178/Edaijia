package cn.edaijia.android.client.maps;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 司机信息类 	@TODO need update someinfo from json return
 * @author CaoZheng		email:caozheng_119@163.com
 * @create time:2012-8-10 10:07
 * --------------------last update:----------------- 
 * coder:                            update
 * time: Copyright (c) YDJ corporation All Rights Reserved. 
 * INFORMATION
 */
public class DriverRecord implements Parcelable{
	
	/** 纬度 */
	private double latitude;
	/** 经度*/
	private double longtitude;
	/** 地址 */
	private String addr;
	/** 更新时间 */
	private String updateTime;
	/** id */
	private String id;
	/** 姓名 */
	private String name;
	/** 手机号 */
	private String phone;
	/** idCard */
	private String idCard;
	/** 籍贯 */
	private String domicile;
	/** card */
	private String card;
	/** 驾龄 */
	private String year;
	/** 星级 */
	private String level;
	/** 状态 */
	private String state;
	/** 距离 */
	private String distance;
	/** 照片(小) */
	private String pic_small;
	/** 照片(中) */
	private String pic_middle;
	/** 照片(大) */
	private String pic_large;
	/** 代驾次数 */
	private String order_count;
	/** 评论条数 */
	private String comment_count;
	/** 价钱 */
	private String price;
	/** 司机id **/
	private String driver_id;
	
	/** 拨打电话时间  **/
	private String call_time;

    public String getCall_time() {
		return call_time;
	}


	public void setCall_time(String call_time) {
		this.call_time = call_time;
	}
	/** 类别 */
	private String sort;
	
	public String getSort() {
		return sort;
	}


	public void setSort(String sort) {
		this.sort = sort;
	}


	/**
	 * 构造函数
	 */
	public DriverRecord(){

	}
	
	
	public String getDriver_id() {
		return driver_id;
	}



	public void setDriver_id(String driver_id) {
		this.driver_id = driver_id;
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getDomicile() {
		return domicile;
	}

	public void setDomicile(String domicile) {
		this.domicile = domicile;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getPic_small() {
		return pic_small;
	}

	public void setPic_small(String pic_small) {
		this.pic_small = pic_small;
	}

	public String getPic_middle() {
		return pic_middle;
	}

	public void setPic_middle(String pic_middle) {
		this.pic_middle = pic_middle;
	}

	public String getPic_large() {
		return pic_large;
	}

	public void setPic_large(String pic_large) {
		this.pic_large = pic_large;
	}

	public String getOrder_count() {
		return order_count;
	}

	public void setOrder_count(String order_count) {
		this.order_count = order_count;
	}

	public String getComment_count() {
		return comment_count;
	}

	public void setComment_count(String comment_count) {
		this.comment_count = comment_count;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getPrice(){
		return price;
	}
	@Override
	public String toString(){
		JSONObject obj = new JSONObject();
		try {
			obj.put("name", name);
			obj.put("id", id);
			obj.put("phone", phone);
			obj.put("idCard", idCard);
			obj.put("domicile", domicile);
			obj.put("card",card); 
			obj.put("year", year);
			obj.put("level", level);
			obj.put("state", state);
			obj.put("price", price);
			obj.put("longitude", longtitude);
			obj.put("latitude", latitude);
			obj.put("distance", distance);
			obj.put("sort", sort);
			obj.put("picture_small", pic_small);
			obj.put("picture_middle", pic_middle);
			obj.put("picture_large", pic_large);
			obj.put("order_count", order_count);
			obj.put("comment_count", comment_count);
			obj.put("driver_id", driver_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}


	public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            DriverRecord driver = new DriverRecord();
            driver.name = source.readString();
            driver.id = source.readString();
            driver.phone = source.readString();
            driver.idCard = source.readString();
            driver.domicile = source.readString();
            driver.card = source.readString();
            driver.year = source.readString();
            driver.level = source.readString();
            driver.state = source.readString();
            driver.price = source.readString();
            driver.longtitude = source.readDouble();
            driver.latitude = source.readDouble();
            driver.distance = source.readString();
            driver.sort = source.readString();
            driver.pic_small = source.readString();
            driver.pic_middle = source.readString();
            driver.pic_large = source.readString();
            driver.order_count = source.readString();
            driver.comment_count = source.readString();
            driver.driver_id = source.readString();
            return driver;
        }

        @Override
        public Object[] newArray(int size) {
            return new DriverRecord[size];
        }
	};
	
	@Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(phone);
        dest.writeString(idCard);
        dest.writeString(domicile);
        dest.writeString(card);
        dest.writeString(year);
        dest.writeString(level);
        dest.writeString(state);
        dest.writeString(price);
        dest.writeDouble(longtitude);
        dest.writeDouble(latitude);
        dest.writeString(distance);
        dest.writeString(sort);
        dest.writeString(pic_small);
        dest.writeString(pic_middle);
        dest.writeString(pic_large);
        dest.writeString(order_count);
        dest.writeString(comment_count);
        dest.writeString(driver_id);
    }
}
