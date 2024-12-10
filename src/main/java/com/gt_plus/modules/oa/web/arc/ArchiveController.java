/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.web.arc;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.gt_plus.modules.bas.service.basmessage.BasMessageService;

import com.gt_plus.modules.oa.entity.arc.ArcCategory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import com.gt_plus.modules.oa.entity.arc.Archive;
import com.gt_plus.modules.oa.service.arc.ArchiveService;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 档案Controller
 * @author LS0077
 * @version 2017-11-07
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/arc/archive")
public class ArchiveController extends BaseController {

	@Autowired
	private ArchiveService archiveService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public Archive get(@RequestParam(required=false) String id) {
		Archive entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = archiveService.get(id);
		}
		if (entity == null){
			entity = new Archive();
		}
		return entity;
	}
	
	

	@RequiresPermissions("oa:arc:archive:list")
	@RequestMapping(value = {"index"})
	public String index(ArcCategory user, Model model) {
		return "modules/oa/arc/archiveIndex";
	}
	
	@RequiresPermissions("oa:arc:archive:list")
	@RequestMapping(value = "userSelect")
	public String userSelect(boolean isMultiSelect, Model model) {
		model.addAttribute("isMultiSelect", isMultiSelect);
		return "modules/common/userSelect";
	}
	
	/*@RequiresPermissions("oa")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String officeId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<ArcCategory> list = systemService.findUserByOfficeId(officeId);
		for (int i=0; i<list.size(); i++){
			User e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", "u_"+e.getId());
			map.put("pId", officeId);
			map.put("name", StringUtils.replace(e.getName(), " ", ""));
			mapList.add(map);
		}
		return mapList;
	}*/
	
	/**
	 * 档案列表页面
	 */
	@RequiresPermissions("oa:arc:archive:list")
	@RequestMapping(value = {"list", ""})
	public String list(Archive archive, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/oa/arc/archiveList";
	}
	
	/**
	 * 档案列表数据
	 */
	@ResponseBody
	@RequiresPermissions("oa:arc:archive:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(Archive archive, HttpServletRequest request, HttpServletResponse response, Model model) {
		archive.getSqlMap().put("dsf", this.getDataScope());
		Page<Archive> page = archiveService.findPage(new Page<Archive>(request, response), archive); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑档案表单页面
	 */
	@RequiresPermissions(value={"oa:arc:archive:view","oa:arc:archive:add","oa:arc:archive:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Archive archive, Model model) {
		model.addAttribute("archive", archive);
		return "modules/oa/arc/archiveForm";
	}

	/**
	 * 保存档案
	 */
	@ResponseBody
	@RequiresPermissions(value={"oa:arc:archive:add","oa:arc:archive:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Archive archive, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, archive)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		*/
		if(!archive.getIsNewRecord()){
			//修改保存
			Archive t = archiveService.get(archive.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) archiveService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(archive, t);
				archiveService.save(t);
			}
		}else{
			//新建保存
			archive.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			archiveService.save(archive);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存档案信息成功","档案"));
		//保存成功后处理逻辑
		this.afterSave("档案", archive);
		return j;
	}
	
	/**
	 * 删除档案
	 */
	@ResponseBody
	@RequiresPermissions("oa:arc:archive:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Archive archive, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			archive.setDelFlag(Global.YES);
			archiveService.saveV(archive); 
		}
		archiveService.delete(archive);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除档案信息成功","档案"));
		return j;
	}
	
	/**
	 * 批量删除档案
	 */
	@ResponseBody
	@RequiresPermissions("oa:arc:archive:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				Archive obj = archiveService.get(id);
				obj.setDelFlag(Global.YES);
				archiveService.saveV(obj); 
			}
			archiveService.delete(archiveService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除档案信息成功","档案"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("oa:arc:archive:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Archive archive, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "档案"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Archive> page = archiveService.findPage(new Page<Archive>(request, response, -1), archive);
    		new ExportExcel("档案", Archive.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出档案信息记录失败！", "档案") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:arc:archive:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Archive> list = ei.getDataList(Archive.class);
			for (Archive archive : list){
				try{
					archiveService.save(archive);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条档案记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条档案记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入档案失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/arc/archive/?repage";
    }
	
	/**
	 * 下载导入档案数据模板
	 */
	@RequiresPermissions("oa:arc:archive:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "档案数据导入模板.xlsx";
    		List<Archive> list = Lists.newArrayList(); 
    		new ExportExcel("档案数据", Archive.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/arc/archive/?repage";
    }
	
	
	/**
	 * 选择档案目录
	 */
	@RequestMapping(value = "selectarc")
	public String selectarc(ArcCategory arc, String url, String fieldLabels, String fieldKeys, String searchLabel, String searchKey, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ArcCategory> page = archiveService.findPageByarc(new Page<ArcCategory>(request, response),  arc);
		try {
			fieldLabels = URLDecoder.decode(fieldLabels, "UTF-8");
			fieldKeys = URLDecoder.decode(fieldKeys, "UTF-8");
			searchLabel = URLDecoder.decode(searchLabel, "UTF-8");
			searchKey = URLDecoder.decode(searchKey, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		model.addAttribute("labelNames", fieldLabels.split("\\|"));
		model.addAttribute("labelValues", fieldKeys.split("\\|"));
		model.addAttribute("fieldLabels", fieldLabels);
		model.addAttribute("fieldKeys", fieldKeys);
		model.addAttribute("url", url);
		model.addAttribute("searchLabel", searchLabel);
		model.addAttribute("searchKey", searchKey);
		model.addAttribute("obj", arc);
		model.addAttribute("page", page);
		return "modules/sys/gridselect";
	}
	
	/**
	 * 创建数据范围
	 */
	private String getDataScope() {
		StringBuilder sqlString = new StringBuilder();
		return sqlString.toString();
	}
	
	/**
	 * 保存成功后处理逻辑
	 */
	private void afterSave(String title, Archive archive) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/arc/archive");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, archive.getOwnerCode(), roleMap);
	}
}