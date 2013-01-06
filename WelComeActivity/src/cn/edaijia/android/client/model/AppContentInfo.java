package cn.edaijia.android.client.model;

import org.json.JSONException;
import org.json.JSONObject;

public class AppContentInfo {
    /**
     * app����
     **/

    /** ��ֹʱ�� **/
    private String expireAt;

    /** �����ı� **/
    private String recommendText;

    /** �ʼ� **/
    private String emailMessageBody;

    /** ΢�� **/
    private String micBlogMessage;

    /** ���� **/
    private String sMSBodyText;

    /** ��ֵ **/
    private String rechargeText;

    /** ��ʾ�ı� **/
    private String tipsText;

    /** ����app-content��ȫ������ */
    private String content;

    public AppContentInfo(String content) {
        this.content = content;
        parse(content);
    }

    public String getTipsText() {
        return tipsText;
    }

    public String getExpireAt() {
        return expireAt;
    }

    public String getRecommendText() {
        return recommendText;
    }

    public String getEmailMessageBody() {
        return emailMessageBody;
    }

    public String getMicBlogMessage() {
        return micBlogMessage;
    }

    public String getsMSBodyText() {
        return sMSBodyText;
    }

    public String getRechargeText() {
        return rechargeText;
    }

    public void setRechargeText(String rechargeText) {
        this.rechargeText = rechargeText;
    }

    @Override
    public String toString() {
        return "AppContentInfo [expireAt=" + expireAt + ", recommendText="
                + recommendText + ", emailMessageBody=" + emailMessageBody
                + ", micBlogMessage=" + micBlogMessage + ", sMSBodyText="
                + sMSBodyText + ", rechargeText=" + rechargeText + "]";
    }

    private void parse(String strContent) {
        try {
            if (strContent != null && strContent.length() > 0) {
                JSONObject obj = new JSONObject(strContent);
                expireAt = obj.getString("expireAt");
                emailMessageBody = obj.getJSONObject("EmailMessageBody")
                        .getString("zh");
                micBlogMessage = obj.getJSONObject("MicBlogMessage").getString(
                        "zh");
                rechargeText = obj.getJSONObject("RechargeText")
                        .getString("zh");
                recommendText = obj.getJSONObject("RecommendText").getString(
                        "zh");
                sMSBodyText = obj.getJSONObject("SMSBodyText").getString("zh");
                tipsText = obj.getJSONObject("TipsText").getString("zh");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getContent() {
        return content;
    }
}
