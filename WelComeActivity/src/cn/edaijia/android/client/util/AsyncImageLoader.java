package cn.edaijia.android.client.util;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader {
	/*Ϊ�˸�����ʹ�����ǿ��Խ��첽����ͼ�񷽷���װһ����
	 *�����ֻ��¶һ���������ɣ�
	 *���ǵ�Ч���������ǿ��������ڴ滺�����
	 *������ ����һ��HashMap�������key��Ϊ����ͼ��url����ֵ��value����ͼ�����Drawable���ȿ�һ�����Ƿ�װ����
	 */
	private Map<String,SoftReference<Drawable>> imageCache ;
	public AsyncImageLoader() {
		imageCache = new HashMap<String,SoftReference<Drawable>>();
	}
	//Ϊ�˼ӿ��ٶȣ����ڴ��п������棨��ҪӦ�����ظ�ͼƬ�϶�ʱ������ͬһ��ͼƬҪ��α����ʣ�������ListViewʱ���ع�����
	/**
    *
    * @param imageUrl     ͼ��url��ַ
    * @param callback     �ص��ӿ�
    * @return     �����ڴ��л����ͼ�񣬵�һ�μ��ط���null
    */
	public Drawable loadDrawable(final String imageUrl, final ImageCallback callback){
		//���������ʹӻ�����ȡ������
		if(imageCache.containsKey(imageUrl)){
			SoftReference<Drawable> softReference = imageCache.get(imageUrl) ;
			Drawable drawable = softReference.get() ;
			if(drawable != null){
				
				return drawable;
			}
		}
		// �����ص�ͼƬ������������
		final Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);				
				callback.imageLoded((Drawable)msg.obj, imageUrl);
				
			}
			
		};
		// �����߳�����ͼƬ 
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
	//����翪�ŵĻص��ӿ�
	public  interface ImageCallback{
		//ע�� �˷�������������Ŀ������ͼ����Դ
		public void imageLoded(Drawable drawable, String imageUrl);
	}
}
