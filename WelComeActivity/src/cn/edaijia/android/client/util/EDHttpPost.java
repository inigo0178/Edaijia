package cn.edaijia.android.client.util;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.EDriverApp;

public class EDHttpPost extends HttpPost {

    public EDHttpPost(String url) {
        super(url);
    }

    public EDHttpPost() {
        super(AppInfo.getBaseUrl());
    }

    public void setEntity(List<NameValuePair> nvps)
            throws UnsupportedEncodingException {
        nvps.add(new BasicNameValuePair("appkey", String
                .valueOf(AppInfo.APP_KEY)));
        nvps.add(new BasicNameValuePair("from", EDriverApp.getChannel()));
        nvps.add(new BasicNameValuePair("macaddress", EDriverApp
                .getMacAddress()));
        nvps.add(new BasicNameValuePair("timestamp", Utils.getDateTime()));
        nvps.add(new BasicNameValuePair("ver", String.valueOf(AppInfo.VERSION)));
        int size = nvps.size();
        String arrStr[] = new String[size];
        TreeMap<String, String> map = new TreeMap<String, String>();

        for (int i = 0; i < size; i++) {
            arrStr[i] = nvps.get(i).getName();
            map.put(arrStr[i], nvps.get(i).getValue());
        }

        String md5 = Utils.toSort(arrStr, map);
        nvps.add(new BasicNameValuePair("sig", md5));

        super.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
    }
}
