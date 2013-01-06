package cn.edaijia.android.client.maps;

import static cn.edaijia.android.client.maps.MapConstants.MAP_ZOOM_LEVEL;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.EDriverApp;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.util.CommonHttp;
import cn.edaijia.android.client.util.Utils;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;

/**
 * ��ͼ�� ʵ��λ�ñ仯�ص��ӿ�
 * 
 * @author CaoZheng email:caozheng_119@163.com
 * @create time:2012-8-10 10:07 --------------------last
 *         update:----------------- coder: update time: Copyright (c) YDJ
 *         corporation All Rights Reserved. INFORMATION
 */
public class EDriverMapActivity extends MapActivity implements OnClickListener,
        EDLocationListener {

    private EDriverApp mApp = null;

    /** ��ͼ **/
    private MapView mMapView = null;

    /** Baidu��ͼ���� **/
    private BMapManager mMapManager = null;

    /** ��ͼ������ **/
    public MapController mMapController = null;

    /** ����˾����ͼ�� **/
    private DriverOverlay mDriverOverlay = null;

    /** ����˾������Ϣ�� **/
    private List<DriverRecord> driverList = null;

    // /** �㣺˾���� **/
    // private static final int LAYER_DRIVER= 1;

    /** ��λ������ **/
    private LocManager mLocManager = null;

    /**  **/
    private ImageView locImg = null;

    private GeoPoint center = null;

    private double lat, lont;

    private LinearLayout map_load;

    private ProgressBar refresh_view_progress;

    private String reSult;// ˾���б�

    private MyLocationOverlay myLoc = null;

    /** ��ǰ��Activity */
    private boolean isActive;

    private final static int MSG_LOAD_SUCC = 0x1;
    private final static int MSG_LOCATE_ERROR = 0x2;
    private final static int MSG_NO_DATA = 0x3;
    private final static int MSG_LOAD_FAILED = 0x4;
    private final static int MSG_SHOW_WAITING = 0x7;
    private final static int MSG_HIDE_WAITING = 0x8;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_LOAD_SUCC:
                try {
                    moveMap();
                    // ����
                    mMapView.invalidate();
                    locImg.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent map_to_list = new Intent("mapTolist");
                map_to_list.putParcelableArrayListExtra("list",
                        (ArrayList<? extends Parcelable>) driverList);
                EDriverMapActivity.this.sendBroadcast(map_to_list);
                break;
            case MSG_SHOW_WAITING:
                map_load.setVisibility(View.VISIBLE);
                break;
            case MSG_HIDE_WAITING:
                map_load.setVisibility(View.GONE);
                locImg.setEnabled(true);
                break;
            case MSG_LOAD_FAILED:
                map_load.setVisibility(View.GONE);
                locImg.setEnabled(true);
                AppInfo.showToast(EDriverMapActivity.this,
                        getString(R.string.time_out));
                break;
            case MSG_LOCATE_ERROR:
                locImg.setEnabled(true);
                map_load.setVisibility(View.GONE);
                locImg.setEnabled(true);
                AppInfo.showToast(EDriverMapActivity.this,
                        getString(R.string.time_out));
                break;
            case MSG_NO_DATA:
                map_load.setVisibility(View.GONE);
                locImg.setEnabled(true);
                AppInfo.showToast(EDriverMapActivity.this,
                        getString(R.string.no_drivers));
                break;
            default:
                break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ��ʼ��ҳ��
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.map);
        mApp = (EDriverApp) this.getApplication();

        mLocManager = mApp.getLocManager();
        mMapManager = mApp.getmBMapMan();
        mLocManager.addLocationListener(this);

        driverList = new ArrayList<DriverRecord>();
        map_load = (LinearLayout) this.findViewById(R.id.refresh_view);
        refresh_view_progress = (ProgressBar) this
                .findViewById(R.id.refresh_view_progress);
        refresh_view_progress = new ProgressBar(EDriverMapActivity.this);
        refresh_view_progress.setIndeterminate(false);

        // Locate Button
        locImg = (ImageView) findViewById(R.id.map_loc);
        locImg.setOnClickListener(this);

        // ��ͼ��ʼ��
        mMapView = (MapView) findViewById(R.id.bmapsView);
        super.initMapActivity(mMapManager);
        initMap();
        mMapController.setZoom(MAP_ZOOM_LEVEL);

        myLoc = new MyLocationOverlay(this, mMapView);
        myLoc.enableCompass(); // ����ָ����
        myLoc.enableMyLocation();
        mMapView.getOverlays().add(myLoc);

        if (CommonHttp.isConnect(this)) {
            mHandler.sendEmptyMessage(MSG_SHOW_WAITING);
            mLocManager.requestLoc();
        } else {
            mHandler.sendEmptyMessage(MSG_HIDE_WAITING);
            AppInfo.showToast(this, getString(R.string.connect_out));
        }
    }

    @Override
    protected void onResume() {
        if (!CommonHttp.isConnect(this)) {
            AppInfo.showToast(this, getString(R.string.httpconnect));
        }

        super.onResume();
        isActive = true;

        if (mMapManager != null) {
            mMapManager.start();
        } else {
            mMapManager = mApp.getmBMapMan();
            mMapManager.start();
        }
        // try{
        // //ʹ��BaiDu��ͼ,��ʼ����ͼActivity
        // super.initMapActivity(mMapManager);
        // initMap();
        // } catch (Exception e) {
        // }
    }

    private void updateDriverList(Intent intent) {
        if (intent == null) {
            return;
        }
        ArrayList<DriverRecord> drivers = intent
                .getParcelableArrayListExtra("info");
        if (drivers == null || drivers.size() == 0) {
            return;
        }
        if (driverList != null) {
            driverList.clear();
        } else {
            driverList = new ArrayList<DriverRecord>();
        }
        driverList.addAll(drivers);

        updateUI();
        mHandler.sendEmptyMessage(MSG_LOAD_SUCC);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    /***
     * ��ʼ����ͼ
     */
    private void initMap() {
        mMapController = mMapView.getController();
        // �����������ſؼ�
        mMapView.setBuiltInZoomControls(true);
        // ���������Ŷ���������Ҳ��ʾoverlay,Ĭ��Ϊ������
        mMapView.setDrawOverlayWhenZooming(true);
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapManager.stop();
        isActive = false;
    }

    private void release() {
        mMapManager.stop();
        driverList.clear();
        System.gc();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.map_loc:
            if (CommonHttp.isConnect(this)) {
                mLocManager.requestLoc();
                mHandler.sendEmptyMessage(MSG_SHOW_WAITING);
                locImg.setEnabled(false);
            } else {
                AppInfo.showToast(this, getString(R.string.connect_out));
            }

            break;

        default:
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return false;
        }
        return false;
    }

    /**
     * ���ݵ�ǰ��ͼ���ļ�˾��λ�ã������ͼ���ģ����ƶ���ͼ
     */
    private void moveMap() {

        if (driverList == null || driverList.size() == 0) {
            return;
        }
        List<DriverRecord> list = new ArrayList<DriverRecord>();
        // ����ѡȡ��ǰ���е�˾��
        for (DriverRecord driver : driverList) {
            if ("0".equalsIgnoreCase(driver.getState())) {
                list.add(driver);
            }
        }

        // û�п��е�˾������ѡȡ��ǰ�����е�˾��
        if (list.size() == 0) {
            for (DriverRecord driver : driverList) {
                if (!"0".equalsIgnoreCase(driver.getState())) {
                    list.add(driver);
                }
            }
        }
        if (list.size() == 0) {
            return;
        }
        DriverRecord first = list.get(0);
        double minLon = first.getLongtitude(), maxLon = first.getLongtitude(), minLat = first
                .getLatitude(), maxLat = first.getLatitude();

        // ��������˾���ķ�Χ
        for (DriverRecord driver : list) {
            double lon = driver.getLongtitude();
            double lat = driver.getLatitude();
            if (minLon > lon) {
                minLon = lon;
            }
            if (maxLon < lon) {
                maxLon = lon;
            }

            if (minLat > lat) {
                minLat = lat;
            }

            if (maxLat < lat) {
                maxLat = lat;
            }
        }

        // ��λת��
        minLon *= 1E6;
        maxLon *= 1E6;
        minLat *= 1E6;
        maxLat *= 1E6;

        // �жϵ�ǰλ��(GPS)�Ƿ���˾����Χ��
        if (lont != 0 && lat != 0) {
            int centerLon = (int) (lont * 1E6);
            int centerLat = (int) (lat * 1E6);
            if (minLon > centerLon) {
                minLon = centerLon;
            }
            if (minLat > centerLat) {
                minLat = centerLat;
            }
            if (maxLon < centerLon) {
                maxLon = centerLon;
            }
            if (maxLat < centerLat) {
                maxLat = centerLat;
            }
        }

        // ����˾������ǰλ�� ��������ķ�Χ
        int spanLon = (int) (maxLon - minLon);
        int spanLat = (int) (maxLat - minLat);

        // ����˾��ͼ��λ�� (192*84)
        int spanLonOff = (int) (EDriverApp.getMarkCoefficient() * 200 * spanLon / EDriverApp
                .getWidth());
        int spanLatOff = (int) (EDriverApp.getMarkCoefficient() * 100 * spanLat / EDriverApp
                .getHeight());

        mMapController.zoomToSpan(spanLat + spanLatOff, spanLon + spanLonOff);

        // ��ͼ���ĵ���
        center.setLatitudeE6((int) (minLat + spanLat / 2));
        center.setLongitudeE6((int) (minLon + spanLon / 2));

        mMapController.animateTo(center);
    }

    @Override
    public void onFailed() {
        mHandler.sendEmptyMessage(MSG_LOCATE_ERROR);
    }

    @Override
    public void onGetLocation(BDLocation location) {
        lat = location.getLatitude();
        lont = location.getLongitude();

        if (center == null) {
            center = new GeoPoint((int) (lat * 1E6), (int) (lont * 1E6));
            mMapController.animateTo(center);
        } else {
            center.setLatitudeE6((int) (lat * 1E6));
            center.setLongitudeE6((int) (lont * 1E6));
        }

        if (isActive) {
            new DriverTask().execute();
        } else {
            mHandler.sendEmptyMessage(MSG_HIDE_WAITING);
        }
    }

    @Override
    public boolean isReceivedLocation() {
        return true;
    }

    private synchronized void updateUI() {
        if (mDriverOverlay == null) {
            mDriverOverlay = new DriverOverlay(EDriverMapActivity.this,
                    mMapView, driverList);
            mMapView.getOverlays().add(mDriverOverlay);
        } else {
            mDriverOverlay.updataDriverMarks(driverList);
        }
    }

    private class DriverTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                reSult = CommonHttp.getDriversInfo(EDriverApp.getImei(),
                        String.valueOf(lont), String.valueOf(lat),
                        mApp.getHttpClient());
                // mLogger.d("map--->"+reSult);
                if (reSult != null) {
                    mHandler.sendEmptyMessage(MSG_HIDE_WAITING);
                    try {
                        List<DriverRecord> driver = Utils.getDriverList(reSult);
                        if (driver.isEmpty()) {
                            mHandler.sendEmptyMessage(MSG_NO_DATA);
                            return null;
                        }
                        if (driverList == null) {
                            driverList = new ArrayList<DriverRecord>();
                        } else {
                            driverList.clear();
                        }
                        driverList.addAll(driver);

                        updateUI();
                        mHandler.sendEmptyMessage(MSG_LOAD_SUCC);

                    } catch (JSONException e) {
                        mHandler.sendEmptyMessage(MSG_HIDE_WAITING);
                        e.printStackTrace();
                    }
                } else {
                    mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
                }
            } catch (Exception e1) {
                mHandler.sendEmptyMessage(MSG_LOCATE_ERROR);
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
