/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectapprove.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.modules.sys.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.projectapprove.entity.ProjectApprove;
import com.gt_plus.modules.projectapprove.dao.ProjectApproveDao;
import com.gt_plus.modules.projectinfo.dao.ProjectInfoDao;
import com.gt_plus.modules.projectinfo.entity.ProjectInfo;
import com.gt_plus.modules.projectinfo.service.ProjectInfoService;
import com.gt_plus.common.service.ActService;
import com.gt_plus.common.utils.StringUtils;

/**
 * 项目立项Service
 * @author zdy
 * @version 2018-03-17
 */
@Service
@Transactional(readOnly = true)
public class ProjectApproveService extends ActService<ProjectApproveDao, ProjectApprove> {
	private static final String PROCDEFKEY = "ProjectApproval";
	
	@Autowired
	private ProjectInfoDao projectInfoDao;
	
	@Autowired
	private ProjectInfoService projectInfoService;
		
	public ProjectApprove get(String id) {
		return super.get(id);
	}
	
	public List<ProjectApprove> findList(ProjectApprove projectApprove) {
		return super.findList(projectApprove);
	}
	
	public Page<ProjectApprove> findPage(Page<ProjectApprove> page, ProjectApprove projectApprove) {
		return super.findPage(page, projectApprove);
	}
	
	@Transactional(readOnly = false)
	public void save(ProjectApprove projectApprove) {
		super.save(projectApprove);
	}
	
	@Transactional(readOnly = false)
	public void saveV(ProjectApprove projectApprove) {
		super.saveV(projectApprove);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProjectApprove projectApprove) {
		super.delete(projectApprove);
		super.deleteAct(projectApprove);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(ProjectApprove projectApprove) {
		Map<String, Object> vars = Maps.newHashMap();
		int completeAct = super.saveAct(projectApprove, "项目立项", PROCDEFKEY, this.getClass().getName(), vars);
		if(completeAct ==1){
			ProjectInfo projectInfo =new ProjectInfo(projectApprove);
			boolean res = projectInfoService.checkAndSave(projectInfo);
			if(res)
			{
				String projectId = projectInfo.getId();
				String projectManagerId = projectInfo.getProjectmanager().getId();
				String roleId = "bb833b95fccb4d34b82cc17786561d18";
				//将项目经理插入项目人员
				projectInfoService.insertUserToPro(projectId,projectManagerId);
				//将项目经理的角色设置为项目经理
				UserUtils.setUserRole(projectManagerId, roleId);	
			}
		}
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(ProjectApprove projectApprove){
		projectApprove.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(projectApprove);
	}
	
	public void setRuleArgs(ProjectApprove projectApprove){
			projectApprove.getAct().setProcDefKey(PROCDEFKEY);
			super.setRuleArgs(projectApprove);
			
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(ProjectApprove projectApprove,String[] ids){
		
			if(ids ==null||ids.length<=0){
				if (StringUtils.isEmpty(projectApprove.getId()) ||
						StringUtils.isEmpty(projectApprove.getProcInsId())){
					projectApprove.getAct().setProcDefKey(PROCDEFKEY);
					return super.getStartingUserList(projectApprove);
				} else{
					projectApprove.getAct().setProcDefKey(PROCDEFKEY);
					return super.getTargetUserList(projectApprove);
				}
			}else{
				return super.getWebUserList(ids); 
			}
		}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 */
	
	public boolean checkProjectInfo(ProjectApprove projectApprove){
		ProjectInfo projectInfo  = new ProjectInfo();
		projectInfo.setProjectName(projectApprove.getProjectName());
		projectInfo.setProjectNum(projectApprove.getProjectNum());
		if(projectInfoDao.checkPro(projectInfo)>0){
			return false;
		}
		else{
			return true;
		}		
	}
	
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param projectApprove
	 * @param path
	 * @return
	 */
	public Page<ProjectApprove> findPage(Page<ProjectApprove> page, ProjectApprove projectApprove, String path) {
		if (super.isUnsent(path)) {
			projectApprove.setPage(page);
			projectApprove.getSqlMap().put("dsf", " AND a.create_by = '" + UserUtils.getUser().getId() + "' ");
			page.setList(dao.findListByProcIsNull(projectApprove));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				projectApprove.setPage(page);
				page.setList(dao.findListByProc(projectApprove, procInsIds));
				System.out.println("1");
			} else {
				projectApprove.setPage(page);
				List<ProjectApprove> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param projectApprove
	 */
	public void setAct(ProjectApprove projectApprove) {
		super.setAct(projectApprove);
	}
	
}