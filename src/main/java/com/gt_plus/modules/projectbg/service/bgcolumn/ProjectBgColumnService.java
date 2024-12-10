/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectbg.service.bgcolumn;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.modules.sys.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gt_plus.modules.sys.utils.UserUtils;

import com.gt_plus.modules.projectinfo.entity.ProjectInfo;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.projectbg.entity.bgcolumn.ProjectBgColumn;
import com.gt_plus.modules.projectbg.dao.bgcolumn.ProjectBgColumnDao;

/**
 * 项目预算行Service
 * @author zdy
 * @version 2018-03-21
 */
@Service
@Transactional(readOnly = true)
public class ProjectBgColumnService extends CrudService<ProjectBgColumnDao, ProjectBgColumn> {
		
	public ProjectBgColumn get(String id) {
		return super.get(id);
	}
	
	public List<ProjectBgColumn> findList(ProjectBgColumn projectBgColumn) {
		return super.findList(projectBgColumn);
	}
	
	public Page<ProjectBgColumn> findPage(Page<ProjectBgColumn> page, ProjectBgColumn projectBgColumn) {
		return super.findPage(page, projectBgColumn);
	}
	
	@Transactional(readOnly = false)
	public void save(ProjectBgColumn projectBgColumn) {
		super.save(projectBgColumn);
	}
	
	@Transactional(readOnly = false)
	public void saveV(ProjectBgColumn projectBgColumn) {
		super.saveV(projectBgColumn);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProjectBgColumn projectBgColumn) {
		super.delete(projectBgColumn);
	}
	
	public Page<ProjectInfo> findPageByprojectInfo(Page<ProjectInfo> page, ProjectInfo projectInfo) {
		projectInfo.setPage(page);
		page.setList(dao.findListByprojectInfo(projectInfo));
		return page;
	}
	
	
}