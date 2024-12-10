/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusercomputersubsidiesapply.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.modules.sys.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gt_plus.modules.sys.utils.UserUtils;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.sysusercomputersubsidiesapply.entity.SysUserComputerSubsidiesApply;
import com.gt_plus.modules.sysusercomputersubsidiesapply.dao.SysUserComputerSubsidiesApplyDao;
import com.gt_plus.common.service.ActService;
import com.gt_plus.common.utils.StringUtils;

/**
 * 电脑补贴申请Service
 * @author wl
 * @version 2018-03-23
 */
@Service
@Transactional(readOnly = true)
public class SysUserComputerSubsidiesApplyService extends ActService<SysUserComputerSubsidiesApplyDao, SysUserComputerSubsidiesApply> {
	private static final String PROCDEFKEY = "computerSubsidiesApply";
		
	public SysUserComputerSubsidiesApply get(String id) {
		return super.get(id);
	}
	
	public List<SysUserComputerSubsidiesApply> findList(SysUserComputerSubsidiesApply sysUserComputerSubsidiesApply) {
		return super.findList(sysUserComputerSubsidiesApply);
	}
	
	public Page<SysUserComputerSubsidiesApply> findPage(Page<SysUserComputerSubsidiesApply> page, SysUserComputerSubsidiesApply sysUserComputerSubsidiesApply) {
		return super.findPage(page, sysUserComputerSubsidiesApply);
	}
	
	@Transactional(readOnly = false)
	public void save(SysUserComputerSubsidiesApply sysUserComputerSubsidiesApply) {
		super.save(sysUserComputerSubsidiesApply);
	}
	
	@Transactional(readOnly = false)
	public void saveV(SysUserComputerSubsidiesApply sysUserComputerSubsidiesApply) {
		super.saveV(sysUserComputerSubsidiesApply);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysUserComputerSubsidiesApply sysUserComputerSubsidiesApply) {
		super.delete(sysUserComputerSubsidiesApply);
		super.deleteAct(sysUserComputerSubsidiesApply);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(SysUserComputerSubsidiesApply sysUserComputerSubsidiesApply) {
		Map<String, Object> vars = Maps.newHashMap();
		super.saveAct(sysUserComputerSubsidiesApply, "电脑补贴申请", PROCDEFKEY, this.getClass().getName(), vars);
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(SysUserComputerSubsidiesApply sysUserComputerSubsidiesApply){
		sysUserComputerSubsidiesApply.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(sysUserComputerSubsidiesApply);
	}
	
	public void setRuleArgs(SysUserComputerSubsidiesApply sysUserComputerSubsidiesApply){
			sysUserComputerSubsidiesApply.getAct().setProcDefKey(PROCDEFKEY);
			super.setRuleArgs(sysUserComputerSubsidiesApply);
			
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(SysUserComputerSubsidiesApply sysUserComputerSubsidiesApply,String[] ids){
		
			if(ids ==null||ids.length<=0){
				if (StringUtils.isEmpty(sysUserComputerSubsidiesApply.getId()) ||
						StringUtils.isEmpty(sysUserComputerSubsidiesApply.getProcInsId())){
					sysUserComputerSubsidiesApply.getAct().setProcDefKey(PROCDEFKEY);
					return super.getStartingUserList(sysUserComputerSubsidiesApply);
				} else{
					sysUserComputerSubsidiesApply.getAct().setProcDefKey(PROCDEFKEY);
					return super.getTargetUserList(sysUserComputerSubsidiesApply);
				}
			}else{
				return super.getWebUserList(ids); 
			}
		}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param sysUserComputerSubsidiesApply
	 * @param path
	 * @return
	 */
	public Page<SysUserComputerSubsidiesApply> findPage(Page<SysUserComputerSubsidiesApply> page, SysUserComputerSubsidiesApply sysUserComputerSubsidiesApply, String path) {
		if (super.isUnsent(path)) {
			sysUserComputerSubsidiesApply.setPage(page);
			sysUserComputerSubsidiesApply.getSqlMap().put("dsf", " AND a.create_by = '" + UserUtils.getUser().getId() + "' ");
			page.setList(dao.findListByProcIsNull(sysUserComputerSubsidiesApply));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				sysUserComputerSubsidiesApply.setPage(page);
				page.setList(dao.findListByProc(sysUserComputerSubsidiesApply, procInsIds));
			} else {
				sysUserComputerSubsidiesApply.setPage(page);
				List<SysUserComputerSubsidiesApply> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param sysUserComputerSubsidiesApply
	 */
	public void setAct(SysUserComputerSubsidiesApply sysUserComputerSubsidiesApply) {
		super.setAct(sysUserComputerSubsidiesApply);
	}
	
}