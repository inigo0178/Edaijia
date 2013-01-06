package cn.edaijia.android.client.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.model.ReviewInfo;

public class ReviewListAdapter extends BaseAdapter {
	/** ÆÀ¼Û adapter **/
	private ArrayList<ReviewInfo> reviewInfos;
	LayoutInflater mInflater;
	ReviewInfo reviewInfo;
	private Context context;

	public ReviewListAdapter() {
	}

	public ReviewListAdapter(Context context, ArrayList<ReviewInfo> reviewInfos) {
		this.reviewInfos = reviewInfos;
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {

		return reviewInfos.size();

	}

	@Override
	public Object getItem(int position) {

		return reviewInfos.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder mHolder = null;

		if (convertView == null) {

			convertView = mInflater.inflate(R.layout.review_listview, null);

			mHolder = new ViewHolder();

			mHolder.review_driver_name = (TextView) convertView
					.findViewById(R.id.review_driver_name);
			mHolder.review_driver_level = (RatingBar) convertView
					.findViewById(R.id.review_driver_level);
			mHolder.review_comments = (TextView) convertView
					.findViewById(R.id.review_connents);
			mHolder.review_kh = (TextView) convertView
					.findViewById(R.id.review_kh);
			mHolder.review_time = (TextView) convertView
					.findViewById(R.id.review_time);
			mHolder.review_driver_tv = (TextView) convertView
					.findViewById(R.id.review_driver_tv);
			convertView.setTag(mHolder);

		} else {
			mHolder = (ViewHolder) convertView.getTag();

		}
		initData(convertView, mHolder, position);
		return convertView;
	}

	static class ViewHolder {
		TextView review_driver_name;
		RatingBar review_driver_level;
		TextView review_comments;
		TextView review_kh;
		TextView review_time;
		TextView review_driver_tv;
	}

	private void initData(View convertView, ViewHolder mHolder, int position) {

		reviewInfo = reviewInfos.get(position);
		mHolder.review_driver_name.setText(reviewInfo.getEmployee_id());
		mHolder.review_comments.setText(reviewInfo.getComments());
		String level = reviewInfo.getLevel();
		if (level != null) {
			float fl = 1f;
			if ("1".equals(level)) {
				fl = 1f;
			} else if ("2".equals(level)) {
				fl = 3f;
			} else if ("3".equals(level)) {
				fl = 5f;
			}
			// Float.parseFloat(reviewInfo.getLevel())
			mHolder.review_driver_level.setRating(fl);
		}
		if (level != null) {
			String str = context.getString(R.string.good_reputation);
			if ("1".equals(str)) {
				str = context.getString(R.string.bad_review);
			} else if ("2".equals(str)) {
				str = context.getString(R.string.medium_review);
			} else if ("3".equals(str)) {
				str = context.getString(R.string.good_reputation);
			}
			mHolder.review_driver_tv.setText(str);
		}
		mHolder.review_kh.setText(reviewInfo.getName());
		mHolder.review_time.setText(reviewInfo.getInsert_time());
	}
}
