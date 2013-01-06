package cn.edaijia.android.client;

import android.content.Context;
import android.widget.Toast;

public class AppInfo {

    public static final String CHANNEL_HOMEPAGE = "edaijia";

    /** ��������url **/
    private static String sUpdateUrl;
    /** �°汾�ӿڵ�ַ */
    private static String sBaseUrl;
    /** MD5 Key */
    private static String sKeyMD5;
    /** ΢��key */
    private static String sWeiboKey;
    /** ΢����Կ */
    private static String sWeiboSecret;
    /** ΢���ض���URL */
    private static String sWeiboUri;
    
    public final static int KEY_BASE_URL = 0x1;
    public final static int KEY_MD5 = 0x2;
    public final static int KEY_WEIBO_KEY = 0x3;
    public final static int KEY_WEIBO_SECRET = 0x4;
    public final static int KEY_WEIBO_URL = 0x5;
    public final static int KEY_BAIDU_MAP_KEY = 0x6;
    
    
   
    
    
    /**
     * �ַ������͵ĳ���
     */
    // ��������ύ�����쳣
    public static final String REQUEST_ERROR = "REQUEST_ERROR";
    public static final String REQUEST_SUCCESS = "REQUEST_SUCCESS";

    /**
     * �������͵ĳ���
     */
    // ����
    public static final int RESPONSE_ERROR = 1;
    public static final int RESPONSE_SUCCESS = 0;

    // �̳߳صĴ�С
    public static final int THREADPOOL_SIZE = 10;

    // appkey
    public final static int APP_KEY = 10000002;

    public static int VERSION = 3;
    
    // �����ٶ�
    public static final int FLING_MIN_DISTANCE = 100;
    public static final int FLING_MIN_VELOCITY = 200;
    public static final String PRE_STATE = "edaijia_state";
    public static final String PARAMS_MAC_ADDRESS = "mac_address";
    public static final String PHONE_NUMBER_400 = "4006913939";
    
    /**
     * sdcard <br>
     * <B>OR</B><br>
     * data/data/cn.edaijia.android.client
     */
    public static String BASE_DATA_DIR = null;

    /**
     * ��ʾ��ʾ��Ϣ
     * 
     * @param context
     *            : �����Ļ���
     * @param message
     *            �� ��ʾ����Ϣ
     * @category This method must called in the UI-Thread.
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getUpdateUrl() {
        return sUpdateUrl;
    }

    public static void setUpdateUrl(String download) {
        sUpdateUrl = download;
    }

    public static String getKeyMD5() {
        return sKeyMD5;
    }

    public static void setKeyMD5(String sKeyMD5) {
        AppInfo.sKeyMD5 = sKeyMD5;
    }

    public static String getWeiboKey() {
        return sWeiboKey;
    }

    public static void setWeiboKey(String sWeiboKey) {
        AppInfo.sWeiboKey = sWeiboKey;
    }

    public static String getWeiboSecret() {
        return sWeiboSecret;
    }

    public static void setWeiboSecret(String sWeiboSecret) {
        AppInfo.sWeiboSecret = sWeiboSecret;
    }

    public static String getWeiboUri() {
        return sWeiboUri;
    }

    public static void setWeiboUri(String sWeiboUri) {
        AppInfo.sWeiboUri = sWeiboUri;
    }

    public static String getBaseUrl() {
        return sBaseUrl;
    }

    public static void setBaseUrl(String sBaseUrl) {
        AppInfo.sBaseUrl = sBaseUrl;
    }
}
