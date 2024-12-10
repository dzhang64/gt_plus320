/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysuserpersonnelturnpositive.service;

import java.util.Date;
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
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.sysuserpersonnelturnpositive.dao.SysUserPersonnelTurnPositiveDao;
import com.gt_plus.modules.sysuserpersonnelturnpositive.entity.SysUserPersonnelTurnPositive;
import com.gt_plus.modules.userinfo.dao.UserInfoDao;

/**
 * 人员转正Service
 * @author wl
 * @version 2018-03-19
 */
@Service
@Transactional(readOnly = true)
public class SysUserPersonnelTurnPositiveService extends ActService<SysUserPersonnelTurnPositiveDao, SysUserPersonnelTurnPositive> {
	private static final String PROCDEFKEY = "PersonnelTurnPositive";
	@Autowired
	private UserInfoDao userInfoDao;
	
	public SysUserPersonnelTurnPositive get(String id) {
		return super.get(id);
	}
	
	public List<SysUserPersonnelTurnPositive> findList(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive) {
		return super.findList(sysUserPersonnelTurnPositive);
	}
	
	public Page<SysUserPersonnelTurnPositive> findPage(Page<SysUserPersonnelTurnPositive> page, SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive) {
		return super.findPage(page, sysUserPersonnelTurnPositive);
	}
	
	@Transactional(readOnly = false)
	public void save(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive) {
		super.save(sysUserPersonnelTurnPositive);
	}
	
	@Transactional(readOnly = false)
	public void saveV(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive) {
		super.saveV(sysUserPersonnelTurnPositive);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive) {
		super.delete(sysUserPersonnelTurnPositive);
		super.deleteAct(sysUserPersonnelTurnPositive);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive) {
		Map<String, Object> vars = Maps.newHashMap();
		int completeAct = super.saveAct(sysUserPersonnelTurnPositive, "人员转正", PROCDEFKEY, this.getClass().getName(), vars);
		//如果流程是最后一步，即结束流程
		if(completeAct ==1){
			//存储用户到基础信息表
		String userId=sysUserPersonnelTurnPositive.getName().getId();
		String userType="2";
		String salaryId=sysUserPersonnelTurnPositive.getAfterSalary().getId();
		Double salaryLevel=sysUserPersonnelTurnPositive.getAfterGradePay();
		String userStatus="3";
		Date turnTime =sysUserPersonnelTurnPositive.getTurnTime();
		userInfoDao.updateById(userId,userType,salaryId,salaryLevel,userStatus,turnTime);
		}
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive){
		sysUserPersonnelTurnPositive.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(sysUserPersonnelTurnPositive);
	}
	
	public void setRuleArgs(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive){
			sysUserPersonnelTurnPositive.getAct().setProcDefKey(PROCDEFKEY);
			super.setRuleArgs(sysUserPersonnelTurnPositive);
			
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive,String[] ids){
		
			if(ids ==null||ids.length<=0){
				if (StringUtils.isEmpty(sysUserPersonnelTurnPositive.getId()) ||
						StringUtils.isEmpty(sysUserPersonnelTurnPositive.getProcInsId())){
					sysUserPersonnelTurnPositive.getAct().setProcDefKey(PROCDEFKEY);
					return super.getStartingUserList(sysUserPersonnelTurnPositive);
				} else{
					sysUserPersonnelTurnPositive.getAct().setProcDefKey(PROCDEFKEY);
					return super.getTargetUserList(sysUserPersonnelTurnPositive);
				}
			}else{
				return super.getWebUserList(ids); 
			}
		}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param sysUserPersonnelTurnPositive
	 * @param path
	 * @return
	 */
	public Page<SysUserPersonnelTurnPositive> findPage(Page<SysUserPersonnelTurnPositive> page, SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive, String path) {
		if (super.isUnsent(path)) {
			sysUserPersonnelTurnPositive.setPage(page);
			sysUserPersonnelTurnPositive.getSqlMap().put("dsf", " AND a.create_by = '" + UserUtils.getUser().getId() + "' ");
			page.setList(dao.findListByProcIsNull(sysUserPersonnelTurnPositive));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				sysUserPersonnelTurnPositive.setPage(page);
				page.setList(dao.findListByProc(sysUserPersonnelTurnPositive, procInsIds));
			} else {
				sysUserPersonnelTurnPositive.setPage(page);
				List<SysUserPersonnelTurnPositive> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param sysUserPersonnelTurnPositive
	 */
	public void setAct(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive) {
		super.setAct(sysUserPersonnelTurnPositive);
	}
	
}