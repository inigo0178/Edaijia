package cn.edaijia.android.client.util;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import cn.edaijia.android.client.EDriverApp;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.db.UploadInfo;
import cn.edaijia.android.client.maps.DriverRecord;
import cn.edaijia.android.client.model.AppContentInfo;
import cn.edaijia.android.client.model.DriverInfo;
import cn.edaijia.android.client.model.FavorableInfo;
import cn.edaijia.android.client.model.PriceInfo;
import cn.edaijia.android.client.model.ReviewInfo;
import cn.edaijia.android.client.model.TitleDetail;

public class UtilListData {

    private EDriverApp mApp = null;
    private HttpClient mHttpClient = null;
    private Context context = null;

    //
    private ArrayList<ReviewInfo> reviewInfos = null;// 留言
    // private ArrayList<DriverRecord> driverRecords = null;//周边空闲司机(百度座标)
    private static UtilListData sInstance;

    private Logger mLogger = Logger.createLogger("UtilListData");

    private UtilListData() {
    }

    public static UtilListData getInstance() {
        if (sInstance == null) {
            sInstance = new UtilListData();
        }
        return sInstance;
    }

    public void init(Application app) {
        mApp = (EDriverApp) app;
        this.context = app.getApplicationContext();
        mHttpClient = mApp.getHttpClient();
    }

