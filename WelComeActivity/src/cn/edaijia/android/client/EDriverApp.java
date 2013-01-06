package cn.edaijia.android.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import cn.edaijia.android.client.db.DBDaoImpl;
import cn.edaijia.android.client.db.DBManager;
import cn.edaijia.android.client.maps.LocManager;
import cn.edaijia.android.client.maps.MapConstants;
import cn.edaijia.android.client.service.EDService;
import cn.edaijia.android.client.util.Logger;
import cn.edaijia.android.client.util.UtilListData;
import cn.edaijia.android.client.util.Utils;

import com.baidu.mapapi.BMapManager;

/**
 * 应用初始化
 * 
 * @author CaoZheng email:caozheng_119@163.com
 * @create time:2012-8-10 10:07 --------------------last
 *         update:----------------- coder: update time: Copyright (c) YDJ
 *         corporation All Rights Reserved. INFORMATION
 */
public class EDriverApp extends Application {

    // 全局连接
    private DefaultHttpClient httpClient = null;

    // 百度MapAPI的管理类
    BMapManager mBMapMan = null;

    // HTTP超时设置
    private static final int REQUEST_TIMEOUT = 30 * 1000;// 设置请求超时15秒
    private static final int SO_TIMEOUT = 30 * 1000; // 设置等待数据超时时间15秒

    // 线程池
    public ExecutorService executorService;

    //
    private LocManager mLocManager = null;

    //
    private TelephonyManager mTelManager = null;

    // imei
    private static String imei = "";

    // mac地址
    public static String macAddress = "";

    private static String sAppVersion;

    private static float markCoefficient = 0;

    private static int width;

    private static int height;

    private static String phoneNuber;

    private static Logger sLogger = Logger.createLogger("EDriverApp");

    /** 管理所有的activity */
    private static List<Activity> activityList = new ArrayList<Activity>();

    private static String sChannel;

    private boolean isWifiOn = false;

