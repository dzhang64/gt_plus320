package com.gt_plus.common.utils;

public class WebUser {
	
	private String type;
	
	private String value;
	
	private String result;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public WebUser(String type, String value, String result) {
		super();
		this.type = type;
		this.value = value;
		this.result = result;
	}

	public WebUser() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
