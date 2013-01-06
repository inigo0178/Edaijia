package cn.edaijia.android.client.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.db.DBDaoImpl;
import cn.edaijia.android.client.maps.DriverRecord;
import cn.edaijia.android.client.util.AsyncImageLoader;
import cn.edaijia.android.client.util.AsyncImageLoader.ImageCallback;
import cn.edaijia.android.client.util.Logger;
import cn.edaijia.android.client.util.TimeUtil;
import cn.edaijia.android.client.util.Utils;

public class MyDriverListAdapter extends BaseAdapter {
	/** 我的代驾 **/
	private LayoutInflater filter;
	private ArrayList<DriverRecord> driverRecords;
	private AsyncImageLoader asyncImageLoder;
	private ListView listView;
	private Context context;
	private Logger mLogger = Logger.createLogger("MyDriverListAdapter");

	public MyDriverListAdapter(Context context,
			ArrayList<DriverRecord> driverRecords, ListView listView) {

		this.context = context;

		filter = LayoutInflater.from(context);
		this.driverRecords = driverRecords;
		this.listView = listView;
		asyncImageLoder = new AsyncImageLoader();
	}

	@Override
	public int getCount() {

		return driverRecords.size();
	}

	@Override
	public Object getItem(int arg0) {

		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHondler mHondler;
		if (convertView == null) {
			convertView = filter.inflate(R.layout.common_my_driver, null);
			mHondler = new ViewHondler();
			convertView.setTag(mHondler);
		} else {
			mHondler = (ViewHondler) convertView.getTag();
		}

		final DriverRecord info = driverRecords.get(position);
		// driver_id = info.getDriver_id();

		mHondler.nearby_image = (ImageView) convertView
				.findViewById(R.id.my_image);
		mHondler.nearby_name = (TextView) convertView
				.findViewById(R.id.my_name);
		mHondler.nearby_star = (RatingBar) convertView
				.findViewById(R.id.my_star);
		mHondler.nearby_state = (TextView) convertView
				.findViewById(R.id.my_state);
		mHondler.image_available = (Button) convertView
				.findViewById(R.id.my_image_available);
		mHondler.time = (TextView) convertView.findViewById(R.id.my_time);

		mHondler.nearby_name.setText(info.getName());
		mHondler.nearby_star.setRating(Float.parseFloat(info.getLevel()));
		mHondler.time.setText(TimeUtil.getMyCallPhone(info.getCall_time()));

		String str_state = info.getState();
		Resources resource = ((Activity) context).getBaseContext()
				.getResources();
		if ("0".equals(str_state)) {
			str_state = context.getString(R.string.free);
			mHondler.nearby_state.setText(str_state);
			ColorStateList csl = resource.getColorStateList(R.color.green);
			mHondler.nearby_state.setTextColor(csl);

		} else if ("1".equals(str_state)) {
			str_state = context.getString(R.string.working);
			mHondler.nearby_state.setText(str_state);
			ColorStateList csl = resource.getColorStateList(R.color.yellow);
			mHondler.nearby_state.setTextColor(csl);
		}

		// ===
		final String url = info.getPic_small();
		mHondler.nearby_image.setTag(url);
		// 如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行
		Drawable cacheImage = asyncImageLoder.loadDrawable(url,
				new ImageCallback() {

					@Override
					public void imageLoded(Drawable drawable, String imageUrl) {
						ImageView imageViewByTag = (ImageView) listView
								.findViewWithTag(imageUrl);
						if (imageViewByTag != null) {
							imageViewByTag.setImageDrawable(drawable);
						}

					}
				});
		if (cacheImage == null) {
			// 默认图标
			mHondler.nearby_image.setImageResource(R.drawable.default_driver);
		} else {

			mHondler.nearby_image.setImageDrawable(cacheImage);
		}
		// 监听
		mHondler.image_available.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phone = info.getPhone();
				mLogger.d("mylist--phone：" + phone);
				Utils.callPhone(context, phone);
				// 录入我的代驾
				DBDaoImpl.getInstance().saveMyDrivers(phone, info.toString());
			}
		});
		return convertView;
	}

	private static class ViewHondler {
		ImageView nearby_image;// 头像
		TextView nearby_name;// 姓名
		RatingBar nearby_star;// 等级
		TextView nearby_state;// 状态
		Button image_available;// 拨电话
		TextView time;// 时间
	}
}
