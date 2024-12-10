/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.service.test;

import java.util.LinkedHashMap; //
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.modules.sys.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.modules.oa.entity.test.MyTest;
import com.gt_plus.modules.oa.dao.test.MyTestDao;
import com.gt_plus.common.service.ActService;

/**
 * 测试OAService
 * @author David
 * @version 2017-11-24
 */
@Service
@Transactional(readOnly = true)
public class MyTestService extends ActService<MyTestDao, MyTest> {
	private static final String PROCDEFKEY = "myTestProcess";
		
	public MyTest get(String id) {
		return super.get(id);
	}
	
	public List<MyTest> findList(MyTest myTest) {
		return super.findList(myTest);
	}
	
	public Page<MyTest> findPage(Page<MyTest> page, MyTest myTest) {
		return super.findPage(page, myTest);
	}
	
	@Transactional(readOnly = false)
	public void save(MyTest myTest) {
		super.save(myTest);
	}
	
	@Transactional(readOnly = false)
	public void saveV(MyTest myTest) {
		super.saveV(myTest);
	}
	
	@Transactional(readOnly = false)
	public void delete(MyTest myTest) {
		super.delete(myTest);
		super.deleteAct(myTest);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(MyTest myTest) {
		Map<String, Object> vars = Maps.newHashMap();
		super.saveAct(myTest, "测试OA", PROCDEFKEY, this.getClass().getName(), vars);
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(MyTest myTest) {
		myTest.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(myTest);
	}
	
	//public List<User> getTargetUserList(MyTest myTest) {
	public LinkedHashMap<String, Object> getTargetUserList(MyTest myTest) {
		myTest.getAct().setProcDefKey(PROCDEFKEY);
		return super.getTargetUserList(myTest);
	}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param myTest
	 * @param path
	 * @return
	 */
	public Page<MyTest> findPage(Page<MyTest> page, MyTest myTest, String path) {
		if (super.isUnsent(path)) {
			myTest.setPage(page);
			page.setList(dao.findListByProcIsNull(myTest));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				myTest.setPage(page);
				page.setList(dao.findListByProc(myTest, procInsIds));
			} else {
				myTest.setPage(page);
				List<MyTest> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param myTest
	 */
	public void setAct(MyTest myTest) {
		super.setAct(myTest);
	}
	
}