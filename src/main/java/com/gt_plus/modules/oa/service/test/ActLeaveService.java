/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.service.test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.modules.sys.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.oa.entity.test.ActLeave;
import com.gt_plus.modules.oa.dao.test.ActLeaveDao;
import com.gt_plus.common.service.ActService;

/**
 * 请假表单Service
 * @author GT0155
 * @version 2017-11-29
 */
@Service
@Transactional(readOnly = true)
public class ActLeaveService extends ActService<ActLeaveDao, ActLeave> {
	private static final String PROCDEFKEY = "actLeaveProcess";
		
	public ActLeave get(String id) {
		return super.get(id);
	}
	
	public List<ActLeave> findList(ActLeave actLeave) {
		return super.findList(actLeave);
	}
	
	public Page<ActLeave> findPage(Page<ActLeave> page, ActLeave actLeave) {
		return super.findPage(page, actLeave);
	}
	
	@Transactional(readOnly = false)
	public void save(ActLeave actLeave) {
		super.save(actLeave);
	}
	
	@Transactional(readOnly = false)
	public void saveV(ActLeave actLeave) {
		super.saveV(actLeave);
	}
	
	@Transactional(readOnly = false)
	public void delete(ActLeave actLeave) {
		super.delete(actLeave);
		super.deleteAct(actLeave);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(ActLeave actLeave) {
		Map<String, Object> vars = Maps.newHashMap();
		super.saveAct(actLeave, "请假表单", PROCDEFKEY, this.getClass().getName(), vars);
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(ActLeave actLeave) {
		actLeave.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(actLeave);
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(ActLeave actLeave) {
		actLeave.getAct().setProcDefKey(PROCDEFKEY);
		return super.getTargetUserList(actLeave);
	}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param actLeave
	 * @param path
	 * @return
	 */
	public Page<ActLeave> findPage(Page<ActLeave> page, ActLeave actLeave, String path) {
		if (super.isUnsent(path)) {
			actLeave.setPage(page);
			page.setList(dao.findListByProcIsNull(actLeave));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				actLeave.setPage(page);
				page.setList(dao.findListByProc(actLeave, procInsIds));
			} else {
				actLeave.setPage(page);
				List<ActLeave> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param actLeave
	 */
	public void setAct(ActLeave actLeave) {
		super.setAct(actLeave);
	}
	
}