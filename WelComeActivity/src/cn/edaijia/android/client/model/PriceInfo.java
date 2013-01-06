package cn.edaijia.android.client.model;

import java.util.ArrayList;

public class PriceInfo {
    private String priceTitle = null;// �۸�����
    private String priceNote = null;// �۸��ע��
    private String expireAt = null;// ����ʱ��
    private ArrayList<TitleDetail> titelDetails;

    public String getPriceTitle() {
        return priceTitle;
    }

    public void setPriceTitle(String priceTitle) {
        this.priceTitle = priceTitle;
    }

    public String getPriceNote() {
        return priceNote;
    }

    public void setPriceNote(String priceNote) {
        this.priceNote = priceNote;
    }

    public String getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(String expireAt) {
        this.expireAt = expireAt;
    }

    public ArrayList<TitleDetail> getTitelDetails() {
        return titelDetails;
    }

    public void setTitelDetails(ArrayList<TitleDetail> titelDetails) {
        this.titelDetails = titelDetails;
    }

    @Override
    public String toString() {
        String tds = null;
        if (titelDetails != null) {
            for (TitleDetail t : titelDetails) {
                String temp = t.toString();
                tds += temp;
            }
        }
        return new StringBuffer().append(priceTitle).append(" | ")
                .append(priceNote).append(" | ").append(expireAt).append(tds)
                .toString();
    }
}
