/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.common.ds;

import org.apache.commons.lang3.StringUtils;

/**
 * 数据源切换类
 * @author gt_plus
 * @version 2014-06-25
 */
public class DataSourceContextHolder {  
	 
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>(); 
    public static final String DEFAULT_DBTYPE = "default";
  
    public static void setDbType(String dbType) {  
        contextHolder.set(dbType);  
    }  
  
    public static String getDbType() {
    	String dataSource = contextHolder.get();
    	if (StringUtils.isEmpty(dataSource)) {
    		return DEFAULT_DBTYPE;
    	} else {
    		return dataSource;
    	}
    }  
  
    public static void clearDbType() {  
        contextHolder.remove();  
    }  
}  