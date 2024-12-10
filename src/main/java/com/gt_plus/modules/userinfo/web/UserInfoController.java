/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.userinfo.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.commons.io.IOUtils;
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
import com.gt_plus.modules.bas.service.basmessage.BasMessageService;

import com.gt_plus.modules.salarylevel.entity.SalaryLevel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.gt_plus.common.utils.CompanyUser;
import com.gt_plus.common.utils.DateUtils;
import com.gt_plus.common.utils.FileUtils;
import com.gt_plus.common.utils.MyBeanUtils;
import com.gt_plus.common.utils.Pinyin;
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
import com.gt_plus.modules.userinfo.dao.UserInfoDao;
import com.gt_plus.modules.userinfo.entity.UserInfo;
import com.gt_plus.modules.userinfo.service.UserInfoService;
import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.service.OfficeService;
import com.gt_plus.modules.sys.service.SystemService;
import com.gt_plus.modules.tools.utils.TwoDimensionCode;
/**
 * 员工信息Controller
 * @author zdy
 * @version 2018-01-30
 */
@Controller
@RequestMapping(value = "${adminPath}/userinfo/userInfo")
public class UserInfoController extends BaseController {

	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	@Autowired
	private UserInfoDao userInfoDao;
	

	
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public UserInfo get(@RequestParam(required=false) String id) {
		UserInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userInfoService.get(id);
		}
		if (entity == null){
			entity = new UserInfo();
		}
		return entity;
	}
	
	/**
	 * 员工信息列表页面
	 */
	@RequiresPermissions("userinfo:userInfo:list")
	@RequestMapping(value = {"list", ""})
	public String list(UserInfo userInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/userinfo/userInfoList";
	}
	
	/**
	 * 员工信息列表数据
	 */
	@ResponseBody
	@RequiresPermissions("userinfo:userInfo:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(UserInfo userInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		userInfo.getSqlMap().put("dsf", this.getDataScope());
		Page<UserInfo> page = userInfoService.findPage(new Page<UserInfo>(request, response), userInfo); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑员工信息表单页面
	 */
	@RequiresPermissions(value={"userinfo:userInfo:view","userinfo:userInfo:add","userinfo:userInfo:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(UserInfo userInfo, Model model) {
		model.addAttribute("userInfo", userInfo);
		return "modules/userinfo/userInfoForm";
	}
	
	/**
	 * 查看，增加，编辑员工信息表单页面
	 */
	@RequiresPermissions(value={"userinfo:userInfo:view","userinfo:userInfo:add","userinfo:userInfo:edit"},logical=Logical.OR)
	@RequestMapping(value = "formchange")
	public String formChange(UserInfo userInfo, Model model) {
		model.addAttribute("userInfo", userInfo);
		return "modules/userinfo/userInfoFormChange";
	}
	
	
	/**
	 * 角色分配 -- 根据部门编号获取用户列表
	 * @param officeId
	 * @param response
	 * @return
	 */
	@RequiresPermissions("userinfo:userInfo:list")
	@ResponseBody
	@RequestMapping(value = "users")
	public List<Map<String, Object>> users(String officeId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		User user = new User();
		user.setOffice(new Office(officeId));
		Page<User> page = systemService.findUser(new Page<User>(1, -1), user);
		for (User e : page.getList()) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", 0);
			map.put("name", e.getName());
			mapList.add(map);			
		}
		return mapList;
	}
	
	/**
	 * 选择员工的GRID
	 */
	@RequestMapping(value = "selectUserInfo")
	public String selectUserInfo(UserInfo userInfo, String url, String fieldLabels, String fieldKeys,String fieldTypes, String filter,String searchLabel, String searchKey,HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			fieldLabels = URLDecoder.decode(fieldLabels, "UTF-8");
			fieldKeys = URLDecoder.decode(fieldKeys, "UTF-8");
			fieldTypes = URLDecoder.decode(fieldTypes, "UTF-8");
			filter = URLDecoder.decode(filter, "UTF-8");
			searchLabel = URLDecoder.decode(searchLabel, "UTF-8");
			searchKey = URLDecoder.decode(searchKey, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//过滤项，修改该位置的值,在XML文件需要设置过滤项的值
		if(filter!=null&&filter!=""&&filter.length()>0){
			userInfo.setUserStatus(filter);
		}
		Page<UserInfo> page = userInfoService.findPageByUserInfo(new Page<UserInfo>(request, response),  userInfo);
		model.addAttribute("labelNames", fieldLabels.split("\\|"));
		model.addAttribute("labelValues", fieldKeys.split("\\|"));
		model.addAttribute("labelTypes", fieldTypes.split("\\|"));
		model.addAttribute("fieldLabels", fieldLabels);
		model.addAttribute("fieldKeys", fieldKeys);
		model.addAttribute("url", url);
		model.addAttribute("searchLabel", searchLabel);
		model.addAttribute("searchKey", searchKey);
		model.addAttribute("obj", userInfo);
		model.addAttribute("page", page);
		return "modules/sys/gridselect";
	}
	

	/**
	 * 保存员工信息
	 */
	@ResponseBody
	@RequiresPermissions(value={"userinfo:userInfo:add","userinfo:userInfo:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(UserInfo userInfo, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, userInfo)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!userInfo.getIsNewRecord()){
			//修改保存
			UserInfo t = userInfoService.get(userInfo.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) userInfoService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(userInfo, t);
				userInfoService.save(t);
			}
		}else{
			//新建保存
			userInfo.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			userInfoService.save(userInfo);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存员工信息信息成功","员工信息"));
		//保存成功后处理逻辑
		this.afterSave("员工信息", userInfo);
		return j;
	}
	
	/**
	 * 删除员工信息
	 */
	@ResponseBody
	@RequiresPermissions("userinfo:userInfo:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(UserInfo userInfo, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			userInfo.setDelFlag(Global.YES);
			userInfoService.saveV(userInfo); 
		}
		userInfoService.delete(userInfo);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除员工信息信息成功","员工信息"));
		return j;
	}
	
	/**
	 * 批量删除员工信息
	 */
	@ResponseBody
	@RequiresPermissions("userinfo:userInfo:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				UserInfo obj = userInfoService.get(id);
				obj.setDelFlag(Global.YES);
				userInfoService.saveV(obj); 
			}
			userInfoService.delete(userInfoService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除员工信息信息成功","员工信息"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("userinfo:userInfo:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(UserInfo userInfo, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "员工信息"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<UserInfo> page = userInfoService.findPage(new Page<UserInfo>(request, response, -1), userInfo);
    		new ExportExcel("员工信息", UserInfo.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出员工信息信息记录失败！", "员工信息") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("userinfo:userInfo:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes,HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<UserInfo> list = ei.getDataList(UserInfo.class);
			for (UserInfo userInfo : list){
				try{
					//构造合同编号
					if(userInfo.getContract()==null||userInfo.getContract().length()<=0){
						String contractNum = CompanyUser.makeContratNum();
						userInfo.setContract(contractNum);
					}
					
					//构造登陆名
					if(userInfo.getLoginName()==null||userInfo.getLoginName().length()<=0){
						String name =  Pinyin.getPinyinString(userInfo.getName());
						String idcardNum = userInfo.getIdCardNum();
						if(name!=null&&name.length()>0&&idcardNum!=null&&idcardNum.length()>4){
							name = UserUtils.getloginName(name, idcardNum);
							userInfo.setLoginName(name);
						}
					}
					
					//获取工号,由于在数据库获取的是事务管理，只能将代码迁移到该位置
					if(userInfo.getNameCode()==null||userInfo.getNameCode().length()<=0){
						String userType = userInfo.getUserType();
						if(userType.equalsIgnoreCase("1")){
							userType="SX";	
						}
						else{
							userType="SS";
						}
						userInfo.setNameCode(UserUtils.getUserCode(userType));
					}
					
					//创建系统用户
					User user = new User(userInfo);
					
					 if (StringUtils.isNotBlank(user.getNewPassword())) {
							user.setPassword(SystemService.entryptPassword(user.getNewPassword()));
							user.setDesPassword(SystemService.entryptDesPassword(user.getNewPassword()));
						}
						
						// 如果SSO密码为空，则不更换SSO密码
					if (StringUtils.isNotBlank(user.getNewSsoDesPassword())) {
							user.setSsoDesPassword(SystemService.entryptDesPassword(user.getNewSsoDesPassword()));
						}
					 List<String> roleIdList =  Arrays.asList("91766cc228e34269a65f0564ba956bd7");  //设置用户为普通用户角色
					 user.setRoleIdList(roleIdList);
					 
					//生成用户二维码，使用登录名
						String realPath = Global.getUserfilesBaseDir() + Global.USERFILES_BASE_URL
						+ user.getId() + "/qrcode/";
						FileUtils.createDirectory(realPath);
						String name= user.getId()+".png"; //encoderImgId此处二维码的图片名
						String filePath = realPath + name;  //存放路径
						TwoDimensionCode.encoderQRCode(user.getLoginName(), filePath, "png");//执行生成二维码
						user.setQrCode(request.getContextPath()+Global.USERFILES_BASE_URL
							+  user.getId()  + "/qrcode/"+name);
					// 保存用户信息
					systemService.saveUser(user);
						
					//更新对象，保证系统存入用户名和工号	
					userInfoService.save(userInfo);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条员工信息记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条员工信息记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入员工信息失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/userinfo/userInfo/?repage";
    }
	
	/**
	 * 下载导入员工信息数据模板
	 */
	@RequiresPermissions("userinfo:userInfo:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "员工信息数据导入模板.xlsx";
    		List<UserInfo> list = Lists.newArrayList(); 
    		new ExportExcel("员工信息数据", UserInfo.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/userinfo/userInfo/?repage";
    }
	
	
	/**
	 * 选择薪资级别
	 */
	@RequestMapping(value = "selectsalary")
	public String selectsalary(SalaryLevel salary, String url, String fieldLabels, String fieldKeys, String searchLabel, String searchKey, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SalaryLevel> page = userInfoService.findPageBysalary(new Page<SalaryLevel>(request, response),  salary);
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
		model.addAttribute("obj", salary);
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
	private void afterSave(String title, UserInfo userInfo) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/userinfo/userInfo");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, userInfo.getOwnerCode(), roleMap);
	}
	
}