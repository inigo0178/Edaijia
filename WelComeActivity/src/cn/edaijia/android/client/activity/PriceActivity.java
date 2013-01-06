package cn.edaijia.android.client.activity;

import java.util.ArrayList;

import com.baidu.location.BDLocation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.adapter.AboutAdapter;
import cn.edaijia.android.client.adapter.PriceListAdapter;
import cn.edaijia.android.client.maps.EDLocationListener;
import cn.edaijia.android.client.maps.LocManager;
import cn.edaijia.android.client.model.PriceInfo;
import cn.edaijia.android.client.model.TitleDetail;
import cn.edaijia.android.client.util.UtilListData;
import cn.edaijia.android.client.util.Utils;

public class PriceActivity extends BaseActivity implements OnItemClickListener,
        EDLocationListener {


    private PriceInfo priceInfo;

    private TextView price_title, price_note;

    private ListView price_listview, about_listview;

    private PriceListAdapter pAdapter;

    private AboutAdapter aAdapter;

    private ArrayList<TitleDetail> titles, adouts;

    public HomeActivity parentActivity;

    private LocManager mLocManager = null;

    private LinearLayout refresh_view;// 进度

    private String cityname;

    private SharedPreferences sharePference;
    private static final String KEY_GOT_CITY_NAME = "isGotCurCity";
    private final String KEY_CITY = "selectCity";

    private final int MSG_GOT_CUR_CITY = 1;
    private final int MSG_CUR_CITY_ERROR = 2;
    private final int MSG_UPDATE_CITY_NAME = 3;


    private final Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_GOT_CUR_CITY:
                refresh_view.setVisibility(View.GONE);
                price_title.setText(priceInfo.getPriceTitle());
                price_note.setText(priceInfo.getPriceNote());
                titles = priceInfo.getTitelDetails();
                pAdapter = new PriceListAdapter(PriceActivity.this, titles);
                price_listview.setAdapter(pAdapter);
                setListViewHeightBasedOnChildren(price_listview);

                break;
            case MSG_CUR_CITY_ERROR:
                parentActivity.titie_bt.setText(sharePference.getString(
                        KEY_CITY, ""));
                sharePference.edit().clear();
                refresh_view.setVisibility(View.GONE);
                Intent intent = new Intent(PriceActivity.this,
                        ErrorNetActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("tag", "PriceActivity");
                parentActivity.vf.removeAllViews();
                parentActivity.vf.addView(parentActivity
                        .getLocalActivityManager()
                        .startActivity("errReview", intent).getDecorView());
                break;
            case MSG_UPDATE_CITY_NAME:
                if (cityname != null) {
                    parentActivity.titie_bt.setText(cityname);
                }
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = (HomeActivity) this.getParent();
        parentActivity.titit_san.setVisibility(View.VISIBLE);
        sharePference = this.getSharedPreferences("city", Context.MODE_PRIVATE);
        mLocManager = EdApp.getLocManager();
        mLocManager.addLocationListener(this);

        setContentView(R.layout.price);

        price_title = (TextView) this.findViewById(R.id.price_title);
        price_note = (TextView) this.findViewById(R.id.price_note);
        price_listview = (ListView) this.findViewById(R.id.price_listview);
        about_listview = (ListView) this.findViewById(R.id.about_listview);
        refresh_view = (LinearLayout) this.findViewById(R.id.refresh_view);
        refresh_view.setVisibility(View.VISIBLE);

        initAboutData();
        initCityName(getIntent());
        mLocManager.requestLoc();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!sharePference.getBoolean(KEY_GOT_CITY_NAME, false)){
            refresh_view.setVisibility(View.VISIBLE);
            mLocManager.requestLoc();
            return;
        }
        // 先取值比较
        String gotCityName = intent.getStringExtra("cityname");
        if (gotCityName != null) {
            sharePference.edit().putString(KEY_CITY, gotCityName).commit();
        }

        // 取得保存的城市记录
        if (gotCityName == null) {
            gotCityName = sharePference.getString(KEY_CITY, null);
        }

        // No city
        if (cityname == null || cityname.length() == 0 || gotCityName == null
                || gotCityName.length() == 0) {
            refresh_view.setVisibility(View.VISIBLE);
            mLocManager.requestLoc();
            return;
        }

        if (!gotCityName.endsWith(cityname)) {
            cityname = gotCityName;
            refresh_view.setVisibility(View.VISIBLE);
            parentActivity.titie_bt.setText(cityname);
            requestPriceData(cityname);
        } else {
            if (priceInfo == null) {
                refresh_view.setVisibility(View.VISIBLE);
                parentActivity.titie_bt.setText(cityname);
                requestPriceData(cityname);
            }
        }
        initAboutData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharePference.edit().putBoolean(KEY_GOT_CITY_NAME, false).commit();
    }
    private void initCityName(Intent intent) {
        String gotCityName = null;
        // 先取值比较
        if (intent != null) {
            gotCityName = intent.getStringExtra("cityname");
        }
        // 取得保存的城市记录
        if (gotCityName == null) {
            gotCityName = sharePference.getString(KEY_CITY, "北京");
        }
        
        if (gotCityName != null){
            cityname = gotCityName;
            mhandler.sendEmptyMessage(MSG_UPDATE_CITY_NAME);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        parentActivity.titit_san.setVisibility(View.VISIBLE);
    }

    /*** 关于e代驾 ****/
    private void initAboutData() {
        TitleDetail t1 = new TitleDetail();
        TitleDetail t2 = new TitleDetail();
        TitleDetail t3 = new TitleDetail();
        t1.setLeftInfo(this.getString(R.string.hotline));
        t1.setRightInfo(this.getString(R.string.phone_400));
        t2.setLeftInfo(this.getString(R.string.feedback));
        t2.setRightInfo(this.getString(R.string.feedback_email));
        t3.setLeftInfo(this.getString(R.string.about_risks));
        t3.setRightInfo("");
        adouts = new ArrayList<TitleDetail>();
        adouts.add(t1);
        adouts.add(t2);
        adouts.add(t3);

        aAdapter = new AboutAdapter(this, adouts);
        about_listview.setAdapter(aAdapter);
        about_listview.setOnItemClickListener(this);
    }

    /** 初始数据 **/
    private void requestPriceData(final String city_name) {
        new Thread() {
            @Override
            public void run() {
                priceInfo = UtilListData.getInstance().getDataPriceInfo(
                        String.valueOf(mLocManager.getLont()),
                        String.valueOf(mLocManager.getLat()), city_name);
                if (priceInfo != null) {
                    mhandler.sendEmptyMessage(MSG_GOT_CUR_CITY);
                } else {
                    mhandler.sendEmptyMessage(MSG_CUR_CITY_ERROR);
                }
            }
        }.start();
    }

    /** 监听 **/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        if (position == 0) {
            // 打电话
            Utils.callPhone(this, AppInfo.PHONE_NUMBER_400);
            Utils.saveCallLog(EdApp, AppInfo.PHONE_NUMBER_400);
        } else if (position == 1) {
            Utils.sendEmail(
                    this,
                    getString(R.string.feedback_email),
                    getString(R.string.app_name) + getString(R.string.feedback),
                    null);
        } else if (position == 2) {
            parentActivity.linear_map.setVisibility(View.GONE);
            parentActivity.linear_title.setVisibility(View.VISIBLE);
            parentActivity.titie_bt.setVisibility(View.GONE);
            parentActivity.titit_san.setVisibility(View.GONE);
            // parentActivity.linear_back.setVisibility(View.VISIBLE);
            parentActivity.title_tv.setText(PriceActivity.this
                    .getString(R.string.risks_details));
            // parentActivity.back.setText("返回");
            Intent in = new Intent(PriceActivity.this, InsureActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            parentActivity.vf.removeAllViews();
            parentActivity.vf.addView(parentActivity.getLocalActivityManager()
                    .startActivity("insure", in).getDecorView());
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

    private void setListViewHeightBasedOnChildren(ListView listView) {

        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);

    }

    @Override
    public void onFailed() {
        mhandler.sendEmptyMessage(MSG_CUR_CITY_ERROR);
    }

    @Override
    public void onGetLocation(BDLocation location) {
        String city = location.getCity();
        if(city == null){
            mhandler.sendEmptyMessage(MSG_CUR_CITY_ERROR);
            return;
        }
        cityname = city.substring(0, city.indexOf("市"));// addr.substring(0, 2);
        sharePference.edit().putString(KEY_CITY, cityname).commit();
        sharePference.edit().putBoolean(KEY_GOT_CITY_NAME, true).commit();
        
        mhandler.sendEmptyMessage(MSG_UPDATE_CITY_NAME);
        requestPriceData(cityname);
    }

    @Override
    public boolean isReceivedLocation() {
        return super.isActive;
    }

}
