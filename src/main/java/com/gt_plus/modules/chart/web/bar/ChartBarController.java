/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.chart.web.bar;

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
import com.gt_plus.modules.chart.entity.bar.ChartBar;
import com.gt_plus.modules.chart.service.bar.ChartBarService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 柱状图Controller
 * @author GT0155
 * @version 2017-09-07
 */
@Controller
@RequestMapping(value = "${adminPath}/chart/bar/chartBar")
public class ChartBarController extends BaseController {

	@Autowired
	private ChartBarService chartBarService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public ChartBar get(@RequestParam(required=false) String id) {
		ChartBar entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = chartBarService.get(id);
		}
		if (entity == null){
			entity = new ChartBar();
		}
		return entity;
	}
	
	/**
	 * 柱状图列表页面
	 */
	@RequiresPermissions("chart:bar:chartBar:list")
	@RequestMapping(value = {"list", ""})
	public String list(ChartBar chartBar, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		
		String title = "柱状图动画延迟";
		String subTitle = "小标题";
		String barName = "'bar1','bar2'";//柱名称数量取决于data数量（两个map对应两个柱名称）
		Map<String, Object> data1 = new HashMap<String, Object>();
		Map<String, Object> data2 = new HashMap<String, Object>();
		for (int i = 1; i <= 10; i++) {
		    data1.put("类目" + i, (Math.sin(i / 5) * (i / 5 -10) + i / 6) * 5);
		    data2.put("类目" + i, (Math.cos(i / 5) * (i / 5 -10) + i / 6) * 5);
		}
		
		Map<String, Object> bar = ChartUtils.getBar(title, subTitle, barName, data1, data2);
		model.addAttribute("barData", bar);
		
		return "modules/chart/bar/chartBarList";
	}
	
	/**
	 * 柱状图列表数据
	 */
	@ResponseBody
	@RequiresPermissions("chart:bar:chartBar:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(ChartBar chartBar, HttpServletRequest request, HttpServletResponse response, Model model) {
		chartBar.getSqlMap().put("dsf", this.getDataScope());
		Page<ChartBar> page = chartBarService.findPage(new Page<ChartBar>(request, response), chartBar); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑柱状图表单页面
	 */
	@RequiresPermissions(value={"chart:bar:chartBar:view","chart:bar:chartBar:add","chart:bar:chartBar:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(ChartBar chartBar, Model model) {
		model.addAttribute("chartBar", chartBar);
		return "modules/chart/bar/chartBarForm";
	}

	/**
	 * 保存柱状图
	 */
	@ResponseBody
	@RequiresPermissions(value={"chart:bar:chartBar:add","chart:bar:chartBar:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(ChartBar chartBar, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, chartBar)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		*/
		if(!chartBar.getIsNewRecord()){
			//修改保存
			ChartBar t = chartBarService.get(chartBar.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) chartBarService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(chartBar, t);
				chartBarService.save(t);
			}
		}else{
			//新建保存
			chartBar.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			chartBarService.save(chartBar);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存柱状图信息成功","柱状图"));
		//保存成功后处理逻辑
		this.afterSave("柱状图", chartBar);
		return j;
	}
	
	/**
	 * 删除柱状图
	 */
	@ResponseBody
	@RequiresPermissions("chart:bar:chartBar:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(ChartBar chartBar, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			chartBar.setDelFlag(Global.YES);
			chartBarService.saveV(chartBar); 
		}
		chartBarService.delete(chartBar);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除柱状图信息成功","柱状图"));
		return j;
	}
	
	/**
	 * 批量删除柱状图
	 */
	@ResponseBody
	@RequiresPermissions("chart:bar:chartBar:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				ChartBar obj = chartBarService.get(id);
				obj.setDelFlag(Global.YES);
				chartBarService.saveV(obj); 
			}
			chartBarService.delete(chartBarService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除柱状图信息成功","柱状图"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("chart:bar:chartBar:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(ChartBar chartBar, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "柱状图"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ChartBar> page = chartBarService.findPage(new Page<ChartBar>(request, response, -1), chartBar);
    		new ExportExcel("柱状图", ChartBar.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出柱状图信息记录失败！", "柱状图") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("chart:bar:chartBar:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ChartBar> list = ei.getDataList(ChartBar.class);
			for (ChartBar chartBar : list){
				try{
					chartBarService.save(chartBar);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条柱状图记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条柱状图记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入柱状图失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/chart/bar/chartBar/?repage";
    }
	
	/**
	 * 下载导入柱状图数据模板
	 */
	@RequiresPermissions("chart:bar:chartBar:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "柱状图数据导入模板.xlsx";
    		List<ChartBar> list = Lists.newArrayList(); 
    		new ExportExcel("柱状图数据", ChartBar.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/chart/bar/chartBar/?repage";
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
	private void afterSave(String title, ChartBar chartBar) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/chart/bar/chartBar");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, chartBar.getOwnerCode(), roleMap);
	}
}