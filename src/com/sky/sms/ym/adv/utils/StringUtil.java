package com.sky.sms.ym.adv.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sky.sms.ym.adv.domain.MyMessage;

public class StringUtil {

	public static String getSMSToString(List<MyMessage> myMessageList) {
		StringBuffer smsContent = new StringBuffer();
		int i = 1;
		for(MyMessage myMessage:myMessageList) {
			smsContent.append(i++ + ".  Name: " + myMessage.getName() + "\n   Phone: " + myMessage.getPhone() + "\n     Time: " + DateUtil.formatyyMMDDHHmmss(myMessage.getReceive_time()) + "\n     Content: " + myMessage.getContent() + "\n\n");
		}
		return smsContent.toString();
	}
	
	public static boolean isEmail(String strEmail) {
	     String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
	     Pattern p = Pattern.compile(strPattern);
	     Matcher m = p.matcher(strEmail);
	     return m.matches();
	 }
	
	 public static String toUnicode(String str){
	        char[]arChar=str.toCharArray();
	        int iValue=0;
	        String uStr="";
	        for(int i=0;i<arChar.length;i++){
	            iValue=(int)str.charAt(i);           
	            if(iValue<=256){
	                uStr+="\\u00"+Integer.toHexString(iValue);
	            }else{
	                uStr+="\\u"+Integer.toHexString(iValue);
	            }
	        }
	        return uStr;
	}

	 
	public static String decodeUnicode(final String dataStr) {
		if("".equals(dataStr)) {
			return "";
		}
		int start = 0;
		int end = 0;
		StringBuffer buffer = new StringBuffer();
		while (start > -1) {
			end = dataStr.indexOf("\\u", start + 2);
			String charStr = "";
			if (end == -1) {
				charStr = dataStr.substring(start + 2, dataStr.length());
			} else {
				charStr = dataStr.substring(start + 2, end);
			}
			char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
			buffer.append(new Character(letter).toString());
			start = end;
		}
		return buffer.toString();
	}
	
	
	
	
}
