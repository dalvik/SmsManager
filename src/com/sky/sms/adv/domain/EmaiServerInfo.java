package com.sky.sms.adv.domain;

import java.io.Serializable;

public class EmaiServerInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String smtpServer;
	
	private String smtpPort;
	
	private String securityType;
	
	private String emailUserName;
	
	private String emailPassword;

	
	public EmaiServerInfo() {
		super();
	}


	public EmaiServerInfo(String smtpServer, String smtpPort,
			String securityType, String emailUserName, String emailPassword) {
		super();
		this.smtpServer = smtpServer;
		this.smtpPort = smtpPort;
		this.securityType = securityType;
		this.emailUserName = emailUserName;
		this.emailPassword = emailPassword;
	}


	public String getSmtpServer() {
		return smtpServer;
	}


	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}


	public String getSmtpPort() {
		return smtpPort;
	}


	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}


	public String getSecurityType() {
		return securityType;
	}


	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}


	public String getEmailUserName() {
		return emailUserName;
	}


	public void setEmailUserName(String emailUserName) {
		this.emailUserName = emailUserName;
	}


	public String getEmailPassword() {
		return emailPassword;
	}


	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}
 

	@Override
	public String toString() {
		return "EmaiServerInfo [smtpServer=" + smtpServer + ", smtpPort="
				+ smtpPort + ", securityType=" + securityType
				+ ", emailUserName=" + emailUserName + ", emailPassword="
				+ emailPassword + "]";
	}
	
	
	
	
}
