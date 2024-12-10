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
import com.gt_plus.modules.oa.entity.edoc.EdocSend;
import com.gt_plus.modules.oa.dao.edoc.EdocSendDao;
import com.gt_plus.common.service.ActService;
import com.gt_plus.common.utils.StringUtils;

/**
 * 发文管理Service
 * @author GT0155
 * @version 2017-12-08
 */
@Service
@Transactional(readOnly = true)
public class EdocSendService extends ActService<EdocSendDao, EdocSend> {
	private static final String PROCDEFKEY = "testEdocProcess";
		
	public EdocSend get(String id) {
		return super.get(id);
	}
	
	public List<EdocSend> findList(EdocSend edocSend) {
		return super.findList(edocSend);
	}
	
	public Page<EdocSend> findPage(Page<EdocSend> page, EdocSend edocSend) {
		return super.findPage(page, edocSend);
	}
	
	@Transactional(readOnly = false)
	public void save(EdocSend edocSend) {
		super.save(edocSend);
	}
	
	@Transactional(readOnly = false)
	public void saveV(EdocSend edocSend) {
		super.saveV(edocSend);
	}
	
	@Transactional(readOnly = false)
	public void delete(EdocSend edocSend) {
		super.delete(edocSend);
		super.deleteAct(edocSend);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(EdocSend edocSend) {
		Map<String, Object> vars = Maps.newHashMap();
		super.saveAct(edocSend, "发文管理", PROCDEFKEY, this.getClass().getName(), vars);
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(EdocSend edocSend) {
		edocSend.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(edocSend);
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(EdocSend edocSend) {
		if (StringUtils.isEmpty(edocSend.getId()) || StringUtils.isEmpty(edocSend.getProcInsId())) {
			edocSend.getAct().setProcDefKey(PROCDEFKEY);
			return super.getStartingUserList(edocSend);
		} else {
			edocSend.getAct().setProcDefKey(PROCDEFKEY);
			return super.getTargetUserList(edocSend);
		}
	}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param edocSend
	 * @param path
	 * @return
	 */
	public Page<EdocSend> findPage(Page<EdocSend> page, EdocSend edocSend, String path) {
		if (super.isUnsent(path)) {
			edocSend.setPage(page);
			edocSend.getSqlMap().put("dsf", " AND a.create_by = '" + UserUtils.getUser().getId() + "' ");
			page.setList(dao.findListByProcIsNull(edocSend));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				edocSend.setPage(page);
				page.setList(dao.findListByProc(edocSend, procInsIds));
			} else {
				edocSend.setPage(page);
				List<EdocSend> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param edocSend
	 */
	public void setAct(EdocSend edocSend) {
		super.setAct(edocSend);
	}

}