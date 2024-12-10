/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.bas.service.basmessage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.bas.entity.basmessage.BasMessage;
import com.gt_plus.modules.bas.entity.tasknotice.BasTaskNotice;
import com.gt_plus.modules.bas.dao.basmessage.BasMessageDao;
import com.gt_plus.modules.bas.dao.tasknotice.BasTaskNoticeDao;
import com.gt_plus.modules.sys.dao.UserDao;
import com.gt_plus.modules.sys.entity.Role;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.utils.UserUtils;

/**
 * 消息Service
 * @author GT0291
 * @version 2017-08-03
 */
@Service
@Transactional(readOnly = false)
public class BasMessageService extends CrudService<BasMessageDao, BasMessage> {

	public static final String SUBSYSTEM_CODE = "subsystem_code";   //子系统标识
	public static final String MESSAGE_ID = "message_id";   //消息编码
	public static final String CREATE_BY = "create_by";     //提交人
	public static final String OWNER_CODE = "ownercode";    //数据范围，机构编码
	public static final String TYPE_TASK = "task";          //任务通知类型
	public static final String TASK_TARGET = "task_target"; //任务通知，角色列表Json对象名称
	public static final String TASK_TITLE = "task_title";   //任务通知，标题名称
	public static final String STATUS_NO = "0";             //任务处理状态，未处理
	public static final String STATUS_YES = "1";            //任务处理状态，已处理
	
	//@Autowired
	//private BasMessageDao basMessageDao;
	
	@Autowired
	private BasTaskNoticeDao basTaskNoticeDao;
	
	@Autowired
	private UserDao userDao;
	
	public BasMessage get(String id) {
		return super.get(id);
	}
	
	public List<BasMessage> findList(BasMessage basMessage) {
		return super.findList(basMessage);
	}
	
	public Page<BasMessage> findPage(Page<BasMessage> page, BasMessage basMessage) {
		return super.findPage(page, basMessage);
	}
	
	@Transactional(readOnly = false)
	public void save(BasMessage basMessage) {
		super.save(basMessage);
	}
	
	@Transactional(readOnly = false)
	public void saveV(BasMessage basMessage) {
		super.saveV(basMessage);
	}
	
	@Transactional(readOnly = false)
	public void delete(BasMessage basMessage) {
		super.delete(basMessage);
	}
	
	/**
	 * 保存消息
	 */
	@Transactional(readOnly = false)
	public void saveMessage(String subSystemCode, String title, String ownerCode, Map<String,String> roleMap) {
		//owner_code, role and action list
		if (roleMap.size() > 0) {
			Map<String,Object> contentMap = new HashMap<String,Object>();
			Gson gson = new Gson();
			BasMessage basMessage = new BasMessage();
			basMessage.setId(UUID.randomUUID().toString());
			
			contentMap.put(SUBSYSTEM_CODE, subSystemCode);
			contentMap.put(MESSAGE_ID, basMessage.getId());
			contentMap.put(CREATE_BY, UserUtils.getUser().getId());
			contentMap.put(TASK_TITLE, title);
			contentMap.put(OWNER_CODE, ownerCode);
			contentMap.put(TASK_TARGET,roleMap);
			
			basMessage.setTypes(TYPE_TASK);
			basMessage.setContent(gson.toJson(contentMap));
			basMessage.setStatus(STATUS_NO); //默认处理状态（是否处理）：否
			//basMessageDao.insert(basMessage);
			basMessage.setIsNewRecord(true);
			this.save(basMessage);
		}
	}
	
