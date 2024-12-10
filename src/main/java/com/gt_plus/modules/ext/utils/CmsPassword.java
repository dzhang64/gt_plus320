package com.gt_plus.modules.ext.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class CmsPassword {

	public static String encodePassword(String userPassword) {
		String password = userPassword;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] digest;
			digest = messageDigest.digest(userPassword.getBytes("UTF-8"));
			password = new String(Hex.encodeHex(digest));
		} catch (NoSuchAlgorithmException err) {
			err.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return password;
	}
	
	public static void main(String[] args) {
		System.out.println("CMS 123456=" + encodePassword("123456"));
	}
}
