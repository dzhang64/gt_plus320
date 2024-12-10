package com.gt_plus.common.aop.aspect;

import java.lang.reflect.Method;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.stereotype.Component;

//import com.gt_plus.common.annotation.DataSource;
import com.gt_plus.common.ds.DataSourceContextHolder;

//@Component
//@Aspect
public class DataSourceAdvice {
	
	/*
	@Pointcut(value = "execution(* com.gt_plus.modules.test.service..*.*(..))")
	public void dataSourceAdvicePointcut() {
		
	}

	@Before(value = "dataSourceAdvicePointcut()")
	public void setDataSource() {
		DataSourceContextHolder.setDbType("default");
	}
	*/
}
