package com.sky.sms.ym.adv.domain;

import java.io.Serializable;

public class MyMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	
	private String phone;
	
	private String name;
	
	private String content;
	
	private long receive_time = System.currentTimeMillis();
	
	private int syncFlag = 0;
	
	private int delFlag = 0;
	
	private boolean checked  = false;

	private int listIndex;
	
	public MyMessage() {
		super();
	}

	
	public MyMessage(String phone, String content) {
		super();
		this.phone = phone;
		this.content = content;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public long getReceive_time() {
		return receive_time;
	}


	public void setReceive_time(long receive_time) {
		this.receive_time = receive_time;
	}


	public int getSyncFlag() {
		return syncFlag;
	}


	public void setSyncFlag(int syncFlag) {
		this.syncFlag = syncFlag;
	}


	public int getDelFlag() {
		return delFlag;
	}


	public void setDelFlag(int delFlag) {
		this.delFlag = delFlag;
	}


	public boolean isChecked() {
		return checked;
	}


	public void setChecked(boolean checked) {
		this.checked = checked;
	}


	public int getListIndex() {
		return listIndex;
	}


	public void setListIndex(int listIndex) {
		this.listIndex = listIndex;
	}


	@Override
	public String toString() {
		return "MyMessage [id=" + id + ", phone=" + phone + ", name=" + name
				+ ", content=" + content + ", receive_time=" + receive_time
				+ ", syncFlag=" + syncFlag + ", delFlag=" + delFlag
				+ ", checked=" + checked + ", listIndex=" + listIndex + "]";
	}

}
