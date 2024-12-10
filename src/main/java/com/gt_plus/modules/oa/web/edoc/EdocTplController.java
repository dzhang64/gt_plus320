/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.web.edoc;

import java.io.File;
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
import com.gt_plus.common.utils.Encodes;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.ExportExcel;
import com.gt_plus.common.utils.excel.ImportExcel;
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.oa.entity.edoc.EdocTpl;
import com.gt_plus.modules.oa.service.edoc.EdocTplService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 模版类型Controller
 * @author GT0155
 * @version 2017-12-17
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/edoc/edocTpl")
public class EdocTplController extends BaseController {

	@Autowired
	private EdocTplService edocTplService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public EdocTpl get(@RequestParam(required=false) String id) {
		EdocTpl entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = edocTplService.get(id);
		}
		if (entity == null){
			entity = new EdocTpl();
		}
		return entity;
	}
	
	/**
	 * 模版类型列表页面
	 */
	@RequiresPermissions("oa:edoc:edocTpl:list")
	@RequestMapping(value = {"list", ""})
	public String list(EdocTpl edocTpl, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/oa/edoc/edocTplList";
	}
	
	/**
	 * 模版类型列表数据
	 */
	@ResponseBody
	@RequiresPermissions("oa:edoc:edocTpl:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(EdocTpl edocTpl, HttpServletRequest request, HttpServletResponse response, Model model) {
		edocTpl.getSqlMap().put("dsf", this.getDataScope());
		Page<EdocTpl> page = edocTplService.findPage(new Page<EdocTpl>(request, response), edocTpl); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑模版类型表单页面
	 */
	@RequiresPermissions(value={"oa:edoc:edocTpl:view","oa:edoc:edocTpl:add","oa:edoc:edocTpl:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(EdocTpl edocTpl, Model model) {
		model.addAttribute("edocTpl", edocTpl);
		return "modules/oa/edoc/edocTplForm";
	}
	

	/**
	 * 保存模版类型
	 */
	@ResponseBody
	@RequiresPermissions(value={"oa:edoc:edocTpl:add","oa:edoc:edocTpl:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(EdocTpl edocTpl, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, edocTpl)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!edocTpl.getIsNewRecord()){
			//修改保存
			EdocTpl t = edocTplService.get(edocTpl.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) edocTplService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(edocTpl, t);
				edocTplService.save(t);
			}
		}else{
			//新建保存
			edocTpl.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			edocTplService.save(edocTpl);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存模版类型信息成功","模版类型"));
		//保存成功后处理逻辑
		this.afterSave("模版类型", edocTpl);
		return j;
	}
	
	/**
	 * 删除模版类型
	 */
	@ResponseBody
	@RequiresPermissions("oa:edoc:edocTpl:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(EdocTpl edocTpl, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			edocTpl.setDelFlag(Global.YES);
			edocTplService.saveV(edocTpl); 
		}
		edocTplService.delete(edocTpl);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除模版类型信息成功","模版类型"));
		return j;
	}
	
	/**
	 * 批量删除模版类型
	 */
	@ResponseBody
	@RequiresPermissions("oa:edoc:edocTpl:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				EdocTpl obj = edocTplService.get(id);
				obj.setDelFlag(Global.YES);
				edocTplService.saveV(obj); 
			}
			edocTplService.delete(edocTplService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除模版类型信息成功","模版类型"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("oa:edoc:edocTpl:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(EdocTpl edocTpl, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "模版类型"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<EdocTpl> page = edocTplService.findPage(new Page<EdocTpl>(request, response, -1), edocTpl);
    		new ExportExcel("模版类型", EdocTpl.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出模版类型信息记录失败！", "模版类型") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:edoc:edocTpl:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<EdocTpl> list = ei.getDataList(EdocTpl.class);
			for (EdocTpl edocTpl : list){
				try{
					edocTplService.save(edocTpl);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条模版类型记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条模版类型记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模版类型失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/edoc/edocTpl/?repage";
    }
	
	/**
	 * 下载导入模版类型数据模板
	 */
	@RequiresPermissions("oa:edoc:edocTpl:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "模版类型数据导入模板.xlsx";
    		List<EdocTpl> list = Lists.newArrayList(); 
    		new ExportExcel("模版类型数据", EdocTpl.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/edoc/edocTpl/?repage";
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
	private void afterSave(String title, EdocTpl edocTpl) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/edoc/edocTpl");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, edocTpl.getOwnerCode(), roleMap);
	}
	
}