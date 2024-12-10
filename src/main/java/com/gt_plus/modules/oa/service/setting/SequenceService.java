/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.service.setting;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.modules.sys.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gt_plus.common.config.Global;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.oa.entity.setting.Sequence;
import com.gt_plus.modules.oa.dao.setting.SequenceDao;

/**
 * 文单序列Service
 * @author GT0291
 * @version 2017-12-25
 */
@Service
@Transactional(readOnly = true)
public class SequenceService extends CrudService<SequenceDao, Sequence> {
		
	public Sequence get(String id) {
		return super.get(id);
	}
	
	public List<Sequence> findList(Sequence sequence) {
		return super.findList(sequence);
	}
	
	public Page<Sequence> findPage(Page<Sequence> page, Sequence sequence) {
		return super.findPage(page, sequence);
	}
	
	@Transactional(readOnly = false)
	public void save(Sequence sequence) {
		super.save(sequence);
	}
	
	@Transactional(readOnly = false)
	public void saveV(Sequence sequence) {
		super.saveV(sequence);
	}
	
	@Transactional(readOnly = false)
	public void delete(Sequence sequence) {
		super.delete(sequence);
	}
	
	/**
	 * 判断年度与当前年度是否一致：
	 *   按年循环，小流水
	 *     等于当前年度，返回currentValue + 1
	 *     小于当前年度1，返回lastYearValue + 1
	 *     大于当前年度1：year+1, currentValue=startValue，返回
	 *     其他，返回空字符串
	 *   一直累加，大流水
	 *     返回currentValue + 1
	 * @param key
	 * @param year
	 * @return
	 */
	@Transactional(readOnly = false)
	public String getSequence(String key, Integer year) {
		Integer value = -1;
		Sequence sequence = new Sequence();
		sequence.setKey(key);
		sequence = dao.getSequence(sequence);
		if (sequence != null && year != null) {
			Integer sequenceYear = sequence.getYear();
			if (sequence.getCycleByYear().equals(Global.YES)) {
				//按年循环，小流水
				if (year.equals(sequenceYear)) {
					value = sequence.getCurrentValue() + 1;
					sequence.setNextValue(value);
					dao.update(sequence);
				} else if (year.equals(sequenceYear - 1)) {
					value = sequence.getLastYearValue() + 1;
					sequence.setNextValue(value);
					dao.update(sequence);
				} else if (year.equals(sequenceYear + 1)) {
					//注意：设置值顺序
					value = sequence.getStartValue();
					sequence.setNextValue(value);
					dao.update(sequence);
				}
			} else {
				//一直累加，大流水
				value = sequence.getCurrentValue() + 1;
				sequence.setNextValue(value);
				dao.update(sequence);
			}
		}
		if(value == -1) {
			return "";
		} else {
			return value.toString();
		}
	}
	
	/**
	 * 确认使用序列值
	 * @param key
	 * @param year
	 * @param value
	 * @return
	 */
	@Transactional(readOnly = false)
	public int confirmSequence(String key, Integer year, Integer value) {
		int updateCount = 0;
		Sequence sequence = new Sequence();
		sequence.setKey(key);
		sequence = dao.getSequence(sequence);
		if (sequence != null && year != null) {
			Integer sequenceYear = sequence.getYear();
			if (sequence.getCycleByYear().equals(Global.YES)) {
				//按年循环，小流水
				if (year.equals(sequenceYear)) {
					sequence.setCurrentValue(value);
					sequence.setConfirmNextValue(value);
					updateCount = dao.update(sequence);
				} else if (year.equals(sequenceYear - 1)) {
					sequence.setLastYearValue(value);
					sequence.setConfirmNextValue(value);
					updateCount = dao.update(sequence);
				} else if (year.equals(sequenceYear + 1)) {
					//注意：设置值顺序
					sequence.setLastYearValue(sequence.getCurrentValue());
					sequence.setCurrentValue(value);
					sequence.setYear(year);
					sequence.setConfirmNextValue(value);
					updateCount = dao.update(sequence);
				}
			} else {
				//一直累加，大流水
				sequence.setCurrentValue(value);
				sequence.setConfirmNextValue(value);
				updateCount = dao.update(sequence);
			}
		}
		return updateCount;
	}
}