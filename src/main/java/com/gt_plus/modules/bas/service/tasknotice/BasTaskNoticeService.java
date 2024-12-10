/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.bas.service.tasknotice;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.bas.entity.tasknotice.BasTaskNotice;
import com.gt_plus.modules.bas.dao.tasknotice.BasTaskNoticeDao;

/**
 * 任务通知Service
 * @author GT0291
 * @version 2017-08-03
 */
@Service
@Transactional(readOnly = true)
public class BasTaskNoticeService extends CrudService<BasTaskNoticeDao, BasTaskNotice> {

	public BasTaskNotice get(String id) {
		return super.get(id);
	}
	
	public List<BasTaskNotice> findList(BasTaskNotice basTaskNotice) {
		return super.findList(basTaskNotice);
	}
	
	public Page<BasTaskNotice> findPage(Page<BasTaskNotice> page, BasTaskNotice basTaskNotice) {
		return super.findPage(page, basTaskNotice);
	}
	
	@Transactional(readOnly = false)
	public void save(BasTaskNotice basTaskNotice) {
		super.save(basTaskNotice);
	}
	
	@Transactional(readOnly = false)
	public void saveV(BasTaskNotice basTaskNotice) {
		super.saveV(basTaskNotice);
	}
	
	@Transactional(readOnly = false)
	public void delete(BasTaskNotice basTaskNotice) {
		super.delete(basTaskNotice);
	}
	
	
	
	
}