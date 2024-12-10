/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusercontractrenew.service;

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
import com.gt_plus.common.utils.CompanyUser;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.sysusercontractrenew.dao.SysUserContractRenewDao;
import com.gt_plus.modules.sysusercontractrenew.entity.SysUserContractRenew;
import com.gt_plus.modules.userinfo.dao.UserInfoDao;

/**
 * 合同续签Service
 * @author wl
 * @version 2018-03-27
 */
@Service
@Transactional(readOnly = true)
public class SysUserContractRenewService extends ActService<SysUserContractRenewDao, SysUserContractRenew> {
	private static final String PROCDEFKEY = "contractRenew";
	@Autowired
	private UserInfoDao userInfoDao;
	
	public SysUserContractRenew get(String id) {
		return super.get(id);
	}
	
	public List<SysUserContractRenew> findList(SysUserContractRenew sysUserContractRenew) {
		return super.findList(sysUserContractRenew);
	}
	
	public Page<SysUserContractRenew> findPage(Page<SysUserContractRenew> page, SysUserContractRenew sysUserContractRenew) {
		return super.findPage(page, sysUserContractRenew);
	}
	
	@Transactional(readOnly = false)
	public void save(SysUserContractRenew sysUserContractRenew) {
		super.save(sysUserContractRenew);
	}
	
	@Transactional(readOnly = false)
	public void saveV(SysUserContractRenew sysUserContractRenew) {
		super.saveV(sysUserContractRenew);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysUserContractRenew sysUserContractRenew) {
		super.delete(sysUserContractRenew);
		super.deleteAct(sysUserContractRenew);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(SysUserContractRenew sysUserContractRenew) {
		Map<String, Object> vars = Maps.newHashMap();
		int completeAct = super.saveAct(sysUserContractRenew, "合同续签", PROCDEFKEY, this.getClass().getName(), vars);
		//如果流程是最后一步，即结束流程
		if(completeAct ==1){
			//存储用户到基础信息表
			String userId=sysUserContractRenew.getName().getId();
			String contractNum = CompanyUser.makeContratNum();
			//sysInternUserInformation.setContract(contractNum);
			Date beginDate=sysUserContractRenew.getBeginDate();
			Date endDate=sysUserContractRenew.getEndDate();
			String contractLimit =sysUserContractRenew.getContractLimit();
			String contractType= sysUserContractRenew.getContractType();
			userInfoDao.updateContractAndDate(contractNum,beginDate,endDate,contractLimit,contractType,userId);
			
		}
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(SysUserContractRenew sysUserContractRenew){
		sysUserContractRenew.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(sysUserContractRenew);
	}
	
	public void setRuleArgs(SysUserContractRenew sysUserContractRenew){
			sysUserContractRenew.getAct().setProcDefKey(PROCDEFKEY);
			super.setRuleArgs(sysUserContractRenew);
			
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(SysUserContractRenew sysUserContractRenew,String[] ids){
		
			if(ids ==null||ids.length<=0){
				if (StringUtils.isEmpty(sysUserContractRenew.getId()) ||
						StringUtils.isEmpty(sysUserContractRenew.getProcInsId())){
					sysUserContractRenew.getAct().setProcDefKey(PROCDEFKEY);
					return super.getStartingUserList(sysUserContractRenew);
				} else{
					sysUserContractRenew.getAct().setProcDefKey(PROCDEFKEY);
					return super.getTargetUserList(sysUserContractRenew);
				}
			}else{
				return super.getWebUserList(ids); 
			}
		}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param sysUserContractRenew
	 * @param path
	 * @return
	 */
	public Page<SysUserContractRenew> findPage(Page<SysUserContractRenew> page, SysUserContractRenew sysUserContractRenew, String path) {
		if (super.isUnsent(path)) {
			sysUserContractRenew.setPage(page);
			sysUserContractRenew.getSqlMap().put("dsf", " AND a.create_by = '" + UserUtils.getUser().getId() + "' ");
			page.setList(dao.findListByProcIsNull(sysUserContractRenew));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				sysUserContractRenew.setPage(page);
				page.setList(dao.findListByProc(sysUserContractRenew, procInsIds));
			} else {
				sysUserContractRenew.setPage(page);
				List<SysUserContractRenew> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param sysUserContractRenew
	 */
	public void setAct(SysUserContractRenew sysUserContractRenew) {
		super.setAct(sysUserContractRenew);
	}
	
}