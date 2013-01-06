package cn.edaijia.android.client.activity;

import android.os.Bundle;
import android.widget.TextView;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.util.Utils;

public class InsureActivity extends BaseActivity {
	/** ������  **/
	
	private TextView insure_tv;//�������ı�
	
	private final String INSURE_NAME = "insure_edj.txt";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.insure);
		//ui��ʼ��
		insure_tv = (TextView) this.findViewById(R.id.insure_tv);
		//data��ʼ��
		String text = Utils.readAssetsFile(this, INSURE_NAME);
        if (text != null) {
            insure_tv.setText(text.replace("\\n", "\n"));
        }
		
	}
}
