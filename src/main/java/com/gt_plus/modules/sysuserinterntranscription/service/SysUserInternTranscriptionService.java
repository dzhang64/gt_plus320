/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysuserinterntranscription.service;

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
import com.gt_plus.modules.sysuserinterntranscription.dao.SysUserInternTranscriptionDao;
import com.gt_plus.modules.sysuserinterntranscription.entity.SysUserInternTranscription;
import com.gt_plus.modules.userinfo.dao.UserInfoDao;

/**
 * 实习生转录用Service
 * @author wl
 * @version 2018-03-20
 */
@Service
@Transactional(readOnly = true)
public class SysUserInternTranscriptionService extends ActService<SysUserInternTranscriptionDao, SysUserInternTranscription> {
	private static final String PROCDEFKEY = "InternTranscription";
	
	@Autowired
	private UserInfoDao userInfoDao;
		
	public SysUserInternTranscription get(String id) {
		return super.get(id);
	}
	
	public List<SysUserInternTranscription> findList(SysUserInternTranscription sysUserInternTranscription) {
		return super.findList(sysUserInternTranscription);
	}
	
	public Page<SysUserInternTranscription> findPage(Page<SysUserInternTranscription> page, SysUserInternTranscription sysUserInternTranscription) {
		return super.findPage(page, sysUserInternTranscription);
	}
	
	@Transactional(readOnly = false)
	public void save(SysUserInternTranscription sysUserInternTranscription) {
		super.save(sysUserInternTranscription);
	}
	
	@Transactional(readOnly = false)
	public void saveV(SysUserInternTranscription sysUserInternTranscription) {
		super.saveV(sysUserInternTranscription);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysUserInternTranscription sysUserInternTranscription) {
		super.delete(sysUserInternTranscription);
		super.deleteAct(sysUserInternTranscription);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(SysUserInternTranscription sysUserInternTranscription) {
		Map<String, Object> vars = Maps.newHashMap();
		int completeAct = super.saveAct(sysUserInternTranscription, "实习生转录用", PROCDEFKEY, this.getClass().getName(), vars);
		//如果流程是最后一步，即结束流程
		if(completeAct ==1){
			//存储用户到基础信息表
		String userId=sysUserInternTranscription.getName().getId();
		String userType="2";
		String salaryId=sysUserInternTranscription.getLevel().getId();
		Double salaryLevel=sysUserInternTranscription.getSalaryLevel();
		String userStatus="3";
		Date turnTime=sysUserInternTranscription.getBeginDate();
		userInfoDao.updateById(userId,userType,salaryId,salaryLevel,userStatus,turnTime);
		}
		
		
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(SysUserInternTranscription sysUserInternTranscription){
		sysUserInternTranscription.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(sysUserInternTranscription);
	}
	
	public void setRuleArgs(SysUserInternTranscription sysUserInternTranscription){
			sysUserInternTranscription.getAct().setProcDefKey(PROCDEFKEY);
			super.setRuleArgs(sysUserInternTranscription);
			
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(SysUserInternTranscription sysUserInternTranscription,String[] ids){
		
			if(ids ==null||ids.length<=0){
				if (StringUtils.isEmpty(sysUserInternTranscription.getId()) ||
						StringUtils.isEmpty(sysUserInternTranscription.getProcInsId())){
					sysUserInternTranscription.getAct().setProcDefKey(PROCDEFKEY);
					return super.getStartingUserList(sysUserInternTranscription);
				} else{
					sysUserInternTranscription.getAct().setProcDefKey(PROCDEFKEY);
					return super.getTargetUserList(sysUserInternTranscription);
				}
			}else{
				return super.getWebUserList(ids); 
			}
		}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param sysUserInternTranscription
	 * @param path
	 * @return
	 */
	public Page<SysUserInternTranscription> findPage(Page<SysUserInternTranscription> page, SysUserInternTranscription sysUserInternTranscription, String path) {
		if (super.isUnsent(path)) {
			sysUserInternTranscription.setPage(page);
			sysUserInternTranscription.getSqlMap().put("dsf", " AND a.create_by = '" + UserUtils.getUser().getId() + "' ");
			page.setList(dao.findListByProcIsNull(sysUserInternTranscription));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				sysUserInternTranscription.setPage(page);
				page.setList(dao.findListByProc(sysUserInternTranscription, procInsIds));
			} else {
				sysUserInternTranscription.setPage(page);
				List<SysUserInternTranscription> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param sysUserInternTranscription
	 */
	public void setAct(SysUserInternTranscription sysUserInternTranscription) {
		super.setAct(sysUserInternTranscription);
	}
	
}