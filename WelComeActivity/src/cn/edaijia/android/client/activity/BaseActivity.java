package cn.edaijia.android.client.activity;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.EDriverApp;
/**
 * 公共Activity
 * @author CaoZheng		email:caozheng_119@163.com
 * @create time:2012-8-10 10:07
 * --------------------last update:----------------- 
 * coder:                            update
 * time: Copyright (c) YDJ corporation All Rights Reserved. 
 * INFORMATION
 */
public class BaseActivity extends Activity {
	
	public EDriverApp EdApp = null;
	
	/** 当前该Activity */
	protected boolean isActive;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	EdApp = (EDriverApp) this.getApplication();
		EDriverApp.addActivity(this);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);	//标题栏隐藏
        
        if(AppInfo.CHANNEL_HOMEPAGE.equals(EDriverApp.getChannel())){
            MobclickAgent.onError(this);
        }
    }
      
	@Override
	protected void onDestroy() {
		EDriverApp.removeActivity(this);
		super.onDestroy();
	}
    
	@Override
	protected void onResume() {
	    super.onResume();
	    isActive = true;
	    if(AppInfo.CHANNEL_HOMEPAGE.equals(EDriverApp.getChannel())){
            MobclickAgent.onPause(this);
        }
	}
    
	@Override
	protected void onPause() {
	    super.onPause();
	    isActive = false;
	    if(AppInfo.CHANNEL_HOMEPAGE.equals(EDriverApp.getChannel())){
            MobclickAgent.onError(this);
        }
	}
}