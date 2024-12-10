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
import com.gt_plus.modules.chart.entity.pie.ChartPie;
import com.gt_plus.modules.chart.service.pie.ChartPieService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 饼图Controller
 * @author GT0155
 * @version 2017-09-06
 */
@Controller
@RequestMapping(value = "${adminPath}/chart/pie/chartPie")
public class ChartPieController extends BaseController {

	@Autowired
	private ChartPieService chartPieService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public ChartPie get(@RequestParam(required=false) String id) {
		ChartPie entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = chartPieService.get(id);
		}
		if (entity == null){
			entity = new ChartPie();
		}
		return entity;
	}
	
	/**
	 * 饼图列表页面
	 */
	@RequiresPermissions("chart:pie:chartPie:list")
	@RequestMapping(value = {"list", ""})
	public String list(ChartPie chartPie, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		
		String title = "PIE CHART DEMO o(╯□╰)o";
		String subTitle = "小标题";
		String describe = "数据描述";
		Map<String, Object> data = new HashMap<String, Object>();
		for (int i = 1; i <= 5; i++) {
			data.put("模块" + i, Math.random());
		}
		
		Map<String, Object> pie = ChartUtils.getPie(title, subTitle, describe, data);
		model.addAttribute("pie", pie);
		
		return "modules/chart/pie/chartPieList";
	}
	
	/**
	 * 饼图列表数据
	 */
	@ResponseBody
	@RequiresPermissions("chart:pie:chartPie:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(ChartPie chartPie, HttpServletRequest request, HttpServletResponse response, Model model) {
		chartPie.getSqlMap().put("dsf", this.getDataScope());
		Page<ChartPie> page = chartPieService.findPage(new Page<ChartPie>(request, response), chartPie); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑饼图表单页面
	 */
	@RequiresPermissions(value={"chart:pie:chartPie:view","chart:pie:chartPie:add","chart:pie:chartPie:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(ChartPie chartPie, Model model) {
		model.addAttribute("chartPie", chartPie);
		return "modules/chart/pie/chartPieForm";
	}

	/**
	 * 保存饼图
	 */
	@ResponseBody
	@RequiresPermissions(value={"chart:pie:chartPie:add","chart:pie:chartPie:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(ChartPie chartPie, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, chartPie)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		*/
		if(!chartPie.getIsNewRecord()){
			//修改保存
			ChartPie t = chartPieService.get(chartPie.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) chartPieService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(chartPie, t);
				chartPieService.save(t);
			}
		}else{
			//新建保存
			chartPie.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			chartPieService.save(chartPie);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存饼图信息成功","饼图"));
		//保存成功后处理逻辑
		this.afterSave("饼图", chartPie);
		return j;
	}
	
	/**
	 * 删除饼图
	 */
	@ResponseBody
	@RequiresPermissions("chart:pie:chartPie:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(ChartPie chartPie, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			chartPie.setDelFlag(Global.YES);
			chartPieService.saveV(chartPie); 
		}
		chartPieService.delete(chartPie);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除饼图信息成功","饼图"));
		return j;
	}
	
	/**
	 * 批量删除饼图
	 */
	@ResponseBody
	@RequiresPermissions("chart:pie:chartPie:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				ChartPie obj = chartPieService.get(id);
				obj.setDelFlag(Global.YES);
				chartPieService.saveV(obj); 
			}
			chartPieService.delete(chartPieService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除饼图信息成功","饼图"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("chart:pie:chartPie:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(ChartPie chartPie, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "饼图"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ChartPie> page = chartPieService.findPage(new Page<ChartPie>(request, response, -1), chartPie);
    		new ExportExcel("饼图", ChartPie.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出饼图信息记录失败！", "饼图") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("chart:pie:chartPie:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ChartPie> list = ei.getDataList(ChartPie.class);
			for (ChartPie chartPie : list){
				try{
					chartPieService.save(chartPie);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条饼图记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条饼图记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入饼图失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/chart/pie/chartPie/?repage";
    }
	
	/**
	 * 下载导入饼图数据模板
	 */
	@RequiresPermissions("chart:pie:chartPie:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "饼图数据导入模板.xlsx";
    		List<ChartPie> list = Lists.newArrayList(); 
    		new ExportExcel("饼图数据", ChartPie.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/chart/pie/chartPie/?repage";
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
	private void afterSave(String title, ChartPie chartPie) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/chart/pie/chartPie");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, chartPie.getOwnerCode(), roleMap);
	}
}