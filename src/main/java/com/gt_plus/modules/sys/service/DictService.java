/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.sys.entity.Dict;
import com.gt_plus.modules.sys.dao.DictDao;

/**
 * 字典Service
 * @author GT0291
 * @version 2017-07-25
 */
@Service
@Transactional(readOnly = true)
public class DictService extends CrudService<DictDao, Dict> {

	/**
	 * 查询字段类型列表
	 * @return
	 */
	public List<String> findTypeList(){
		return dao.findTypeList(new Dict());
	}
	
	public Dict get(String id) {
		return super.get(id);
	}
	
	public List<Dict> findList(Dict dict) {
		return super.findList(dict);
	}
	
	public Page<Dict> findPage(Page<Dict> page, Dict dict) {
		return super.findPage(page, dict);
	}
	
	@Transactional(readOnly = false)
	public void save(Dict dict) {
		super.save(dict);
	}
	
	@Transactional(readOnly = false)
	public void saveV(Dict dict) {
		super.saveV(dict);
	}
	
	@Transactional(readOnly = false)
	public void delete(Dict dict) {
		super.delete(dict);
	}
}