/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectinfo.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.modules.sys.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gt_plus.modules.sys.utils.UserUtils;

import com.gt_plus.modules.userinfo.entity.UserInfo;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.common.utils.IdGen;
import com.gt_plus.modules.projectinfo.entity.ProjectInfo;
import com.gt_plus.modules.projectinfo.dao.ProjectInfoDao;

/**
 * 项目信息Service
 * @author zdy
 * @version 2018-02-24
 */
@Service
@Transactional(readOnly = true)
public class ProjectInfoService extends CrudService<ProjectInfoDao, ProjectInfo> {
	
	@Autowired
	private ProjectInfoDao projectInfoDao;
		
	public ProjectInfo get(String id) {
		return super.get(id);
	}
	
	public List<ProjectInfo> findList(ProjectInfo projectInfo) {
		return super.findList(projectInfo);
	}
	
	public Page<ProjectInfo> findPage(Page<ProjectInfo> page, ProjectInfo projectInfo) {
		return super.findPage(page, projectInfo);
	}
	
	/*选择用户*/
	@Transactional(readOnly = false)
	public List<UserInfo> findUserToPro(String id) {
			
			return projectInfoDao.findUserToPro(id);
	}
	

	@Transactional(readOnly = false)
	public void insertUserToPro(String projectNo, String id) {
		projectInfoDao.insertUserToPro(projectNo,id);
	}
	
	@Transactional(readOnly = false)
	public void deleteUserToPro(String userId, String projectNum) {
		projectInfoDao.deleteUserToPro(userId,projectNum);
	}
	
	@Transactional(readOnly = false)
	public void save(ProjectInfo projectInfo) {
			super.save(projectInfo);
	}
	
	
	/*保存信息前检查是否存在的保存*/
	@Transactional(readOnly = false)
	public boolean checkAndSave(ProjectInfo projectInfo) {
		int checkProCount =  projectInfoDao.checkPro(projectInfo);
		if(checkProCount == 0){
			this.save(projectInfo);
			return true;
		}
		else{
			return false;
			
		}	
	}
	
	
	@Transactional(readOnly = false)
	public void saveV(ProjectInfo projectInfo) {
		super.saveV(projectInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProjectInfo projectInfo) {
		super.delete(projectInfo);
	}
	
	public Page<ProjectInfo> findPageByprojectInfo(Page<ProjectInfo> page, ProjectInfo projectInfo) {
		projectInfo.setPage(page);
		page.setList(projectInfoDao.findListByprojectInfo(projectInfo));
		return page;
		
	}
		
	
}