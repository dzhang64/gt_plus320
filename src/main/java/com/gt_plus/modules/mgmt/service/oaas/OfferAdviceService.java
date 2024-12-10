/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.mgmt.service.oaas;

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
import com.gt_plus.modules.mgmt.entity.oaas.OfferAdvice;
import com.gt_plus.modules.mgmt.dao.oaas.OfferAdviceDao;

/**
 * 建言献策Service
 * @author GT0155
 * @version 2017-11-28
 */
@Service
@Transactional(readOnly = true)
public class OfferAdviceService extends CrudService<OfferAdviceDao, OfferAdvice> {
		
	public OfferAdvice get(String id) {
		return super.get(id);
	}
	
	public List<OfferAdvice> findList(OfferAdvice offerAdvice) {
		return super.findList(offerAdvice);
	}
	
	public Page<OfferAdvice> findPage(Page<OfferAdvice> page, OfferAdvice offerAdvice) {
		return super.findPage(page, offerAdvice);
	}
	
	@Transactional(readOnly = false)
	public void save(OfferAdvice offerAdvice) {
		super.save(offerAdvice);
	}
	
	@Transactional(readOnly = false)
	public void saveV(OfferAdvice offerAdvice) {
		super.saveV(offerAdvice);
	}
	
	@Transactional(readOnly = false)
	public void delete(OfferAdvice offerAdvice) {
		super.delete(offerAdvice);
	}
	
	
	
}