/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysuserresignation.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.ActService;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.modules.sys.dao.UserDao;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.service.SystemService;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.sysuserresignation.dao.SysUserResignationDao;
import com.gt_plus.modules.sysuserresignation.entity.SysUserResignation;
import com.gt_plus.modules.userinfo.dao.UserInfoDao;
import com.gt_plus.modules.userinfo.entity.UserInfo;

/**
 * 离职申请Service
 * @author wl
 * @version 2018-03-21
 */
@Service
@Transactional(readOnly = true)
public class SysUserResignationService extends ActService<SysUserResignationDao, SysUserResignation> {
	private static final String PROCDEFKEY = "Resignation";
	
	@Autowired
	private SystemService systemService;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserInfoDao userInfoDao;
	public SysUserResignation get(String id) {
		return super.get(id);
	}
	
	public List<SysUserResignation> findList(SysUserResignation sysUserResignation) {
		return super.findList(sysUserResignation);
	}
	
	public Page<SysUserResignation> findPage(Page<SysUserResignation> page, SysUserResignation sysUserResignation) {
		return super.findPage(page, sysUserResignation);
	}
	
	@Transactional(readOnly = false)
	public void save(SysUserResignation sysUserResignation) {
		if (sysUserResignation.getLeaveSum()==null) {
			sysUserResignation.setLeaveSum(0.0);
		}
		super.save(sysUserResignation);
	}
	
	@Transactional(readOnly = false)
	public void saveV(SysUserResignation sysUserResignation) {
		super.saveV(sysUserResignation);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysUserResignation sysUserResignation) {
		super.delete(sysUserResignation);
		super.deleteAct(sysUserResignation);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(SysUserResignation sysUserResignation) {
		Map<String, Object> vars = Maps.newHashMap();
		int completeAct = super.saveAct(sysUserResignation, "离职申请", PROCDEFKEY, this.getClass().getName(), vars);
		//如果流程是最后一步，即结束流程
		if(completeAct ==1){
			//存储用户到基础信息表
			String id=sysUserResignation.getName().getId();
			User user=userDao.get(id);
			String useable="0";
			userDao.deleteUserRole(user);//从角色中删除该人员
			systemService.deleteUserRole(user,useable,id);//从流程表单删除，并清除用户缓存
			userInfoDao.updateDeflag(id);
			
	
		}
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(SysUserResignation sysUserResignation){
		sysUserResignation.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(sysUserResignation);
	}
	
	public void setRuleArgs(SysUserResignation sysUserResignation){
			sysUserResignation.getAct().setProcDefKey(PROCDEFKEY);
			super.setRuleArgs(sysUserResignation);
			
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(SysUserResignation sysUserResignation,String[] ids){
		
			if(ids ==null||ids.length<=0){
				if (StringUtils.isEmpty(sysUserResignation.getId()) ||
						StringUtils.isEmpty(sysUserResignation.getProcInsId())){
					sysUserResignation.getAct().setProcDefKey(PROCDEFKEY);
					return super.getStartingUserList(sysUserResignation);
				} else{
					sysUserResignation.getAct().setProcDefKey(PROCDEFKEY);
					return super.getTargetUserList(sysUserResignation);
				}
			}else{
				return super.getWebUserList(ids); 
			}
		}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param sysUserResignation
	 * @param path
	 * @return
	 */
	public Page<SysUserResignation> findPage(Page<SysUserResignation> page, SysUserResignation sysUserResignation, String path) {
		if (super.isUnsent(path)) {
			sysUserResignation.setPage(page);
			sysUserResignation.getSqlMap().put("dsf", " AND a.create_by = '" + UserUtils.getUser().getId() + "' ");
			page.setList(dao.findListByProcIsNull(sysUserResignation));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				sysUserResignation.setPage(page);
				page.setList(dao.findListByProc(sysUserResignation, procInsIds));
			} else {
				sysUserResignation.setPage(page);
				List<SysUserResignation> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param sysUserResignation
	 */
	public void setAct(SysUserResignation sysUserResignation) {
		super.setAct(sysUserResignation);
	}
	
}