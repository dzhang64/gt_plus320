/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusertransferapplication.service;

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

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.sysusertransferapplication.entity.SysUserTransferApplication;
import com.gt_plus.modules.sysusertransferapplication.dao.SysUserTransferApplicationDao;
import com.gt_plus.modules.userinfo.dao.UserInfoDao;
import com.gt_plus.common.service.ActService;
import com.gt_plus.common.utils.StringUtils;

/**
 * 调薪申请Service
 * @author wl
 * @version 2018-03-29
 */
@Service
@Transactional(readOnly = true)
public class SysUserTransferApplicationService extends ActService<SysUserTransferApplicationDao, SysUserTransferApplication> {
	private static final String PROCDEFKEY = "transferApplication";
	@Autowired
	private UserInfoDao userInfoDao;
		
	public SysUserTransferApplication get(String id) {
		return super.get(id);
	}
	
	public List<SysUserTransferApplication> findList(SysUserTransferApplication sysUserTransferApplication) {
		return super.findList(sysUserTransferApplication);
	}
	
	public Page<SysUserTransferApplication> findPage(Page<SysUserTransferApplication> page, SysUserTransferApplication sysUserTransferApplication) {
		return super.findPage(page, sysUserTransferApplication);
	}
	
	@Transactional(readOnly = false)
	public void save(SysUserTransferApplication sysUserTransferApplication) {
		super.save(sysUserTransferApplication);
	}
	
	@Transactional(readOnly = false)
	public void saveV(SysUserTransferApplication sysUserTransferApplication) {
		super.saveV(sysUserTransferApplication);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysUserTransferApplication sysUserTransferApplication) {
		super.delete(sysUserTransferApplication);
		super.deleteAct(sysUserTransferApplication);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(SysUserTransferApplication sysUserTransferApplication) {
		Map<String, Object> vars = Maps.newHashMap();
		int completeAct =super.saveAct(sysUserTransferApplication, "调薪申请", PROCDEFKEY, this.getClass().getName(), vars);
		//如果流程是最后一步，即结束流程
		if(completeAct ==1){
			//存储用户到基础信息表
		String levelId=sysUserTransferApplication.getAfterSalary().getId();
		String salary=sysUserTransferApplication.getAfterSalaryLevel();
		String userId=sysUserTransferApplication.getName().getId();
		userInfoDao.updateSalaryById(levelId,salary,userId);
		}
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(SysUserTransferApplication sysUserTransferApplication){
		sysUserTransferApplication.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(sysUserTransferApplication);
	}
	
	public void setRuleArgs(SysUserTransferApplication sysUserTransferApplication){
			sysUserTransferApplication.getAct().setProcDefKey(PROCDEFKEY);
			super.setRuleArgs(sysUserTransferApplication);
			
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(SysUserTransferApplication sysUserTransferApplication,String[] ids){
		
			if(ids ==null||ids.length<=0){
				if (StringUtils.isEmpty(sysUserTransferApplication.getId()) ||
						StringUtils.isEmpty(sysUserTransferApplication.getProcInsId())){
					sysUserTransferApplication.getAct().setProcDefKey(PROCDEFKEY);
					return super.getStartingUserList(sysUserTransferApplication);
				} else{
					sysUserTransferApplication.getAct().setProcDefKey(PROCDEFKEY);
					return super.getTargetUserList(sysUserTransferApplication);
				}
			}else{
				return super.getWebUserList(ids); 
			}
		}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param sysUserTransferApplication
	 * @param path
	 * @return
	 */
	public Page<SysUserTransferApplication> findPage(Page<SysUserTransferApplication> page, SysUserTransferApplication sysUserTransferApplication, String path) {
		if (super.isUnsent(path)) {
			sysUserTransferApplication.setPage(page);
			sysUserTransferApplication.getSqlMap().put("dsf", " AND a.create_by = '" + UserUtils.getUser().getId() + "' ");
			page.setList(dao.findListByProcIsNull(sysUserTransferApplication));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				sysUserTransferApplication.setPage(page);
				page.setList(dao.findListByProc(sysUserTransferApplication, procInsIds));
			} else {
				sysUserTransferApplication.setPage(page);
				List<SysUserTransferApplication> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param sysUserTransferApplication
	 */
	public void setAct(SysUserTransferApplication sysUserTransferApplication) {
		super.setAct(sysUserTransferApplication);
	}
	
}