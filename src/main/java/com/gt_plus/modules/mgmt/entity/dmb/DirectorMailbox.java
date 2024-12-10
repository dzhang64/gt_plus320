/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.mgmt.entity.dmb;

import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.DataEntity;

/**
 * 局长信箱Entity
 * @author GT0155
 * @version 2017-11-28
 */
public class DirectorMailbox extends DataEntity<DirectorMailbox> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String comp;		// 单位名称
	private String name;		// 姓名
	private String tel;		// 联系电话
	private String address;		// 联系地址
	private String email;		// 电子邮箱
	private String pcode;		// 邮政编码
	private String kind;		// 分类
	private String title;		// 标题
	private String content0;		// 正文
	private String content1;		// 正文1
	private String content2;		// 正文2
	
	public DirectorMailbox() {
		super();
	}

	public DirectorMailbox(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="单位名称", align=2, sort=8)
	public String getComp() {
		return comp;
	}

	public void setComp(String comp) {
		this.comp = comp;
	}
	
	
	@ExcelField(title="姓名", align=2, sort=9)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	@ExcelField(title="联系电话", align=2, sort=10)
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
	
	
	@ExcelField(title="联系地址", align=2, sort=11)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
	@ExcelField(title="电子邮箱", align=2, sort=12)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
	@ExcelField(title="邮政编码", align=2, sort=13)
	public String getPcode() {
		return pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}
	
	
	@ExcelField(title="分类", dictType="director_mailbox_kind", align=2, sort=14)
	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}
	
	
	@ExcelField(title="标题", align=2, sort=15)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
	@ExcelField(title="正文", align=2, sort=16)
	public String getContent0() {
		return content0;
	}

	public void setContent0(String content0) {
		this.content0 = content0;
	}
	
	
	@ExcelField(title="正文1", align=2, sort=17)
	public String getContent1() {
		return content1;
	}

	public void setContent1(String content1) {
		this.content1 = content1;
	}
	
	
	@ExcelField(title="正文2", align=2, sort=18)
	public String getContent2() {
		return content2;
	}

	public void setContent2(String content2) {
		this.content2 = content2;
	}
	
	public String getContent() {
		String content = "　　";
		if(content0!=null){
			content += content0;
		}
		if(content1!=null){
			content += content1;
		}
		if(content2!=null){
			content += content2;
		}
		if(content!=""){
			return content.replaceAll("&lt;br&gt;", "\r");
		}
		return content;
	}
}