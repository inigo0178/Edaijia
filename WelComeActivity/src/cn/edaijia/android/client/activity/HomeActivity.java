package cn.edaijia.android.client.activity;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.EDriverApp;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.maps.DriverRecord;
import cn.edaijia.android.client.maps.EDriverMapActivity;
import cn.edaijia.android.client.maps.LocManager;
import cn.edaijia.android.client.service.EDService;
import cn.edaijia.android.client.util.Logger;

import com.umeng.analytics.MobclickAgent;

/**
 * UI框架类 整体UI分布控制管理
 * 
 * @author CaoZheng email:caozheng_119@163.com
 * @create time:2012-8-10 10:07 --------------------last
 *         update:----------------- coder: update time: Copyright (c) YDJ
 *         corporation All Rights Reserved. INFORMATION
 */
public class HomeActivity extends ActivityGroup implements OnClickListener {

    private EDriverApp mApp = null;

    public ViewFlipper vf = null;

    private LocManager mLocManager = null;

    private Button btnMap = null;

    private Button btnDriverList = null;

    private long exitTime = 0l;

    private String oneId = null;

    private String twoId = null;

    public LocalActivityManager localActivityManager;

    LinearLayout linear_title, linear_map;// linear_back;

    public TextView title_tv;// 标题

    public Button titie_bt, titit_san;// back;//城市

    private Button title_favorable;// 优惠劵

    public SharedPreferences sharedfers;

    public String cityName;

    private ListBroadcastReciver mDriverListReceiver;

    private ArrayList<DriverRecord> driverList;

