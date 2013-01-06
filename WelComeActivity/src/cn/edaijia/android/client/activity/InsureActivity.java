package cn.edaijia.android.client.activity;

import android.os.Bundle;
import android.widget.TextView;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.util.Utils;

public class InsureActivity extends BaseActivity {
	/** 代驾险  **/
	
	private TextView insure_tv;//代驾险文本
	
	private final String INSURE_NAME = "insure_edj.txt";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.insure);
		//ui初始化
		insure_tv = (TextView) this.findViewById(R.id.insure_tv);
		//data初始化
		String text = Utils.readAssetsFile(this, INSURE_NAME);
        if (text != null) {
            insure_tv.setText(text.replace("\\n", "\n"));
        }
		
	}
}
