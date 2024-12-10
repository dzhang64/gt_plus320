/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt Plus</a> All rights reserved.
 */
package com.gt_plus.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * bean中文名注解
 */
@Target(ElementType.METHOD)  
@Retention(RetentionPolicy.RUNTIME)  
public @interface FieldName {

	String value();
	
}
