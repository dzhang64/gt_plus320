package com.gt_plus.modules.act.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.gt_plus.common.persistence.ActEntity;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.modules.act.entity.Act;
import com.gt_plus.modules.act.service.ActTaskService;
import com.gt_plus.modules.oa.entity.edoc.EdocSend;
import com.gt_plus.modules.oa.entity.setting.TaskSettingVersion;
import com.gt_plus.modules.oa.service.setting.TaskSettingService;
import com.gt_plus.modules.oa.service.setting.TaskSettingVersionService;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.service.SystemService;
import com.gt_plus.modules.sys.utils.UserUtils;

@Component
public class ExtActUtils<T extends ActEntity<T>> {
	
	@Autowired
	private ProcessEngineFactoryBean processEngine;
	
	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private HistoryService historyService;
	
	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private TaskSettingService taskSettingService;
	
	@Autowired
	private TaskSettingVersionService taskSettingVersionService;
	
	@Autowired
	private ActTaskService actTaskService;
	
	protected enum Button {
		暂存, 提交;
	}
	
	protected enum UserButton {
		提交, 同意, 知会, 归档, 退回, 重申, 销毁;
	}
	
	protected enum PassButton {
		提交, 同意, 知会, 归档, 重申;
	}
	
	protected enum FailButton {
		退回, 销毁;
	}
	
	protected enum SettingValue {
		id, type, name,
		office, post, level, role, user, relative;
	}
	
	protected enum RelativeIdPre {
		creater_, 
		creater, createrSuperDept, createrDeptMain, createrDeptOther, 
		pre, preSuperDept, preDeptMain, preDeptOther;
	}
	
	protected enum RelativeId {
		Member, LeaderMain, LeaderOther;
	}
	
	protected enum Path {
		todo, doing, done, sent, unsent;
	}
	
	protected enum Type {
		userTask, Gateway;
	}
	
	protected enum Property {
		type, conditionText;
	}
	
	protected enum Flag {
		yes, no, flag;
	}
	
	//工具相关↓---------------------------------------------
	protected String toUpperCase(String arg){
		char[] charArray = arg.toLowerCase().toCharArray();
	    charArray[0] -= 32;
		return String.valueOf(charArray);
	}
	//工具相关↑---------------------------------------------
	//逻辑相关↓---------------------------------------------
	public List<User> getUserList(T entity) {
		String settingValue = getTaskSettingVersionByAct(entity.getAct()).getSettingValue();
		settingValue = StringEscapeUtils.unescapeHtml(settingValue);
		@SuppressWarnings("unchecked")
		List<Map<String, String>> settingValueList = new Gson().fromJson(settingValue, List.class);
		List<String> officeIdList = Lists.newArrayList();
		List<String> postIdList = Lists.newArrayList();
		List<String> levelIdList = Lists.newArrayList();
		List<String> roleIdList = Lists.newArrayList();
		List<String> userIdList = Lists.newArrayList();
		List<String> relativeIdList = Lists.newArrayList();
		for (Map<String, String> map : settingValueList) {
			if (map.get(SettingValue.type.name()).equalsIgnoreCase(SettingValue.office.name())) {
				officeIdList.add(map.get(SettingValue.id.name()));
			} else if (map.get(SettingValue.type.name()).equalsIgnoreCase(SettingValue.level.name())) {
				levelIdList.add(map.get(SettingValue.id.name()));
			} else if (map.get(SettingValue.type.name()).equalsIgnoreCase(SettingValue.post.name())) {
				postIdList.add(map.get(SettingValue.id.name()));
			} else if (map.get(SettingValue.type.name()).equalsIgnoreCase(SettingValue.role.name())) {
				roleIdList.add(map.get(SettingValue.id.name()));
			} else if (map.get(SettingValue.type.name()).equalsIgnoreCase(SettingValue.user.name())) {
				userIdList.add(map.get(SettingValue.id.name()));
			} else if (map.get(SettingValue.type.name()).equalsIgnoreCase(SettingValue.relative.name())) {
				relativeIdList.add(map.get(SettingValue.id.name()));
			}
		}
		List<User> userListTemp = Lists.newArrayList();
		Map<String, User> userMap = Maps.newHashMap();
		if (officeIdList.size() > 0) {
			userListTemp = systemService.findUserListByOfficeIdList(officeIdList);
			for (User user : userListTemp) {
				userMap.put(user.getId(), user);
			}
		}
		if (levelIdList.size() > 0) {
			userListTemp = systemService.findUserListByLevelIdList(levelIdList);
			for (User user : userListTemp) {
				userMap.put(user.getId(), user);
			}
		}
		if (postIdList.size() > 0) {
			userListTemp = systemService.findUserListByPostIdList(postIdList);
			for (User user : userListTemp) {
				userMap.put(user.getId(), user);
			}
		}
		if (roleIdList.size() > 0) {
			userListTemp = systemService.findUserListByRoleIdList(roleIdList);
			for (User user : userListTemp) {
				userMap.put(user.getId(), user);
			}
		}
		if (userIdList.size() > 0) {
			userListTemp = systemService.findUserListByUserIdList(userIdList);
			for (User user : userListTemp) {
				userMap.put(user.getId(), user);
			}
		}
		if (relativeIdList.size() > 0) {
			for (String relativeId : relativeIdList) {
				//简易版，只遍历creater_
				if (RelativeIdPre.creater_.name().equalsIgnoreCase(relativeId)) {
					HistoricVariableInstance historicVariableInstance = historyService
							.createHistoricVariableInstanceQuery()
							.processInstanceId(entity.getProcInsId())
							.variableName("applyUserId").singleResult();
					User user = systemService.getUserByLoginName(historicVariableInstance.getValue().toString());
					userMap.put(user.getId(), user);
				} else {
					/*	双层遍历，排列组合所有情况，查询对应userId，此注释不可删除
					for (RelativeIdPre relativeIdPre : RelativeIdPre.values()) {
						if (relativeIdPre.name().equalsIgnoreCase(relativeId)) { 
							//do ..
						}
					}*/
				}
			}
		}
		List<User> userList = Lists.newArrayList();
		for (Entry<String, User> entry : userMap.entrySet()) {
			userList.add(entry.getValue());
		}
		return userList;
	}
	
