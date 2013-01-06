package cn.edaijia.android.client.activity;

import java.util.ArrayList;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.util.UtilListData;

public class CityListActivity extends BaseActivity implements
        OnItemClickListener, OnClickListener {
    /** 城市列表 **/

    private TextView curr_city_name;

    private ListView city_listview;

    private LinearLayout refresh_view;

    private ArrayList<String> cityLists = new ArrayList<String>();

    private HomeActivity parentView;

    private LocalActivityManager lactivityManager;

    private static String titil_city_name, resume_city_name;// 当前城市

    private static final int REFRESH_SUC = 1;
    private static final int REFRESH_FAIL = 2;

    private ArrayAdapter<String> adpter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case REFRESH_SUC:
                refresh_view.setVisibility(View.GONE);
                adpter.notifyDataSetChanged();
                break;
            case REFRESH_FAIL:
                refresh_view.setVisibility(View.GONE);
                break;
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.city_list);
        parentView = (HomeActivity) this.getParent();
        // parentView.linear_back.setVisibility(View.VISIBLE);
        lactivityManager = parentView.getLocalActivityManager();

        initView();
        refresh_view.setVisibility(View.VISIBLE);
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            titil_city_name = intent.getStringExtra("crr_city");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 第一次跳转
        Intent oIntent = this.getIntent();
        if (oIntent != null) {
            resume_city_name = oIntent.getStringExtra("crr_city");
        }
        //
        if (titil_city_name == null) {
            titil_city_name = resume_city_name;
        }
        curr_city_name.setText(titil_city_name);
        
        if(cityLists.isEmpty()){
        	initData();
        }
    }

    /** ui初始化 **/
    private void initView() {
        curr_city_name = (TextView) this.findViewById(R.id.curr_city_name);
        city_listview = (ListView) this.findViewById(R.id.city_listview);
        refresh_view = (LinearLayout) this.findViewById(R.id.refresh_view);
        // 监听
        city_listview.setOnItemClickListener(this);
        curr_city_name.setOnClickListener(this);

        adpter = new ArrayAdapter<String>(CityListActivity.this,
                R.layout.simple_list_item_1, cityLists);
        city_listview.setAdapter(adpter);
    }

    /** 数据初始化 **/
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                ArrayList<String> cities = UtilListData.getInstance()
                        .getDataCityList();
                if (cities != null && cities.size() > 0) {
                    if (!cityLists.isEmpty()) {
                        cityLists.clear();
                    }
                    cityLists.addAll(cities);
                    mHandler.sendEmptyMessage(REFRESH_SUC);
                } else {
                    if (!cityLists.isEmpty()) {
                        cityLists.clear();
                    }
                    mHandler.sendEmptyMessage(REFRESH_FAIL);
                }
            }
        }.start();
    }

    /** 点击当前城市 **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.curr_city_name:
            backPrice();
            break;
        }
    }

    /** 监听 **/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {

        String cname = cityLists.get(position);

        Intent intent = new Intent(parentView, PriceActivity.class);
        intent.putExtra("cityname", cname);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        parentView.titie_bt.setVisibility(View.VISIBLE);
        // parentView.linear_back.setVisibility(View.GONE);
        parentView.title_tv.setText(CityListActivity.this
                .getString(R.string.price));
        parentView.vf.removeAllViews();
        parentView.vf.addView(lactivityManager
                .startActivity("actPrice", intent).getDecorView());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backPrice();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /** 返回价格表 **/
    private void backPrice() {
        Intent intent = new Intent(this, PriceActivity.class);
        if (titil_city_name == null) {
            titil_city_name = resume_city_name;
        }
        intent.putExtra("cityname", titil_city_name);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        parentView.titie_bt.setVisibility(View.VISIBLE);
        parentView.titit_san.setVisibility(View.VISIBLE);
        // parentView.linear_back.setVisibility(View.GONE);

        parentView.title_tv.setText(CityListActivity.this
                .getString(R.string.price));
        parentView.vf.removeAllViews();
        parentView.vf.addView(lactivityManager
                .startActivity("actPrice", intent).getDecorView());
    }
}
