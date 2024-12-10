/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.service.teamwork;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.utils.UserUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.oa.entity.teamwork.Teamwork;
import com.gt_plus.modules.oa.dao.teamwork.TeamworkDao;
import com.gt_plus.common.service.ActService;
import com.gt_plus.common.utils.StringUtils;

/**
 * 协同管理Service
 * @author LS0077
 * @version 2017-12-11
 */
@Service
@Transactional(readOnly = true)
public class TeamworkService extends ActService<TeamworkDao, Teamwork> {
	private static final String PROCDEFKEY = "teamProcess";
		
	public Teamwork get(String id) {
		return super.get(id);
	}
	
	public List<Teamwork> findList(Teamwork teamwork) {
		return super.findList(teamwork);
	}
	
	public Page<Teamwork> findPage(Page<Teamwork> page, Teamwork teamwork) {
		return super.findPage(page, teamwork);
	}
	
	@Transactional(readOnly = false)
	public void save(Teamwork teamwork) {
		super.save(teamwork);
	}
	
	@Transactional(readOnly = false)
	public void saveV(Teamwork teamwork) {
		super.saveV(teamwork);
	}
	
	@Transactional(readOnly = false)
	public void delete(Teamwork teamwork) {
		super.delete(teamwork);
		super.deleteAct(teamwork);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(Teamwork teamwork) {
		Map<String, Object> vars = Maps.newHashMap();
		super.saveAct(teamwork, "协同管理", PROCDEFKEY, this.getClass().getName(), vars);
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(Teamwork teamwork) {
		teamwork.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(teamwork);
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(Teamwork teamwork) {
		if (StringUtils.isEmpty(teamwork.getId()) || StringUtils.isEmpty(teamwork.getProcInsId())) {
			teamwork.getAct().setProcDefKey(PROCDEFKEY);
			return super.getStartingUserList(teamwork);
		} else {
			teamwork.getAct().setProcDefKey(PROCDEFKEY);
			return super.getTargetUserList(teamwork);
		}
	}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param teamwork
	 * @param path
	 * @return
	 */
	public Page<Teamwork> findPage(Page<Teamwork> page, Teamwork teamwork, String path) {
		if (super.isUnsent(path)) {
			teamwork.setPage(page);
			teamwork.getSqlMap().put("dsf", "AND a.create_by = '" + UserUtils.getUser().getId() + "'");
			page.setList(dao.findListByProcIsNull(teamwork));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				teamwork.setPage(page);
				page.setList(dao.findListByProc(teamwork, procInsIds));
			} else {
				teamwork.setPage(page);
				List<Teamwork> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param teamwork
	 */
	public void setAct(Teamwork teamwork) {
		super.setAct(teamwork);
	}
	
}