/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.bas.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.gt_plus.common.service.BaseService;
import com.gt_plus.modules.bas.dao.basmessage.BasMessageDao;
import com.gt_plus.modules.bas.dao.tasknotice.BasTaskNoticeDao;
import com.gt_plus.modules.bas.entity.basmessage.BasMessage;
import com.gt_plus.modules.bas.entity.tasknotice.BasTaskNotice;
import com.gt_plus.modules.sys.dao.UserDao;
import com.gt_plus.modules.sys.entity.User;

/**
 * 任务相关的服务接口。
 * @author gt_plus
 * @version 2013-12-05
 */
@Service
@Transactional(readOnly = true)
public class TaskService  extends BaseService implements InitializingBean {
	
	public static final String MESSAGE_ID = "message_id";   //消息编码
	public static final String OWNER_CODE = "ownercode";    //数据范围，机构编码
	public static final String TYPE_TASK = "task";          //任务通知类型
	public static final String TASK_TARGET = "task_target"; //任务通知，角色列表Json对象名称
	public static final String TASK_TITLE = "task_title";   //任务通知，标题名称
	public static final String STATUS_NO = "0";             //任务处理状态，未处理
	
	@Autowired
	private BasMessageDao basMessageDao;
	
	@Autowired
	private BasTaskNoticeDao basTaskNoticeDao;
	
	@Autowired
	private UserDao userDao;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	/**
	 * 保存消息
	 */
	@Transactional(readOnly = false)
	public void saveMessage(String title, String ownerCode, Map<String,String> roleMap) {
		//owner_code, role and action list
		if (roleMap.size() > 0) {
			Map<String,Object> contentMap = new HashMap<String,Object>();
			Gson gson = new Gson();
			BasMessage basMessage = new BasMessage();
			basMessage.setId(UUID.randomUUID().toString());
			
			contentMap.put(MESSAGE_ID, basMessage.getId());
			contentMap.put(TASK_TITLE, "关于“" + title + "”的任务通知");
			contentMap.put(OWNER_CODE, ownerCode);
			contentMap.put(TASK_TARGET,roleMap);
			
			basMessage.setTypes(TYPE_TASK);
			basMessage.setContent(gson.toJson(contentMap));
			basMessage.setStatus(STATUS_NO); //默认处理状态（是否处理）：否
			basMessageDao.insert(basMessage);
		}
	}
	
	/**
	 * 处理消息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public void anlyzeMessage(String messageType) {
		if (StringUtils.isEmpty(messageType) || messageType.equals(TYPE_TASK)) {
			//消息类型为空，或者类型是 task
			//遍历消息队列表，分发消息
			BasMessage basMessage = new BasMessage();
			basMessage.setStatus(STATUS_NO);
			basMessage.setTypes(TYPE_TASK);
			List<BasMessage> list = basMessageDao.findList(basMessage);
			Gson gson = new Gson();
			for(BasMessage obj : list) {
				//String content = obj.getContent();
				Map<String,Object> contentMap = (HashMap<String,Object>)gson.fromJson(obj.getContent(), HashMap.class);
				String messageId = (String) contentMap.get(MESSAGE_ID);
				String title = (String) contentMap.get(TASK_TITLE);
				String ownerCode = (String) contentMap.get(OWNER_CODE);
				Map<String,String> roleMap = (HashMap<String,String>) contentMap.get(TASK_TARGET);
				for (Entry<String, String> role : roleMap.entrySet()) {
					this.sendNotice(messageId, title, ownerCode, role.getKey(), role.getValue());
				}
			}
		}
	}
	
	@Transactional(readOnly = false)
	private void sendNotice(String messageId, String title, String ownerCode, String role, String action) {
		//根据角色和ownerCode查找用户列表
		//遍历用户列表，发送消息
		List<User> userList = userDao.findUsersForNotice(ownerCode, role);
		for(User theUser : userList) {
			BasTaskNotice notice = new BasTaskNotice();
			notice.setMessageId(messageId);
			notice.setContent(title);
			notice.setStatus(STATUS_NO);
			notice.setRemarks(action);
			notice.setUserId(theUser.getId());
			notice.setUserName(theUser.getName());
			notice.setOfficeName(theUser.getCompany().getName() + "　" + theUser.getOffice().getName()); //单位名称+部门名称
			basTaskNoticeDao.insert(notice);
		}
	}
}