    private Logger mLogger = Logger.createLogger("HomeActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置视图
        setContentView(R.layout.main);
        mApp = (EDriverApp) getApplication();

        // 获得ViewFlipper
        vf = (ViewFlipper) findViewById(R.id.homeview_control);
        // 获得RadioGroup
        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
        initRGButtons(rg);
        // -----
        localActivityManager = getLocalActivityManager();
        // ----
        // button
        btnMap = (Button) this.findViewById(R.id.map_bt);
        btnDriverList = (Button) this.findViewById(R.id.driver_bt);
        btnDriverList.setEnabled(true);
        btnMap.setEnabled(false);

        // back = (Button) this.findViewById(R.id.back);
        // linear_back = (LinearLayout) this.findViewById(R.id.linear_back);
        linear_map = (LinearLayout) this.findViewById(R.id.linear_map);
        linear_title = (LinearLayout) this.findViewById(R.id.linear_title);
        title_tv = (TextView) this.findViewById(R.id.title_tv);
        titie_bt = (Button) this.findViewById(R.id.titie_bt);
        titit_san = (Button) this.findViewById(R.id.titit_san);
        title_favorable = (Button) this.findViewById(R.id.title_favorable);
        // back.setOnClickListener(this);
        titie_bt.setOnClickListener(this);
        title_favorable.setOnClickListener(this);
        title_favorable.setVisibility(View.VISIBLE);
        //
        mLocManager = mApp.getLocManager();
        //
        sharedfers = getSharedPreferences("city", Context.MODE_PRIVATE);
        //

        initMDButtons();
        vf.removeAllViews();
        vf.addView(localActivityManager.startActivity(
                "actMap",
                new Intent(HomeActivity.this, EDriverMapActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                .getDecorView());
        vf.getCurrentView().setId(7);

        // Upload call log
        Intent serviceIntent = new Intent(this, EDService.class);
        serviceIntent.setAction(EDService.ACTION_UPLOAD_CALL_LOG);
        startService(serviceIntent);

        if (AppInfo.CHANNEL_HOMEPAGE.equals(EDriverApp.getChannel())) {
            MobclickAgent.onError(this);
        }

        // 注册广播
        IntentFilter filter_list = new IntentFilter("mapTolist");
        mDriverListReceiver = new ListBroadcastReciver();
        this.registerReceiver(mDriverListReceiver, filter_list);

    }

    private void initRGButtons(RadioGroup rg) {
        rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                vf.removeAllViews();
                switch (checkedId) {
                case R.id.radioButton1:
                    linear_map.setVisibility(View.VISIBLE);
                    linear_title.setVisibility(View.GONE);
                    titie_bt.setVisibility(View.GONE);
                    titit_san.setVisibility(View.GONE);
                    // linear_back.setVisibility(View.GONE);
                    title_favorable.setVisibility(View.VISIBLE);
                    btnMap.setEnabled(false);
                    btnDriverList.setEnabled(true);
                    vf.addView(localActivityManager
                            .startActivity(
                                    "actMap",
                                    new Intent(HomeActivity.this,
                                            EDriverMapActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT))
                            .getDecorView());
                    vf.getCurrentView().setId(7);
                    break;
                case R.id.radioButton2:
                    linear_map.setVisibility(View.GONE);
                    linear_title.setVisibility(View.VISIBLE);
                    titie_bt.setVisibility(View.GONE);
                    titit_san.setVisibility(View.GONE);
                    title_favorable.setVisibility(View.GONE);
                    // linear_back.setVisibility(View.GONE);
                    title_tv.setText(HomeActivity.this
                            .getString(R.string.review));
                    vf.addView(localActivityManager
                            .startActivity(
                                    "actAccount",
                                    new Intent(HomeActivity.this,
                                            AccountActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            .getDecorView());
                    break;
                case R.id.radioButton3:
                    linear_map.setVisibility(View.GONE);
                    linear_title.setVisibility(View.VISIBLE);
                    titie_bt.setVisibility(View.GONE);
                    titit_san.setVisibility(View.GONE);
                    title_favorable.setVisibility(View.GONE);
                    // linear_back.setVisibility(View.GONE);
                    title_tv.setText(HomeActivity.this
                            .getString(R.string.recommend));
                    vf.addView(localActivityManager.startActivity(
                            "actReview",
                            new Intent(HomeActivity.this, Share2Friend.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            .getDecorView());
                    break;
                case R.id.radioButton4:
                    backPrice();
                    break;
                default:
                    break;
                }
            }
        });

    }

    private void initMDButtons() {
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMap.setEnabled(false);
                btnDriverList.setEnabled(true);
                vf.removeAllViews();

                Intent mapIntent = new Intent(HomeActivity.this,
                        EDriverMapActivity.class);
                mapIntent.putParcelableArrayListExtra("info", driverList);
                mapIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                vf.addView(localActivityManager.startActivity("actMap",
                        mapIntent).getDecorView());
            }
        });

        btnDriverList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btnMap.setEnabled(true);
                btnDriverList.setEnabled(false);
                vf.removeAllViews();
                Intent intent_driver = new Intent(HomeActivity.this,
                        DriverListActivity.class);
                intent_driver.putParcelableArrayListExtra("info", driverList);
                intent_driver.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                vf.addView(localActivityManager.startActivity("actDriver",
                        intent_driver).getDecorView());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.titie_bt:
            if (HomeActivity.this.getString(R.string.price).equals(
                    title_tv.getText().toString())) {
                linear_map.setVisibility(View.GONE);
                linear_title.setVisibility(View.VISIBLE);
                titie_bt.setVisibility(View.GONE);
                titit_san.setVisibility(View.GONE);
                // linear_back.setVisibility(View.VISIBLE);

                title_tv.setText(HomeActivity.this
                        .getString(R.string.city_choice));
                Intent intent = new Intent(HomeActivity.this,
                        CityListActivity.class);
                intent.putExtra("crr_city", titie_bt.getText().toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                vf.removeAllViews();
                vf.addView(localActivityManager.startActivity("cityList",
                        intent).getDecorView());
            }
            break;
        case R.id.title_favorable: {
            Intent intent = new Intent(HomeActivity.this,
                    FavorableActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            HomeActivity.this.startActivity(intent);
        }
            break;
        // case R.id.back:
        // backPrice();
        // break;
        }
    }

    /**
     * 返回退出程序
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            oneId = localActivityManager.getCurrentId();
            if ("insure".equals(oneId)) {// 代驾险
                backPrice();
            } else {
                backDown();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void backDown() {
        mLogger.d(localActivityManager.getCurrentId());
        if (System.currentTimeMillis() - exitTime > 1800) {
            Toast.makeText(this, this.getString(R.string.click_agine_eixt),
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
            twoId = localActivityManager.getCurrentId();
        } else if (oneId.equals(twoId)) {
            mLogger.d("退出");
            // 停止定位服务
            mLocManager.stopLocService();

            mApp.getmBMapMan().destroy();
            // 退出程序
            Intent exitIntent = new Intent(Intent.ACTION_MAIN);
            exitIntent.addCategory(Intent.CATEGORY_HOME);

            HomeActivity.this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    // 广播
    class ListBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String act = intent.getAction();
            if ("mapTolist".equals(act)) {
                driverList = intent.getParcelableArrayListExtra("list");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mDriverListReceiver);
        } catch (Exception e) {
            mLogger.e("error when close the receiver in HomeActivity ", e);
        }
        EDriverApp.finishAllActivity();
    }

    @Override
    protected void onPause() {
        // if (vf.getCurrentView().getId()==7) {
        // vf.removeAllViews();
        // }
        super.onPause();
        if (AppInfo.CHANNEL_HOMEPAGE.equals(EDriverApp.getChannel())) {
            MobclickAgent.onPause(this);
        }
    }

    @Override
    protected void onResume() {
        // if (vf.getCurrentView() == null) {
        // vf.addView(localActivityManager.startActivity(
        // "actMap",
        // new Intent(HomeActivity.this, EDriverMapActivity.class)
        // .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT))
        // .getDecorView());
        // vf.getCurrentView().setId(7);
        // }

        super.onResume();
        if (AppInfo.CHANNEL_HOMEPAGE.equals(EDriverApp.getChannel())) {
            MobclickAgent.onResume(this);
        }
    }

    /** 价格表 **/
    private void backPrice() {
        linear_map.setVisibility(View.GONE);
        linear_title.setVisibility(View.VISIBLE);
        titie_bt.setVisibility(View.VISIBLE);
        titit_san.setVisibility(View.VISIBLE);
        title_favorable.setVisibility(View.GONE);
        // linear_back.setVisibility(View.GONE);
        title_tv.setText(HomeActivity.this.getString(R.string.price));

        // cityName = sharedfers.getString("selectCity", "北京");
        // titie_bt.setText(cityName);

        Intent intent = new Intent(HomeActivity.this, PriceActivity.class);
        intent.putExtra("cityname", cityName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        vf.removeAllViews();
        vf.addView(localActivityManager.startActivity("actPrice", intent)
                .getDecorView());
    }

}
