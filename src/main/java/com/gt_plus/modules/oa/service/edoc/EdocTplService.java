/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.service.edoc;

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
import com.gt_plus.modules.oa.entity.edoc.EdocTpl;
import com.gt_plus.modules.oa.dao.edoc.EdocTplDao;

/**
 * 模版类型Service
 * @author GT0155
 * @version 2017-12-17
 */
@Service
@Transactional(readOnly = true)
public class EdocTplService extends CrudService<EdocTplDao, EdocTpl> {
		
	public EdocTpl get(String id) {
		return super.get(id);
	}
	
	public List<EdocTpl> findList(EdocTpl edocTpl) {
		return super.findList(edocTpl);
	}
	
	public Page<EdocTpl> findPage(Page<EdocTpl> page, EdocTpl edocTpl) {
		return super.findPage(page, edocTpl);
	}
	
	@Transactional(readOnly = false)
	public void save(EdocTpl edocTpl) {
		super.save(edocTpl);
	}
	
	@Transactional(readOnly = false)
	public void saveV(EdocTpl edocTpl) {
		super.saveV(edocTpl);
	}
	
	@Transactional(readOnly = false)
	public void delete(EdocTpl edocTpl) {
		super.delete(edocTpl);
	}
	
	
	
}