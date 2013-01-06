package cn.edaijia.android.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.R;

public class GuideView extends BaseActivity implements OnTouchListener, OnGestureListener{
	
	private ViewFlipper vfGuide;
	
	private GestureDetector mGestureDetector;
	
	private final int[] pics = {R.drawable.design_intro1_1, R.drawable.design_intro2_1, R.drawable.design_intro3_1};
	
	//����ֵ 
    private int flag = 0;
	
    private ImageView[] dots;
    
	//��¼��ǰѡ��λ��  
    private int currentIndex;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guideview);
		vfGuide = (ViewFlipper) findViewById(R.id.flipper);
		vfGuide.setOnTouchListener(this);
		//������,ʶ���϶������� 
		vfGuide.setLongClickable(true);
		//ע������ʶ����  
        mGestureDetector = new GestureDetector(this);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        for(int i=0;i<pics.length;i++){
        	ImageView iv = new ImageView(this);
        	iv.setLayoutParams(mParams);
        	iv.setImageResource(pics[i]);
        	vfGuide.addView(iv);
        }
        initDots();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(e1.getX() - e2.getX() > AppInfo.FLING_MIN_DISTANCE && Math.abs(velocityX) > AppInfo.FLING_MIN_VELOCITY) { 
			 // ������໬����ʱ��
			 //����View������Ļʱ��ʹ�õĶ���
			 vfGuide.setInAnimation(inFromRightAnim());
			 //����View�˳���Ļʱ��ʹ�õĶ���
			 vfGuide.setOutAnimation(outToLeftAnim());
			 //�ж��Ƿ�Ϊ���һ��
			 if (flag % pics.length != pics.length - 1) {
				 vfGuide.showNext();
				 flag = (flag + 1) % pics.length;
				 setCurDot(flag);
			 } else {
				 startActivity(new Intent(this,HomeActivity.class));
				 //activity�л�Ч��
				 overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				 finish();
			 }
			 
		 } else if (e2.getX() - e1.getX() > AppInfo.FLING_MIN_DISTANCE && Math.abs(velocityX) > AppInfo.FLING_MIN_VELOCITY) {
			 // �����Ҳ໬����ʱ��
			 vfGuide.setInAnimation(inFromLeftAnim());
			 vfGuide.setOutAnimation(outToRightAnim());
			 //�ж��Ƿ�Ϊ��һ��
			 if (flag % pics.length == 0) {
				 return false;
			 } else {
				 vfGuide.showPrevious();
				 flag = (flag - 1) % pics.length;
				 setCurDot(flag);
			 }
			 
		 }
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		return mGestureDetector.onTouchEvent(event);
	}
	
	/***
	 * �Ҳ���붯��
	 * @return
	 */
	private Animation inFromRightAnim(){
		 Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.0f
				 										, Animation.RELATIVE_TO_PARENT,0.0f
				 										, Animation.RELATIVE_TO_PARENT,0.0f
				 										, Animation.RELATIVE_TO_PARENT,0.0f);
		 inFromRight.setDuration(350);
		 inFromRight.setInterpolator(new AccelerateInterpolator());
		 return inFromRight;
	}
	
	/***
	 * ����˳�����
	 */
	private Animation outToLeftAnim(){
		Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f
													, Animation.RELATIVE_TO_PARENT,-1.0f
													, Animation.RELATIVE_TO_PARENT,0.0f
													, Animation.RELATIVE_TO_PARENT,0.0f);
		 outtoLeft.setDuration(350);
		 outtoLeft.setInterpolator(new AccelerateInterpolator());
		 return outtoLeft;
	}
	
	/***
	 * �����붯��
	 */
	private Animation inFromLeftAnim(){
		Animation inFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f
													, Animation.RELATIVE_TO_PARENT,0.0f
													, Animation.RELATIVE_TO_PARENT,0.0f
													, Animation.RELATIVE_TO_PARENT,0.0f); 
			inFromLeft.setDuration(350);
			inFromLeft.setInterpolator(new AccelerateInterpolator()); 
			return inFromLeft;
	}
	
	/** 
	 * �Ҳ��˳����� 
	 * @return 
	 */ 
	private Animation outToRightAnim() { 

		Animation outtoRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0.0f
													, Animation.RELATIVE_TO_PARENT, +1.0f
													, Animation.RELATIVE_TO_PARENT,0.0f 
													, Animation.RELATIVE_TO_PARENT,0.0f);
	       outtoRight.setDuration(350);
	       outtoRight.setInterpolator(new AccelerateInterpolator()); 
	        return outtoRight; 
	    }
	
	/***
	 * ��ʼ���ײ�С��
	 */
	private void initDots(){
		LinearLayout ll = (LinearLayout) findViewById(R.id.dots_parent);
		dots = new ImageView[pics.length];
		for (int i=0;i<pics.length;i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(false);//����Ϊ��ɫ
			dots[i].setTag(i);//����λ��tag������ȡ���뵱ǰλ�ö�Ӧ
		}
		currentIndex = 0;
		dots[currentIndex].setEnabled(true);//����Ϊ��ɫ����ѡ��״̬
	}
	
	/***
	 * ���õײ�С��״̬
	 * @param positon
	 */
	private void setCurDot(int positon){  
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {  
            return;  
        }  
  
        dots[positon].setEnabled(true);  
        dots[currentIndex].setEnabled(false);  
  
        currentIndex = positon;  
    }  
}
