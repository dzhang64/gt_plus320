/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysuserinterentry.service;

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
import com.gt_plus.modules.sysusercontractrenew.entity.SysUserContractRenew;
import com.gt_plus.modules.sysusercontractrenew.service.SysUserContractRenewService;
import com.gt_plus.modules.sysuserinterentry.dao.SysInternUserInformationDao;
import com.gt_plus.modules.sysuserinterentry.entity.SysInternUserInformation;
import com.gt_plus.modules.tools.utils.TwoDimensionCode;
import com.gt_plus.modules.userinfo.entity.UserInfo;
import com.gt_plus.modules.userinfo.service.UserInfoService;

/**
 * 实习生入职Service
 * @author zdy
 * @version 2018-01-31
 */
@Service
@Transactional(readOnly = true)
public class SysInternUserInformationService extends ActService<SysInternUserInformationDao, SysInternUserInformation> {
	private static final String PROCDEFKEY = "TraineeEntry";
	
	@Autowired
	private SysInternUserInformationDao sysInternUserInformationDao;
	
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private SysUserContractRenewService sysUserContractRenewService;
	
	@Autowired
	private SystemService systemService;

		
	public SysInternUserInformation get(String id) {
		return super.get(id);
	}
	
	public List<SysInternUserInformation> findList(SysInternUserInformation sysInternUserInformation) {
		return super.findList(sysInternUserInformation);
	}
	
	public Page<SysInternUserInformation> findPage(Page<SysInternUserInformation> page, SysInternUserInformation sysInternUserInformation) {
		return super.findPage(page, sysInternUserInformation);
	}
	
	@Transactional(readOnly = false)
	public void save(SysInternUserInformation sysInternUserInformation) {
		super.save(sysInternUserInformation);
	}
	
	@Transactional(readOnly = false)
	public void saveV(SysInternUserInformation sysInternUserInformation) {
		super.saveV(sysInternUserInformation);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysInternUserInformation sysInternUserInformation) {
		super.delete(sysInternUserInformation);
		super.deleteAct(sysInternUserInformation);
	}
	
	
	@Transactional(readOnly = false)
	public void saveAct(SysInternUserInformation sysInternUserInformation,HttpServletRequest request) {
		
		//构造合同编号
		if(sysInternUserInformation.getContract()==null||sysInternUserInformation.getContract().length()<=0){
			String contractNum = CompanyUser.makeContratNum();
			sysInternUserInformation.setContract(contractNum);
		}
		
		Map<String, Object> vars = Maps.newHashMap();
		int completeAct = super.saveAct(sysInternUserInformation, "实习生入职", PROCDEFKEY, this.getClass().getName(), vars);
		
		//如果流程是最后一步，即结束流程
		if(completeAct ==1){
			
			//构造登陆名
			if(sysInternUserInformation.getLoginName()==null||sysInternUserInformation.getLoginName().length()<=0){
				String name =  Pinyin.getPinyinString(sysInternUserInformation.getName());
				String idcardNum = sysInternUserInformation.getIdcardNum();
				if(name!=null&&name.length()>0&&idcardNum!=null&&idcardNum.length()>4){
					name = UserUtils.getloginName(name, idcardNum);
					sysInternUserInformation.setLoginName(name);
				}
			}
			
		     //获取工号
			if(sysInternUserInformation.getNameCode()==null||sysInternUserInformation.getNameCode().length()<=0){
				sysInternUserInformation.setNameCode(UserUtils.getUserCode("SX"));
			}
			
			//存储用户到基础信息表
			 UserInfo userInfo = new UserInfo(sysInternUserInformation);
			 userInfoService.save(userInfo);
			
			 //存储到合同续签表
			 SysUserContractRenew sysUserContractRenew = new SysUserContractRenew(sysInternUserInformation);
			 sysUserContractRenewService.save(sysUserContractRenew);
			 
			 //存储用户到系统登录表
			 User user = new User(sysInternUserInformation);
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
				this.save(sysInternUserInformation);
		}
	}
	
	public LinkedHashMap<String, Object> getStartingUserList(SysInternUserInformation sysInternUserInformation){
		sysInternUserInformation.getAct().setProcDefKey(PROCDEFKEY);
		return super.getStartingUserList(sysInternUserInformation);
	}
	
	public void setRuleArgs(SysInternUserInformation sysInternUserInformation){
			sysInternUserInformation.getAct().setProcDefKey(PROCDEFKEY);
			super.setRuleArgs(sysInternUserInformation);
			
	}
	
	public LinkedHashMap<String, Object> getTargetUserList(SysInternUserInformation sysInternUserInformation,String[] ids){
		
			if(ids ==null||ids.length<=0){
				if (StringUtils.isEmpty(sysInternUserInformation.getId()) ||
						StringUtils.isEmpty(sysInternUserInformation.getProcInsId())){
					sysInternUserInformation.getAct().setProcDefKey(PROCDEFKEY);
					return super.getStartingUserList(sysInternUserInformation);
				} else{
					sysInternUserInformation.getAct().setProcDefKey(PROCDEFKEY);
					return super.getTargetUserList(sysInternUserInformation);
				}
			}else{
				return super.getWebUserList(ids); 
			}
		}
	
	/**
	 * 通过待办、在办、已办、待发、已发读取相应的列表
	 * @param page
	 * @param sysInternUserInformation
	 * @param path
	 * @return
	 */
	public Page<SysInternUserInformation> findPage(Page<SysInternUserInformation> page, SysInternUserInformation sysInternUserInformation, String path) {
		if (super.isUnsent(path)) {
			sysInternUserInformation.setPage(page);
			sysInternUserInformation.getSqlMap().put("dsf", " AND a.create_by = '" + UserUtils.getUser().getId() + "' ");
			page.setList(dao.findListByProcIsNull(sysInternUserInformation));
		} else { 
			List<String> procInsIds = super.getProcInsIds(path, page);
			if (procInsIds.size() > 0) {
				sysInternUserInformation.setPage(page);
				page.setList(dao.findListByProc(sysInternUserInformation, procInsIds));
			} else {
				sysInternUserInformation.setPage(page);
				List<SysInternUserInformation> list = Lists.newArrayList();
				page.setList(list);
			}
		}
		return page;
	}
	
	/**
	 * 设置工作流信息，打开Form表单时调用
	 * @param sysInternUserInformation
	 */
	public void setAct(SysInternUserInformation sysInternUserInformation) {
		super.setAct(sysInternUserInformation);
	}
	
	
	
}