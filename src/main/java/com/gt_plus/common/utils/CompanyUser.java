package com.gt_plus.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.gt_plus.modules.sysuserinterentry.dao.SysInternUserInformationDao;
import com.gt_plus.modules.sysuserinterentry.entity.SysInternUserInformation;

public class CompanyUser {

	/**
	 * 
	 * @return 构造合同编号
	 */
	public synchronized static String makeContratNum() {
		// 构造合同编号
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd-hh-mm-SSS");
			return formatter.format(new Date());
	}
		

	
}
