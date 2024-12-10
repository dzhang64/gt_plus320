/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.userinfo.dao;

import com.gt_plus.modules.salarylevel.entity.SalaryLevel;

import java.util.Date;
import java.util.List;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.userinfo.entity.UserInfo;

/**
 * 员工信息DAO接口
 * @author zdy
 * @version 2018-01-30
 */
@MyBatisDao
public interface UserInfoDao extends CrudDao<UserInfo> {

	public List<SalaryLevel> findListBysalary(SalaryLevel salary);
	
	public String getLastUserCode();
	
	public List<UserInfo> findListByUserInfo(UserInfo userInfo);

	public void updateById(@Param("userId")String userId,@Param("userType")String userType,@Param("salaryId")String salaryId,@Param("salaryLevel")Double salaryLevel,@Param("turnTime")String userStatus,@Param("userStatus")Date turnTime);

	public void updateDeflag(@Param("id")String id);

	public void updateContractAndDate(@Param("contractNum")String contractNum, @Param("beginDate")Date beginDate,@Param("endDate")Date endDate,@Param("contractLimit")String contractLimit,@Param("contractType")String contractType,@Param("userId")String userId);

	public void updateSalaryById(@Param("levelId")String levelId, @Param("salary")String salary,@Param("userId") String userId);

	public void updateSscByuserId(@Param("ssc")String ssc, @Param("coordinateNum")String coordinateNum,@Param("providentNum")String providentNum,@Param("userId") String userId);
	
}