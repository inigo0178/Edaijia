package cn.edaijia.android.client.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.db.DBDaoImpl;
import cn.edaijia.android.client.model.AppContentInfo;
import cn.edaijia.android.client.util.CommonHttp;
import cn.edaijia.android.client.util.Logger;
import cn.edaijia.android.client.util.Utils;

public class ErrorNetActivity extends BaseActivity implements OnClickListener {
    /**
     * 无法定位或者网络不通的情况下，提示用户拨打400电话进行呼叫 重新定位
     * **/

    private String tag_new, tag_resume;// 标志

    private SharedPreferences sharedfers;

    private HomeActivity parentActivity;

    private AppContentInfo info;

    private Logger mLogger = Logger.createLogger("ErrorNetActivity");

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.error_net);
        parentActivity = (HomeActivity) this.getParent();
        sharedfers = getSharedPreferences("city", Context.MODE_PRIVATE);
        info = DBDaoImpl.getInstance().getDbApp();
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            tag_new = intent.getStringExtra("tag");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null){
            tag_resume = this.getIntent().getStringExtra("tag");
        }
    }

    /** 初始ui **/
    public void initView() {
        Button btnCall400 = (Button) this.findViewById(R.id.er_bt);
        Button btnReLocate = (Button) this.findViewById(R.id.new_bt);
        TextView tips_error_net = (TextView) this.findViewById(R.id.tips_error_net);
        tips_error_net.setText(info.getTipsText());
        btnCall400.setOnClickListener(this);
        btnReLocate.setOnClickListener(this);
    }

    /** 监听 **/
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
        case R.id.er_bt:
            // 打电话
            Utils.callPhone(this, AppInfo.PHONE_NUMBER_400);
            Utils.saveCallLog(EdApp, AppInfo.PHONE_NUMBER_400);
            break;
        case R.id.new_bt:
            // 判断是否有网络
            if (!CommonHttp.isConnect(this)) {
                AppInfo.showToast(this, this.getString(R.string.httpconnect));
                return;
            }
            String tag = null;
            if (tag_new == null) {
                tag = tag_resume;
            } else {
                tag = tag_new;
            }
            mLogger.d("------tag---->" + tag);
            if ("PriceActivity".equals(tag)) {
                parentActivity.vf.removeAllViews();
                String cityName = sharedfers.getString("selectCity", "");
                //重新定位后，设置已经取得当前城市标识为false
                sharedfers.edit().putBoolean("isGotCurCity", false).commit();
                Intent intent = new Intent(ErrorNetActivity.this,
                        PriceActivity.class);
                intent.putExtra("cityname", cityName);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                parentActivity.vf.addView(parentActivity
                        .getLocalActivityManager()
                        .startActivity("actPrice", intent).getDecorView());

            } else if ("DriverListActivity".equals(tag)) {

                parentActivity.vf.removeAllViews();
                Intent intent_driver = new Intent(ErrorNetActivity.this,
                        DriverListActivity.class);
                // intent_driver.putExtra("info", listString);
                intent_driver.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                parentActivity.vf.addView(parentActivity
                        .getLocalActivityManager()
                        .startActivity("actDriver", intent_driver)
                        .getDecorView());

            }
            break;
        }
    }

}
