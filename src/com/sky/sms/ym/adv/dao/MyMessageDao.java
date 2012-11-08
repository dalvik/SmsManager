package com.sky.sms.ym.adv.dao;

import java.util.List;

import com.sky.sms.ym.adv.domain.DataException;
import com.sky.sms.ym.adv.domain.FiltInfo;
import com.sky.sms.ym.adv.domain.MyMessage;

public interface MyMessageDao {

	public List<MyMessage> getMyMessageList(int offSet) throws DataException;

	public List<MyMessage> getMyMessageListByID(long id) throws DataException;
	
	public int getTotalSmsNumber() throws DataException ;
	
	public void addMyMessage(MyMessage myMessage) throws DataException;
	
	public void delMyMessage(long id) throws DataException;
	
	public void delAllMyMessage(List<MyMessage> myMessageList) throws DataException;
	
	// 更新已同步标记
	public void updateSyncedFlag(List<MyMessage> myMessageList) throws DataException;
	
	/********************************/
	// 添加过滤的电话号码
	public void addFiltNum(String oldNum, String newNum) throws DataException;
	
	// 查询是否是需要过滤的电话号码
	public boolean isFiltNum(String num) throws DataException;
	
	// 查询过滤电话号码列表
	public List<FiltInfo> getFiltNum() throws DataException;
	
	// 更新过滤电话号码
	public void updateFiltNum(String num) throws DataException;

	// 删除过滤的电话号码
	public void deleteFiltNum(String num) throws DataException;
}
