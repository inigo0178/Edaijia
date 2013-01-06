package cn.edaijia.android.client.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.maps.DriverRecord;
import cn.edaijia.android.client.util.AsyncImageLoader;
import cn.edaijia.android.client.util.AsyncImageLoader.ImageCallback;

public class NearbyListAdapter extends BaseAdapter {

	LayoutInflater mInflater;
	ArrayList<DriverRecord> mDriverList;
	private AsyncImageLoader asyncImageLoder;
	private ListView listView;
	private Context context;

	public NearbyListAdapter() {
	}

	public NearbyListAdapter(Context context,
			ArrayList<DriverRecord> driverList, ListView listView) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.mDriverList = driverList;
		this.listView = listView;
		asyncImageLoder = new AsyncImageLoader();

		// mLogger.d("------------"+driverList.size());
	}

	@Override
	public int getCount() {

		if (mDriverList != null && mDriverList.size() != 0) {
			return mDriverList.size();
		} else {
			return 0;
		}

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
			convertView = mInflater.inflate(R.layout.common_driver, null);
			mHondler = new ViewHondler();
			convertView.setTag(mHondler);
		} else {
			mHondler = (ViewHondler) convertView.getTag();
		}
		DriverRecord info = mDriverList.get(position);
		// mLogger.d(position+"");
		mHondler.nearby_count = (TextView) convertView
				.findViewById(R.id.ney_count);
		mHondler.nearby_distance = (TextView) convertView
				.findViewById(R.id.ny_distance);
		mHondler.nearby_domicile = (TextView) convertView
				.findViewById(R.id.ney_domicile);
		mHondler.nearby_image = (ImageView) convertView
				.findViewById(R.id.nearby_image);
		mHondler.nearby_name = (TextView) convertView
				.findViewById(R.id.nearby_name);
		mHondler.nearby_star = (RatingBar) convertView.findViewById(R.id.star);
		mHondler.nearby_state = (TextView) convertView
				.findViewById(R.id.nearby_state);
		mHondler.nearby_year = (TextView) convertView
				.findViewById(R.id.ny_year);
		mHondler.nearby_count.setText(info.getOrder_count() + "次");
		mHondler.nearby_distance.setText(info.getDistance());
		mHondler.nearby_domicile.setText(info.getDomicile());
		mHondler.nearby_name.setText(info.getName());
		mHondler.nearby_star.setRating(Float.parseFloat(info.getLevel()));
		mHondler.nearby_year.setText(info.getYear() + "年");

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

		// ====
		info = null;
		return convertView;
	}

	private static class ViewHondler {
		ImageView nearby_image;// 头像
		TextView nearby_name;// 姓名
		RatingBar nearby_star;// 等级
		TextView nearby_year;// 驾龄
		TextView nearby_count;// 代驾次数
		TextView nearby_state;// 状态
		TextView nearby_domicile;// 籍贯
		TextView nearby_distance;// 距离
	}

}
