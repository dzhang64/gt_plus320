/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.service.meeting;

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
import com.gt_plus.modules.oa.entity.meeting.Meeting;
import com.gt_plus.modules.oa.dao.meeting.MeetingDao;
import com.gt_plus.common.service.ActService;
import com.gt_plus.common.utils.StringUtils;

/**
 * 会议管理Service
 * @author LS0077
 * @version 2017-12-12
 */
@Service
@Transactional(readOnly = true)
public class MeetingService extends ActService<MeetingDao, Meeting> {
	private static final String PROCDEFKEY = "meetingProcess";
		
	public Meeting get(String id) {
		return super.get(id);
	}
	
	public List<Meeting> findList(Meeting meeting) {
		return super.findList(meeting);
	}
	
	public Page<Meeting> findPage(Page<Meeting> page, Meeting meeting) {
		return super.findPage(page, meeting);
	}
	
	@Transactional(readOnly = false)
	public void save(Meeting meeting) {
		super.save(meeting);
	}
	
	@Transactional(readOnly = false)
	public void saveV(Meeting meeting) {
		super.saveV(meeting);
	}
	
	@Transactional(readOnly = false)
	public void delete(Meeting meeting) {
		super.delete(meeting);
		super.deleteAct(meeting);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(Meeting meeting) {
		Map<String, Object> vars = Maps.newHashMap();
		super.saveAct(meeting, "会议管理", PROCDEFKEY, this.getClass().getName(), vars);
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(Meeting meeting) {
		meeting.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(meeting);
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(Meeting meeting) {
		if (StringUtils.isEmpty(meeting.getId()) || StringUtils.isEmpty(meeting.getProcInsId())) {
			meeting.getAct().setProcDefKey(PROCDEFKEY);
			return super.getStartingUserList(meeting);
		} else {
			meeting.getAct().setProcDefKey(PROCDEFKEY);
			return super.getTargetUserList(meeting);
		}
	}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param meeting
	 * @param path
	 * @return
	 */
	public Page<Meeting> findPage(Page<Meeting> page, Meeting meeting, String path) {
		if (super.isUnsent(path)) {
			meeting.setPage(page);
			meeting.getSqlMap().put("dsf", "AND a.create_by = '" + UserUtils.getUser().getId() + "'");
			page.setList(dao.findListByProcIsNull(meeting));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				meeting.setPage(page);
				page.setList(dao.findListByProc(meeting, procInsIds));
			} else {
				meeting.setPage(page);
				List<Meeting> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 读取待开会议相应的列表
	 * @param page
	 * @param meeting
	 * @param path
	 * @return
	 */
	public Page<Meeting> pendingFindPage(Page<Meeting> page, Meeting meeting, String path) {
		if (super.isUnsent(path)) {
			meeting.setPage(page);
			page.setList(dao.findListByProcIsNull(meeting));
		} else if (super.isTodoAndDoingAndDone(path)) {
			int count = 0;
			path = "todoAndDoing";
			List<String> todoAndDoingProcInsIds = super.getProcInsIds(path, page);
			List<Meeting> pendingFindListByProc = Lists.newArrayList();
			if (todoAndDoingProcInsIds.size() > 0) {
				pendingFindListByProc = dao.pendingFindListByProc(meeting, todoAndDoingProcInsIds);
				count += pendingFindListByProc.size();
			}
			path = "Done";
			List<String> doneProcInsIds = super.getProcInsIds(path, page);
			meeting.getSqlMap().put("dsf", " AND a.participation_name LIKE '%" + UserUtils.getUser().getName() +":canj%'");
			if (doneProcInsIds.size() > 0) {
				List<Meeting> list = dao.pendingFindListByProc(meeting, doneProcInsIds);
				count += list.size();
				if (pendingFindListByProc.size() < 10) {
					for (int i = pendingFindListByProc.size(), j = 0; i < 10 && j < list.size(); i++, j++) {
						Map<String , String> map = Maps.newHashMap();
						map.put("done", "done");
						list.get(j).setRules(map);
						pendingFindListByProc.add(list.get(j));
					}
				}
			}
			page.setCount(count);
			meeting.setPage(page);
			page.setList(pendingFindListByProc);
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				meeting.setPage(page);
				page.setList(dao.pendingFindListByProc(meeting, procInsIds));
			} else {
				meeting.setPage(page);
				List<Meeting> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param meeting
	 */
	public void setAct(Meeting meeting) {
		super.setAct(meeting);
	}
	
}