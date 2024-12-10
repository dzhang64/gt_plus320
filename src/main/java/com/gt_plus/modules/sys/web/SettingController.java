/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.web;

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

import com.google.common.collect.Lists;
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
import com.gt_plus.modules.sys.entity.Setting;
import com.gt_plus.modules.sys.service.SettingService;
/**
 * 参数设置Controller
 * @author David
 * @version 2017-11-07
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/setting")
public class SettingController extends BaseController {

	@Autowired
	private SettingService settingService;
	
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public Setting get(@RequestParam(required=false) String id) {
		Setting entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = settingService.get(id);
		}
		if (entity == null){
			entity = new Setting();
		}
		return entity;
	}
	
	/**
	 * 参数设置列表页面
	 */
	@RequiresPermissions("sys:setting:list")
	@RequestMapping(value = {"list", ""})
	public String list(Setting setting, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/sys/settingList";
	}
	
	/**
	 * 参数设置列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sys:setting:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(Setting setting, HttpServletRequest request, HttpServletResponse response, Model model) {
		setting.getSqlMap().put("dsf", this.getDataScope());
		Page<Setting> page = settingService.findPage(new Page<Setting>(request, response), setting); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑参数设置表单页面
	 */
	@RequiresPermissions(value={"sys:setting:view","sys:setting:add","sys:setting:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Setting setting, Model model) {
		model.addAttribute("setting", setting);
		return "modules/sys/settingForm";
	}

	/**
	 * 保存参数设置
	 */
	@ResponseBody
	@RequiresPermissions(value={"sys:setting:add","sys:setting:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Setting setting, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, setting)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		*/
		if(!setting.getIsNewRecord()){
			//修改保存
			Setting t = settingService.get(setting.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) settingService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(setting, t);
				settingService.save(t);
			}
		}else{
			//新建保存
			settingService.save(setting);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存参数设置信息成功","参数设置"));
		//保存成功后处理逻辑
		this.afterSave("参数设置", setting);
		return j;
	}
	
	/**
	 * 删除参数设置
	 */
	@ResponseBody
	@RequiresPermissions("sys:setting:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Setting setting, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			setting.setDelFlag(Global.YES);
			settingService.saveV(setting); 
		}
		settingService.delete(setting);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除参数设置信息成功","参数设置"));
		return j;
	}
	
	/**
	 * 批量删除参数设置
	 */
	@ResponseBody
	@RequiresPermissions("sys:setting:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				Setting obj = settingService.get(id);
				obj.setDelFlag(Global.YES);
				settingService.saveV(obj); 
			}
			settingService.delete(settingService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除参数设置信息成功","参数设置"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("sys:setting:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Setting setting, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "参数设置"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Setting> page = settingService.findPage(new Page<Setting>(request, response, -1), setting);
    		new ExportExcel("参数设置", Setting.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出参数设置信息记录失败！", "参数设置") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sys:setting:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Setting> list = ei.getDataList(Setting.class);
			for (Setting setting : list){
				try{
					settingService.save(setting);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条参数设置记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条参数设置记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入参数设置失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/setting/?repage";
    }
	
	/**
	 * 下载导入参数设置数据模板
	 */
	@RequiresPermissions("sys:setting:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "参数设置数据导入模板.xlsx";
    		List<Setting> list = Lists.newArrayList(); 
    		new ExportExcel("参数设置数据", Setting.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/setting/?repage";
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
	private void afterSave(String title, Setting setting) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/sys/setting");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, setting.getOwnerCode(), roleMap);
	}
}