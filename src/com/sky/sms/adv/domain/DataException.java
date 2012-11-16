package com.sky.sms.adv.domain;

public class DataException extends Exception {

	private static final long serialVersionUID = 1L;

	public DataException() {

	}

	public DataException(String detailMessage) {
		super(detailMessage);
	}

	public DataException(Throwable throwable) {
		super(throwable);
	}

	public DataException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
