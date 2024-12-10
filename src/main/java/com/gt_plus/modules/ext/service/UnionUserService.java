package com.gt_plus.modules.ext.service;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.modules.ext.dao.UnionUserDao;
import com.gt_plus.modules.ext.utils.CmsPassword;
import com.gt_plus.modules.ext.utils.FlyPassword;
import com.gt_plus.modules.ext.utils.TrsPassword;

@Service
@Transactional(readOnly = true)
public class UnionUserService {

	@Autowired
	private UnionUserDao unionUserDao;
	
	@Transactional(readOnly=false)
	public void updateUnionUserPasswordForBbs(String loginName,String oldPassoword, String newPassword) {
		String encodeNewPassword = CmsPassword.encodePassword(newPassword);
		String encodeOldPassword = CmsPassword.encodePassword(oldPassoword);
		String sql = "UPDATE JO_USER SET PASSWORD = '" + encodeNewPassword + "' WHERE USERNAME = '" + loginName /*+ "' AND PASSWORD = '" + encodeOldPassword*/ + "'";
		unionUserDao.execUpdateSql(sql);
	}
	
	@Transactional(readOnly=false)
	public void updateUnionUserPasswordForPortal(String loginName,String oldPassoword, String newPassword) {
		String encodeNewPassword = CmsPassword.encodePassword(newPassword);
		String encodeOldPassword = CmsPassword.encodePassword(oldPassoword);
		String sql = "UPDATE JO_USER SET PASSWORD = '" + encodeNewPassword + "' WHERE USERNAME = '" + loginName /*+ "' AND PASSWORD = '" + encodeOldPassword*/ + "'";
		unionUserDao.execUpdateSql(sql);
	}
	
	@Transactional(readOnly=false)
	public void updateUnionUserPasswordForFly(String loginName,String oldPassoword, String newPassword) {
		String encodeNewPassword = FlyPassword.encodePassword(newPassword);
		String encodeOldPassword = FlyPassword.encodePassword(oldPassoword);
		String sql = "UPDATE \"OAIS\".\"APP_USER\" SET PASSWORD = '" + encodeNewPassword + "' WHERE USERNAME = '" + loginName /*+ "' AND PASSWORD = '" + encodeOldPassword*/ + "'";
		unionUserDao.execUpdateSql(sql);
	}
	
	@Transactional(readOnly=false)
	public void updateUnionUserPasswordForTrs(String loginName,String oldPassoword, String newPassword) {
		TrsPassword trsPassword = new TrsPassword();
		String encodeNewPassword = trsPassword.encodePassword(newPassword);
		String encodeOldPassword = trsPassword.encodePassword(oldPassoword);
		String sql = "UPDATE \"TRSWCMV7\".\"WCMUSER\" SET PASSWORD = '" + encodeNewPassword + "' WHERE USERNAME = '" + loginName /*+ "' AND PASSWORD = '" + encodeOldPassword*/ + "'";
		unionUserDao.execUpdateSql(sql);
	}

	@Transactional(readOnly=false)
	public boolean checkUnionUserPasswordForBbs(String loginName, String oldPassword) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String encodePassword = CmsPassword.encodePassword(oldPassword);
		String sql = "SELECT PASSWORD FROM JO_USER WHERE USERNAME = '" + loginName + "'";
		List<Object> list = unionUserDao.execSelectSql(sql);
		for (Object object : list) {
			Field[] fields = object.getClass().getFields();
			for (Field field : fields) {
				System.out.println("bbs:" + String.valueOf(field.get(object)));
				System.out.println("bbs:" + String.valueOf(field.get(object)).equals(encodePassword));
				if (String.valueOf(field.get(object)).equals(encodePassword)) {
					return true;
				}
			}
		}
		return false;
	}

	@Transactional(readOnly=false)
	public boolean checkUnionUserPasswordForPortal(String loginName, String oldPassword) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String encodePassword = CmsPassword.encodePassword(oldPassword);
		String sql = "SELECT PASSWORD FROM JO_USER WHERE USERNAME = '" + loginName + "'";
		List<Object> list = unionUserDao.execSelectSql(sql);
		for (Object object : list) {
			Field[] fields = object.getClass().getFields();
			for (Field field : fields) {
				System.out.println("cms:" + String.valueOf(field.get(object)));
				System.out.println("cms:" + String.valueOf(field.get(object)).equals(encodePassword));
				if (String.valueOf(field.get(object)).equals(encodePassword)) {
					return true;
				}
			}
		}
		return false;
	}

	@Transactional(readOnly=false)
	public boolean checkUnionUserPasswordForFly(String loginName, String oldPassword) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String encodePassword = FlyPassword.encodePassword(oldPassword);
		String sql = "SELECT PASSWORD FROM \"OAIS\".\"APP_USER\" WHERE USERNAME = '" + loginName + "'";
		List<Object> list = unionUserDao.execSelectSql(sql);
		for (Object object : list) {
			Field[] fields = object.getClass().getFields();
			for (Field field : fields) {
				System.out.println("fly:" + String.valueOf(field.get(object)));
				System.out.println("fly:" + String.valueOf(field.get(object)).equals(encodePassword));
				if (String.valueOf(field.get(object)).equals(encodePassword)) {
					return true;
				}
			}
		}
		return false;
	}

	@Transactional(readOnly=false)
	public boolean checkUnionUserPasswordForTrs(String loginName, String oldPassword) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		TrsPassword trsPassword = new TrsPassword();
		String encodePassword = trsPassword.encodePassword(oldPassword);
		String sql = "SELECT PASSWORD FROM \"TRSWCMV7\".\"WCMUSER\" WHERE USERNAME = '" + loginName + "'";
		List<Object> list = unionUserDao.execSelectSql(sql);
		for (Object object : list) {
			Field[] fields = object.getClass().getFields();
			for (Field field : fields) {
				System.out.println("trs:" + String.valueOf(field.get(object)));
				System.out.println("trs:" + String.valueOf(field.get(object)).equals(encodePassword));
				if (String.valueOf(field.get(object)).equals(encodePassword)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
