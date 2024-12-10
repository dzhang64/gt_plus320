package com.gt_plus.common.service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.gt_plus.common.persistence.ActEntity;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.utils.Encodes;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.WebUser;
import com.gt_plus.modules.act.entity.Act;
import com.gt_plus.modules.act.service.ActTaskService;
import com.gt_plus.modules.oa.entity.setting.TaskPermission;
import com.gt_plus.modules.oa.entity.setting.TaskSettingVersion;
import com.gt_plus.modules.projectinfo.entity.ProjectInfo;
import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.modules.oa.service.setting.TaskSettingVersionService;
import com.gt_plus.modules.sys.entity.Organization;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.service.OfficeService;
import com.gt_plus.modules.sys.service.OrganizationService;
import com.gt_plus.modules.sys.service.SystemService;
import com.gt_plus.modules.sys.utils.UserUtils;

public class ActService<D extends CrudDao<T>, T extends ActEntity<T>> extends CrudService<D, T>  {

	//------------------------------ActivitiService--Begin------------------------------
	/**
	 * 流程引擎
	 */
	/*@Autowired*/
	/*private ProcessEngine processEngine;*/
	/**
	 * 资源
	 */
	@Autowired
	private RepositoryService repositoryService;
	/**
	 * 运行
	 */
	@Autowired
	private RuntimeService runtimeService;
	/**
	 * 任务
	 */
	@Autowired
	private TaskService taskService;
	/**
	 * 历史
	 */
	@Autowired
	private HistoryService historyService;
	/**
	 * 表单
	 */
	/*@Autowired*/
	/*private FormService formService;*/
	/**
	 * 身份
	 */
	/*@Autowired*/
	/*private IdentityService identityService;*/
	/**
	 * 管理
	 */
	/*@Autowired*/
	/*private ManagementService managementService;*/
	//------------------------------ActivitiService--End--------------------------------
	
	//------------------------------Enum--Begin-----------------------------------------

	protected enum ButtonType {
		save, 
		saveAndStart, 
		saveAndClaim, 
		saveAndComplete, 
		saveAndTerminate, 
		saveAndReject, 
		saveAndRejectTo;
	}
	
	//------------------------------Enum--End-------------------------------------------
	
	//------------------------------Method--Begin---------------------------------------
	
	
	
	//------------------------------Method--End-----------------------------------------
	
//2017-12-22--before--↓--↓--↓--
	
	public static final String 	PATH_QUERY = "query"; //用于公文查询标识
	//@Autowired
	//private ProcessEngineFactoryBean processEngine;
	
	@Autowired
	private SystemService systemService;
	
	//@Autowired
	//private TaskSettingService taskSettingService;
	
	@Autowired
	private TaskSettingVersionService taskSettingVersionService;
	
	@Autowired
	private ActTaskService actTaskService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private OrganizationService organizationService;
	
	protected enum Button {
		暂存, 提交;
	}
	
	protected enum UserButton {
		提交, 同意, 知会, 归档, 重申, 销毁, 会签, 确定, 签发;
	}
	
	protected enum PassButton {
		提交, 同意, 知会, 归档, 重申, 会签, 签发;
	}
	
	protected enum FailButton {
		退回, 销毁;
	}
	
	protected enum SettingValue {
		id, type, name,
		office, post, level, role, user, relative, org;
	}
	
	protected enum WebUserType {
		projectNo;
	}
	
	protected enum RelativeIdPre {
		creater_, 
		creater, createrSuperDept, createrDeptMain, createrDeptOther, createrOrg, createrOrgMain,
		pre, preSuperDept, preDeptMain, preDeptOther, preOrg, preOrgMain;
	}
	
	protected enum RelativeId {
		DeptMember, DeptLeaderMain, DeptLeaderOther,
		OrgMember, OrgLeaderMain;
	}
	
	protected enum Path {
		todo, doing, done, sent, unsent, todoAndDoing, todoAndDoingAndDone;
	}
	
	protected enum Type {
		userTask, Gateway;
	}
	
	protected enum Property {
		type, conditionText;
	}
	
	protected enum Flag {
		yes, no, other,
		flag;
	}
	
