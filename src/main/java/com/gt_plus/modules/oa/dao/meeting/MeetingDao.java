/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.dao.meeting;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.oa.entity.meeting.Meeting;

/**
 * 会议管理DAO接口
 * @author LS0077
 * @version 2017-12-12
 */
@MyBatisDao
public interface MeetingDao extends CrudDao<Meeting> {

	
	List<Meeting> findListByProc(@Param("meeting")Meeting meeting, @Param("procInsIds")List<String> procInsIds);
	
	List<Meeting> findListByProcIsNull(@Param("meeting")Meeting meeting);
	
	List<Meeting> pendingFindListByProc(@Param("meeting")Meeting meeting, @Param("procInsIds")List<String> procInsIds);
}