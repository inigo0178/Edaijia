package cn.edaijia.android.client.model;

public class ReviewInfo {
	/***����**/
	private String name;//�ͻ�����
	private String level;//���۵ȼ�
	private String comments;//����
	private String employee_id;//˾��
	private String insert_time;//ʱ��
	private String status;
	private String uuid;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getEmployee_id() {
		return employee_id;
	}
	public void setEmployee_id(String employee_id) {
		this.employee_id = employee_id;
	}
	public String getInsert_time() {
		return insert_time;
	}
	public void setInsert_time(String insert_time) {
		this.insert_time = insert_time;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	@Override
	public String toString(){
		return new StringBuffer()		
		.append(" | "+level)
		.append(" | "+employee_id)
		.append(" | "+insert_time)
		.append(" | "+name)
		.append(" | "+status)
		.append(" | "+uuid)
		.append(" | "+comments).toString();
	}
}
