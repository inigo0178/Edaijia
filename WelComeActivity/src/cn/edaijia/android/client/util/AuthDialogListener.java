package cn.edaijia.android.client.util;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.R;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

public class AuthDialogListener implements WeiboAuthListener {
    /** Î¢²©ÊÚÈ¨ ¼àÌý **/
    private Context context;
    private String mWeiboContent;
    private Logger mLogger = Logger.createLogger("AuthDialogListener");

    public AuthDialogListener() {
    }

    public AuthDialogListener(Context context, String wb) {
        this.context = context;
        this.mWeiboContent = wb;
    }

    @Override
    public void onComplete(Bundle values) {

        String token = values.getString("access_token");
        String expires_in = values.getString("expires_in");
        mLogger.d(token + "  expires_in: " + expires_in);
        Oauth2AccessToken accessToken = new Oauth2AccessToken(token, expires_in);

        if (accessToken.isSessionValid()) {
            StatusesAPI api = new StatusesAPI(accessToken);
            api.upload(mWeiboContent, Utils.getWeiboSharedPicture(), null, null,
                    new RequestListener() {
                        @Override
                        public void onIOException(final IOException e) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissSendingDialog();
                                    AppInfo.showToast(context, context.getString(R.string.weibo_submit_fail));
                                }
                            });
                        }

                        @Override
                        public void onError(final WeiboException e) {

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissSendingDialog();
                                    if (e != null && e.getMessage() != null && e.getMessage().contains("repeat content")) {
                                        AppInfo.showToast(context, context.getString(R.string.weibo_submit_fail_same));
                                    } else {
                                        AppInfo.showToast(context, context.getString(R.string.weibo_submit_fail));
                                    }
                                }
                            });
                        }

                        // RequestListenre#
                        @Override
                        public void onComplete(String response) {

                            ((Activity) context).runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    dismissSendingDialog();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(
                                            context);
                                    builder.setTitle(context
                                            .getString(R.string.weibo_tips));
                                    builder.setMessage(context
                                            .getString(R.string.weibo_submit_success));
                                    builder.setPositiveButton(context
                                            .getString(R.string.commom_affirm),
                                            new OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    ((Activity) context)
                                                            .finish();
                                                }
                                            });
                                    builder.setCancelable(false);
                                    builder.show();
                                }
                            });
                        }
                    });
            showSendingDialog();
        }
    }

    @Override
    public void onWeiboException(final WeiboException e) {
        AppInfo.showToast(
                context,
                context.getString(R.string.weibo_authorization)
                        + e.getMessage());
    }

    @Override
    public void onCancel() {
        AppInfo.showToast(context, context.getString(R.string.weibo_auth_ancle));
    }

    @Override
    public void onError(final WeiboDialogError e) {
        AppInfo.showToast(
                context,
                context.getString(R.string.weibo_authorization)
                        + e.getMessage());
    }

    private ProgressDialog mWaitDialog;

    private void showSendingDialog() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWaitDialog = new ProgressDialog(context);
                mWaitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mWaitDialog.setMessage(context.getString(R.string.sending));
                mWaitDialog.setCancelable(false);
                mWaitDialog.show();
            }
        });
    }

    private void dismissSendingDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
        }
    }
}
