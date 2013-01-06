package cn.edaijia.android.client.activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.EDriverApp;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.adapter.ReviewListAdapter;
import cn.edaijia.android.client.db.DBDaoImpl;
import cn.edaijia.android.client.maps.DriverRecord;
import cn.edaijia.android.client.model.DriverInfo;
import cn.edaijia.android.client.model.ReviewInfo;
import cn.edaijia.android.client.util.AsyncImageLoader;
import cn.edaijia.android.client.util.AsyncImageLoader.ImageCallback;
import cn.edaijia.android.client.util.MyListView;
import cn.edaijia.android.client.util.MyListView.OnRefreshListener;
import cn.edaijia.android.client.util.UtilListData;
import cn.edaijia.android.client.util.Utils;

public class DriverDetails extends BaseActivity implements OnClickListener {
    /**** ˾������ ****/
    private String driver_info = ""; // info

    private String picture = ""; // ͼƬ

    private String large_pic = "";// ��ͼ

    private String name = ""; // ����

    private String level = ""; // �ȼ�

    private String count = ""; // ���ݴ���

    private String year = ""; // ����

    private String domicile = ""; // ����

    private String idCard = ""; // ��ʻ֤

    private String phone = ""; // �绰

    private String state = ""; // ״̬

    private String driver_id = ""; // dirver_id

    private ImageView nearby_image; //

    private TextView nearby_name; //

    private RatingBar nearby_star; //

    private TextView nearby_count; //

    private TextView nearby_year; //

    private TextView nearby_domicile; // ����

    private TextView nearby_idCard; //

    private ImageButton btnCallDriver; //

    private TextView nearby_state;

    private AsyncImageLoader asImageLoader; //

    private int pageNo = 0;

    private final int PAGE_SIZE = 10;

    private ArrayList<ReviewInfo> reviewInfo = new ArrayList<ReviewInfo>();// �����б�

    private ReviewListAdapter rlAdapter;

    private MyListView driver_listView;

    private LinearLayout linear_review, linear_progress;// ��������

    private DriverInfo driverinfo;

    private Button comm_back;

    private Bitmap bmp;

    private int displayWidth, displayHeight;

    private float scaleWidth = 1, scaleHeight = 1;

    private Bitmap resizeBmp;

    private ImageView large_image;

    private AlertDialog dialog;// dialog��ʾͼƬ

    private View view;// dialog

    private final int PHONE_DIALOG = 0X1;

