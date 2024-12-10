/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.service.arc;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.service.TreeService;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.modules.oa.entity.arc.ArcCategory;
import com.gt_plus.modules.oa.dao.arc.ArcCategoryDao;

/**
 * 档案目录Service
 * @author LS0077
 * @version 2017-11-07
 */
@Service
@Transactional(readOnly = true)
public class ArcCategoryService extends TreeService<ArcCategoryDao, ArcCategory> {

	public ArcCategory get(String id) {
		return super.get(id);
	}
	
	public List<ArcCategory> findList(ArcCategory arcCategory) {
		if (StringUtils.isNotBlank(arcCategory.getParentIds())){
			arcCategory.setParentIds(","+arcCategory.getParentIds()+",");
		}
		return super.findList(arcCategory);
	}
	
	@Transactional(readOnly = false)
	public void save(ArcCategory arcCategory) {
		super.save(arcCategory);
	}
	
	@Transactional(readOnly = false)
	public void saveV(ArcCategory arcCategory) {
		super.saveV(arcCategory);
	}
	
	@Transactional(readOnly = false)
	public void delete(ArcCategory arcCategory) {
		super.delete(arcCategory);
	}
	
}