/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusercommonentry.service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.ActService;
import com.gt_plus.common.utils.CompanyUser;
import com.gt_plus.common.utils.FileUtils;
import com.gt_plus.common.utils.Pinyin;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.service.SystemService;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.sysusercommonentry.dao.SysCommonUserInformationDao;
import com.gt_plus.modules.sysusercommonentry.entity.SysCommonUserInformation;
import com.gt_plus.modules.sysusercontractrenew.entity.SysUserContractRenew;
import com.gt_plus.modules.sysusercontractrenew.service.SysUserContractRenewService;
import com.gt_plus.modules.tools.utils.TwoDimensionCode;
import com.gt_plus.modules.userinfo.entity.UserInfo;
import com.gt_plus.modules.userinfo.service.UserInfoService;

/**
 * 员工信息表Service
 * @author zdy
 * @version 2018-02-22
 */
@Service
@Transactional(readOnly = true)
public class SysCommonUserInformationService extends ActService<SysCommonUserInformationDao, SysCommonUserInformation> {
	private static final String PROCDEFKEY = "CommonEntry";
	
	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private SystemService systemService;
	@Autowired
	private SysUserContractRenewService sysUserContractRenewService;
		
	public SysCommonUserInformation get(String id) {
		return super.get(id);
	}
	
	public List<SysCommonUserInformation> findList(SysCommonUserInformation sysCommonUserInformation) {
		return super.findList(sysCommonUserInformation);
	}
	
	public Page<SysCommonUserInformation> findPage(Page<SysCommonUserInformation> page, SysCommonUserInformation sysCommonUserInformation) {
		return super.findPage(page, sysCommonUserInformation);
	}
	
	@Transactional(readOnly = false)
	public void save(SysCommonUserInformation sysCommonUserInformation) {
		super.save(sysCommonUserInformation);
	}
	
	@Transactional(readOnly = false)
	public void saveV(SysCommonUserInformation sysCommonUserInformation) {
		super.saveV(sysCommonUserInformation);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysCommonUserInformation sysCommonUserInformation) {
		super.delete(sysCommonUserInformation);
		super.deleteAct(sysCommonUserInformation);
	}
	
	@Transactional(readOnly = false)
	public void saveAct(SysCommonUserInformation sysCommonUserInformation,HttpServletRequest request) {
		//构造合同编号
		if(sysCommonUserInformation.getContract()==null||sysCommonUserInformation.getContract().length()<=0)
		{
					String contractNum = CompanyUser.makeContratNum();
					sysCommonUserInformation.setContract(contractNum);
		}
		
		Map<String, Object> vars = Maps.newHashMap();
		int completeAct = super.saveAct(sysCommonUserInformation, "员工信息表", PROCDEFKEY, this.getClass().getName(), vars);
		
		//如果流程是最后一步，即结束流程
				if(completeAct ==1){
					
					//构造登陆名
					if(sysCommonUserInformation.getLoginName()==null||sysCommonUserInformation.getLoginName().length()<=0){
						String name =  Pinyin.getPinyinString(sysCommonUserInformation.getName());
						String idcardNum = sysCommonUserInformation.getIdcardNum();
						if(name!=null&&name.length()>0&&idcardNum!=null&&idcardNum.length()>4){
							name = UserUtils.getloginName(name, idcardNum);
							sysCommonUserInformation.setLoginName(name);
						}
					}
					
				     //获取工号
					if(sysCommonUserInformation.getNameCode()==null||sysCommonUserInformation.getNameCode().length()<=0){
						sysCommonUserInformation.setNameCode(UserUtils.getUserCode("SS"));
					}
					
					//存储用户到基础信息表
					 UserInfo userInfo = new UserInfo(sysCommonUserInformation);
					 userInfoService.save(userInfo);
					 //存储到合同续签表
					 SysUserContractRenew sysUserContractRenew = new SysUserContractRenew(sysCommonUserInformation);
					 sysUserContractRenewService.save(sysUserContractRenew);
					 
					 //存储用户到系统登录表
					 User user = new User(sysCommonUserInformation);
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
						this.save(sysCommonUserInformation);
				}
	}
	
	
	public LinkedHashMap<String, Object> getStartingUserList(SysCommonUserInformation sysCommonUserInformation){
		sysCommonUserInformation.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(sysCommonUserInformation);
	}
	
	public void setRuleArgs(SysCommonUserInformation sysCommonUserInformation){
			sysCommonUserInformation.getAct().setProcDefKey(PROCDEFKEY);
			super.setRuleArgs(sysCommonUserInformation);
			
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(SysCommonUserInformation sysCommonUserInformation,String[] ids){
		
			if(ids ==null||ids.length<=0){
				if (StringUtils.isEmpty(sysCommonUserInformation.getId()) ||
						StringUtils.isEmpty(sysCommonUserInformation.getProcInsId())){
					sysCommonUserInformation.getAct().setProcDefKey(PROCDEFKEY);
					return super.getStartingUserList(sysCommonUserInformation);
				} else{
					sysCommonUserInformation.getAct().setProcDefKey(PROCDEFKEY);
					return super.getTargetUserList(sysCommonUserInformation);
				}
			}else{
				return super.getWebUserList(ids); 
			}
		}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param sysCommonUserInformation
	 * @param path
	 * @return
	 */
	public Page<SysCommonUserInformation> findPage(Page<SysCommonUserInformation> page, SysCommonUserInformation sysCommonUserInformation, String path) {
		if (super.isUnsent(path)) {
			sysCommonUserInformation.setPage(page);
			sysCommonUserInformation.getSqlMap().put("dsf", " AND a.create_by = '" + UserUtils.getUser().getId() + "' ");
			page.setList(dao.findListByProcIsNull(sysCommonUserInformation));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				sysCommonUserInformation.setPage(page);
				page.setList(dao.findListByProc(sysCommonUserInformation, procInsIds));
			} else {
				sysCommonUserInformation.setPage(page);
				List<SysCommonUserInformation> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param sysCommonUserInformation
	 */
	public void setAct(SysCommonUserInformation sysCommonUserInformation) {
		
		super.setAct(sysCommonUserInformation);
	}
	
}