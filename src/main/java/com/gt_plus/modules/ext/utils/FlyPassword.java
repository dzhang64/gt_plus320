package com.gt_plus.modules.ext.utils;

import java.security.MessageDigest;

public class FlyPassword {

	  public static String encodePassword(String userPassword)
	  {
	    byte[] unencodedPassword = userPassword.getBytes();

	    MessageDigest md = null;
	    try
	    {
	      md = MessageDigest.getInstance("SHA");
	    } catch (Exception e) {
	      return userPassword;
	    }

	    md.reset();

	    md.update(unencodedPassword);

	    byte[] encodedPassword = md.digest();

	    StringBuffer buf = new StringBuffer();

	    for (int i = 0; i < encodedPassword.length; i++) {
	      if ((encodedPassword[i] & 0xFF) < 16) {
	        buf.append("0");
	      }

	      buf.append(Long.toString(encodedPassword[i] & 0xFF, 16));
	    }

	    return buf.toString();
	  }

	  public static void main(String[] args)
	  {
	    System.out.println("Fly 123456=" + encodePassword("123456"));
	  }
}