	public List<User> getUserListFirstTime(T entity) {
		//未启动，读取默认值
		if (entity.getIsNewRecord() || StringUtils.isEmpty(entity.getProcInsId())) {
			ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().processDefinitionKey(entity.getAct().getProcDefKey()).latestVersion().singleResult();
			entity.getAct().setProcDefId(procDef.getId());
			entity.getAct().setTaskDefKey(procDef.getDescription());
			return getUserList(entity);
		} else {
			return null;
		}
	}
	
	public List<User> getUserListOtherTime(T entity) {
		ActivityImpl nextTaskDefinition = getNextTaskDefinition(entity);
		//启动，读取流程
		if (!entity.getIsNewRecord() && StringUtils.isNotEmpty(entity.getProcInsId()) 
				&& nextTaskDefinition != null) {
			//设置下一节点信息
			entity.getAct().setProcDefId(getProcIns(entity.getProcInsId()).getProcessDefinitionId());
			entity.getAct().setTaskDefKey(nextTaskDefinition.getId());
			return getUserList(entity);
		} else {
			return null;
		}
	}
	
	public TaskSettingVersion getTaskSettingVersionByAct(Act act) {
		return taskSettingVersionService.getTaskSettingVersionByAct(act);
	}
	
	public boolean saveOnly(T entity) {
		return (entity.getAct().getFlag().equalsIgnoreCase(Button.暂存.name()) 
				&& StringUtils.isEmpty(entity.getAct().getProcInsId()));
	}
	
	public boolean saveAndStart(T entity) {
		return (entity.getAct().getFlag().equalsIgnoreCase(Button.提交.name()) 
				&& StringUtils.isEmpty(entity.getAct().getProcInsId()));
	}
	
	public boolean claimOnly(T entity) {
		return (entity.getAct().getFlag().equalsIgnoreCase(Button.暂存.name()) 
				&& StringUtils.isNotEmpty(entity.getAct().getProcInsId()));
	}

	public boolean claimAndComplete(T entity) {
		boolean flag = false;
		for (UserButton userButton : UserButton.values()) {
			if (userButton.name().equalsIgnoreCase(entity.getAct().getFlag())) {
				flag = true;
			}
		}
		return (flag && StringUtils.isNotEmpty(entity.getAct().getProcInsId()));
	}
	
