package cn.edaijia.android.client.util;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader {
	/*为了更方便使用我们可以将异步加载图像方法封装一个类
	 *对外界只暴露一个方法即可，
	 *考虑到效率问题我们可以引入内存缓存机制
	 *做法是 建立一个HashMap，其键（key）为加载图像url，其值（value）是图像对象Drawable。先看一下我们封装的类
	 */
	private Map<String,SoftReference<Drawable>> imageCache ;
	public AsyncImageLoader() {
		imageCache = new HashMap<String,SoftReference<Drawable>>();
	}
	//为了加快速度，在内存中开启缓存（主要应用于重复图片较多时，或者同一个图片要多次被访问，比如在ListView时来回滚动）
	/**
    *
    * @param imageUrl     图像url地址
    * @param callback     回调接口
    * @return     返回内存中缓存的图像，第一次加载返回null
    */
	public Drawable loadDrawable(final String imageUrl, final ImageCallback callback){
		//如果缓存过就从缓存中取出数据
		if(imageCache.containsKey(imageUrl)){
			SoftReference<Drawable> softReference = imageCache.get(imageUrl) ;
			Drawable drawable = softReference.get() ;
			if(drawable != null){
				
				return drawable;
			}
		}
		// 将下载的图片保存至缓存中
		final Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);				
				callback.imageLoded((Drawable)msg.obj, imageUrl);
				
			}
			
		};
		// 开启线程下载图片 
		new Thread(){

			@Override
			public void run() {
				
				super.run();
				try {
					Drawable drawable = loadImageFromUrl(imageUrl); 				
					imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
					 Message message  =  handler.obtainMessage( 0 , drawable);
                     handler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}.start();
		return null;
	}
	
	protected Drawable loadImageFromUrl(String imageUrl) {
		
		try {
			Drawable draw = Drawable.createFromStream(new URL(imageUrl).openStream(), "src");
			if(draw != null){
				return draw;
			}
			return null;
		}catch (Exception e) {			
			
			return null;
		}
	}
	//对外界开放的回调接口
	public  interface ImageCallback{
		//注意 此方法是用来设置目标对象的图像资源
		public void imageLoded(Drawable drawable, String imageUrl);
	}
}
