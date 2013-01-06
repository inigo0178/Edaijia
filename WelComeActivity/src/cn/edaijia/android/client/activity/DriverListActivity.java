package cn.edaijia.android.client.activity;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import com.baidu.location.BDLocation;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.EDriverApp;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.adapter.NearbyListAdapter;
import cn.edaijia.android.client.maps.DriverRecord;
import cn.edaijia.android.client.maps.EDLocationListener;
import cn.edaijia.android.client.maps.LocManager;
import cn.edaijia.android.client.util.CommonHttp;
import cn.edaijia.android.client.util.MyListView;
import cn.edaijia.android.client.util.MyListView.OnRefreshListener;
import cn.edaijia.android.client.util.UtilListData;

public class DriverListActivity extends BaseActivity implements
        OnItemClickListener, EDLocationListener {

    private UtilListData uList;

    static ArrayList<DriverRecord> mDriverList;

    private cn.edaijia.android.client.util.MyListView nearby_listview;

    private NearbyListAdapter nblAdapter;

    private LocManager mLocManager = null;

    public LocalActivityManager localActivityManager;

    public HomeActivity parentActivity;

    private LinearLayout refresh_view;

    private static final int MSG_LOAD_SUCC = 0x1;
    private static final int MSG_LOAD_FAILED = 0x2;
    private static final int MSG_SHOW_WAITING = 0x3;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case MSG_LOAD_SUCC:
                refresh_view.setVisibility(View.GONE);
                nblAdapter.notifyDataSetChanged();
                Intent map_to_list = new Intent("mapTolist");
                map_to_list.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) mDriverList);
                DriverListActivity.this.sendBroadcast(map_to_list);
                break;
            case MSG_LOAD_FAILED:
                err();
                break;
            case MSG_SHOW_WAITING:
                refresh_view.setVisibility(View.VISIBLE);
                break;
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_nearby);

        uList = UtilListData.getInstance(); // new UtilListData(this);
        mLocManager = EdApp.getLocManager();

        parentActivity = (HomeActivity) this.getParent();

        nearby_listview = (MyListView) this.findViewById(R.id.nearby_listview);
        refresh_view = (LinearLayout) this.findViewById(R.id.refresh_view);
        nearby_listview.setOnItemClickListener(this);

        mLocManager.addLocationListener(this);

        mDriverList = new ArrayList<DriverRecord>();
        nblAdapter = new NearbyListAdapter(DriverListActivity.this,
                mDriverList, nearby_listview);
        nearby_listview.setAdapter(nblAdapter);
        nearby_listview.setonRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                new DownLoadTask().execute();
            }
        });

        Intent intent = this.getIntent();
        updateDriverList(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        updateDriverList(intent);
        refresh_view.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 无网络
        if (!CommonHttp.isConnect(this)) {
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
            return;
        }
    }

    private void updateDriverList(Intent intent) {
        if (intent != null) {
            ArrayList<DriverRecord> drivers = intent
                    .getParcelableArrayListExtra("info");
            if (drivers != null && drivers.size() > 0) {
                if (mDriverList != null) {
                    mDriverList.clear();
                }
                mDriverList.addAll(drivers);
            }
            if (mDriverList != null && mDriverList.size() > 0) {
                nblAdapter.notifyDataSetChanged();
            } else {
                mLocManager.requestLoc();
                mHandler.sendEmptyMessage(MSG_SHOW_WAITING);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /*** 数据初始化 ****/
    private void requestData() {
        if (CommonHttp.isConnect(DriverListActivity.this)) {
            try {
                ArrayList<DriverRecord> drivers = uList.getDataDriverRecord(
                        EDriverApp.getImei(),
                        String.valueOf(mLocManager.getLont()),
                        String.valueOf(mLocManager.getLat()));
                if (drivers != null && drivers.size() > 0) {
                    if (mDriverList != null) {
                        mDriverList.clear();
                    } else {
                        mDriverList = new ArrayList<DriverRecord>();
                    }
                    mDriverList.addAll(drivers);
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            }
            if (mDriverList != null && mDriverList.size() != 0) {
                mHandler.sendEmptyMessage(MSG_LOAD_SUCC);
            } else {
                mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
                return;
            }
        } else {
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
    }

    /** errView **/
    private void err() {
        refresh_view.setVisibility(View.GONE);
        // 网络连接有问题 或 定位失败
        AppInfo.showToast(this, this.getString(R.string.connect_or_position));

        Intent intent = new Intent(DriverListActivity.this,
                ErrorNetActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("tag", "DriverListActivity");
        parentActivity.vf.removeAllViews();
        parentActivity.vf.addView(parentActivity.getLocalActivityManager()
                .startActivity("errReview", intent).getDecorView());

    }

    private class DownLoadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            requestData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            nblAdapter.notifyDataSetChanged();
            nearby_listview.onRefreshComplete();
        }
    }

    /*** 监听 ****/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        if (mDriverList != null) {
            DriverRecord info = mDriverList.get(position - 1);

            Intent intent = new Intent(DriverListActivity.this,
                    DriverDetails.class);

            intent.putExtra("info", info);
            this.startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onFailed() {
        mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
    }

    @Override
    public void onGetLocation(BDLocation location) {
        new DownLoadTask().execute();
    }

    @Override
    public boolean isReceivedLocation() {
        return super.isActive;
    }
}
