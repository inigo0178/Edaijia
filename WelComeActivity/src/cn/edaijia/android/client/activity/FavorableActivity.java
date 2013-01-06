package cn.edaijia.android.client.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.db.DBDaoImpl;
import cn.edaijia.android.client.model.AppContentInfo;
import cn.edaijia.android.client.model.FavorableInfo;
import cn.edaijia.android.client.util.UtilListData;

public class FavorableActivity extends BaseActivity implements OnClickListener{
	/** �Ż�ȯ **/
	
	private AppContentInfo info;
	
	private EditText cash_num;//�ֽ���
	
	private EditText phone_num;//�绰����
	
	private String str_cash  = null,str_phone = null;
	
	private static final int EDIT_NULL = 1;
	
	private static final int PHONE_ERROR = 2;
	
	private static final int SUBMIT_SCESS = 3;
	
	private static final int SUBMIT_WRONG = 4;
	
	private FavorableInfo fInfo;
	
	private String message;
	
	private AlertDialog.Builder builder_wrong; //��ֵʧ��
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.favorabl);
		info  =  DBDaoImpl.getInstance().getDbApp();

		initView();
		
	}
	/** ui��ʼ��**/
	public void initView(){
		Button comm_back = (Button) this.findViewById(R.id.comm_back);
		comm_back.setVisibility(View.GONE);
		TextView title_tv = (TextView) this.findViewById(R.id.title_tv);
		Button comm_submit = (Button) this.findViewById(R.id.comm_submit);
		TextView favorable_tv = (TextView) this.findViewById(R.id.favorable_tv);
		cash_num  = (EditText) this.findViewById(R.id.cash_num);
		phone_num = (EditText) this.findViewById(R.id.phone_num);
		title_tv.setText("�Ż݄�");
		comm_submit.setText("���");
		if(info != null && info.getRechargeText() != null) {
		    favorable_tv.setText(info.getRechargeText().replace("\\n", "\n"));
		}
		comm_submit.setVisibility(View.VISIBLE);
		//
		comm_back.setOnClickListener(this);
		comm_submit.setOnClickListener(this);
		//��������
		cash_num.setFocusable(true);
		cash_num.requestFocus();
		InputMethodManager imm  = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(cash_num, 0);
	}
	/** ���� **/
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.comm_back:
			finish();
			break;
		case R.id.comm_submit:
			submitData();
			break;
		}
	}
	/** �ύ���� **/
	private void submitData(){
		str_cash = cash_num.getText().toString();
		str_phone = phone_num.getText().toString();
		if(str_cash == null || str_cash.length() ==0 || str_phone == null || str_phone.length() == 0){
			//�Ż�ȯ������ֻ��Ų���Ϊ��			
			showDialog(EDIT_NULL);
		}else{
			if(str_phone.length() != 11){
				showDialog(PHONE_ERROR);
			}else{
				submintCash();
			}
		}
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		
		switch(id){
		case EDIT_NULL:
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("��ʾ");
			builder.setMessage("�Ż�ȯ������ֻ��Ų���Ϊ��");
			builder.setPositiveButton("ȷ��", null);
			return builder.create();
		case PHONE_ERROR:
			AlertDialog.Builder builder_phone = new Builder(this);
			builder_phone.setTitle("��ʾ");
			builder_phone.setMessage("ֻ֧�� 11 λ�ֻ���");
			builder_phone.setPositiveButton("ȷ��", null);
			return builder_phone.create();
		case SUBMIT_SCESS:
			AlertDialog.Builder builder_secss = new Builder(this);
			builder_secss.setTitle("��ֵ�ɹ�");
			builder_secss.setMessage("�ѳɹ�Ϊ�ֻ�����"+str_phone+"���û���ֵ"+fInfo.getRecharge()+" Ԫ,��ע����ն���");
			builder_secss.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					FavorableActivity.this.finish();
				}
			});
			return builder_secss.create();
		case SUBMIT_WRONG:
			
			builder_wrong = new Builder(this);
			builder_wrong.setTitle("��ֵʧ��");
			builder_wrong.setMessage(message);
			builder_wrong.setPositiveButton("ȷ��", null);			
			return builder_wrong.create();
		}
		return super.onCreateDialog(id);
	}
	
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		
		super.onPrepareDialog(id, dialog);
		if(id ==  SUBMIT_WRONG ){
			message = fInfo.getMessage();
			AlertDialog msgDialog = (AlertDialog)dialog;
			msgDialog.setMessage(message);
//			mLogger.d("favorable-->"+fInfo.getMessage());			
		}
	}
	/** �ֽ��ֵ **/
	private void submintCash(){
		new Thread(){
			@Override
			public void run() {
				fInfo = UtilListData.getInstance().getDataCash(String.valueOf(0), String.valueOf(0), str_phone, str_cash, String.valueOf(2));
				if(fInfo != null){
				    message = fInfo.getMessage();
					if("0".equals(fInfo.getCode())){
						mHandler.sendEmptyMessage(SUBMIT_SCESS);
					}else if("-1".equals(fInfo.getCode())){
						mHandler.sendEmptyMessage(SUBMIT_WRONG);
					}
				}
			}
		}.start();
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			switch(msg.what){
			case SUBMIT_SCESS:
				showDialog(SUBMIT_SCESS);
				break;
			case SUBMIT_WRONG:
				showDialog(SUBMIT_WRONG);
				break;
			}
		}
		
	};
}