    /******
     * 获取司机信息
     * 
     * @param driverID
     * @param timestamp
     */
    public DriverInfo getDataDriverInfo(String driverID) {
        String infos = CommonHttp.getDriverInfo(driverID, mHttpClient);

        // mLogger.d(infos);
        if (infos != null) {
            try {
                JSONObject obj = new JSONObject(infos);
                String code = obj.getString("code");
                String message = obj.getString("message");
                if ("0".equals(code)) {
                    JSONObject arr = obj.getJSONObject("driverInfo");
                    DriverInfo info = new DriverInfo();
                    info.setDriverID(arr.getString("driverID"));
                    info.setPicture(arr.getString("picture"));
                    info.setName(arr.getString("name"));
                    info.setLevel(arr.getString("level"));
                    info.setCarCard(arr.getString("carCard"));
                    info.setYear(arr.getString("year"));
                    info.setServiceTimes(arr.getString("serviceTimes"));
                    info.setHighOpinionTimes(arr.getString("highOpinionTimes"));
                    info.setLowOpinionTimes(arr.getString("lowOpinionTimes"));
                    info.setState(arr.getString("state"));

                    return info;
                } else {
                    mLogger.d(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            mLogger.d(context.getString(R.string.httpError1));
        }
        return null;
    }

    /******
     * 留言墙
     * 
     * @param pageNO
     * @param pageSize
     * 
     */
    public ArrayList<ReviewInfo> getDataReviewInfo(int pageNo, int pageSize) {

        String infos = CommonHttp.getReviewInfo(pageNo + "", pageSize + "",
                mHttpClient);

        // mLogger.d(infos);
        if (infos != null) {
            reviewInfos = new ArrayList<ReviewInfo>();
            try {
                JSONObject obj = new JSONObject(infos);
                String code = obj.getString("code");
                String message = obj.getString("message");
                if ("0".equals(code)) {
                    JSONObject list = obj.getJSONObject("commentList");
                    ReviewInfo info;
                    for (int i = pageNo; i < pageSize; i++) {
                        JSONObject arr = list.getJSONObject(i + "");
                        info = new ReviewInfo();

                        info.setName(arr.getString("name"));
                        info.setLevel(arr.getString("level"));
                        info.setComments(arr.getString("comments"));
                        info.setInsert_time(arr.getString("insert_time"));
                        info.setEmployee_id(arr.getString("employee_id"));
                        info.setStatus(arr.getString("status"));
                        info.setUuid(arr.getString("uuid"));

                        reviewInfos.add(info);
                        // mLogger.d("==1== "+info.toString());
                        info = null;

                    }
                    // mLogger.d(reviewInfos.size()+"-----11111111");
                    return reviewInfos;
                } else {
                    mLogger.d(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            mLogger.d(context.getString(R.string.httpError1));
        }

        return null;
    }

    /******
     * 留言墙,获取司机评价
     * 
     * @param driverID
     * @param pageNO
     * @param pageSize
     * 
     */
    public ArrayList<ReviewInfo> getDataReviewInfo(String driverID, int pageNo,
            int pageSize) {
        if (driverID == null) {
            return null;
        }
        String infos = CommonHttp.getReviewInfo(driverID, pageNo + "", pageSize
                + "", mHttpClient);

        // mLogger.d(infos);
        if (infos != null) {
            reviewInfos = new ArrayList<ReviewInfo>();
            try {
                JSONObject obj = new JSONObject(infos);
                String code = obj.getString("code");
                String message = obj.getString("message");
                if ("0".equals(code)) {
                    JSONObject list = obj.getJSONObject("commentList");
                    int total = list.getInt("total");
                    int tot = 0;
                    if (total < pageSize) {
                        tot = total;
                    } else {
                        int p_currt = (pageNo * pageSize);
                        int currt = ((pageNo + 1) * pageSize);
                        if (total >= currt) {
                            tot = pageSize;
                        }
                        if (p_currt < total) {
                            if (total < currt) {
                                tot = total - p_currt;
                            }
                        }
                    }
                    ReviewInfo info;
                    for (int i = 0; i < tot; i++) {

                        JSONObject arr = list.getJSONObject(i + "");
                        info = new ReviewInfo();

                        info.setName(arr.getString("name"));
                        info.setLevel(arr.getString("level"));
                        info.setComments(arr.getString("comments"));
                        info.setInsert_time(arr.getString("insert_time"));
                        info.setEmployee_id(arr.getString("employee_id"));
                        info.setStatus(arr.getString("status"));
                        info.setUuid(arr.getString("uuid"));

                        reviewInfos.add(info);
                        info = null;

                    }
                    return reviewInfos;
                } else {
                    mLogger.d(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            mLogger.d(context.getString(R.string.httpError1));
        }

        return null;
    }

    /***
     * 价格表
     * 
     * @param longitude
     * @param latitude
     * @return
     */
    public PriceInfo getDataPriceInfo(String longitude, String latitude,
            String cityName ) {

        String infos = null;
        infos = CommonHttp.getPriceTable(longitude, latitude, cityName,
                    mHttpClient);

        // mLogger.d(infos);
        PriceInfo priceInfo = null;
        
        if (infos != null) {

            try {
                JSONObject obj = new JSONObject(infos);
                JSONArray arr = obj.getJSONArray("priceList");
                priceInfo = new PriceInfo();
                priceInfo.setExpireAt(obj.getString("expireAt"));
                // mLogger.d(obj.getString("expireAt"));
                priceInfo
                        .setPriceTitle(arr.getJSONObject(0).getString("title"));

                ArrayList<TitleDetail> tds = new ArrayList<TitleDetail>();

                int len = arr.length();
                JSONObject oArr;
                TitleDetail td;
                for (int i = 1; i < len; i++) {
                    td = new TitleDetail();
                    oArr = arr.getJSONObject(i);
                    td.setLeftInfo(oArr.getString("title"));
                    td.setRightInfo(oArr.getString("detail"));
                    tds.add(td);
                    td = null;
                }
                priceInfo.setTitelDetails(tds);

                JSONObject jmemo = obj.getJSONObject("memo");
//                String m = jmemo.getString("memo");
                int str_total = jmemo.getInt("total");
                String note = "";
                for (int i = 1; i <= str_total; i++) {
                    String temp = jmemo.getString(i + "");
                    note += temp + "\n";

                }

                // priceInfo.setPriceNote(m+"\n"+note);
                priceInfo.setPriceNote(note);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return priceInfo;
        } else {
            mLogger.d(context.getString(R.string.httpError1));
            return null;
        }
    }

    /***
     * 周边空闲司机(百度座标)
     * 
     * @param udid
     * @param longitude
     * @param latitude
     * @return
     * @throws SocketTimeoutException
     */
    public ArrayList<DriverRecord> getDataDriverRecord(String udid,
            String longitude, String latitude) throws SocketTimeoutException {

        String infos = CommonHttp.getDriversInfo(udid, longitude, latitude,
                mHttpClient);

        List<DriverRecord> driver = null;
        try {
            driver = Utils.getDriverList(infos);
            if (driver == null || driver.isEmpty()) {
                return null;
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return (ArrayList<DriverRecord>) driver;

    }

    /** 获取app内容 **/
    public AppContentInfo getDataAppContent() {
        String infos = CommonHttp.getAppContent(mHttpClient);
        if (infos != null) {
            mLogger.d(infos);
            return new AppContentInfo(infos);
        } else {
            mLogger.d(context.getString(R.string.httpError1));
        }
        return null;
    }

    /** 城市列表 **/
    public ArrayList<String> getDataCityList() {

        String infos = CommonHttp.getCityList(mHttpClient);
        // mLogger.d(infos);
        if (infos != null) {
            try {
                ArrayList<String> list = new ArrayList<String>();
                JSONObject obj = new JSONObject(infos);
                String code = obj.getString("code");
                String message = obj.getString("message");
                if ("0".equals(code)) {
                    JSONObject arr = obj.getJSONObject("cityList");
                    int len = arr.length();
                    for (int i = 1; i < len + 1; i++) {
                        list.add(arr.getString(i + ""));
                    }
                    return list;
                } else {
                    mLogger.d(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            mLogger.d(context.getString(R.string.httpError1));
        }

        return null;
    }

    /** 现金充值 **/
    public FavorableInfo getDataCash(String fee, String token, String phone,
            String bonus, String type) {
        String infos = CommonHttp.getCashNumber(fee, token, phone, bonus, type,
                mHttpClient);
        if (infos != null) {
            try {
                JSONObject obj = new JSONObject(infos);
                FavorableInfo info = new FavorableInfo();
                String code = obj.getString("code");
                String message = obj.getString("message");
                String recharge = null;
                if ("0".equals(code)) {
                    recharge = obj.getString("recharge");
                }
                info.setCode(code);
                info.setMessage(message);
                info.setRecharge(recharge);
                // mLogger.d("cash-->"+code+" "+recharge+" "+message);
                return info;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /** 记录通话记录 **/
    public String upLoadDataCalllog(UploadInfo info) {
        String infos = CommonHttp.upLoadCallLog(info.getUdid(),
                info.getPhone(), info.getDriver_id(), info.getDevice(),
                info.getOs(), info.getVersion(), info.getLongtitude(),
                info.getLatitude(), info.getCall_time(), mHttpClient);
        mLogger.d("call-->" + infos);
        if (infos != null) {
            try {
                JSONObject obj = new JSONObject(infos);
                String code = obj.getString("code");
                return code;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /** 获取 App 版本信息 **/
    public String getDataAppVersion() {

        String infos = CommonHttp.getAppVersion(mHttpClient);
        // mLogger.d("appVersion--->"+infos);
        if (infos != null) {
            try {
                JSONObject obj = new JSONObject(infos);
                JSONObject ob = obj.getJSONObject("appVersionAndroid");
                String latest = ob.getString("latest");
                String url = ob.getString("url");
                return latest + "&" + url;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
