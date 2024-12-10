package com.gt_plus.modules.ext.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

public class SeeyonPassword {
	static MessageDigest digester = null;

	public static String encodePassword(String userName ,String userPassword)
			throws SecurityException {
		String password = userPassword;
		try {
			if (digester == null) digester = MessageDigest.getInstance("SHA-1");
			byte[] value;
			synchronized (digester) {
				digester.reset();
				value = digester.digest(userPassword.getBytes());

				digester.update(userName.getBytes());
				value = digester.digest(value);
			}
			password = new String(Base64.encodeBase64(value));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return password;
	}
	
	public static void main(String[] args) {
		System.out.println("Seeyon wangq 123456=" + encodePassword("wangq", "123456"));
	}
}