	/**
	 * 保存消息
	 */
	@Transactional(readOnly = false)
	public void saveMessage(String subSystemCode, String title, String ownerCode, List<Role> roleList, String action) {
		//owner_code, role and action list
		if (roleList.size() > 0) {
			Map<String,String> roleMap = new HashMap<String,String>();
			for(Role theRole : roleList) {
				roleMap.put(theRole.getEnname(), action);
			}
			Map<String,Object> contentMap = new HashMap<String,Object>();
			Gson gson = new Gson();
			BasMessage basMessage = new BasMessage();
			basMessage.setId(UUID.randomUUID().toString());
			
			contentMap.put(SUBSYSTEM_CODE, subSystemCode);
			contentMap.put(MESSAGE_ID, basMessage.getId());
			contentMap.put(CREATE_BY, UserUtils.getUser().getId());
			contentMap.put(TASK_TITLE, title);
			contentMap.put(OWNER_CODE, ownerCode);
			contentMap.put(TASK_TARGET,roleMap);
			
			basMessage.setTypes(TYPE_TASK);
			basMessage.setContent(gson.toJson(contentMap));
			basMessage.setStatus(STATUS_NO); //默认处理状态（是否处理）：否
			//basMessageDao.insert(basMessage);
			basMessage.setIsNewRecord(true);
			this.save(basMessage);
		}
	}
	
	
	/**
	 * 每5分钟定时处理消息
	 */
	public void callAnlyzeMessage(){
		anlyzeMessage(null,false,null);
	}

	/**
	 * 处理消息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public void anlyzeMessage(String messageType, boolean redo, String id) {
		if (StringUtils.isEmpty(messageType) || messageType.equals(TYPE_TASK)) {
			//消息类型为空，或者类型是 task
			//遍历消息队列表，分发消息
			BasMessage basMessage = new BasMessage();
			if(false == redo) {
				basMessage.setStatus(STATUS_NO);
			} 
			if(id!=null && id!=""){
				basMessage.setId(id);
			}
			basMessage.setTypes(TYPE_TASK);
			//List<BasMessage> list = basMessageDao.findList(basMessage);
			List<BasMessage> list = this.findList(basMessage);
			Gson gson = new Gson();
			for(BasMessage obj : list) {
				//String content = obj.getContent();
				Map<String,Object> contentMap = (HashMap<String,Object>)gson.fromJson(obj.getContent(), HashMap.class);
				String messageId = (String) contentMap.get(MESSAGE_ID);
				String subSystemCode = (String) contentMap.get(SUBSYSTEM_CODE);
				String submitUserId = (String) contentMap.get(CREATE_BY);
				String title = (String) contentMap.get(TASK_TITLE);
				String ownerCode = (String) contentMap.get(OWNER_CODE);
				LinkedTreeMap<String,String> roleMap = (LinkedTreeMap<String,String>) contentMap.get(TASK_TARGET);
				//Map<String,String> roleMap = (HashMap<String,String>) contentMap.get(TASK_TARGET);
				for (Entry<String, String> role : roleMap.entrySet()) {
					this.sendNotice(messageId, subSystemCode, userDao.get(submitUserId), title, ownerCode, role.getKey(), role.getValue());
				}
				//处理完成，改变状态
				basMessage = this.get(messageId);
				basMessage.setStatus(STATUS_YES);
				this.save(basMessage);
			}
		}
	}
	
	@Transactional(readOnly = false)
	private void sendNotice(String messageId, String subSystemCode, User submitUser, String title, String ownerCode, String role, String action) {
		//根据角色和ownerCode查找用户列表
		//遍历用户列表，发送消息
		List<User> userList = userDao.findUsersForNotice(ownerCode + "%", role);
		for(User theUser : userList) {
			BasTaskNotice notice = new BasTaskNotice();
			notice.setId(UUID.randomUUID().toString());
			notice.setMessageId(messageId);
			notice.setTypes(subSystemCode);
			notice.setContent(title);
			notice.setStatus(STATUS_NO);
			notice.setRemarks(action);
			notice.setUserId(theUser.getId());        //接收人ID
			notice.setUserName(theUser.getName());    //接收人姓名
			notice.setCreateUserName(submitUser.getName()); //提交人姓名
			String officeName = "";
			if (false == StringUtils.isEmpty(submitUser.getCompany().getName())) officeName += submitUser.getCompany().getName();
			if (false == StringUtils.isEmpty(submitUser.getOffice().getName())) officeName +=  "　" + submitUser.getOffice().getName();
			notice.setOfficeName(officeName); //提交人 单位名称+部门名称
			notice.setIsNewRecord(true);
			//notice.preInsert();
			notice.setCreateDate(new Date());
			notice.setCreateBy(submitUser);
			basTaskNoticeDao.insert(notice);
		}
	}
	
	
}