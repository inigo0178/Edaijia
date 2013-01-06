package cn.edaijia.android.client.util;


import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import cn.edaijia.android.client.EDriverApp;

/**********************************************
 * 网络通讯类	RequestMethod:POST or GET
 * @author CaoZheng		email:caozheng_119@163.com
 * @create time:2012-8-10 10:07
 * --------------------last update:----------------- 
 * coder:                            update
 * time: Copyright (c) YDJ corporation All Rights Reserved. 
 * INFORMATION
 */

public class CommonHttp {	
	
    private static Logger sLogger = Logger.createLogger("CommonHttp");
	
	/** 获取周边空闲司机(百度座标) **/
	public static synchronized String getDriversInfo( String udid , String longitude, String latitude ,  HttpClient mHttpClient) throws SocketTimeoutException{
		
		EDHttpPost httpost = new EDHttpPost();
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		
		nvps.add(new BasicNameValuePair("udid", udid));
		nvps.add(new BasicNameValuePair("gps_type", "baidu"));
		nvps.add(new BasicNameValuePair("longitude", longitude));
		nvps.add(new BasicNameValuePair("latitude",latitude));
		nvps.add(new BasicNameValuePair("method", "driver.nearby"));
		
		try {
			
			httpost.setEntity(nvps);
			HttpResponse response = mHttpClient.execute(httpost);
			
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/** 司机信息 **/
	public static String getDriverInfo( String driverID , HttpClient mHttpClient){
		
		EDHttpPost httpost = new EDHttpPost();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		nvps.add(new BasicNameValuePair("driverID", driverID));
		nvps.add(new BasicNameValuePair("method", "driver.get"));
		
		
		
		try {
			httpost.setEntity(nvps);
			HttpResponse response = mHttpClient.execute(httpost);
			
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/** 司机评价列表 **/
	public static String getReviewInfo( String pageNo,String pageSize , HttpClient mHttpClient){
		
		EDHttpPost httPost = new EDHttpPost();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		
		nvps.add(new BasicNameValuePair("method","comment.list" ));
		nvps.add(new BasicNameValuePair("pageNo",pageNo ));
		nvps.add(new BasicNameValuePair("pageSize",pageSize ));

		
		try {
			httPost.setEntity(nvps);
			HttpResponse response = mHttpClient.execute(httPost);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity) ;
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	/** 获取司机评价 **/
	public static String getReviewInfo( String driverID,String pageNo,String pageSize , HttpClient mHttpClient){
		
		EDHttpPost httPost = new EDHttpPost();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		
		
		nvps.add(new BasicNameValuePair("method","driver.comment.list" ));
		nvps.add(new BasicNameValuePair("driverID",driverID ));
		nvps.add(new BasicNameValuePair("pageNo",pageNo ));
		nvps.add(new BasicNameValuePair("pageSize",pageSize ));
		
		
		try {
			httPost.setEntity(nvps);
			HttpResponse response = mHttpClient.execute(httPost);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity) ;
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** 价格表 **/
	public static String getPriceTable( String longitude,String latitude ,
			String cityName , HttpClient mHttpClient){
		
		EDHttpPost httpost = new EDHttpPost();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		
		
		nvps.add(new BasicNameValuePair("method", "app.price"));
		nvps.add(new BasicNameValuePair("latitude", latitude));
		nvps.add(new BasicNameValuePair("longitude", longitude));
		nvps.add(new BasicNameValuePair("cityName", cityName));

	
		
		try {
			httpost.setEntity(nvps);
			HttpResponse response = mHttpClient.execute(httpost);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/** 获取 App 文字内容 **/
	public static String getAppContent(HttpClient mHttpClient){
		
		EDHttpPost httpost = new EDHttpPost();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		
		nvps.add(new BasicNameValuePair("method", "app.content"));
		
		
		try {
			httpost.setEntity(nvps);
			HttpResponse response = mHttpClient.execute(httpost);
			
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}
	
	/** 获取 App 版本信息 **/
	public static String getAppVersion(HttpClient mHttpClient){
		EDHttpPost httpost = new EDHttpPost();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		nvps.add(new BasicNameValuePair("method", "app.version.get"));
		
		
		try {
			httpost.setEntity(nvps);
			HttpResponse response = mHttpClient.execute(httpost);
			
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/** 城市列表 **/
	public static String getCityList( HttpClient mHttpClient){
		EDHttpPost httpost = new EDHttpPost();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		nvps.add(new BasicNameValuePair("method", "app.city.list"));

		
		try {
			httpost.setEntity(nvps);
			HttpResponse response = mHttpClient.execute(httpost);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	/** 码充值 **/
	public static String getCashNumber(String fee , String token ,
			String phone ,String bonus ,String type, HttpClient mHttpClient){
		EDHttpPost httpost = new EDHttpPost();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		nvps.add(new BasicNameValuePair("method", "customer.account.recharge"));
		nvps.add(new BasicNameValuePair("fee", fee));
		nvps.add(new BasicNameValuePair("token", token));
		nvps.add(new BasicNameValuePair("phone", phone));
		nvps.add(new BasicNameValuePair("bonus", bonus));
		nvps.add(new BasicNameValuePair("type", type));
		
		try {
			httpost.setEntity(nvps);
			HttpResponse response = mHttpClient.execute(httpost);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/** 记录通话记录 **/
	public static String upLoadCallLog(String udid, String phone ,String driverID,
			String device,String os ,String version ,String longitude ,
			String latitude,String callTime ,HttpClient mHttpClient){
		EDHttpPost httpost = new EDHttpPost();
		httpost.addHeader("User-Agent", EDriverApp.getWidth() +"*" + EDriverApp.getHeight());
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		nvps.add(new BasicNameValuePair("method", "customer.calllog"));
		nvps.add(new BasicNameValuePair("udid", udid));
		nvps.add(new BasicNameValuePair("driverID", driverID));
		nvps.add(new BasicNameValuePair("phone", phone));
		nvps.add(new BasicNameValuePair("device", device));
		nvps.add(new BasicNameValuePair("os", os));
		nvps.add(new BasicNameValuePair("version", version));
		nvps.add(new BasicNameValuePair("longitude", longitude));
		nvps.add(new BasicNameValuePair("latitude", latitude));
		nvps.add(new BasicNameValuePair("callTime", callTime));
		
		try {
			httpost.setEntity(nvps);
			HttpResponse response = mHttpClient.execute(httpost);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/** 检测网络连接状态 **/
    public static boolean isConnect(Context context){
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            sLogger.e(e.toString());
            return false;
        }
        return false;
    }
}