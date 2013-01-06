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
	
	//索引值 
    private int flag = 0;
	
    private ImageView[] dots;
    
	//记录当前选中位置  
    private int currentIndex;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guideview);
		vfGuide = (ViewFlipper) findViewById(R.id.flipper);
		vfGuide.setOnTouchListener(this);
		//允许长按,识别拖动等手势 
		vfGuide.setLongClickable(true);
		//注册手势识别类  
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
			 // 当向左侧滑动的时候
			 //设置View进入屏幕时候使用的动画
			 vfGuide.setInAnimation(inFromRightAnim());
			 //设置View退出屏幕时候使用的动画
			 vfGuide.setOutAnimation(outToLeftAnim());
			 //判断是否为最后一张
			 if (flag % pics.length != pics.length - 1) {
				 vfGuide.showNext();
				 flag = (flag + 1) % pics.length;
				 setCurDot(flag);
			 } else {
				 startActivity(new Intent(this,HomeActivity.class));
				 //activity切换效果
				 overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				 finish();
			 }
			 
		 } else if (e2.getX() - e1.getX() > AppInfo.FLING_MIN_DISTANCE && Math.abs(velocityX) > AppInfo.FLING_MIN_VELOCITY) {
			 // 当像右侧滑动的时候
			 vfGuide.setInAnimation(inFromLeftAnim());
			 vfGuide.setOutAnimation(outToRightAnim());
			 //判断是否为第一张
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
	 * 右侧进入动画
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
	 * 左侧退出动画
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
	 * 左侧进入动画
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
	 * 右侧退出动画 
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
	 * 初始化底部小点
	 */
	private void initDots(){
		LinearLayout ll = (LinearLayout) findViewById(R.id.dots_parent);
		dots = new ImageView[pics.length];
		for (int i=0;i<pics.length;i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(false);//都设为灰色
			dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应
		}
		currentIndex = 0;
		dots[currentIndex].setEnabled(true);//设置为黑色，即选中状态
	}
	
	/***
	 * 设置底部小点状态
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
