package cn.edaijia.android.client.db;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import cn.edaijia.android.client.maps.DriverRecord;
import cn.edaijia.android.client.model.AppContentInfo;
import cn.edaijia.android.client.util.CommonHttp;
import cn.edaijia.android.client.util.UtilListData;
import cn.edaijia.android.client.util.Utils;

public class DBDaoImpl implements DBDao {

    private Context context;

    private String FILE_NAME = "app_content.txt";

    private static DBDaoImpl sInstance;

    private DBDaoImpl() {

    }

    public void initDatabase(Application app) {
        context = app.getApplicationContext();
    }

    public static DBDaoImpl getInstance() {
        if (sInstance == null) {
            sInstance = new DBDaoImpl();
        }
        return sInstance;
    }

    /** 保存app内容 **/
    @Override
    public void saveDBApp(String expireAt, String content) {
        DBManager.saveAppContent(expireAt, content);
    }

    /** 查询app内容 **/
    @Override
    public AppContentInfo queryDBApp() {
        return DBManager.getAppContent();
    }

    /** 更新app **/
    @Override
    public void updateDBApp(String expireAt, String content) {
        DBManager.updateAppContent(expireAt, content);
    }

    /** app **/
    public AppContentInfo getDbApp() {
        // Read from server
        AppContentInfo info = queryDBApp();
        // Local data
        if (info == null) {
            info = new AppContentInfo(Utils.readAssetsFile(context, FILE_NAME));
        }
        return info;
    }

    /** 代驾记录 **/

    @Override
    public void saveMyDrivers(String phone, String driverlist) {
        String has_id = DBManager.getCountbyId(phone);
        if (has_id != null) {
            DBManager.deleteCount(has_id);
        }
        long curr_time = System.currentTimeMillis();
        DBManager.saveDriverlist(phone, curr_time, driverlist);
    }

    /** 查询电话记录 **/

    @Override
    public ArrayList<DriverRecord> getDriverList() {

        ArrayList<DriverRecord> mapInfo = null;
        ArrayList<String> list = DBManager.getCountDriverList();

        if (list != null) {
            mapInfo = new ArrayList<DriverRecord>();
            for (String json : list) {
                mapInfo.add(getDInfo(json));
            }
        }

        return mapInfo;

    }

    private DriverRecord getDInfo(String json) {
        DriverRecord info = null;
        try {
            String arr[] = json.split("\\|");

            JSONObject oArr = new JSONObject(arr[1]);
            info = new DriverRecord();
            info.setCall_time(arr[0]);
            info.setId(oArr.getString("id"));
            info.setName(oArr.getString("name"));
            info.setPhone(oArr.getString("phone"));
            info.setIdCard(oArr.getString("idCard"));
            info.setDomicile(oArr.getString("domicile"));
            info.setCard(oArr.getString("card"));
            info.setYear(oArr.getString("year"));
            info.setLevel(oArr.getString("level"));
            info.setState(oArr.getString("state"));
            info.setPrice(oArr.getString("price"));
            info.setLongtitude(Double.parseDouble(oArr.getString("longitude")));
            info.setLatitude(Double.parseDouble(oArr.getString("latitude")));
            info.setDistance(oArr.getString("distance"));
            // info.setSort(oArr.getString("sort"));
            info.setPic_small(oArr.getString("picture_small"));
            info.setPic_middle(oArr.getString("picture_middle"));
            info.setPic_large(oArr.getString("picture_large"));
            info.setOrder_count(oArr.getString("order_count"));
            info.setComment_count(oArr.getString("comment_count"));
            info.setDriver_id(oArr.getString("driver_id"));

            return info;
        } catch (JSONException e) {

            e.printStackTrace();
        }

        return null;
    }

    /** 上传通话记录失败 **/

    @Override
    public void saveCalllog(UploadInfo fail) {
        DBManager.saveFailedInfo(fail.getLatitude(), fail.getLongtitude(),
                fail.getVersion(), fail.getPhone(), fail.getDriver_id(),
                fail.getDevice(), fail.getOs(), fail.getCall_time(),
                fail.getUdid());

    }

    public void upload() {
        int retry = 0;
        while (CommonHttp.isConnect(context) && DBManager.hasUnUploadCallLog()) {
            // 查询最后一条记录
            UploadInfo fail = DBManager.getFailedInfoLaste();
            if (fail != null) {
                String code = UtilListData.getInstance()
                        .upLoadDataCalllog(fail);
                if ("0".equals(code)) {// 成功
                    // 删除记录
                    DBManager.deleteFailedInfo(fail.getCall_time());
                    retry = 0;
                } else {
                    // 发送失败后重试5次，停止发送
                    retry++;
                    if (retry == 5) {
                        retry = 0;
                        break;
                    }
                }
            } else {
                //没有需要发送的数据
                break;
            }
        }
    }
}