    private BroadcastReceiver mWifiReveiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            String gotAction = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(gotAction)) {
                int wifiState = intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE, 0);
                if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                    Intent serviceIntent = new Intent(context, EDService.class);
                    serviceIntent.setAction(EDService.ACTION_UPDATE_MACADDRESS);
                    startService(serviceIntent);
                }
            }
        }
    };

    static {
        System.loadLibrary("edaijia");

        AppInfo.setBaseUrl(get(AppInfo.KEY_BASE_URL));
        AppInfo.setKeyMD5(get(AppInfo.KEY_MD5));
        AppInfo.setWeiboKey(get(AppInfo.KEY_WEIBO_KEY));
        AppInfo.setWeiboSecret(get(AppInfo.KEY_WEIBO_SECRET));
        AppInfo.setWeiboUri(get(AppInfo.KEY_WEIBO_URL));
    }

    @Override
    public void onCreate() {

        super.onCreate();

        httpClient = createHttpClient();
        mBMapMan = new BMapManager(this);
        mBMapMan.init(MapConstants.BAIDU_MAPS_API_KEY, null);
        mLocManager = new LocManager(this);

        executorService = Executors.newFixedThreadPool(AppInfo.THREADPOOL_SIZE);

        mTelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelManager != null) {
            imei = mTelManager.getDeviceId();
            phoneNuber = mTelManager.getLine1Number();
        }

        // Get Mac address
        SharedPreferences spref = getSharedPreferences(AppInfo.PRE_STATE,
                MODE_PRIVATE);
        macAddress = spref.getString(AppInfo.PARAMS_MAC_ADDRESS, null);
        if (macAddress == null) {
            final WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            final WifiInfo info = (null == wifiMgr ? null : wifiMgr
                    .getConnectionInfo());
            if (info != null) {
                macAddress = info.getMacAddress();
            }

            if (macAddress == null || macAddress.length() == 0) {
                isWifiOn = wifiMgr.isWifiEnabled();
                if (!isWifiOn) {
                    registerReceiver(mWifiReveiver, new IntentFilter(
                            WifiManager.WIFI_STATE_CHANGED_ACTION));
                    wifiMgr.setWifiEnabled(true);
                }
            } else {
                saveMacAddr();
            }
        }

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager mWm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWm.getDefaultDisplay().getMetrics(dm);
        markCoefficient = dm.scaledDensity;
        width = dm.widthPixels;
        height = dm.heightPixels;

        sChannel = getMetaData(this, "UMENG_CHANNEL", AppInfo.CHANNEL_HOMEPAGE);
        sAppVersion = getVersionName();

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            AppInfo.BASE_DATA_DIR = Environment.getExternalStorageDirectory()
                    .getPath();
        } else {
            AppInfo.BASE_DATA_DIR = getFilesDir().getPath();
        }

        UtilListData.getInstance().init(this);
        DBManager.createTables();
        DBDaoImpl.getInstance().initDatabase(this);

        // upload the call log
        if (mTelManager != null) {
            mTelManager.listen(new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    super.onCallStateChanged(state, incomingNumber);
                    switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        Intent intent = new Intent(getApplicationContext(),
                                EDService.class);
                        intent.setAction(EDService.ACTION_UPLOAD_CALL_LOG);
                        getApplicationContext().startService(intent);
                        break;
                    default:
                        break;
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private String getMetaData(Context ctx, String key, String default_value) {
        String defaultValue = default_value;
        try {
            ApplicationInfo ai = ctx.getPackageManager().getApplicationInfo(
                    ctx.getPackageName(), PackageManager.GET_META_DATA);
            Object value = ai.metaData.get(key);
            if (value != null) {
                defaultValue = value.toString();
            } else {
                sLogger.d("get " + key + " is null");
            }
        } catch (Exception e) {
        }
        return defaultValue;
    }

    // 当前 version
    private String getVersionName() {
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        shutdownHttpClient();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mBMapMan != null) {
            mBMapMan.destroy();
            mBMapMan = null;
        }
        shutdownHttpClient();
        try {
            unregisterReceiver(mWifiReveiver);
        } catch (Exception e) {
            sLogger.e("error when close wifiReveiver", e);
        }
    }

    private DefaultHttpClient createHttpClient() {
        sLogger.d("createHttpClent()------>");
        HttpParams params = new BasicHttpParams();
        params.setParameter("charset", HTTP.UTF_8);
        /** 超时设置 **/
        /* 连接超时 */
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                REQUEST_TIMEOUT);
        /* 请求超时 */
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params,
                HTTP.DEFAULT_PROTOCOL_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schReg.register(new Scheme("https", PlainSocketFactory
                .getSocketFactory(), 443));
        // 使用线程安全的连接管理来创建HttpClient
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                params, schReg);
        sLogger.d("createHttpClent()<------");
        return new DefaultHttpClient(conMgr, params);
        // return new DefaultHttpClient();
    }

    private void shutdownHttpClient() {
        if (httpClient != null && httpClient.getConnectionManager() != null) {
            httpClient.getConnectionManager().shutdown();
        }
    }

    public DefaultHttpClient getHttpClient() {
        return this.httpClient;
    }

    public void setmBMapMan(BMapManager mBMapMan) {
        this.mBMapMan = mBMapMan;
    }

    public BMapManager getmBMapMan() {
        return mBMapMan;
    }

    /** 向该集合中添加activity */
    public static void addActivity(Activity act) {
        activityList.add(act);
    }

    /** 移除activity */
    public static void removeActivity(Activity act) {
        activityList.remove(act);
    }

    /** 将所有的activity销毁 */
    public static void finishAllActivity() {
        for (Activity act : activityList) {
            act.finish();
        }
    }

    public LocManager getLocManager() {
        return this.mLocManager;
    }

    public static String getImei() {
        return imei == null ? (imei = Utils.getIMEI(imei, macAddress)) : imei;
    }

    public static String getMacAddress() {
        return macAddress == null ? "12:34:56:78:9A:BC" : macAddress;
    }

    public static float getMarkCoefficient() {
        return markCoefficient;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static String getPhoneNumber() {
        return phoneNuber == null ? "" : phoneNuber;
    }

    public static String getChannel() {
        return sChannel == null ? "" : sChannel;
    }

    public static String getAppVersion() {
        return sAppVersion == null ? "" : sAppVersion;
    }

    public void updateMacAddr() {
        final WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        final WifiInfo info = (null == wifiMgr ? null : wifiMgr
                .getConnectionInfo());
        if (info != null) {
            macAddress = info.getMacAddress();
        }
        if (macAddress != null && macAddress.length() > 0) {
            imei = Utils.getIMEI(imei, macAddress);
            saveMacAddr();
        }
        if (!isWifiOn) {
            wifiMgr.setWifiEnabled(false);
            try {
                unregisterReceiver(mWifiReveiver);
            } catch (Exception e) {
                sLogger.e("error when close wifiReveiver", e);
            }
        }
    }

    private void saveMacAddr() {
        if (macAddress != null && macAddress.length() > 0) {
            SharedPreferences spref = getSharedPreferences(AppInfo.PRE_STATE,
                    MODE_PRIVATE);
            spref.edit().putString(AppInfo.PARAMS_MAC_ADDRESS, macAddress)
                    .commit();
        }
    }

    /** Get App Env Params */
    public static native String get(int key);
}