	/**
	 * 获取下一节点代理人
	 * @param entity
	 * @return
	 */
	public String getNextTaskDefinitionAgent(T entity) {
		String nextTaskAgent = "";
		ActivityImpl nextTaskDefinition = getNextTaskDefinition(entity);
		if (nextTaskDefinition != null) {
			ActivityBehavior activityBehavior = nextTaskDefinition.getActivityBehavior();
			if (activityBehavior instanceof UserTaskActivityBehavior) {
				Set<Expression> candidateUserIds = (((UserTaskActivityBehavior) nextTaskDefinition
						.getActivityBehavior()).getTaskDefinition())
						.getCandidateUserIdExpressions();
				if (candidateUserIds.toArray().length > 0) {
					nextTaskAgent = String.valueOf(candidateUserIds.toArray()[0]);
				}
			} else if (activityBehavior instanceof ParallelMultiInstanceBehavior) {
				String assignee = ((ParallelMultiInstanceBehavior) nextTaskDefinition
						.getActivityBehavior()).getCollectionExpression()
						.getExpressionText();
				nextTaskAgent = assignee;
			}
			if (!StringUtils.isEmpty(nextTaskAgent) && nextTaskAgent.lastIndexOf("{") != -1
					&& nextTaskAgent.lastIndexOf("}") != -1) {
				nextTaskAgent = nextTaskAgent.substring(nextTaskAgent.lastIndexOf("{") + 1, nextTaskAgent.lastIndexOf("}"));
			}
		}
		return nextTaskAgent;
	}
	
