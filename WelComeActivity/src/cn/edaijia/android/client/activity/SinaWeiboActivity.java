package cn.edaijia.android.client.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.util.AuthDialogListener;
import cn.edaijia.android.client.util.Logger;

import com.weibo.sdk.android.Weibo;

public class SinaWeiboActivity extends BaseActivity implements OnClickListener {
	/*** 新浪微博分享 ***/
	Button comm_back, comm_submit;// 返回 、提交

	TextView title_tv; // 标题

	EditText sina_et; // 微博内容

	Weibo weibo; // 微博

	private String blog_sina;

	private Logger mLogger = Logger.createLogger("SinaWeiboActivity");

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.sinawb);
		Intent intent = this.getIntent();
		blog_sina = intent.getStringExtra("weibo");
		initView();
	}

	/*** 初始ui ***/
	public void initView() {
		comm_back = (Button) this.findViewById(R.id.comm_back);
		comm_submit = (Button) this.findViewById(R.id.comm_submit);
		title_tv = (TextView) this.findViewById(R.id.title_tv);
		sina_et = (EditText) this.findViewById(R.id.sina_et);
		title_tv.setText(this.getString(R.string.share_weibo));
		comm_submit.setVisibility(View.VISIBLE);
		comm_back.setOnClickListener(this);
		comm_submit.setOnClickListener(this);
		sina_et.setText(blog_sina);
		// 弹出键盘
		sina_et.setFocusable(true);
		sina_et.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(sina_et, 0);

	}

	/** 监听 **/
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.comm_back:
			finish();
			break;
		case R.id.comm_submit:
			// if(CommonCode.sina_token == ""){
			weibo = Weibo.getInstance(AppInfo.getWeiboKey(), AppInfo.getWeiboUri());
			weibo.authorize(this, new AuthDialogListener(
					SinaWeiboActivity.this, sina_et.getText().toString()));
			// }
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mLogger.d("----------sina--------");
			SinaWeiboActivity.this.finish();
			return false;
		}
		return true;
	}
}
