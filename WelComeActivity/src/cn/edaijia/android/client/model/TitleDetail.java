package cn.edaijia.android.client.model;

public class TitleDetail{
	private String leftInfo;
	private String rightInfo;
	public String getLeftInfo() {
		return leftInfo;
	}
	public void setLeftInfo(String leftInfo) {
		this.leftInfo = leftInfo;
	}
	public String getRightInfo() {
		return rightInfo;
	}
	public void setRightInfo(String rightInfo) {
		this.rightInfo = rightInfo;
	}
	@Override
	public  String toString(){
		return " | "+leftInfo +" | "+rightInfo;
	}
}
