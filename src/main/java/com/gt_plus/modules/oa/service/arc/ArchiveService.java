/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.service.arc;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.modules.oa.entity.arc.ArcCategory;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.oa.entity.arc.Archive;
import com.gt_plus.modules.oa.dao.arc.ArchiveDao;

/**
 * 档案Service
 * @author LS0077
 * @version 2017-11-07
 */
@Service
@Transactional(readOnly = true)
public class ArchiveService extends CrudService<ArchiveDao, Archive> {

	public Archive get(String id) {
		return super.get(id);
	}
	
	public List<Archive> findList(Archive archive) {
		return super.findList(archive);
	}
	
	public Page<Archive> findPage(Page<Archive> page, Archive archive) {
		return super.findPage(page, archive);
	}
	
	@Transactional(readOnly = false)
	public void save(Archive archive) {
		super.save(archive);
	}
	
	@Transactional(readOnly = false)
	public void saveV(Archive archive) {
		super.saveV(archive);
	}
	
	@Transactional(readOnly = false)
	public void delete(Archive archive) {
		super.delete(archive);
	}
	
	public Page<ArcCategory> findPageByarc(Page<ArcCategory> page, ArcCategory arc) {
		arc.setPage(page);
		page.setList(dao.findListByarc(arc));
		return page;
	}
	
	@Transactional(readOnly = false)
	public void inArchiveData(String title,String oaArcTypeCode) {
		Archive archive = new Archive();
		archive.setTitle(title);		//标题
		archive.setOaArcTypeCode(oaArcTypeCode); //公文类型
		
		
		
		super.save(archive);
	}
	
}