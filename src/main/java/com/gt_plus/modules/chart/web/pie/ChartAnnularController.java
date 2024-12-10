/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.chart.web.pie;

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
import com.gt_plus.modules.sys.utils.ChartUtils;
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.chart.entity.pie.ChartAnnular;
import com.gt_plus.modules.chart.service.pie.ChartAnnularService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 环形图Controller
 * @author GT0155
 * @version 2017-09-07
 */
@Controller
@RequestMapping(value = "${adminPath}/chart/pie/chartAnnular")
public class ChartAnnularController extends BaseController {

	@Autowired
	private ChartAnnularService chartAnnularService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public ChartAnnular get(@RequestParam(required=false) String id) {
		ChartAnnular entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = chartAnnularService.get(id);
		}
		if (entity == null){
			entity = new ChartAnnular();
		}
		return entity;
	}
	
	/**
	 * 环形图列表页面
	 */
	@RequiresPermissions("chart:pie:chartAnnular:list")
	@RequestMapping(value = {"list", ""})
	public String list(ChartAnnular chartAnnular, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		
		String title = "环形图示例";
		String subTitle = "小标题";
		String describe = "数据描述";
		Map<String, Object> data = new HashMap<String, Object>();
		for (int i = 1; i <= 5; i++){
			data.put("模块" + i , Math.random());
		}
		
		Map<String, Object> annular = ChartUtils.getAnnular(title, subTitle, describe, data);
		model.addAttribute("annular", annular);
		
		return "modules/chart/pie/chartAnnularList";
	}
	
	/**
	 * 环形图列表数据
	 */
	@ResponseBody
	@RequiresPermissions("chart:pie:chartAnnular:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(ChartAnnular chartAnnular, HttpServletRequest request, HttpServletResponse response, Model model) {
		chartAnnular.getSqlMap().put("dsf", this.getDataScope());
		Page<ChartAnnular> page = chartAnnularService.findPage(new Page<ChartAnnular>(request, response), chartAnnular); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑环形图表单页面
	 */
	@RequiresPermissions(value={"chart:pie:chartAnnular:view","chart:pie:chartAnnular:add","chart:pie:chartAnnular:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(ChartAnnular chartAnnular, Model model) {
		model.addAttribute("chartAnnular", chartAnnular);
		return "modules/chart/pie/chartAnnularForm";
	}

	/**
	 * 保存环形图
	 */
	@ResponseBody
	@RequiresPermissions(value={"chart:pie:chartAnnular:add","chart:pie:chartAnnular:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(ChartAnnular chartAnnular, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, chartAnnular)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		*/
		if(!chartAnnular.getIsNewRecord()){
			//修改保存
			ChartAnnular t = chartAnnularService.get(chartAnnular.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) chartAnnularService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(chartAnnular, t);
				chartAnnularService.save(t);
			}
		}else{
			//新建保存
			chartAnnular.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			chartAnnularService.save(chartAnnular);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存环形图信息成功","环形图"));
		//保存成功后处理逻辑
		this.afterSave("环形图", chartAnnular);
		return j;
	}
	
	/**
	 * 删除环形图
	 */
	@ResponseBody
	@RequiresPermissions("chart:pie:chartAnnular:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(ChartAnnular chartAnnular, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			chartAnnular.setDelFlag(Global.YES);
			chartAnnularService.saveV(chartAnnular); 
		}
		chartAnnularService.delete(chartAnnular);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除环形图信息成功","环形图"));
		return j;
	}
	
	/**
	 * 批量删除环形图
	 */
	@ResponseBody
	@RequiresPermissions("chart:pie:chartAnnular:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				ChartAnnular obj = chartAnnularService.get(id);
				obj.setDelFlag(Global.YES);
				chartAnnularService.saveV(obj); 
			}
			chartAnnularService.delete(chartAnnularService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除环形图信息成功","环形图"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("chart:pie:chartAnnular:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(ChartAnnular chartAnnular, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "环形图"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ChartAnnular> page = chartAnnularService.findPage(new Page<ChartAnnular>(request, response, -1), chartAnnular);
    		new ExportExcel("环形图", ChartAnnular.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出环形图信息记录失败！", "环形图") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("chart:pie:chartAnnular:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ChartAnnular> list = ei.getDataList(ChartAnnular.class);
			for (ChartAnnular chartAnnular : list){
				try{
					chartAnnularService.save(chartAnnular);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条环形图记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条环形图记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入环形图失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/chart/pie/chartAnnular/?repage";
    }
	
	/**
	 * 下载导入环形图数据模板
	 */
	@RequiresPermissions("chart:pie:chartAnnular:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "环形图数据导入模板.xlsx";
    		List<ChartAnnular> list = Lists.newArrayList(); 
    		new ExportExcel("环形图数据", ChartAnnular.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/chart/pie/chartAnnular/?repage";
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
	private void afterSave(String title, ChartAnnular chartAnnular) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/chart/pie/chartAnnular");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, chartAnnular.getOwnerCode(), roleMap);
	}
}