package cn.edaijia.android.client.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.adapter.MyDriverListAdapter;
import cn.edaijia.android.client.db.DBDaoImpl;
import cn.edaijia.android.client.maps.DriverRecord;
import cn.edaijia.android.client.util.MyListView;
import cn.edaijia.android.client.util.MyListView.OnRefreshListener;

public class AccountActivity extends BaseActivity {

    /** 我的代驾 **/
    private LinearLayout refresh_view;// 进度条

    private LinearLayout mydriver_on;// 没有用过代驾

    private MyListView mydriver_listView;// 列表

    private ArrayList<DriverRecord> driverInfos = new ArrayList<DriverRecord>();

    private MyDriverListAdapter adapter;

    private final int MSG_VIEW_HIDE = 0x1;

    private final int MSG_NO_DRIVER = 0x2;

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_VIEW_HIDE:
                refresh_view.setVisibility(View.GONE);
                mydriver_on.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                break;
            case MSG_NO_DRIVER:
                refresh_view.setVisibility(View.GONE);
                mydriver_on.setVisibility(View.VISIBLE);
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_driverlist);

        refresh_view = (LinearLayout) this.findViewById(R.id.refresh_view);
        mydriver_on = (LinearLayout) this.findViewById(R.id.mydriver_on);
        mydriver_listView = (MyListView) this
                .findViewById(R.id.mydriver_listView);

        refresh_view.setVisibility(View.VISIBLE);
        initMyDriverList();
        initData();
    }

    private void initMyDriverList() {
        adapter = new MyDriverListAdapter(AccountActivity.this, driverInfos,
                mydriver_listView);
        mydriver_listView.setAdapter(adapter);
        mydriver_listView.setonRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... arg0) {
                        initData();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        adapter.notifyDataSetChanged();
                        mydriver_listView.onRefreshComplete();
                    }
                }.execute();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        refresh_view.setVisibility(View.VISIBLE);
        initData();
    }

    /** 数据初始化 **/
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                ArrayList<DriverRecord> drives = DBDaoImpl.getInstance()
                        .getDriverList();
                if (drives != null && drives.size() > 0) {
                    if (!driverInfos.isEmpty()) {
                        driverInfos.clear();
                    }
                    driverInfos.addAll(drives);
                    mHandler.sendEmptyMessage(MSG_VIEW_HIDE);
                } else {
                    if (driverInfos != null) {
                        driverInfos.clear();
                    }
                    mHandler.sendEmptyMessage(MSG_NO_DRIVER);
                }
            }

        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
