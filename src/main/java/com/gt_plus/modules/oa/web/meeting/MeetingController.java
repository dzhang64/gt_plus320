/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.web.meeting;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.google.common.collect.Maps;
import com.gt_plus.modules.bas.service.basmessage.BasMessageService;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.gt_plus.common.utils.DateUtils;
import com.gt_plus.common.utils.MyBeanUtils;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.ExportExcel;
import com.gt_plus.common.utils.excel.ImportExcel;
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.oa.entity.meeting.Meeting;
import com.gt_plus.modules.oa.service.meeting.MeetingService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 会议管理Controller
 * @author LS0077
 * @version 2017-12-12
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/meeting/meeting")
public class MeetingController extends BaseController {

	@Autowired
	private MeetingService meetingService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public Meeting get(@RequestParam(required=false) String id) {
		Meeting entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = meetingService.get(id);
		}
		if (entity == null){
			entity = new Meeting();
		}
		return entity;
	}
	
	/**
	 * 会议管理列表页面
	 */
	@RequiresPermissions("oa:meeting:meeting:list")
	@RequestMapping(value = "list/{path}")
	public String list(@PathVariable("path")String path, Meeting meeting, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		model.addAttribute("path", path);
		if(path.equalsIgnoreCase(MeetingService.PATH_QUERY)) {
			return "modules/oa/meeting/meetingListQuery";
		} else {
			return "modules/oa/meeting/meetingList";
		}
	}
	
	/**
	 * 会议管理待办列表页面
	 */
	@RequiresPermissions("oa:meeting:meeting:pendingList")
	@RequestMapping(value = "pendingList/{path}")
	public String pendingList(@PathVariable("path")String path, Meeting meeting, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		model.addAttribute("path", path);
		if(path.equalsIgnoreCase(MeetingService.PATH_QUERY)) {
			return "modules/oa/meeting/meetingListQuery";
		} else {
			return "modules/oa/meeting/meetingPendingList";
		}
	}
	
	/**
	 * 会议管理列表数据
	 */
	@ResponseBody
	@RequiresPermissions("oa:meeting:meeting:list")
	@RequestMapping(value = "data/{path}")
	public Map<String, Object> data(@PathVariable("path")String path, Meeting meeting, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Meeting> page = new Page<Meeting>();
		if(path.equalsIgnoreCase(MeetingService.PATH_QUERY)) {
			meeting.getSqlMap().put("dsf", this.getDataScopeForAct());
			page = meetingService.findPage(new Page<Meeting>(request, response), meeting); 
		} else {
			meeting.getSqlMap().put("dsf", this.getDataScope());
			page = meetingService.findPage(new Page<Meeting>(request, response), meeting, path); 
		}
		return getBootstrapData(page);
	}
	
	/**
	 * 会议待开列表数据
	 */
	@ResponseBody
	@RequiresPermissions("oa:meeting:meeting:list")
	@RequestMapping(value = "pendingData/{path}")
	public Map<String, Object> pendingData(@PathVariable("path")String path, Meeting meeting, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Meeting> page = new Page<Meeting>();
		if(path.equalsIgnoreCase(MeetingService.PATH_QUERY)) {
			meeting.getSqlMap().put("dsf", this.getDataScopeForAct());
			page = meetingService.findPage(new Page<Meeting>(request, response), meeting); 
		} else {
			//meeting.getSqlMap().put("dsf", " AND a.participation_name LIKE '%" + UserUtils.getUser().getName() +":canj%'");
			page = meetingService.pendingFindPage(new Page<Meeting>(request, response), meeting, path); 
		}
		//首页统计数量---begin---2017-12-14
		if ("count".equalsIgnoreCase(path)) {
			path = "Done";
			Map<String, Object> map = Maps.newHashMap();
			//meeting.getSqlMap().put("dsf", " AND a.participation_name LIKE '%" + UserUtils.getUser().getName() +":canj%'");
			page = meetingService.pendingFindPage(new Page<Meeting>(request, response), meeting, path); 
			map.put("count", page.getList().size());
			return map;
		}
		//首页统计数量---end
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑会议管理表单页面
	 */
	@RequiresPermissions(value={"oa:meeting:meeting:view","oa:meeting:meeting:add","oa:meeting:meeting:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Meeting meeting, Model model) {
		if (StringUtils.isEmpty(meeting.getProcInsId())) {
			LinkedHashMap<String, Object> map = meetingService.getStartingUserList(meeting);
			model.addAttribute("userList", new Gson().toJson(map.get("userList")));
			model.addAttribute("type", map.get("type"));
		}
		meetingService.setAct(meeting);
		meetingService.setRuleArgs(meeting);
		//放入当前人姓名
		model.addAttribute("userName", UserUtils.getUser().getName());
		model.addAttribute("meeting", meeting);
		return "modules/oa/meeting/meetingForm";
	}

	/**
	 * 保存会议管理
	 */
	@ResponseBody
	@RequiresPermissions(value={"oa:meeting:meeting:add","oa:meeting:meeting:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Meeting meeting, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, meeting)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!meeting.getIsNewRecord()){
			//修改保存
			Meeting t = meetingService.get(meeting.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) meetingService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(meeting, t);
				meetingService.saveAct(t);
			}
		}else{
			//新建保存
			meeting.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			meetingService.saveAct(meeting);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存会议管理信息成功","会议管理"));
		//保存成功后处理逻辑
		this.afterSave("会议管理", meeting);
		return j;
	}
	
	/**
	 * 删除会议管理
	 */
	@ResponseBody
	@RequiresPermissions("oa:meeting:meeting:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Meeting meeting, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			meeting.setDelFlag(Global.YES);
			meetingService.saveV(meeting); 
		}
		meetingService.delete(meeting);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除会议管理信息成功","会议管理"));
		return j;
	}
	
	/**
	 * 批量删除会议管理
	 */
	@ResponseBody
	@RequiresPermissions("oa:meeting:meeting:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				Meeting obj = meetingService.get(id);
				obj.setDelFlag(Global.YES);
				meetingService.saveV(obj); 
			}
			meetingService.delete(meetingService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除会议管理信息成功","会议管理"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("oa:meeting:meeting:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Meeting meeting, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "会议管理"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Meeting> page = meetingService.findPage(new Page<Meeting>(request, response, -1), meeting);
    		new ExportExcel("会议管理", Meeting.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出会议管理信息记录失败！", "会议管理") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:meeting:meeting:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Meeting> list = ei.getDataList(Meeting.class);
			for (Meeting meeting : list){
				try{
					meetingService.save(meeting);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条会议管理记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条会议管理记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入会议管理失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/meeting/meeting/?repage";
    }
	
	/**
	 * 下载导入会议管理数据模板
	 */
	@RequiresPermissions("oa:meeting:meeting:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "会议管理数据导入模板.xlsx";
    		List<Meeting> list = Lists.newArrayList(); 
    		new ExportExcel("会议管理数据", Meeting.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/meeting/meeting/?repage";
    }
	
	
	
	/**
	 * 创建数据范围
	 */
	private String getDataScope() {
		StringBuilder sqlString = new StringBuilder();
		return sqlString.toString();
	}
	
	private String getDataScopeForAct() {
		StringBuilder sqlString = new StringBuilder();
		return sqlString.toString();
	}
	
	/**
	 * 保存成功后处理逻辑
	 */
	private void afterSave(String title, Meeting meeting) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/meeting/meeting");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, meeting.getOwnerCode(), roleMap);
	}
	
	@ResponseBody
	@RequiresPermissions("oa:meeting:meeting:list")
	@RequestMapping(value = "getUserList")
	public AjaxJson getUserList(Meeting meeting, HttpServletRequest request, HttpServletResponse response, Model model) {
		AjaxJson j = new AjaxJson();
		LinkedHashMap<String, Object> targetUserInfo = meetingService.getTargetUserList(meeting);
		j.setBody(targetUserInfo);
		j.setSuccess(true);
		return j;
	}
}