package cn.edaijia.android.client.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.adapter.AboutAdapter;
import cn.edaijia.android.client.db.DBDaoImpl;
import cn.edaijia.android.client.model.AppContentInfo;
import cn.edaijia.android.client.model.TitleDetail;
import cn.edaijia.android.client.util.Utils;

public class Share2Friend extends BaseActivity implements OnItemClickListener {
    /**** 推荐好友 *****/

    DBDaoImpl impl;

    private AppContentInfo info;

    private ListView share_listview;

    private AboutAdapter aAdapter;

    private ArrayList<TitleDetail> adouts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share2friend);
        impl = DBDaoImpl.getInstance(); // new DBDaoImpl(this);
        info = impl.getDbApp();

        initView();
        initData();
    }

    /** 初始数据 **/
    private void initData() {
        TitleDetail t1 = new TitleDetail();
        TitleDetail t2 = new TitleDetail();
        TitleDetail t3 = new TitleDetail();
        t1.setLeftInfo(this.getString(R.string.share_weibo));
        t1.setRightInfo("");
        t2.setLeftInfo(this.getString(R.string.share_email));
        t2.setRightInfo("");
        t3.setLeftInfo(this.getString(R.string.share_sms));
        t3.setRightInfo("");

        adouts = new ArrayList<TitleDetail>();
        adouts.add(t1);
        adouts.add(t2);
        adouts.add(t3);

        //
        aAdapter = new AboutAdapter(this, adouts);
        share_listview.setAdapter(aAdapter);
    }

    /**** 初始ui ****/
    private void initView() {
        share_listview = (ListView) this.findViewById(R.id.share_listview);
        // 监听
        share_listview.setOnItemClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initView();
        initData();
    }

    /*** 监听 ***/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Intent intent = null;
        switch (position) {
        case 0:
            intent = new Intent(Share2Friend.this, SinaWeiboActivity.class);
            intent.putExtra("weibo", info.getMicBlogMessage());
            this.startActivity(intent);
            break;
        case 1:
            Utils.sendEmail(this, null, getString(R.string.app_name) + "-"
                    + getString(R.string.shared_email_subject),
                    info.getEmailMessageBody());
            break;
        case 2:
            intent = new Intent(Intent.ACTION_VIEW);
            // 设置要默认发送的内容
            intent.putExtra("sms_body", info.getsMSBodyText());
            intent.setType("vnd.android-dir/mms-sms");
            this.startActivity(intent);
            break;
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
}
