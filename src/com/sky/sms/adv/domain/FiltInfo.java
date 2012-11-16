package com.sky.sms.adv.domain;

import java.io.Serializable;

public class FiltInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	private int id;
	
	private String num;
	
	private String name;

	private boolean checked;
	
	private int postionIndex;
	
	public FiltInfo() {
		super();
	}


	public FiltInfo(String num, String name) {
		super();
		this.num = num;
		this.name = name;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getNum() {
		return num;
	}


	public void setNum(String num) {
		this.num = num;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public boolean isChecked() {
		return checked;
	}


	public void setChecked(boolean checked) {
		this.checked = checked;
	}


	public int getPostionIndex() {
		return postionIndex;
	}


	public void setPostionIndex(int postionIndex) {
		this.postionIndex = postionIndex;
	}


	@Override
	public String toString() {
		return "FiltInfo [id=" + id + ", num=" + num + ", name=" + name
				+ ", checked=" + checked + ", postionIndex=" + postionIndex
				+ "]";
	}

}
