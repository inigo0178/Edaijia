package cn.edaijia.android.client.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.View.MeasureSpec;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.EDriverApp;
import cn.edaijia.android.client.R;
import cn.edaijia.android.client.db.DBDaoImpl;
import cn.edaijia.android.client.db.UploadInfo;
import cn.edaijia.android.client.maps.DriverRecord;
import cn.edaijia.android.client.maps.EDLocationListener;
import cn.edaijia.android.client.maps.LocManager;

/**********************************************
 * ���ù�����
 * 
 * @author CaoZheng email:caozheng_119@163.com
 * @create time:2012-8-10 10:07 --------------------last
 *         update:----------------- coder: update time: Copyright (c) YDJ
 *         corporation All Rights Reserved. INFORMATION
 */
public class Utils {

    public static boolean sDebug = false;
    public static String BASE_FOLDER_NAME = "eDaiJia";
    private static final String ENCODING_UTF8 = "UTF-8";
    private static final String FILE_NAME = "price.txt";
    private static final String PICTURE = "weibo.png";// ͼƬ

    private static Logger sLogger = Logger.createLogger("Utils");

    /**
     * SD����Ŀ¼��ͼƬ�ļ�
     */
    public static String getWeiboSharedPicture() {
        String path = FileUtils.getFilePath(PICTURE);
        if (path == null) {
            return null;
        }
        return path;
    }

