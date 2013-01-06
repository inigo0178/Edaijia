package cn.edaijia.android.client.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.db.DBDaoImpl;
import cn.edaijia.android.client.model.AppContentInfo;
import cn.edaijia.android.client.service.EDService;
import cn.edaijia.android.client.util.TimeUtil;
import cn.edaijia.android.client.util.UtilListData;
import cn.edaijia.android.client.util.Utils;

/**
 * ��ӭ����
 * 
 * @author CaoZheng email:caozheng_119@163.com
 * @create time:2012-8-10 10:07 --------------------last
 *         update:----------------- coder: update time: Copyright (c) YDJ
 *         corporation All Rights Reserved. INFORMATION
 */
public class WelComeActivity extends BaseActivity implements AnimationListener {

    private Animation mAnimation; // ,carAnim;

    private ImageView logIn; // , car; //��ӭҳ��

    private SharedPreferences sp = null;

    private String PRF_KEY_FIRST = "isFrist";

    // private Logger mLogger = Logger.createLogger("WelComeActivity");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.welcome);
        mAnimation.setFillEnabled(true); // ����Fill����
        mAnimation.setFillAfter(true); // ���ö��������һ֡�Ǳ�����View����
        mAnimation.setAnimationListener(this);

        // carAnim = AnimationUtils.loadAnimation(this, R.anim.car_anim);
        // carAnim.setAnimationListener(this);

        // car = (ImageView) findViewById(R.id.car);
        logIn = (ImageView) findViewById(R.id.welcomeImg);
        logIn.setAnimation(mAnimation);

        sp = getSharedPreferences(AppInfo.PRE_STATE, Context.MODE_PRIVATE);

        // ΢��
        Utils.saveWeiboSharedPicture(this);
        // ���汾
        Intent serviceIntent = new Intent(this, EDService.class);
        serviceIntent.setAction(EDService.ACTION_UPDATE);
        startService(serviceIntent);

        // app
        initApp();
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // if (animation.equals(mAnimation)) {
        // car.startAnimation(carAnim);
        // } else {
        if (sp.getBoolean(PRF_KEY_FIRST, true)) {
            startActivity(new Intent(this, GuideView.class));
            // activity�л�Ч��
            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            sp.edit().putBoolean(PRF_KEY_FIRST, false).commit();
            finish();
        } else {
            Intent in = new Intent();
            in.setClass(this, HomeActivity.class);
            startActivity(in);
            // activity�л�Ч��
            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            finish();
        }
        // }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationStart(Animation animation) {
       
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    /** ��ʼapp_content **/
    private void initApp() {
        new Thread() {
            @Override
            public void run() {
                AppContentInfo gotInfo = UtilListData.getInstance()
                        .getDataAppContent();
                if (gotInfo != null) {
                    long gotExpriedTime = TimeUtil.getTime(gotInfo
                            .getExpireAt());
                    // �����ݿ��
                    AppContentInfo savedInfo = DBDaoImpl.getInstance()
                            .queryDBApp();
                    long savedExprieTime = savedInfo != null ? TimeUtil
                            .getTime(savedInfo.getExpireAt()) : 0;
                    if (savedExprieTime == 0L) {// ��һ��д��
                        DBDaoImpl.getInstance().saveDBApp(
                                gotInfo.getExpireAt(), gotInfo.getContent());
                    } else {// ����
                        if (gotExpriedTime > savedExprieTime) {
                            DBDaoImpl.getInstance()
                                    .updateDBApp(gotInfo.getExpireAt(),
                                            gotInfo.getContent());
                        }
                    }
                }
            }
        }.start();
    }
}
