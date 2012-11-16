package com.sky.sms.adv.main;

import java.io.Serializable;

public class SendGPSTask implements Serializable {

	private static final long serialVersionUID = 2875346997043489587L;

	// 定位任务的唯一性标识
	private long tid;
	
	// 任务名称
	private String name = "新建任务";
	
	// 周几执行的任务
	private String week;
	
	// 任务开始时间
	private long start = 0;
	
	// 任务结束时间
	private long end = 0;
	
	//  上传的频率
	private int rate = 1;  // 0   睡眠上传  1 正常上传  2  高频上传
	
	// 联网方式
	private int net = 3; // 0 WIFI  1 手机
	
	// 定位方式
	private int mod = 0;  // 0 gps   1  network  2 gps&network
	
	// 是否保存至SD卡
	private int local = 0;// 0 否    1  是
	
	private int valid = 1;
	
	private long time = 0;
	
	// 用户的身份表示（普通用户或者是管理员） (1为管理员)
	private int identify = 0;
	
	private int sync = 0;
	
	private int delflag = 0;
	
	public SendGPSTask() {
		super();
	}

	public long getTid() {
		return tid;
	}

	public void setTid(long tid) {
		this.tid = tid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getNet() {
		return net;
	}

	public void setNet(int net) {
		this.net = net;
	}

	public int getMod() {
		return mod;
	}

	public void setMod(int mod) {
		this.mod = mod;
	}

	public int getLocal() {
		return local;
	}

	public void setLocal(int local) {
		this.local = local;
	}

	public int getValid() {
		return valid;
	}

	public void setValid(int valid) {
		this.valid = valid;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getIdentify() {
		return identify;
	}

	public void setIdentify(int identify) {
		this.identify = identify;
	}

	public int getSync() {
		return sync;
	}

	public void setSync(int sync) {
		this.sync = sync;
	}

	public int getDelflag() {
		return delflag;
	}

	public void setDelflag(int delflag) {
		this.delflag = delflag;
	}

	@Override
	public String toString() {
		return "SendGPSTask [tid=" + tid + ", name=" + name + ", week=" + week
				+ ", start=" + start + ", end=" + end + ", rate=" + rate
				+ ", net=" + net + ", mod=" + mod + ", local=" + local
				+ ", valid=" + valid + ", time=" + time + ", identify="
				+ identify + ", sync=" + sync + ", delflag=" + delflag + "]";
	}
}
