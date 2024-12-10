/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.mgmt.service.dmb;

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
import com.gt_plus.modules.mgmt.entity.dmb.DirectorMailbox;
import com.gt_plus.modules.mgmt.dao.dmb.DirectorMailboxDao;

/**
 * 局长信箱Service
 * @author GT0155
 * @version 2017-11-28
 */
@Service
@Transactional(readOnly = true)
public class DirectorMailboxService extends CrudService<DirectorMailboxDao, DirectorMailbox> {
		
	public DirectorMailbox get(String id) {
		return super.get(id);
	}
	
	public List<DirectorMailbox> findList(DirectorMailbox directorMailbox) {
		return super.findList(directorMailbox);
	}
	
	public Page<DirectorMailbox> findPage(Page<DirectorMailbox> page, DirectorMailbox directorMailbox) {
		return super.findPage(page, directorMailbox);
	}
	
	@Transactional(readOnly = false)
	public void save(DirectorMailbox directorMailbox) {
		super.save(directorMailbox);
	}
	
	@Transactional(readOnly = false)
	public void saveV(DirectorMailbox directorMailbox) {
		super.saveV(directorMailbox);
	}
	
	@Transactional(readOnly = false)
	public void delete(DirectorMailbox directorMailbox) {
		super.delete(directorMailbox);
	}
	
	
	
}