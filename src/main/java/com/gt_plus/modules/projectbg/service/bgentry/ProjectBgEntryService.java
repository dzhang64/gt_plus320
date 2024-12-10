/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectbg.service.bgentry;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.common.service.TreeService;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.modules.projectbg.entity.bgentry.ProjectBgEntry;
import com.gt_plus.modules.projectbg.dao.bgentry.ProjectBgEntryDao;

/**
 * 预算条目字典Service
 * @author zdy
 * @version 2018-03-19
 */
@Service
@Transactional(readOnly = true)
public class ProjectBgEntryService extends TreeService<ProjectBgEntryDao, ProjectBgEntry> {

	public ProjectBgEntry get(String id) {
		return super.get(id);
	}
	
	public List<ProjectBgEntry> findList(ProjectBgEntry projectBgEntry) {
		if (StringUtils.isNotBlank(projectBgEntry.getParentIds())){
			projectBgEntry.setParentIds(","+projectBgEntry.getParentIds()+",");
		}
		return super.findList(projectBgEntry);
	}
	
	/*用于别的类调用直接返回树的数据*/
	public List<Map<String, String>> treeData() {
		List<Map<String, String>> mapList = Lists.newArrayList();
		List<ProjectBgEntry> list = this.findList(new ProjectBgEntry());
		for (int i=0; i<list.size(); i++){
			ProjectBgEntry e = list.get(i);
				Map<String, String> map = Maps.newHashMap();
				map.put("id",e.getId());
				map.put("pId",e.getParentId());
				map.put("name",e.getName());
				mapList.add(map);
		}
	    
	    return mapList;
	}
	
	
	@Transactional(readOnly = false)
	public void save(ProjectBgEntry projectBgEntry) {
		super.save(projectBgEntry);
	}
	
	@Transactional(readOnly = false)
	public void saveV(ProjectBgEntry projectBgEntry) {
		super.saveV(projectBgEntry);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProjectBgEntry projectBgEntry) {
		super.delete(projectBgEntry);
	}
	
}