    /**
     * ͼƬ���浽sdcard
     */
    public static void saveWeiboSharedPicture(Context context) {
        if (isSdcardWritable()) {
            try {
                String filePath = FileUtils.getFilePath(PICTURE);
                if (filePath == null) {
                    AssetManager assetManager = context.getAssets();
                    InputStream in = assetManager.open(PICTURE);
                    FileUtils.write2SDFromInput("", PICTURE, in);
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** �˿�ʱ�� */
    public static String getDateTime() {
        return TimeUtil.getRemindDateFormat(System.currentTimeMillis());
    }

    /************
     * save pricejson * �����ļ�
     */
    public static void save(String json, Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME,
                    Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readAssetsFile(Context context, String NAME) {
        BufferedReader bReader = null;
        InputStream in = null;
        try {
            AssetManager assetManager = context.getAssets();
            in = assetManager.open(NAME);
            bReader = new BufferedReader(new InputStreamReader(in, "GBK"));
            String line = null;
            StringBuilder buffer = new StringBuilder();
            while ((line = bReader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bReader != null) {
                try {
                    bReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /*******************************************
     * ѹ��
     * 
     * @return String byte[] buf=bout.toByteArray();//��ȡ�ڴ滺���е�����
     */
    public static byte[] compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DeflaterOutputStream gzip = new DeflaterOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        return out.toByteArray();// ��ȡ�ڴ滺���е�����;
    }

    /*****************************************
     * ·��json д���ļ���
     * 
     * @param filePath
     */
    public static void writePathLog(String filePath, byte[] byte_str) {
        try {
            File file = new File(filePath);
            OutputStream out = new FileOutputStream(file);
            out.write(byte_str);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*****************************************
     * ·��json �ļ��ɹ��ϴ��������� ɾ��
     * 
     * @param filePath
     */
    public static boolean deletePathLog(String filePath) {
        File file = new File(filePath.substring(4, filePath.length()));
        if (!file.exists()) {
            return false;
        }

        boolean isdelete = file.delete();
        return isdelete;
    }

    /******************************************
     * ��ȡUUID
     * 
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    /****************************************************
     * ��һ���ַ�����ת����UTF-8������ֽ�����
     * 
     * @param string
     *            ��Ҫת����Դ�ַ���
     * @return byte[] ת������ֽ�����
     */
    public static byte[] getUTF8Bytes(String string) {
        if (string == null)
            return new byte[0];

        try {
            return string.getBytes(ENCODING_UTF8);
        } catch (UnsupportedEncodingException e) {
            /*
             * If system doesn't support UTF-8, use another way
             */
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                dos.writeUTF(string);
                byte[] jdata = bos.toByteArray();
                bos.close();
                dos.close();
                byte[] buff = new byte[jdata.length - 2];
                System.arraycopy(jdata, 2, buff, 0, buff.length);
                return buff;
            } catch (IOException ex) {
                return new byte[0];
            }
        }
    }

    /********************************************
     * ���ַ����е�ͨ������ƥ���ϵ��ַ����滻�������ַ���
     * 
     * @param res
     *            : Դ�ַ���
     * @param PatternStr
     *            �� ƥ���ַ���
     * @param replaceStr
     *            �� �滻�ַ���
     * @return String : �����滻����ַ���
     */
    public static String replace(String res, String PatternStr,
            String replaceStr) {
        Pattern p = Pattern.compile(PatternStr);
        Matcher m = p.matcher(res);
        String after = m.replaceAll(replaceStr);
        return after;
    }

    /****************************************************
     * ��һ�� Bitmap ����ת����Drawable ����
     * 
     * @param bitmap
     *            �� ��Ҫת���� Bitmap ����
     * @return Drawable
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        if (bitmap == null)
            return null;

        return new BitmapDrawable(bitmap);
    }

    /***************************************************
     * ����byte[]�ֽ����飬����һ��bitmap����
     * 
     * @param bytes
     *            : bitmap���ֽ�����
     * @return Bitmap
     */
    public static Bitmap createBitmap(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return null;

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    /***************************************************
     * ��ȡһ��bitmap������ֽ�����
     * 
     * @param bitmap
     * @return
     */
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        byte[] data = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        data = baos.toByteArray();

        return data;
    }

    /****************************************************
     * �Ƚ�2���ֽ������е������Ƿ����
     * 
     * @param a
     *            : �ֽ�����
     * @param b
     *            : �ֽ�����
     * @return boolean
     */
    public static boolean isEqual(byte[] a, byte[] b) {
        if (a == null || b == null)
            return a == b;

        else if (a.length != b.length)
            return false;
        else {
            for (int i = 0; i < b.length; i++) {
                if (a[i] != b[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    /***************************************************
     * ���SDCard�Ƿ��д
     */
    public static boolean isSdcardWritable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /***************************************************
     * ���SDCard�Ƿ�ɶ�
     */
    public static boolean isSdcardReadable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)
                || Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /****************************************************
     * ������ά���ַ
     */
    public static HashMap<String, String> parserUri(Uri uri) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        String paras[] = uri.getQuery().split("&");
        for (String s : paras) {
            if (s.indexOf("=") != -1) {
                String[] item = s.split("=");
                parameters.put(item[0], item[1]);
            } else {
                return null;
            }
        }
        return parameters;
    }

    /** �������� **/
    public static int toInt(double val) {
        return Integer.parseInt(new BigDecimal(val).setScale(0,
                BigDecimal.ROUND_HALF_UP).toString());
    }

    /** ���� **/
    public static List<String> formatSequence(String[] input) {
        if (input == null) {
            return null;
        }
        List<String> list = Arrays.asList(input);
        Collections.sort(list);
        return list;
    }

    /***************
     * ���� �� MD5
     * 
     */
    public static String toSort(String[] arrStr, TreeMap<String, String> listMap) {
        String return_str = "";
        String tag = "";
        // ����
        Comparator comp = Collator.getInstance(java.util.Locale.ENGLISH);
        Arrays.sort(arrStr, comp);
        int as = arrStr.length;

        for (int i = 0; i < as; i++) {
            tag = arrStr[i];

            for (Map.Entry<String, String> entry : listMap.entrySet()) {
                if (tag.equals(entry.getKey())) {
                    return_str += entry.getKey() + entry.getValue();
                }
            }

        }
        // sLogger.d( return_str);
        return MD5.md5(return_str + AppInfo.getKeyMD5());
    }

    /** У�鶨λ��ַ�Ϸ��� **/
    public static boolean isErrorLoc(double lat, double lont) {
        if (lat == 0 || lont == 0) {
            return true;
        } else if ((lat + "").contains("E") || (lont + "").contains("E")) {
            return true;
        }
        return false;
    }

    /**
     * �õ���Χ˾��List
     * 
     * @throws JSONException
     **/
    public static List<DriverRecord> getDriverList(String data)
            throws JSONException {
        if (data == null || data.length() == 0) {
            return null;
        }

        JSONObject jsobj = new JSONObject(data);
        String code = jsobj.get("code").toString();
        // String message = jsobj.get("message").toString();
        List<DriverRecord> result = new ArrayList<DriverRecord>();

        if ("0".equals(code)) {
            JSONArray jsarr = jsobj.getJSONArray("driverList");
            int lenth = jsarr.length();
            JSONObject jo;

            for (int i = 0; i < lenth; i++) {
                jo = jsarr.getJSONObject(i);
                DriverRecord dr = new DriverRecord();
                dr.setId(jo.getString("id"));
                dr.setName(jo.getString("name"));
                dr.setPhone(jo.getString("phone"));
                dr.setIdCard(jo.getString("idCard"));
                dr.setDomicile(jo.getString("domicile"));
                dr.setCard(jo.getString("card"));
                dr.setYear(jo.getString("year"));
                dr.setLevel(jo.getString("level"));
                dr.setState(jo.getString("state"));
                dr.setPrice(jo.getString("price"));
                dr.setLatitude(Double.parseDouble(jo.getString("latitude")));
                dr.setLongtitude(Double.parseDouble(jo.getString("longitude")));
                dr.setDistance(jo.getString("distance"));
                // dr.setSort(jo.getString("sort"));
                dr.setPic_small(jo.getString("picture_small"));
                dr.setPic_middle(jo.getString("picture_middle"));
                dr.setPic_large(jo.getString("picture_large"));
                dr.setOrder_count(jo.getString("order_count"));
                dr.setComment_count(jo.getString("comment_count"));
                dr.setDriver_id(jo.getString("driver_id"));

                result.add(dr);
            }
        }
        return result;
    }

    /***
     * Viewת��Bitmap
     * 
     * @param v
     * @return
     */
    public static Bitmap View2Bitmap(View v) {
        v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.buildDrawingCache();
        Bitmap b = v.getDrawingCache();
        return b;
    }

    public static String getIMEI(String imei, String macAddr) {
        if (imei == null || imei.length() != 15) {
            imei = createFakeIMEIFromWlanMac(macAddr);
            if (imei == null) {
                imei = "101010101010103";
            }
        }
        return imei;
    }

    private static int getIMEICheckDigit(String imei)
            throws IllegalArgumentException {
        int sum = 0;
        if (imei.length() != 14)
            throw new IllegalArgumentException(
                    "IMEI Calculate Check digit, wrong length of imei");

        for (int i = 0; i < imei.length(); i++) {
            int digit = Character.digit(imei.charAt(i), 10);
            if (i % 2 != 0) {
                digit *= 2;
            }
            sum += digit / 10 + digit % 10;
        }
        sum %= 10;

        return sum == 0 ? 0 : 10 - sum;
    }

    private static String createFakeIMEIFromWlanMac(String macAddr) {

        if (macAddr == null || macAddr.length() > 0) {
            return null;
        }

        String imei = "";
        try {
            String[] macArray = macAddr.split(":");

            if (macArray.length == 6) {
                long mac = Long.parseLong(macArray[5]) << 0
                        | Long.parseLong(macArray[4]) << 8
                        | Long.parseLong(macArray[3]) << 16
                        | Long.parseLong(macArray[2]) << 24
                        | Long.parseLong(macArray[1]) << 32;
                imei = String.format("%014d", mac);
                imei += String.format("%d", getIMEICheckDigit(imei));
            } else {
                sLogger.e("Wrong number of elements in wlan mac addr file "
                        + macAddr);
                imei = "";
            }

        } catch (NumberFormatException e) {
            sLogger.e("Unable to convert wlan mac string " + macAddr
                    + " to numbers");
            imei = "";
        } catch (Exception e) {
            sLogger.e("Exception" + e + "\nWhen parsing wlan macaddr "
                    + macAddr);
            imei = "";
        }
        return (imei != null && imei.length() == 0) ? null : imei;
    }

    public static void callPhone(Context context, String phoneNumber) {
        if (context == null || phoneNumber == null || phoneNumber.length() == 0) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void saveCallLog(final EDriverApp app, final String driverID) {
        final LocManager locManager = app.getLocManager();
        if (locManager != null) {
            locManager.addLocationListener(new EDLocationListener() {
                @Override
                public void onGetLocation(BDLocation location) {
                    saveCallLog(location.getLatitude(), location.getLongitude(), driverID);
                    locManager.removeLocationListener(this);
                }

                @Override
                public void onFailed() {
                    saveCallLog(locManager.getLat(), locManager.getLont(),
                            driverID);
                    locManager.removeLocationListener(this);
                }

                @Override
                public boolean isReceivedLocation() {
                    return true;
                }
            });
            locManager.requestLoc();
        } else {
            saveCallLog(0, 0, driverID);
        }
    }

    private static synchronized void saveCallLog(final double lat,
            final double lont, final String driverID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UploadInfo callLog = new UploadInfo();
                callLog.setCall_time(TimeUtil.getcurrentTimes());
                callLog.setUdid(EDriverApp.getImei());
                callLog.setDevice(android.os.Build.MANUFACTURER + "-"
                        + android.os.Build.MODEL);
                callLog.setLatitude(String.valueOf(lat));
                callLog.setLongtitude(String.valueOf(lont));
                callLog.setOs(android.os.Build.VERSION.RELEASE);
                callLog.setPhone(EDriverApp.getPhoneNumber());
                callLog.setVersion(EDriverApp.getAppVersion());
                callLog.setDriver_id(driverID);
                callLog.setResolution(EDriverApp.getWidth() + "*"
                        + EDriverApp.getHeight());
                // �����ݿ�
                DBDaoImpl.getInstance().saveCalllog(callLog);
            }
        }).start();
    }

    public static void sendEmail(Context context, String email_to,
            String subject, String email_body) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (email_to != null) {
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email_to });
        }
        // �����ʼ�Ĭ�ϱ���
        if (subject != null) {
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        }
        // ����ҪĬ�Ϸ��͵�����
        if (email_body != null) {
            intent.putExtra(Intent.EXTRA_TEXT, email_body);
        }
        intent.setType("text/html");
        context.startActivity(Intent.createChooser(intent,
                context.getString(R.string.share_email_choice)));
    }
}
