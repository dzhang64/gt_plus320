/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.web.teamwork;

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
import com.gt_plus.modules.oa.entity.teamwork.Teamwork;
import com.gt_plus.modules.oa.service.teamwork.TeamworkService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 协同管理Controller
 * @author LS0077
 * @version 2017-12-11
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/teamwork/teamwork")
public class TeamworkController extends BaseController {

	@Autowired
	private TeamworkService teamworkService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public Teamwork get(@RequestParam(required=false) String id) {
		Teamwork entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = teamworkService.get(id);
		}
		if (entity == null){
			entity = new Teamwork();
		}
		return entity;
	}
	
	/**
	 * 协同管理列表页面
	 */
	@RequiresPermissions("oa:teamwork:teamwork:list")
	@RequestMapping(value = "list/{path}")
	public String list(@PathVariable("path")String path, Teamwork teamwork, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		model.addAttribute("path", path);
		if(path.equalsIgnoreCase(TeamworkService.PATH_QUERY)) {
			return "modules/oa/teamwork/teamworkListQuery";
		} else {
			return "modules/oa/teamwork/teamworkList";
		}
	}
	
	/**
	 * 协同管理列表数据
	 */
	@ResponseBody
	@RequiresPermissions("oa:teamwork:teamwork:list")
	@RequestMapping(value = "data/{path}")
	public Map<String, Object> data(@PathVariable("path")String path, Teamwork teamwork, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Teamwork> page = new Page<Teamwork>();
		if(path.equalsIgnoreCase(TeamworkService.PATH_QUERY)) {
			teamwork.getSqlMap().put("dsf", this.getDataScopeForAct());
			page = teamworkService.findPage(new Page<Teamwork>(request, response), teamwork); 
		} else {
			teamwork.getSqlMap().put("dsf", this.getDataScope());
			page = teamworkService.findPage(new Page<Teamwork>(request, response), teamwork, path); 
		}
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑协同管理表单页面
	 */
	@RequiresPermissions(value={"oa:teamwork:teamwork:view","oa:teamwork:teamwork:add","oa:teamwork:teamwork:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Teamwork teamwork, Model model) {
		if (StringUtils.isEmpty(teamwork.getProcInsId())) {
			LinkedHashMap<String, Object> map = teamworkService.getStartingUserList(teamwork);
			model.addAttribute("userList", new Gson().toJson(map.get("userList")));
			model.addAttribute("type", map.get("type"));
		}	
		teamworkService.setAct(teamwork);
		teamworkService.setRuleArgs(teamwork);
		model.addAttribute("teamwork", teamwork);
		return "modules/oa/teamwork/teamworkForm";
	}

	/**
	 * 保存协同管理
	 */
	@ResponseBody
	@RequiresPermissions(value={"oa:teamwork:teamwork:add","oa:teamwork:teamwork:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Teamwork teamwork, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, teamwork)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!teamwork.getIsNewRecord()){
			//修改保存
			Teamwork t = teamworkService.get(teamwork.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) teamworkService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(teamwork, t);
				teamworkService.saveAct(t);
			}
		}else{
			//新建保存
			teamwork.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			teamworkService.saveAct(teamwork);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存协同管理信息成功","协同管理"));
		//保存成功后处理逻辑
		this.afterSave("协同管理", teamwork);
		return j;
	}
	
	/**
	 * 删除协同管理
	 */
	@ResponseBody
	@RequiresPermissions("oa:teamwork:teamwork:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Teamwork teamwork, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			teamwork.setDelFlag(Global.YES);
			teamworkService.saveV(teamwork); 
		}
		teamworkService.delete(teamwork);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除协同管理信息成功","协同管理"));
		return j;
	}
	
	/**
	 * 批量删除协同管理
	 */
	@ResponseBody
	@RequiresPermissions("oa:teamwork:teamwork:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				Teamwork obj = teamworkService.get(id);
				obj.setDelFlag(Global.YES);
				teamworkService.saveV(obj); 
			}
			teamworkService.delete(teamworkService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除协同管理信息成功","协同管理"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("oa:teamwork:teamwork:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Teamwork teamwork, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "协同管理"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Teamwork> page = teamworkService.findPage(new Page<Teamwork>(request, response, -1), teamwork);
    		new ExportExcel("协同管理", Teamwork.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出协同管理信息记录失败！", "协同管理") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:teamwork:teamwork:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Teamwork> list = ei.getDataList(Teamwork.class);
			for (Teamwork teamwork : list){
				try{
					teamworkService.save(teamwork);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条协同管理记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条协同管理记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入协同管理失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/teamwork/teamwork/?repage";
    }
	
	/**
	 * 下载导入协同管理数据模板
	 */
	@RequiresPermissions("oa:teamwork:teamwork:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "协同管理数据导入模板.xlsx";
    		List<Teamwork> list = Lists.newArrayList(); 
    		new ExportExcel("协同管理数据", Teamwork.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/teamwork/teamwork/?repage";
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
	private void afterSave(String title, Teamwork teamwork) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/teamwork/teamwork");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, teamwork.getOwnerCode(), roleMap);
	}
	
	@ResponseBody
	@RequiresPermissions("oa:teamwork:teamwork:list")
	@RequestMapping(value = "getUserList")
	public AjaxJson getUserList(Teamwork teamwork, HttpServletRequest request, HttpServletResponse response, Model model) {
		AjaxJson j = new AjaxJson();
		LinkedHashMap<String, Object> targetUserInfo = teamworkService.getTargetUserList(teamwork);
		j.setBody(targetUserInfo);
		j.setSuccess(true);
		return j;
	}
}