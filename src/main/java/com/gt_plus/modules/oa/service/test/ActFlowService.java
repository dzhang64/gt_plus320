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
import com.gt_plus.modules.oa.entity.test.ActFlow;
import com.gt_plus.modules.oa.dao.test.ActFlowDao;
import com.gt_plus.common.service.ActService;

/**
 * act工作流Service
 * @author GT0155
 * @version 2017-11-29
 */
@Service
@Transactional(readOnly = true)
public class ActFlowService extends ActService<ActFlowDao, ActFlow> {
	private static final String PROCDEFKEY = "actflowProcess";
		
	public ActFlow get(String id) {
		return super.get(id);
	}
	
	public List<ActFlow> findList(ActFlow actFlow) {
		return super.findList(actFlow);
	}
	
	public Page<ActFlow> findPage(Page<ActFlow> page, ActFlow actFlow) {
		return super.findPage(page, actFlow);
	}
	
	@Transactional(readOnly = false)
	public void save(ActFlow actFlow) {
		super.save(actFlow);
	}
	
	@Transactional(readOnly = false)
	public void saveV(ActFlow actFlow) {
		super.saveV(actFlow);
	}
	
	@Transactional(readOnly = false)
	public void delete(ActFlow actFlow) {
		super.delete(actFlow);
		super.deleteAct(actFlow);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(ActFlow actFlow) {
		Map<String, Object> vars = Maps.newHashMap();
		super.saveAct(actFlow, "act工作流", PROCDEFKEY, this.getClass().getName(), vars);
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(ActFlow actFlow) {
		actFlow.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(actFlow);
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(ActFlow actFlow) {
		actFlow.getAct().setProcDefKey(PROCDEFKEY);
		return super.getTargetUserList(actFlow);
	}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param actFlow
	 * @param path
	 * @return
	 */
	public Page<ActFlow> findPage(Page<ActFlow> page, ActFlow actFlow, String path) {
		if (super.isUnsent(path)) {
			actFlow.setPage(page);
			page.setList(dao.findListByProcIsNull(actFlow));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				actFlow.setPage(page);
				page.setList(dao.findListByProc(actFlow, procInsIds));
			} else {
				actFlow.setPage(page);
				List<ActFlow> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param actFlow
	 */
	public void setAct(ActFlow actFlow) {
		super.setAct(actFlow);
	}
	
}