	protected enum RuleArgs {
		key, value, 
		flag, form, content;
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
		List<String> orgIdList = Lists.newArrayList();
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
			} else if (map.get(SettingValue.type.name()).equalsIgnoreCase(SettingValue.org.name())) {
				orgIdList.add(map.get(SettingValue.id.name()));
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
		if (orgIdList.size() > 0) {
			userListTemp = systemService.findUserListByOrgIdList(orgIdList);
			for (User user : userListTemp) {
				userMap.put(user.getId(), user);
			}
		}
		if (relativeIdList.size() > 0) {
			User applyUser = null;
			User currentUser = UserUtils.getUser();
			if (entity != null && false == StringUtils.isEmpty(entity.getProcInsId())) {
				HistoricVariableInstance historicVariableInstance = historyService
						.createHistoricVariableInstanceQuery()
						.processInstanceId(entity.getProcInsId())
						.variableName("applyUserId").singleResult();
				String loginName = historicVariableInstance.getValue().toString();
				if (loginName.indexOf("[") != -1) {
					applyUser = systemService.getUserByLoginName(loginName.substring(1, loginName.lastIndexOf("]")));
				} else {
					applyUser = systemService.getUserByLoginName(loginName);
				}
			} else {
				applyUser = currentUser;
			}
			
			List<Office> officeList = Lists.newArrayList();
			List<Organization> orgList = Lists.newArrayList();
			for (String relativeId : relativeIdList) {
				//遍历前缀
				if (relativeId.equalsIgnoreCase(RelativeIdPre.creater_.name())) {
					//发起者
					userMap.put(applyUser.getId(), applyUser);
					
				} else if (relativeId.indexOf(RelativeIdPre.creater.name()) != -1) {
					//发起者所在部门
					//userMap.put(applyUser.getId(), applyUser);
					officeList.add(applyUser.getOffice());
				
				} else if (relativeId.indexOf(RelativeIdPre.createrSuperDept.name()) != -1) {
					//查询applyUser上级部门
					String code = applyUser.getOffice().getCode();
					Office office = officeService.findUniqueByProperty("ownerCode", code.substring(0, code.length() - 3));
					officeList.add(office);
					
				} else if (relativeId.indexOf(RelativeIdPre.createrDeptMain.name()) != -1) {
					//查询主负责人是applyUser的部门
					Office office = new Office();
					office.getSqlMap().put("dsf", " AND a.primary_person = '" + applyUser.getId() + "' ");
					officeList = officeService.findList(office);
				
				} else if (relativeId.indexOf(RelativeIdPre.createrDeptOther.name()) != -1) {
					//查询副负责人是applyUser的部门
					Office office = new Office();
					office.getSqlMap().put("dsf", " AND a.deputy_person = '" + applyUser.getId() + "' ");
					officeList = officeService.findList(office);
					
				} else if (relativeId.indexOf(RelativeIdPre.preSuperDept.name()) != -1) {
					//查询currentUser上级部门
					String code = currentUser.getOffice().getCode();
					Office office = officeService.findUniqueByProperty("ownerCode", code.substring(0, code.length() - 3));
					officeList.add(office);
						
				} else if (relativeId.indexOf(RelativeIdPre.preDeptMain.name()) != -1) {
					//查询主负责人是currentUser的部门
					Office office = new Office();
					office.getSqlMap().put("dsf", " AND a.primary_person = '" + currentUser.getId() + "' ");
					officeList = officeService.findList(office);
				
				} else if (relativeId.indexOf(RelativeIdPre.preDeptOther.name()) != -1) {
					//查询副负责人是currentUser的部门
					Office office = new Office();
					office.getSqlMap().put("dsf", " AND a.deputy_person = '" + currentUser.getId() + "' ");
					officeList = officeService.findList(office);
					
				}
				//遍历后缀
				if (relativeId.indexOf(RelativeId.DeptMember.name()) != -1) {
					officeIdList = Lists.newArrayList();
					for (Office office : officeList) {
						officeIdList.add(office.getId());
					}
					//查询部门成员
					if (officeIdList.size() > 0) {
						List<User> userList = systemService.findUserListByOfficeIdList(officeIdList);
						for (User user : userList) {
							userMap.put(user.getId(), user);
						}
					}
					
				} else if (relativeId.indexOf(RelativeId.DeptLeaderMain.name()) != -1) {
					//查询部门主负责人
					userIdList = Lists.newArrayList();
					for (Office office : officeList) {
						userIdList.add(office.getPrimaryPerson().getId());
					}
					if (userIdList.size() > 0) {
						List<User> userList = systemService.findUserListByUserIdList(userIdList);
						for (User user : userList) {
							userMap.put(user.getId(), user);
						}
					}
					
				} else if (relativeId.indexOf(RelativeId.DeptLeaderOther.name()) != -1) {
					//查询部门副负责人
					userIdList = Lists.newArrayList();
					for (Office office : officeList) {
						userIdList.add(office.getDeputyPerson().getId());
					}
					if (userIdList.size() > 0) {
						List<User> userList = systemService.findUserListByUserIdList(userIdList);
						for (User user : userList) {
							userMap.put(user.getId(), user);
						}
					}
					
				}
				
				//遍历组织前缀（组织无法与其他遍历抽象在一起）
				if (relativeId.indexOf(RelativeIdPre.createrOrg.name()) != -1) {
					Organization organization = new Organization();
					organization.getSqlMap().put("dsf", " AND sou.user_id = '" + applyUser.getId() + "' ");
					orgList = organizationService.findListByUser(organization);
					Map<String, Organization> orgListTempMap = Maps.newHashMap();
					for (Organization org : orgList) {
						orgListTempMap.put(org.getId(), org);
					}
					orgList = Lists.newArrayList();
					for (Entry<String, Organization> entry : orgListTempMap.entrySet()) {
						orgList.add(entry.getValue());
					}
					
				} else if (relativeId.indexOf(RelativeIdPre.createrOrgMain.name()) != -1) {
					Organization organization = new Organization();
					organization.getSqlMap().put("dsf", " AND a.primaryperson_id = '" + applyUser.getId() + "' ");
					orgList = organizationService.findListByUser(organization);
					Map<String, Organization> orgListTempMap = Maps.newHashMap();
					for (Organization org : orgList) {
						orgListTempMap.put(org.getId(), org);
					}
					orgList = Lists.newArrayList();
					for (Entry<String, Organization> entry : orgListTempMap.entrySet()) {
						orgList.add(entry.getValue());
					}
					
				} else if (relativeId.indexOf(RelativeIdPre.preOrg.name()) != -1) {
					Organization organization = new Organization();
					organization.getSqlMap().put("dsf", " AND sou.user_id = '" + currentUser.getId() + "' ");
					orgList = organizationService.findListByUser(organization);
					Map<String, Organization> orgListTempMap = Maps.newHashMap();
					for (Organization org : orgList) {
						orgListTempMap.put(org.getId(), org);
					}
					orgList = Lists.newArrayList();
					for (Entry<String, Organization> entry : orgListTempMap.entrySet()) {
						orgList.add(entry.getValue());
					}
					
				} else if (relativeId.indexOf(RelativeIdPre.preOrgMain.name()) != -1) {
					Organization organization = new Organization();
					organization.getSqlMap().put("dsf", " AND a.primaryperson_id = '" + currentUser.getId() + "' ");
					orgList = organizationService.findListByUser(organization);
					Map<String, Organization> orgListTempMap = Maps.newHashMap();
					for (Organization org : orgList) {
						orgListTempMap.put(org.getId(), org);
					}
					orgList = Lists.newArrayList();
					for (Entry<String, Organization> entry : orgListTempMap.entrySet()) {
						orgList.add(entry.getValue());
					}
					
				}
				
				//遍历组织后缀（组织无法与其他遍历抽象在一起）
				
				if (relativeId.indexOf(RelativeId.OrgMember.name()) != -1) {
					orgIdList = Lists.newArrayList();
					for (Organization org : orgList) {
						orgIdList.add(org.getId());
					}
					if (orgIdList.size() > 0) {
						List<User> userList = systemService.findUserListByOrgIdList(orgIdList);
						for (User user : userList) {
							userMap.put(user.getId(), user);
						}
					}
					
				} else if (relativeId.indexOf(RelativeId.OrgLeaderMain.name()) != -1) {
					userIdList = Lists.newArrayList();
					for (Organization org : orgList) {
						userIdList.add(org.getPrimaryPerson().getId());
					}
					if (userIdList.size() > 0) {
						List<User> userList = systemService.findUserListByUserIdList(userIdList);
						for (User user : userList) {
							userMap.put(user.getId(), user);
						}
					}
					
				}
				
			}
		}
		List<User> userList = Lists.newArrayList();
		for (Entry<String, User> entry : userMap.entrySet()) {
			userList.add(entry.getValue());
		}
		return userList;
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(T entity) {
		//未启动，读取默认值
		if (entity.getIsNewRecord() || StringUtils.isEmpty(entity.getProcInsId())) {
			ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().processDefinitionKey(entity.getAct().getProcDefKey()).latestVersion().singleResult();
			entity.getAct().setProcDefId(procDef.getId());
			entity.getAct().setTaskDefKey(procDef.getDescription().split(",")[0]);
			List<User> userList = getUserList(entity);
			LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
			map.put("userList", userList);
			String[] description = procDef.getDescription().split(",");
			if (description.length > 1) {
				//map.put("type", procDef.getDescription().split(",")[1]);//填写multi
				//任意填写都是multi
				map.put("type", "multi");
			} else {
				map.put("type", "single");
			}
			entity.getAct().setProcDefId(null);
			entity.getAct().setTaskDefKey(null);
			return map;
		} else {
			return null;
		}
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(T entity) {
		LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
		/*this.setRuleArgs(entity);*/
		ActivityImpl nextTaskDefinition = getNextTaskDefinition(entity);
		//启动，读取流程
		if (!entity.getIsNewRecord() && StringUtils.isNotEmpty(entity.getProcInsId()) 
				&& nextTaskDefinition != null) {
			//设置下一节点信息
			entity.getAct().setProcDefId(getProcIns(entity.getProcInsId()).getProcessDefinitionId());
			entity.getAct().setTaskDefKey(nextTaskDefinition.getId());
			map.put("userList", getUserList(entity));
			if (nextTaskDefinition.getActivityBehavior() instanceof SequentialMultiInstanceBehavior
					|| nextTaskDefinition.getActivityBehavior() instanceof ParallelMultiInstanceBehavior) {
				map.put("type", "multi");
			} else {
				map.put("type", "single");
			}
			return map;
		} else {
			return null;
		}
	}
	
	   //获取WEB页面用户
		public LinkedHashMap<String, Object> getWebUserList(String[] ids) {
			LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
			List<User> userList = Lists.newArrayList();
			LinkedHashMap<String, User> userMap = Maps.newLinkedHashMap();
			//遍历用户规则变量获取用户
			for (String id : ids) 
			{
				WebUser webUser = new Gson().fromJson(id, WebUser.class);
				//如果设置的值是项目编号
				if(webUser.getType().equalsIgnoreCase(WebUserType.projectNo.name()))
				{
					ProjectInfo projectInfo =new ProjectInfo();
					projectInfo.setProjectNum(webUser.getValue());
					projectInfo = systemService.getByProjectNo(projectInfo);
					if(webUser.getResult().equals("1")){ //获取项目经理
						User user = new User();
						user.setId(projectInfo.getProjectmanager().getId());
						user = systemService.getUser(user.getId());
						userMap.put(user.getId(), user);
						
					}
					if(webUser.getResult().equals("2")){ //获取部门经理
						User user =new User();
						user.setId(projectInfo.getOffice().getPrimaryPerson().getId());
						user = systemService.getUser(user.getId());
						userMap.put(user.getId(), user);	
					}
				}	
			}
			
			for (Entry<String, User> entry : userMap.entrySet()) {
				userList.add(entry.getValue());
			}
			
			map.put("userList", userList);
		    map.put("type", "single");
		    
		    return map;
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
	
	public boolean claimAndBack(T entity) {
		boolean flag = false;
		for (FailButton failButton : FailButton.values()) {
			if (failButton.name().equalsIgnoreCase(entity.getAct().getFlag())) {
				flag = true;
			}
		}
		//return (flag && StringUtils.isNotEmpty(entity.getAct().getProcInsId()));
		return flag;
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
			} else if (activityBehavior instanceof SequentialMultiInstanceBehavior) {
				String assignee = ((SequentialMultiInstanceBehavior) nextTaskDefinition
						.getActivityBehavior()).getCollectionExpression()
						.getExpressionText();
				nextTaskAgent = assignee;
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
		if (!entity.getAct().getFlag().equalsIgnoreCase(Flag.yes.name())
				&& !entity.getAct().getFlag().equalsIgnoreCase(Flag.no.name())) {
		}
		RuleArgs[] values = RuleArgs.values();
		boolean flagDesignByRuleArgs = false;
		for (RuleArgs ruleArgs : values) {
			if (entity.getAct().getFlag().equalsIgnoreCase(ruleArgs.name())) {
				flagDesignByRuleArgs = true;
				break;
			}
		}
		if (!flagDesignByRuleArgs) {
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
		}
		List<ActivityImpl> activities = ((ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(getProcIns(entity.getProcInsId())
				.getProcessDefinitionId())).getActivities();
		String activityId = ((ExecutionEntity) runtimeService
				.createProcessInstanceQuery().processInstanceId(entity.getProcInsId())
				.singleResult()).getActivityId();
		if (StringUtils.isEmpty(activityId)) {
			activityId = getTaskByProcInsId(entity.getProcInsId()).getTaskDefinitionKey();
		}
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
	
	protected void taskRollBack(T entity) {
		try {
			String taskId = getTaskByProcInsId(entity.getProcInsId()).getId();
			Map<String, Object> variables;
			// 取得当前任务
			HistoricTaskInstance currTask = historyService
					.createHistoricTaskInstanceQuery().taskId(taskId)
					.singleResult();
			// 取得流程实例
			ProcessInstance instance = runtimeService
					.createProcessInstanceQuery()
					.processInstanceId(currTask.getProcessInstanceId())
					.singleResult();
			if (instance == null) {
				// 流程结束
			}
			variables = instance.getProcessVariables();
			// 取得流程定义
			ProcessDefinitionEntity definition = (ProcessDefinitionEntity) (repositoryService
					.getProcessDefinition(currTask.getProcessDefinitionId()));
			if (definition == null) {
				// 流程定义未找到
				return;
			}
			// 取得上一步活动
			ActivityImpl currActivity = ((ProcessDefinitionImpl) definition)
					.findActivity(currTask.getTaskDefinitionKey());
			List<PvmTransition> nextTransitionList = currActivity
					.getIncomingTransitions();
			// 清除当前活动的出口
			List<PvmTransition> oriPvmTransitionList = Lists.newArrayList();
			List<PvmTransition> pvmTransitionList = currActivity
					.getOutgoingTransitions();
			for (PvmTransition pvmTransition : pvmTransitionList) {
				oriPvmTransitionList.add(pvmTransition);
			}
			pvmTransitionList.clear();
			// 建立新出口
			List<TransitionImpl> newTransitions = Lists.newArrayList();
			for (PvmTransition nextTransition : nextTransitionList) {
				PvmActivity nextActivity = nextTransition.getSource();
				ActivityImpl nextActivityImpl = ((ProcessDefinitionImpl) definition)
						.findActivity(nextActivity.getId());
				TransitionImpl newTransition = currActivity
						.createOutgoingTransition();
				newTransition.setDestination(nextActivityImpl);
				newTransitions.add(newTransition);
			}
			// 完成任务
			List<Task> tasks = taskService.createTaskQuery()
					.processInstanceId(instance.getId())
					.taskDefinitionKey(currTask.getTaskDefinitionKey()).list();
			for (Task task : tasks) {
				taskService.complete(task.getId(), variables);
				historyService.deleteHistoricTaskInstance(task.getId());
			}
			// 恢复方向
			for (TransitionImpl transitionImpl : newTransitions) {
				currActivity.getOutgoingTransitions().remove(transitionImpl);
			}
			for (PvmTransition pvmTransition : oriPvmTransitionList) {
				pvmTransitionList.add(pvmTransition);
			}
			return;
		} catch (Exception e) {
			return;
		}
	}
	
	protected void terminateProcessInstance(T entity) {
		try {
			String taskId = getTaskByProcInsId(entity.getProcInsId()).getId();
			Map<String, Object> variables;
			// 取得当前任务
			HistoricTaskInstance currTask = historyService
					.createHistoricTaskInstanceQuery().taskId(taskId)
					.singleResult();
			// 取得流程实例
			ProcessInstance instance = runtimeService
					.createProcessInstanceQuery()
					.processInstanceId(currTask.getProcessInstanceId())
					.singleResult();
			if (instance == null) {
				// 流程结束
			}
			variables = instance.getProcessVariables();
			// 取得流程定义
			ProcessDefinitionEntity definition = (ProcessDefinitionEntity) (repositoryService
					.getProcessDefinition(currTask.getProcessDefinitionId()));
			if (definition == null) {
				// 流程定义未找到
				return;
			}
			// 取得上一步活动
			ActivityImpl currActivity = ((ProcessDefinitionImpl) definition)
					.findActivity(currTask.getTaskDefinitionKey());
			/*List<PvmTransition> nextTransitionList = currActivity
					.getIncomingTransitions();*/
			// 清除当前活动的出口
			List<PvmTransition> oriPvmTransitionList = Lists.newArrayList();
			List<PvmTransition> pvmTransitionList = currActivity
					.getOutgoingTransitions();
			for (PvmTransition pvmTransition : pvmTransitionList) {
				oriPvmTransitionList.add(pvmTransition);
			}
			pvmTransitionList.clear();
			// 建立新出口
			List<TransitionImpl> newTransitions = Lists.newArrayList();
			ProcessDefinitionImpl procDefImpl = new ProcessDefinitionImpl(getProcIns(entity.getProcInsId()).getProcessDefinitionId());
			ActivityImpl end = new ActivityImpl("end", procDefImpl);
			TransitionImpl newTransition = currActivity
					.createOutgoingTransition();
			newTransition.setDestination(end);
			newTransitions.add(newTransition);
			// 完成任务
			List<Task> tasks = taskService.createTaskQuery()
					.processInstanceId(instance.getId())
					.taskDefinitionKey(currTask.getTaskDefinitionKey()).list();
			for (Task task : tasks) {
				taskService.complete(task.getId(), variables);
				historyService.deleteHistoricTaskInstance(task.getId());
			}
			// 恢复方向
			for (TransitionImpl transitionImpl : newTransitions) {
				currActivity.getOutgoingTransitions().remove(transitionImpl);
			}
			for (PvmTransition pvmTransition : oriPvmTransitionList) {
				pvmTransitionList.add(pvmTransition);
			}
			return;
		} catch (Exception e) {
			return;
		}
	}
	
	/**
	 * 获取procInsIds集合
	 * @param path
	 * @return
	 */
	public List<String> getProcInsIds(String path, Page<T> page) {
		LinkedHashMap<String, String> procInsIdsMap = Maps.newLinkedHashMap();
		if (Path.todo.name().equalsIgnoreCase(path)) {
			List<Task> taskList = taskService.createTaskQuery()
					.taskCandidateUser(UserUtils.getUser().getLoginName())
					.active().includeProcessVariables().orderByTaskCreateTime()
					.asc().list();
			for (Task task : taskList) {
				procInsIdsMap.put(task.getProcessInstanceId(), task.getProcessInstanceId());
			}
		} else if (Path.doing.name().equalsIgnoreCase(path)) {
			List<Task> taskList = taskService.createTaskQuery()
					.taskAssignee(UserUtils.getUser().getLoginName())
					.active().includeProcessVariables().orderByTaskCreateTime()
					.asc().list();
			for (Task task : taskList) {
				procInsIdsMap.put(task.getProcessInstanceId(), task.getProcessInstanceId());
			}
		} else if (Path.todoAndDoing.name().equalsIgnoreCase(path)) {
			List<Task> taskList = taskService.createTaskQuery()
					.taskCandidateOrAssigned(UserUtils.getUser().getLoginName())
					.active().includeProcessVariables().orderByTaskCreateTime()
					.asc().list();
			for (Task task : taskList) {
				procInsIdsMap.put(task.getProcessInstanceId(), task.getProcessInstanceId());
			}
		} else if (Path.done.name().equalsIgnoreCase(path)) {
			List<HistoricTaskInstance> taskList = historyService
					.createHistoricTaskInstanceQuery()
					.taskAssignee(UserUtils.getUser().getLoginName())
					.finished().includeProcessVariables()
					.orderByHistoricTaskInstanceEndTime().asc().list();
			for (HistoricTaskInstance historicTaskInstance : taskList) {
				procInsIdsMap.put(historicTaskInstance.getProcessInstanceId(), historicTaskInstance.getProcessInstanceId());
			}
		} else if (Path.sent.name().equalsIgnoreCase(path)) {
			List<HistoricProcessInstance> list = historyService
					.createHistoricProcessInstanceQuery()
					.startedBy(UserUtils.getUser().getLoginName())
					.includeProcessVariables()
					.orderByProcessInstanceStartTime().asc().list();
			for (HistoricProcessInstance historicProcessInstance : list) {
				procInsIdsMap.put(historicProcessInstance.getId(), historicProcessInstance.getId());
			}
		}
		//page.setCount(procInsIdsMap.size());
		List<String> procInsIdsTemp = Lists.newArrayList();
		for (Entry<String, String> entry : procInsIdsMap.entrySet()) {
			procInsIdsTemp.add(entry.getValue());
		}
		List<String> procInsIds = Lists.newArrayList();
		if (procInsIdsTemp.size() > 0) {
			int begin = procInsIdsTemp.size() - page.getFirstResult() - 1;
			int end = (begin - page.getMaxResults()) < 0 ? 0 : begin - page.getMaxResults();
			for (int i = begin; i >= end; i--) {
				procInsIds.add(procInsIdsTemp.get(i));
			}
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
		//Task task = getTaskByProcInsId(entity.getProcInsId());
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
	
	/**
	 * 待办在办已办
	 * @param path
	 * @return
	 */
	public boolean isTodoAndDoingAndDone(String path) {
		return Path.todoAndDoingAndDone.name().equalsIgnoreCase(path);
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
		List<Task> list = taskService.createTaskQuery()
				.processInstanceId(procInsId)
				.taskCandidateOrAssigned(UserUtils.getUser().getLoginName())
				.list();
		if (list.size() > 0) {
			return list.get(0);
		} else {
			List<Task> list1 = taskService.createTaskQuery()
					.processInstanceId(procInsId)
					.list();
			if (list1.size() > 0) {
				return list1.get(0);
			} else {
				return null;
			}
		}
	}
	
	/**
	 * 签收任务
	 * @param edocSend
	 */
	public void claim(String procInsId) {
		actTaskService.claim(getTaskByProcInsId(procInsId).getId(), UserUtils.getUser().getLoginName());
	}
	
	/**
	 * 签收任务
	 * @param edocSend
	 */
	public void unclaim(String procInsId) {
		actTaskService.claim(getTaskByProcInsId(procInsId).getId(), null);
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
	
	//======================================================================================================================
	
	@Transactional(readOnly = false)
	public int saveAct(T entity, String title, String procDefKey, String tableName, Map<String, Object> vars) {
		int completeAct = 0; //设置流程结束标识
		String param = Encodes.unescapeHtml(entity.getAct().getFlag());
		@SuppressWarnings("unchecked")
		Map<String, String> map = new Gson().fromJson(param, Map.class);
		entity.getAct().setFlag(map.get("flag"));
		String button = " [ " + map.get("button") + " ] ";
		String type = map.get("type");
		//暂存，流程未启动时暂存
		if (ButtonType.save.name().equalsIgnoreCase(type)) {
			this.save(entity);
		}
		//提交，流程未启动时，保存并启动流程
		else if (ButtonType.saveAndStart.name().equalsIgnoreCase(type)) {
			this.save(entity);
			//设置代理人
			this.setAssignee(entity);
			String procInsId = this.startProcess(procDefKey, tableName, entity.getId(), title);
			entity.setProcInsId(procInsId);
			entity.getAct().setProcDefId(this.getProcIns(procInsId).getProcessDefinitionId());
			entity.getAct().setTaskDefKey(this.getTaskByProcInsId(procInsId).getTaskDefinitionKey());
			//设置规则变量
			/*this.setRuleArgs(entity);*/
			vars.put(this.getNextTaskDefinitionAgent(entity), Arrays.asList(entity.getTempLoginName()));
			this.setCondition(entity, vars);
			this.complete(this.getTaskByProcInsId(procInsId).getId(), procInsId, button + entity.getAct().getComment(), title, vars);
			//完成任务再次更新实体
			ProcessInstance procIns = this.getProcIns(entity.getProcInsId());
			if (procIns != null) {
				entity.getAct().setProcDefId(procIns.getProcessDefinitionId());
				entity.getAct().setTaskDefKey(this.getTaskByProcInsId(procIns.getId()).getTaskDefinitionKey());
				entity.setProcTaskName(entity.getAct().getTaskDefKey());
				TaskPermission taskPermission = new TaskPermission();
				taskPermission.setId(this.getTaskSettingVersionByAct(entity.getAct()).getPermission());
				entity.setProcTaskPermission(taskPermission);
				this.save(entity);
			}
		}
		//暂存，流程运行时暂存
		else if (ButtonType.saveAndClaim.name().equalsIgnoreCase(type)) {
			this.save(entity);
			this.claim(entity.getProcInsId());
		}
		//提交，流程运行时，保存并完成任务
		else if (ButtonType.saveAndComplete.name().equalsIgnoreCase(type)) {
			ActivityImpl nextTaskDefinition = getNextTaskDefinition(entity);
			if(nextTaskDefinition==null)
			{
				completeAct =1;
				entity.setIsDo(1);
				
			}
			this.save(entity);
			//签收，待办>>在办
			this.claim(entity.getProcInsId());
			//设置代理人
			this.setAssignee(entity);
			//设置规则变量
			/*this.setRuleArgs(entity);*/
			List<String> loginNameList = Lists.newArrayList();
			if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
				for (String loginName : entity.getTempLoginName()) {
					if (!StringUtils.isEmpty(loginName)) {
						loginNameList.add(loginName);
					}
				}
			} else {
				LinkedHashMap<String, Object> targetUserList = getTargetUserList(entity);
				if (targetUserList != null && targetUserList.get("userList") != null) {
					@SuppressWarnings("unchecked")
					List<User> userList = (List<User>)targetUserList.get("userList");
					for (User user : userList) {
						loginNameList.add(user.getLoginName());
					}
				}
			}
			vars.put(this.getNextTaskDefinitionAgent(entity), loginNameList);
			//完成任务
			this.setCondition(entity, vars);
			this.complete(this.getTaskByProcInsId(entity.getProcInsId()).getId(), entity.getProcInsId(), button + entity.getAct().getComment(), title, vars);
			//完成任务再次更新实体
			ProcessInstance procIns = this.getProcIns(entity.getProcInsId());
			if (procIns != null) {
				entity.getAct().setProcDefId(procIns.getProcessDefinitionId());
				entity.getAct().setTaskDefKey(this.getTaskByProcInsId(procIns.getId()).getTaskDefinitionKey());
				entity.setProcTaskName(entity.getAct().getTaskDefKey());
				TaskPermission taskPermission = new TaskPermission();
				taskPermission.setId(this.getTaskSettingVersionByAct(entity.getAct()).getPermission());
				entity.setProcTaskPermission(taskPermission);
				this.save(entity);
			}
		}
		//提交，流程运行时，保存并回到前一个任务
		else if (ButtonType.saveAndReject.name().equalsIgnoreCase(type)) {
			this.save(entity);
			//签收，待办>>在办
			//this.claim(entity.getProcInsId());
			//设置代理人
			//this.setAssignee(entity);
			//设置规则变量
			/*this.setRuleArgs(entity);*/
			this.taskRollBack(entity);
			//完成任务再次更新实体
			ProcessInstance procIns = this.getProcIns(entity.getProcInsId());
			if (procIns != null) {
				entity.getAct().setProcDefId(procIns.getProcessDefinitionId());
				entity.getAct().setTaskDefKey(this.getTaskByProcInsId(procIns.getId()).getTaskDefinitionKey());
				entity.setProcTaskName(entity.getAct().getTaskDefKey());
				TaskPermission taskPermission = new TaskPermission();
				taskPermission.setId(this.getTaskSettingVersionByAct(entity.getAct()).getPermission());
				entity.setProcTaskPermission(taskPermission);
				this.save(entity);
			}
		}
		//终止，结束流程
		else if (ButtonType.saveAndTerminate.name().equalsIgnoreCase(type)) {
			this.save(entity);
			//签收，待办>>在办
			this.claim(entity.getProcInsId());
			//设置代理人
			this.setAssignee(entity);
			//设置规则变量
			/*this.setRuleArgs(entity);*/
			this.terminateProcessInstance(entity);
		}
		
		return completeAct;
	}
	
	/**
	 * 工作流级联删除
	 * @param edocSend
	 */
	public void deleteAct(T entity) {
		if (entity.getProcInsId() != null) {
			long runtimeCount = runtimeService.createProcessInstanceQuery().processInstanceId(entity.getProcInsId()).count();
			if (runtimeCount > 0) {
				runtimeService.deleteProcessInstance(entity.getProcInsId(), "DeleteCascade");
			}
			long historyCount = historyService.createHistoricProcessInstanceQuery().processInstanceId(entity.getProcInsId()).count();
			if (historyCount > 0) {
				historyService.deleteHistoricProcessInstance(entity.getProcInsId());
			}
			List<Task> list = taskService.createTaskQuery().processInstanceId(entity.getProcInsId()).list();
			List<String> taskIds = Lists.newArrayList();
			for (Task task : list) {
				taskIds.add(task.getId());
			}
			if (taskIds.size() > 0) {
				taskService.deleteTasks(taskIds, true);
			}
		}
	}
	
	/**
	 * 设置规则变量
	 * @param entity
	 */
	public void setRuleArgs(T entity) {
		if(entity.getAct().getProcDefId()==null)
		{
		  ProcessDefinition procDef = repositoryService.
				  createProcessDefinitionQuery().
				  processDefinitionKey(entity.getAct().getProcDefKey()).
				  latestVersion().singleResult();
		  entity.getAct().setProcDefId(procDef.getId());
		  entity.getAct().setTaskDefKey("request");
		}
		TaskSettingVersion taskSettingVersionByAct = getTaskSettingVersionByAct(entity.getAct());
		if (taskSettingVersionByAct != null && taskSettingVersionByAct.getRuleArgs() != null) {
			String ruleArgs = taskSettingVersionByAct.getRuleArgs();
			ruleArgs = StringEscapeUtils.unescapeHtml(ruleArgs);
			@SuppressWarnings("unchecked")
			List<Map<String, String>> ruleArgsList = new Gson().fromJson(ruleArgs, List.class);
			if (ruleArgsList != null && ruleArgsList.size() > 0) {
				ScriptEngineManager manager = new ScriptEngineManager();  
				ScriptEngine engine = manager.getEngineByName("js"); 
				
				Map<String, String> formMap = Maps.newHashMap();
				
				for (Map<String, String> map : ruleArgsList) {
					String key = map.get(RuleArgs.key.name());
					String value = map.get(RuleArgs.value.name());
					
					String scope = key.split("_")[0];
					if (RuleArgs.flag.name().equalsIgnoreCase(scope)) {
						try {
							String property = key.split("_")[1];
							Field field = entity.getClass().getDeclaredField(property);
							field.setAccessible(true);
							
							String expression = value.split("_")[0];
							String result = value.split("_")[1];
							engine.put(property, field.get(entity).toString());
							if ((boolean)engine.eval(expression)) {
								entity.getAct().setFlag(result);
							}
						} catch (NoSuchFieldException | SecurityException | ScriptException | IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
					} else if (RuleArgs.form.name().equalsIgnoreCase(scope)) {
						String property = key.split("_")[1];
						formMap.put(property, value);
					} else if (RuleArgs.content.name().equalsIgnoreCase(scope)) {
						String property = key.split("_")[1];
						formMap.put(property, value);
					}
				}
				entity.getRuleArgs().put(RuleArgs.form.name(), formMap);
			}
		}
	}
	
}
