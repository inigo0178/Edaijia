package cn.edaijia.android.client.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.EDriverApp;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.activity.HomeActivity;
import cn.edaijia.android.client.db.DBDaoImpl;
import cn.edaijia.android.client.util.Logger;
import cn.edaijia.android.client.util.UtilListData;
import cn.edaijia.android.client.util.Utils;

public class EDService extends IntentService {

    public static final String ACTION_UPDATE = "cn.edaijia.android.ACTION_CHECK_UPDATE";
    public static final String ACTION_UPLOAD_CALL_LOG = "cn.edaijia.android.ACTION_UPLOAD_CALL_LOG";
    public static final String ACTION_UPDATE_MACADDRESS = "cn.edaijia.android.ACTION_UPDATE_MAC_ADDRESS";

    private NotificationManager notifiManager;

    private Notification notification;

    private File tempFile = null;

    private boolean cancelUpdate = false;

    private int download_precent = 0;

    private RemoteViews views;

    private int notificationId = 12345;

    private String url;

    private final int MSG_URL_ERROR = 0x5;

    private Logger mLogger = Logger.createLogger("EDService");
    
    static final int toast = 0, nothing = 1, suc = 2, refresh = 3, fail = 4;
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                case toast:
                    Toast.makeText(EDService.this, msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case nothing:
                    break;
                case suc:
                    // ������ɺ��������������Ϣ��ִ�а�װ��ʾ
                    download_precent = 0;
                    notifiManager.cancel(notificationId);
                    Instanll((File) msg.obj, EDService.this);

                    // ֹͣ����ǰ�ķ���
                    stopSelf();
                    break;
                case refresh:
                    // ����״̬���ϵ����ؽ�����Ϣ
                    views.setTextViewText(R.update_id.tvProcess, "������"
                            + download_precent + "%");
                    views.setProgressBar(R.update_id.pbDownload, 100,
                            download_precent, false);
                    notification.contentView = views;
                    notifiManager.notify(notificationId, notification);
                    break;
                case fail:
                    notifiManager.cancel(notificationId);
                    break;
                case MSG_URL_ERROR:
                    // ShowMessage.showToast(getApplicationContext(),
                    // "���ظ����ļ�ʧ��,�����ٷ���վ�������°汾");
                    break;
                default:
                    break;
                }
            }
        }

    };

    public EDService() {
        super("UpdataService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notifiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = android.R.drawable.stat_sys_download;
        // notification.icon=android.R.drawable.stat_sys_download_done;
        notification.tickerText = getString(R.string.app_name) + "����";
        notification.when = System.currentTimeMillis();
        notification.defaults = Notification.DEFAULT_LIGHTS;
        // ���������������ؽ�����ʾ��views
        views = new RemoteViews(getPackageName(), R.layout.download);
        notification.contentView = views;

        // ���õ����ת��activity
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(this, "", "", contentIntent);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /** �����ļ� 
     * @throws Exception **/
    private void downFile(final String url) throws Exception {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            long length = entity.getContentLength();
            InputStream is = entity.getContent();
            if (is != null) {
                File rootFile = new File(
                        Environment.getExternalStorageDirectory(), "/" + Utils.BASE_FOLDER_NAME);
                if (!rootFile.exists() && !rootFile.isDirectory()) {
                    rootFile.mkdirs();
                }
                tempFile = new File(rootFile.getPath() + url.substring(url.lastIndexOf("/") + 1));
                if (tempFile.exists()) {
                    tempFile.delete();
                    tempFile.createNewFile();
                }
                // ����������Ϊ��������һ�����л����������
                BufferedInputStream bis = new BufferedInputStream(is);
                // ����һ���µ������������ȡ����ͼ������д�뵽�ļ���
                FileOutputStream fos = new FileOutputStream(tempFile);
                // ��д������Ϊ��������һ�����л����д����
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                int read;
                long count = 0;
                int precent = 0;
                byte[] buffer = new byte[1024];
                while ((read = bis.read(buffer)) != -1 && !cancelUpdate) {
                    bos.write(buffer, 0, read);
                    count += read;
                    precent = (int) ((1.0f * count / length) * 100);

                    // ÿ�������5%��֪ͨ�����������޸����ؽ���
                    if (precent - download_precent >= 5) {
                        download_precent = precent;
                        Message message = mHandler.obtainMessage(refresh, precent);
                        mHandler.sendMessage(message);
                    }
                }
                bos.flush();
                bos.close();
                fos.flush();
                fos.close();
                is.close();
                bis.close();
            }

            if (!cancelUpdate) {
                Message message = mHandler.obtainMessage(suc, tempFile);
                mHandler.sendMessage(message);
            } else {
                tempFile.delete();
            }

        } catch (Exception e) {
            Message msg = mHandler.obtainMessage(fail, "���ظ����ļ�ʧ��!");
            mHandler.sendMessage(msg);
            throw e;
        }
    }

    /** ��װ���غ��APK **/
    private void Instanll(File file, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
    
    /**  ���汾  
     * @throws Exception **/
    private void detectionVersion() throws Exception{
        String versionUrl  = UtilListData.getInstance().getDataAppVersion();
                
        if(versionUrl == null) {
            return;
        }
        String [] arr = versionUrl.split("&");
        String latest = arr[0];
        if (latest == null || latest.trim().length() == 0){
            return;
        }
        latest = latest.trim();
        
        AppInfo.setUpdateUrl(arr[1]);
        
        String currtent_version = EDriverApp.getAppVersion();
        
        mLogger.d("latest:"+latest+"currtent:"+currtent_version+" url:"+AppInfo.getUpdateUrl());
        try{
        int lastest_int = Integer.valueOf(latest.replace(".", ""));
        int cur_version_int = Integer.valueOf(currtent_version.replace(".", ""));
        if(lastest_int > cur_version_int){
            // �����߳̿�ʼִ����������
            url = AppInfo.getUpdateUrl();
            if (url == null || url.length() == 0) {
                mHandler.sendEmptyMessage(MSG_URL_ERROR);
            } else {
                // ������������ӵ���������
                notifiManager.notify(notificationId, notification);
                // ��ʼ��������������views
                Message message = mHandler.obtainMessage(refresh, 0);
                mHandler.sendMessage(message);
                downFile(url);
            }
        }
        }catch(NumberFormatException e){
            mLogger.e("error when parse version ", e);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent == null || intent.getAction() == null) {
            return;
        }
        String gotAction = intent.getAction();
        if (ACTION_UPDATE.equals(gotAction)) {
            try {
				detectionVersion();
			} catch (Exception e) {
				e.printStackTrace();
			}
        } else if (ACTION_UPLOAD_CALL_LOG.equals(gotAction)) {
            DBDaoImpl.getInstance().upload();
        } else if (ACTION_UPDATE_MACADDRESS.equals(gotAction)){
            ((EDriverApp)getApplication()).updateMacAddr();
        }
    }
}
