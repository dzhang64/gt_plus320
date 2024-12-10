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
import com.gt_plus.modules.oa.entity.test.TestActFlow;
import com.gt_plus.modules.oa.dao.test.TestActFlowDao;
import com.gt_plus.common.service.ActService;

/**
 * 测试002Service
 * @author GT0155
 * @version 2017-11-28
 */
@Service
@Transactional(readOnly = true)
public class TestActFlowService extends ActService<TestActFlowDao, TestActFlow> {
	private static final String PROCDEFKEY = "testActFlowProcess";
		
	public TestActFlow get(String id) {
		return super.get(id);
	}
	
	public List<TestActFlow> findList(TestActFlow testActFlow) {
		return super.findList(testActFlow);
	}
	
	public Page<TestActFlow> findPage(Page<TestActFlow> page, TestActFlow testActFlow) {
		return super.findPage(page, testActFlow);
	}
	
	@Transactional(readOnly = false)
	public void save(TestActFlow testActFlow) {
		super.save(testActFlow);
	}
	
	@Transactional(readOnly = false)
	public void saveV(TestActFlow testActFlow) {
		super.saveV(testActFlow);
	}
	
	@Transactional(readOnly = false)
	public void delete(TestActFlow testActFlow) {
		super.delete(testActFlow);
		super.deleteAct(testActFlow);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(TestActFlow testActFlow) {
		Map<String, Object> vars = Maps.newHashMap();
		super.saveAct(testActFlow, "测试002", PROCDEFKEY, this.getClass().getName(), vars);
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(TestActFlow testActFlow) {
		testActFlow.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(testActFlow);
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(TestActFlow testActFlow) {
		testActFlow.getAct().setProcDefKey(PROCDEFKEY);
		return super.getTargetUserList(testActFlow);
	}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param testActFlow
	 * @param path
	 * @return
	 */
	public Page<TestActFlow> findPage(Page<TestActFlow> page, TestActFlow testActFlow, String path) {
		if (super.isUnsent(path)) {
			testActFlow.setPage(page);
			page.setList(dao.findListByProcIsNull(testActFlow));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				testActFlow.setPage(page);
				page.setList(dao.findListByProc(testActFlow, procInsIds));
			} else {
				testActFlow.setPage(page);
				List<TestActFlow> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param testActFlow
	 */
	public void setAct(TestActFlow testActFlow) {
		super.setAct(testActFlow);
	}
	
}