	/**
	 * 获取下一节点任务
	 * @param entity
	 * @return
	 */
	public ActivityImpl getNextTaskDefinition(T entity){
		for (PassButton passButton : PassButton.values()) {
			if (passButton.name().equalsIgnoreCase(entity.getAct().getFlag())) {
				entity.getAct().setFlag(Flag.yes.name());
			}
		}
		for (FailButton failButton : FailButton.values()) {
			if (failButton.name().equalsIgnoreCase(entity.getAct().getFlag())) {
				entity.getAct().setFlag(Flag.no.name());
			}
		}
		
		List<ActivityImpl> activities = ((ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(getProcIns(entity.getProcInsId())
				.getProcessDefinitionId())).getActivities();
		String activityId = ((ExecutionEntity) runtimeService
				.createProcessInstanceQuery().processInstanceId(entity.getProcInsId())
				.singleResult()).getActivityId();
		for (ActivityImpl activityImpl : activities) {
			if (activityId.equals(activityImpl.getId())) {
				ActivityImpl nextActivityImpl = nextTaskDefinition(activityImpl, activityId, entity.getAct().getFlag(), entity.getProcInsId());
				return nextActivityImpl;
			}
		}
		return null;
	}
	

	/**
	 * 获取下一节点
	 * @param activityImpl
	 * @param activityId
	 * @param flag
	 * @param procInsId
	 * @return
	 */
	protected ActivityImpl nextTaskDefinition(ActivityImpl activityImpl, String activityId, String flag, String procInsId) {
		if (Type.userTask.name().equals(activityImpl.getProperty(Property.type.name())) && !activityId.equals(activityImpl.getId())) {
			return activityImpl;
		} else if (String.valueOf(activityImpl.getProperty(Property.type.name())).indexOf(Type.userTask.name()) != -1) {
            List<PvmTransition> outgoingTransitions = activityImpl.getOutgoingTransitions();
            for (PvmTransition pvmTransition : outgoingTransitions) {
            	PvmActivity destination = pvmTransition.getDestination();
            	if (String.valueOf(destination.getProperty(Property.type.name())).indexOf(Type.userTask.name()) != -1) {
            		ActivityImpl nextActivityImpl = (ActivityImpl) destination;
            		return nextTaskDefinition(nextActivityImpl, activityId, flag, procInsId);
            	} else if (String.valueOf(destination.getProperty(Property.type.name())).indexOf(Type.Gateway.name()) != -1) {
            		List<PvmTransition> nextOutgoingTransitions = destination.getOutgoingTransitions();
            		if (nextOutgoingTransitions.size() == 1) {
            			ActivityImpl nextActivityImpl = (ActivityImpl)nextOutgoingTransitions.get(0).getDestination();
            			return nextTaskDefinition(nextActivityImpl, activityId, flag, procInsId);
            		} else if (nextOutgoingTransitions.size() > 1) {
            			for (PvmTransition nextPvmTransition : nextOutgoingTransitions) {
            				Object property = nextPvmTransition.getProperty(Property.conditionText.name());
            				if (String.valueOf(property).indexOf(flag) != -1) {
            					ActivityImpl nextActivityImpl = (ActivityImpl) nextPvmTransition.getDestination();
                                return nextTaskDefinition(nextActivityImpl, activityId, flag, procInsId);
                            }
						}
            		}
            	}
			}
		}
		return null;
	}
	
	/**
	 * 获取procInsIds集合
	 * @param path
	 * @return
	 */
	public List<String> getProcInsIds(String path) {
		Map<String, String> procInsIdsMap = Maps.newHashMap();
		if (Path.todo.name().equalsIgnoreCase(path)) {
			List<Task> taskList = taskService.createTaskQuery()
					.taskCandidateUser(UserUtils.getUser().getLoginName())
					.active().includeProcessVariables().orderByTaskCreateTime()
					.desc().list();
			for (Task task : taskList) {
				procInsIdsMap.put(task.getProcessInstanceId(), task.getProcessInstanceId());
			}
		} else if (Path.doing.name().equalsIgnoreCase(path)) {
			List<Task> taskList = taskService.createTaskQuery()
					.taskAssignee(UserUtils.getUser().getLoginName())
					.active().includeProcessVariables().orderByTaskCreateTime()
					.desc().list();
			for (Task task : taskList) {
				procInsIdsMap.put(task.getProcessInstanceId(), task.getProcessInstanceId());
			}
		} else if (Path.done.name().equalsIgnoreCase(path)) {
			List<HistoricTaskInstance> taskList = historyService
					.createHistoricTaskInstanceQuery()
					.taskAssignee(UserUtils.getUser().getLoginName())
					.finished().includeProcessVariables()
					.orderByHistoricTaskInstanceEndTime().desc().list();
			for (HistoricTaskInstance historicTaskInstance : taskList) {
				procInsIdsMap.put(historicTaskInstance.getProcessInstanceId(), historicTaskInstance.getProcessInstanceId());
			}
		} else if (Path.sent.name().equalsIgnoreCase(path)) {
			List<HistoricProcessInstance> list = historyService
					.createHistoricProcessInstanceQuery()
					.startedBy(UserUtils.getUser().getLoginName())
					.includeProcessVariables()
					.orderByProcessInstanceStartTime().desc().list();
			for (HistoricProcessInstance historicProcessInstance : list) {
				procInsIdsMap.put(historicProcessInstance.getId(), historicProcessInstance.getId());
			}
		}
		List<String> procInsIds = Lists.newArrayList();
		for (Entry<String, String> entry : procInsIdsMap.entrySet()) {
			procInsIds.add(entry.getValue());
		}
		return procInsIds;
	}
	
	/**
	 * 设置表单ACT相关信息
	 * @param edocSend
	 */
	public void setAct(T entity) {
		if (StringUtils.isNotEmpty(entity.getProcInsId())) {
			Task task = getTaskByProcInsId(entity.getProcInsId());
			ProcessInstance procIns = getProcIns(entity.getProcInsId());
			if (task != null && procIns != null) {
				entity.getAct().setTaskId(task.getId());
				entity.getAct().setTaskName(task.getName());
				entity.getAct().setTaskDefKey(task.getTaskDefinitionKey());
				entity.getAct().setProcInsId(entity.getProcInsId());
				entity.getAct().setProcDefId(procIns.getProcessDefinitionId());
			} else {
				List<HistoricTaskInstance> taskList = historyService
						.createHistoricTaskInstanceQuery()
						.processInstanceId(entity.getProcInsId())
						.taskAssignee(UserUtils.getUser().getLoginName())
						.finished().includeProcessVariables()
						.orderByHistoricTaskInstanceEndTime().desc().list();
				HistoricTaskInstance historicTaskInstance = taskList.get(0);
				entity.getAct().setTaskId(historicTaskInstance.getId());
				entity.getAct().setTaskName(historicTaskInstance.getName());
				entity.getAct().setTaskDefKey(historicTaskInstance.getTaskDefinitionKey());
				entity.getAct().setProcInsId(entity.getProcInsId());
				entity.getAct().setProcDefId(historicTaskInstance.getProcessDefinitionId());
			}
		}
	}
	
	/**
	 * 设置代理人
	 * @param edocSend
	 */
	public void setAssignee(T entity) {
		entity.getAct().setAssignee(UserUtils.getUser().getLoginName());
	}
	
	/**
	 * 设置流程变量
	 * @param edocSend
	 * @param vars
	 */
	public void setCondition(T entity, Map<String, Object> vars) {
		vars.put(Flag.flag.name(), entity.getAct().getFlag());
	}
	
	/**
	 * 是否未发
	 * @param path
	 * @return
	 */
	public boolean isUnsent(String path) {
		return Path.unsent.name().equalsIgnoreCase(path);
	}
	//逻辑相关↑---------------------------------------------
	//ACT相关↓ 引用actTaskService全部重构---------------------
	
	/**
	 * 启动流程
	 * @param procDefKey流程定义KEY
	 * @param businessTable业务表表名
	 * @param businessId业务表表名
	 * @return 流程实例ID
	 */
	public String startProcess(String procDefKey, String businessTable, String businessId){
		return actTaskService.startProcess(procDefKey, businessTable, businessId);
	}
	
	/**
	 * 启动流程
	 * @param procDefKey流程定义KEY
	 * @param businessTable业务表表名
	 * @param businessId业务表表名
	 * @param title流程标题，显示在待办任务标题
	 * @return 流程实例ID
	 */
	public String startProcess(String procDefKey, String businessTable, String businessId, String title){
		return actTaskService.startProcess(procDefKey, businessTable, businessId, title);
	}
	
	/**
	 * 启动流程
	 * @param procDefKey
	 * @param businessTable
	 * @param businessId
	 * @param title流程标题，显示在待办任务标题
	 * @param vars流程变量
	 * @return 流程实例ID
	 */
	public String startProcess(String procDefKey, String businessTable, String businessId, String title, Map<String, Object> vars){
		return actTaskService.startProcess(procDefKey, businessTable, businessId, title, vars);
	}
	
	/**
	 * 完成任务
	 * @param taskId
	 * @param procInsId
	 * @param comment
	 * @param vars
	 */
	public void complete(String taskId, String procInsId, String comment, Map<String, Object> vars){
		actTaskService.complete(taskId, procInsId, comment, vars);
	}
	
	/**
	 * 完成任务
	 * @param taskId
	 * @param procInsId
	 * @param comment
	 * @param title
	 * @param vars
	 */
	public void complete(String taskId, String procInsId, String comment, String title, Map<String, Object> vars){
		actTaskService.complete(taskId, procInsId, comment, title, vars);
	}
	
	/**
	 * 获取正在运行的流程实例对象
	 * @param procInsId 流程实例ID
	 * @return 流程实例
	 */
	public ProcessInstance getProcIns(String procInsId){
		return actTaskService.getProcIns(procInsId);
	}
	
	/**
	 * 
	 * @param procInsId 流程实例ID
	 * @return 任务
	 */
	public Task getTaskByProcInsId(String procInsId){
		return actTaskService.getTaskByProcInsId(procInsId);
	}
	
	/**
	 * 签收任务
	 * @param edocSend
	 */
	public void claim(String procInsId) {
		actTaskService.claim(getTaskByProcInsId(procInsId).getId(), UserUtils.getUser().getLoginName());
	}
	
	//ACT相关↑---------------------------------------------
	
	
	//已优化  ↑
	//======================================================================================
	//原      ↓
	
	
	
	
	/**
	 * 获取当前节点页面
	 */
	/*public String getTaskView(T entity){
		taskSettingService.findList(entity.getAct().getProcDefId());
		
		
		//判断流程是否已完成 待修改
		if (!StringUtils.isBlank(entity.getAct().getTaskId())
				&& entity.getAct().isFinishTask()) {
			return Suffix.FinishForm.name();
		} else {
			String taskDefKey = entity.getAct().getTaskDefKey();
			if (!StringUtils.isBlank(taskDefKey)) {
				String nodePermission = taskDefKey.split("_")[0];
				for (Permission permission : Permission.values()) {
					if (permission.name().equalsIgnoreCase(nodePermission)) {
						return toUpperCase(nodePermission) + Suffix.Form;
					}
				}
			}
			return Suffix.StartForm.name();
		}
	}*/

/*	protected enum Permission {
		ARCHIVE, AUDIT, KNOW, MODIFY, SIGN;
	}*/
	
	/*protected enum Suffix {
		Form, StartForm, FinishForm,
		Process;
	}*/
	
/*	protected enum Assignee {
		applyUser, defaultAgent, List;
	}*/
	
/*	protected enum Flag {
		yes, no;
	}*/
	
	

	/**
	 * 获取当前进程
	 */
//	public String getProcess(String prefix) {
//		return prefix + Suffix.Process;
//	}

	/**
	 * 完成节点前置
	 */
	/*public void preComplete(T entity, Map<String, Object> vars) {
		String comment = entity.getAct().getComment();
		if (Flag.yes.name().equalsIgnoreCase(entity.getAct().getFlag())) {
			entity.getAct().setComment(" [ " + Button.同意.name() + " ] " + (comment == null ? "" : comment));
			vars.put(entity.getAct().getTaskDefKey(), entity.getAct().getFlag());
		} else if (Flag.no.name().equalsIgnoreCase(entity.getAct().getFlag())) {
			entity.getAct().setComment(" [ " + Button.退回.name() + " ] " + (comment == null ? "" : comment));
			vars.put(entity.getAct().getTaskDefKey(), entity.getAct().getFlag());
		}
	}
*/
	/**
	 * 是否暂存（待办>>在办）
	 */
	/*public boolean isClaim(T entity) {
		if (Flag.claim.name().equalsIgnoreCase(entity.getAct().getFlag())) {
			return true;
		}
		return false;
	}*/
}