    private final static int MSG_UPDATE_DRIVER_STATE = 0x1;
    private final static int MSG_NO_COMMENTS = 0x2;
    private final static int MSG_UPDATE_DRIVER_COMMENTS = 0x3;
    private final static int MSG_NO_MORE_COMMENTS = 0x4;
    final Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case MSG_UPDATE_DRIVER_STATE:
                state = driverinfo.getState();
                // ״̬
                updateState();
                break;
            case MSG_NO_COMMENTS:
                linear_progress.setVisibility(View.GONE);
                linear_review.setVisibility(View.VISIBLE);
                break;
            case MSG_UPDATE_DRIVER_COMMENTS:
                linear_progress.setVisibility(View.GONE);
                rlAdapter.notifyDataSetChanged();
                driver_listView.onRefreshComplete();
                break;
            case MSG_NO_MORE_COMMENTS:
                linear_progress.setVisibility(View.GONE);
                driver_listView.onRefreshComplete();
            default:
                break;
            }
        };
    };

    // private Logger mLogger = Logger.createLogger("DriverDetails");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.driverdetails);
        
        asImageLoader = new AsyncImageLoader();
        Intent intent = this.getIntent();
        if (intent != null){
            DriverRecord driver = intent.getParcelableExtra("info");
            if (driver != null) {
                driver_info = driver.toString();
            }
        }
        initView();
        initDriverComments();

        initDriverDetails();

        // ȡ����Ļ�ֱ���
        displayWidth = EDriverApp.getWidth();
        displayHeight = EDriverApp.getHeight() - 120;

    }

    /*** UI��ʼ�� ***/
    public void initView() {
        nearby_count = (TextView) this.findViewById(R.id.ny_count);
        nearby_domicile = (TextView) this.findViewById(R.id.ny_domicile);
        nearby_idCard = (TextView) this.findViewById(R.id.ny_idCard);
        nearby_image = (ImageView) this.findViewById(R.id.nearby_image);
        nearby_name = (TextView) this.findViewById(R.id.nearby_name);
        nearby_star = (RatingBar) this.findViewById(R.id.nearby_star);
        nearby_year = (TextView) this.findViewById(R.id.nearby_year);
        btnCallDriver = (ImageButton) this.findViewById(R.id.image_available);
        linear_review = (LinearLayout) this.findViewById(R.id.linear_review);
        linear_progress = (LinearLayout) this
                .findViewById(R.id.linear_progress);
        linear_progress.setVisibility(View.VISIBLE);
        driver_listView = (MyListView) this.findViewById(R.id.driver_listView);
        comm_back = (Button) this.findViewById(R.id.comm_back);
        comm_back.setVisibility(View.GONE);
        nearby_state = (TextView) this.findViewById(R.id.nearby_state);

        // ==ͷ��
        LayoutInflater inflater = LayoutInflater.from(DriverDetails.this);
        view = inflater.inflate(R.layout.dialog_photo_entry, null);
        dialog = new AlertDialog.Builder(DriverDetails.this).create();
        large_image = (ImageView) view.findViewById(R.id.large_image);

        // ==�绰

        // ����
        btnCallDriver.setOnClickListener(this);
        comm_back.setOnClickListener(this);
        nearby_image.setOnClickListener(this);
    }

    /*** ���ݳ�ʼ�� ****/
    private void initDriverDetails() {
        if (driver_info != null) {
            try {
                JSONObject obj = new JSONObject(driver_info);
                picture = obj.getString("picture_small");
                large_pic = obj.getString("picture_large");
                name = obj.getString("name");
                level = obj.getString("level");
                count = obj.getString("order_count");
                year = obj.getString("year");
                idCard = obj.getString("idCard");
                domicile = obj.getString("domicile");
                phone = obj.getString("phone");
                state = obj.getString("state");
                driver_id = obj.getString("driver_id");

                nearby_name.setText(name);
                nearby_star.setRating(Float.parseFloat(level));
                nearby_count.setText(count + "��");
                nearby_year.setText(year + "��");
                nearby_domicile.setText(domicile);
                nearby_idCard.setText(idCard);

                // ״̬
                updateState();
                // ���е绰
                // ͷ��
                if (picture != null) {
                    Drawable imageDrawable = asImageLoader.loadDrawable(
                            picture, new ImageCallback() {

                                @Override
                                public void imageLoded(Drawable drawable,
                                        String imageUrl) {
                                    if (drawable != null) {
                                        nearby_image.setImageDrawable(drawable);
                                    }
                                }
                            });
                    if (imageDrawable != null) {
                        nearby_image.setImageDrawable(imageDrawable);
                    }
                }
                // ��ͼ
                if (large_pic != null) {
                    Drawable large_imageDrawable = asImageLoader.loadDrawable(
                            large_pic, new ImageCallback() {

                                @Override
                                public void imageLoded(Drawable drawable,
                                        String imageUrl) {
                                    if (drawable != null) {

                                        BitmapDrawable bd = (BitmapDrawable) drawable;
                                        bmp = bd.getBitmap();
                                    }
                                }
                            });
                    if (large_imageDrawable != null) {

                    }
                }
                // ===ͨ��driver_id ��������˾��״̬
                if (driver_id != null) {
                    initDriverInfo(driver_id);
                }
                // ����
                requestComments(driver_id);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void updateState() {
        Resources resource = getResources();
        if ("0".equals(state)) {
            state = this.getString(R.string.free);
            nearby_state.setText(state);
            ColorStateList csl = resource.getColorStateList(R.color.green);
            nearby_state.setTextColor(csl);
            btnCallDriver.setBackgroundResource(R.drawable.btn_call_drivers);
            btnCallDriver.setEnabled(true);
        } else if ("1".equals(state)) {
            state = this.getString(R.string.working);
            nearby_state.setText(state);
            ColorStateList csl = resource.getColorStateList(R.color.yellow);
            nearby_state.setTextColor(csl);
            btnCallDriver.setBackgroundResource(R.drawable.workings);
            btnCallDriver.setEnabled(false);
        }
    }

    private void initDriverInfo(final String driver_id) {
        new Thread() {
            @Override
            public void run() {
                driverinfo = UtilListData.getInstance().getDataDriverInfo(
                        driver_id);
                if (driverinfo != null) {
                    mHandler.sendEmptyMessage(MSG_UPDATE_DRIVER_STATE);
                }
            }
        }.start();
    }

    /*** ���� ***/
    private void requestComments(final String driver_id) {
        new Thread() {
            @Override
            public void run() {
                ArrayList<ReviewInfo> infos = UtilListData.getInstance()
                        .getDataReviewInfo(driver_id, pageNo, PAGE_SIZE);
                if (infos != null && infos.size() != 0) {
                    if (!reviewInfo.isEmpty()) {
                        reviewInfo.clear();
                    }
                    reviewInfo.addAll(infos);
                    mHandler.sendEmptyMessage(MSG_UPDATE_DRIVER_COMMENTS);
                } else {
                    mHandler.sendEmptyMessage(MSG_NO_COMMENTS);
                }
            }
        }.start();
    }

    private void initDriverComments() {
        rlAdapter = new ReviewListAdapter(this, reviewInfo);
        driver_listView.setAdapter(rlAdapter);

        driver_listView.setonRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, ArrayList<ReviewInfo>>() {
                    protected void onPreExecute() {
                    };

                    @Override
                    protected ArrayList<ReviewInfo> doInBackground(Void... arg0) {
                        ArrayList<ReviewInfo> result = null;
                        int size = reviewInfo.size();
                        if (size != 0 && size % PAGE_SIZE == 0) {
                            pageNo++;
                            result = UtilListData.getInstance()
                                    .getDataReviewInfo(driver_id, pageNo,
                                            PAGE_SIZE);
                        }
                        return result;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<ReviewInfo> result) {
                        if (result != null && result.size() > 0) {
                            reviewInfo.addAll(result);
                            mHandler.sendEmptyMessage(MSG_UPDATE_DRIVER_COMMENTS);
                        } else {
                            mHandler.sendEmptyMessage(MSG_NO_MORE_COMMENTS);
                        }
                    }
                }.execute();
            }
        });
    }

    /*** ���� **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.image_available:
            showDialog(PHONE_DIALOG);
            break;
        case R.id.comm_back:
            finish();
            break;
        case R.id.nearby_image:
            if (bmp != null) {
                showDriverPicture();
            }
            break;
        }
    }

    /*** �Ŵ�ͼƬ ****/
    private void showDriverPicture() {
        // ���Bitmap�ĸߺͿ�
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        // ������С����
        double scale = 1.25;
        // ��������Ҫ��С�ı���
        scaleWidth = (float) (scaleWidth * scale);
        scaleHeight = (float) (scaleHeight * scale);
        // ����resize���Bitmap����
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        if (resizeBmp == null) {
            resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight,
                    matrix, true);
        }

        // ==dialog ��ʾͼƬ

        large_image.setImageBitmap(resizeBmp);
        dialog.setView(view);
        dialog.show();
        if (scaleWidth * scale * bmpWidth > displayWidth
                || scaleHeight * scale * scaleHeight > displayHeight) {

        }
        large_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return false;
        }
        return false;
    }

    /** ��绰�����Ի��� **/
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case PHONE_DIALOG:
            AlertDialog.Builder phone_builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout lin = (LinearLayout) inflater.inflate(
                    R.layout.phone_dailog, null);
            Button bt_driver_phone_dailog = (Button) lin
                    .findViewById(R.id.bt_driver_phone_dailog);
            Button bt_400_phone_dailog = (Button) lin
                    .findViewById(R.id.bt_400_phone_dailog);
            bt_driver_phone_dailog.setText(this
                    .getString(R.string.driver_phone) + phone);
            // ����˾��
            bt_driver_phone_dailog.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismissDialog(PHONE_DIALOG);
                    // ֪ͨactivtity�������call����
                    Utils.callPhone(DriverDetails.this, phone);
                    // ¼�뱾�� �ҵĴ���
                    DBDaoImpl.getInstance().saveMyDrivers(phone, driver_info);
                    // �ϴ�ͨ����¼
                    Utils.saveCallLog(EdApp, driver_id + "#"
                            + AppInfo.PHONE_NUMBER_400);
                }
            });
            // 400
            bt_400_phone_dailog.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Utils.callPhone(DriverDetails.this,
                            AppInfo.PHONE_NUMBER_400);
                    dismissDialog(PHONE_DIALOG);
                    Utils.saveCallLog(EdApp, AppInfo.PHONE_NUMBER_400);
                }
            });
            phone_builder.setView(lin);
            phone_builder.setNegativeButton("ȡ��", null);
            return phone_builder.create();
        }
        return super.onCreateDialog(id);
    }
}
