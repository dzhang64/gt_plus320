/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.service.edoc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.modules.sys.utils.UserUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.modules.oa.entity.edoc.EdocSign;
import com.gt_plus.modules.oa.dao.edoc.EdocSignDao;
import com.gt_plus.common.service.ActService;
import com.gt_plus.common.utils.StringUtils;

/**
 * 签报管理Service
 * @author GT0155
 * @version 2017-12-08
 */
@Service
@Transactional(readOnly = true)
public class EdocSignService extends ActService<EdocSignDao, EdocSign> {
	private static final String PROCDEFKEY = "oaEdocSignProcess";
		
	public EdocSign get(String id) {
		return super.get(id);
	}
	
	public List<EdocSign> findList(EdocSign edocSign) {
		return super.findList(edocSign);
	}
	
	public Page<EdocSign> findPage(Page<EdocSign> page, EdocSign edocSign) {
		return super.findPage(page, edocSign);
	}
	
	@Transactional(readOnly = false)
	public void save(EdocSign edocSign) {
		super.save(edocSign);
	}
	
	@Transactional(readOnly = false)
	public void saveV(EdocSign edocSign) {
		super.saveV(edocSign);
	}
	
	@Transactional(readOnly = false)
	public void delete(EdocSign edocSign) {
		super.delete(edocSign);
		super.deleteAct(edocSign);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(EdocSign edocSign) {
		Map<String, Object> vars = Maps.newHashMap();
		super.saveAct(edocSign, "签报管理", PROCDEFKEY, this.getClass().getName(), vars);
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(EdocSign edocSign) {
		edocSign.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(edocSign);
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(EdocSign edocSign) {
		if (StringUtils.isEmpty(edocSign.getId()) || StringUtils.isEmpty(edocSign.getProcInsId())) {
			edocSign.getAct().setProcDefKey(PROCDEFKEY);
			return super.getStartingUserList(edocSign);
		} else {
			edocSign.getAct().setProcDefKey(PROCDEFKEY);
			return super.getTargetUserList(edocSign);
		}
	}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param edocSign
	 * @param path
	 * @return
	 */
	public Page<EdocSign> findPage(Page<EdocSign> page, EdocSign edocSign, String path) {
		if (super.isUnsent(path)) {
			edocSign.setPage(page);
			edocSign.getSqlMap().put("dsf", " AND a.create_by = '" + UserUtils.getUser().getId() + "' ");
			page.setList(dao.findListByProcIsNull(edocSign));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				edocSign.setPage(page);
				page.setList(dao.findListByProc(edocSign, procInsIds));
			} else {
				edocSign.setPage(page);
				List<EdocSign> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param edocSign
	 */
	public void setAct(EdocSign edocSign) {
		super.setAct(edocSign);
	}
	
}