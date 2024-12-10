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
import com.gt_plus.modules.sys.utils.UserUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.oa.entity.edoc.EdocReceive;
import com.gt_plus.modules.oa.dao.edoc.EdocReceiveDao;
import com.gt_plus.common.service.ActService;
import com.gt_plus.common.utils.StringUtils;

/**
 * 收文管理Service
 * @author GT0155
 * @version 2017-12-08
 */
@Service
@Transactional(readOnly = true)
public class EdocReceiveService extends ActService<EdocReceiveDao, EdocReceive> {
	private static final String PROCDEFKEY = "oaEdocReceiveProcess";
		
	public EdocReceive get(String id) {
		return super.get(id);
	}
	
	public List<EdocReceive> findList(EdocReceive edocReceive) {
		return super.findList(edocReceive);
	}
	
	public Page<EdocReceive> findPage(Page<EdocReceive> page, EdocReceive edocReceive) {
		return super.findPage(page, edocReceive);
	}
	
	@Transactional(readOnly = false)
	public void save(EdocReceive edocReceive) {
		super.save(edocReceive);
	}
	
	@Transactional(readOnly = false)
	public void saveV(EdocReceive edocReceive) {
		super.saveV(edocReceive);
	}
	
	@Transactional(readOnly = false)
	public void delete(EdocReceive edocReceive) {
		super.delete(edocReceive);
		super.deleteAct(edocReceive);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(EdocReceive edocReceive) {
		Map<String, Object> vars = Maps.newHashMap();
		super.saveAct(edocReceive, "收文管理", PROCDEFKEY, this.getClass().getName(), vars);
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(EdocReceive edocReceive) {
		edocReceive.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(edocReceive);
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(EdocReceive edocReceive) {
		if (StringUtils.isEmpty(edocReceive.getId()) || StringUtils.isEmpty(edocReceive.getProcInsId())) {
			edocReceive.getAct().setProcDefKey(PROCDEFKEY);
			return super.getStartingUserList(edocReceive);
		} else {
			edocReceive.getAct().setProcDefKey(PROCDEFKEY);
			return super.getTargetUserList(edocReceive);
		}
	}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param edocReceive
	 * @param path
	 * @return
	 */
	public Page<EdocReceive> findPage(Page<EdocReceive> page, EdocReceive edocReceive, String path) {
		if (super.isUnsent(path)) {
			edocReceive.setPage(page);
			edocReceive.getSqlMap().put("dsf", " AND a.create_by = '" + UserUtils.getUser().getId() + "' ");
			page.setList(dao.findListByProcIsNull(edocReceive));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				edocReceive.setPage(page);
				page.setList(dao.findListByProc(edocReceive, procInsIds));
			} else {
				edocReceive.setPage(page);
				List<EdocReceive> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param edocReceive
	 */
	public void setAct(EdocReceive edocReceive) {
		super.setAct(edocReceive);
	}
	
}