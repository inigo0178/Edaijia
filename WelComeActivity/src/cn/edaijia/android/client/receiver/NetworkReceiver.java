package cn.edaijia.android.client.receiver;

import cn.edaijia.android.client.service.EDService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/***
 * ÍøÂç×´Ì¬¼àÌý
 * 
 * @author CaoZheng
 * 
 */
public class NetworkReceiver extends BroadcastReceiver {

    private final static String NETWORK_KACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        String gotAction = intent.getAction();

        if (NETWORK_KACTION.equals(gotAction)) {
            ConnectivityManager connectMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectMgr != null) {
                NetworkInfo wifi = connectMgr
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifi != null && wifi.isConnected()) {
                    Intent serviceIntent = new Intent(context, EDService.class);
                    serviceIntent.setAction(EDService.ACTION_UPLOAD_CALL_LOG);
                    context.startService(serviceIntent);
                }
            }
        }
    }
}
