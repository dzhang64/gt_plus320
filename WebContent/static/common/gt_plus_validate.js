$.validator.addMethod("mobile", function(value, element) {
    var length = value.length;
    return this.optional(element) || (length == 11 && /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/.test(value));    
}, "输入格式有误，请重新输入"); //手机号码格式错误

//1、输入区号+号码
//2、号码+分机号（1-5位）  或者   号码+转（汉字）+分机号（1-5位）
//3、号码
$.validator.addMethod("phone", function(value, element) {
	var tel = /^((\d{3,4}\-)|)\d{7,8}(|([-\u8f6c]{1}\d{1,5}))$/;
	return this.optional(element) || (tel.test(value));
}, "输入格式有误，请重新输入"); //电话号码格式错误

// 联系电话(手机/电话皆可)验证
$.validator.addMethod("telephone", function(value, element) {
	var length = value.length;
	var mobile = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
	var tel = /^((\d{3,4}\-)|)\d{7,8}(|([-\u8f6c]{1}\d{1,5}))$/;
	return this.optional(element) || tel.test(value)
			|| (length == 11 && mobile.test(value));
}, "输入格式有误，请重新输入");//请正确填写您的联系方式

$.validator.addMethod("isQq", function(value, element) {
	return this.optional(element) || /^[1-9]\d{4,12}$/;
}, "输入格式有误，请重新输入"); //QQ号码不正确

$.validator.addMethod("fax", function(value, element) {
	var tel = /^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$/;
	return this.optional(element) || (tel.test(value));
}, "输入格式有误，请重新输入"); //传真号码格式错误

$.validator.addMethod("zipCode", function(value, element) {
	var tel = /^[0-9]{6}$/;
	return this.optional(element) || (tel.test(value));
}, "输入格式有误，请重新输入");//邮政编码格式错误

// 身份证号码验证
$.validator.addMethod("idCard", function(value, element) {
	return this.optional(element) || isIdCardNo(value);
}, "输入格式有误，请重新输入");//请输入正确的身份证号码

// 身份证号码验证  (人员基本信息用)
$.validator.addMethod("idCardUser", function(value, element) {
   var value= $.trim(value);
	var flag= this.optional(element) || isIdCardNo(value);
	if(flag){
       birthday();
	}
	return flag;
}, "输入格式有误，请重新输入");//请输入正确的身份证号码

// 银行卡号验证
$.validator.addMethod("bankNo", function(value, element) {
	return this.optional(element) || isBankNo(value);
}, "输入格式有误，请重新输入");//请输入正确的银行卡号

//IP地址验证   
$.validator.addMethod("ip", function(value, element) {    
	return this.optional(element) || /^((?:(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d))))$/.test(value);    
}, "输入格式有误，请重新输入");//请填写正确的IP地址。

// 请输入正确的纬度数90"
$.validator.addMethod("latitude", function(value, element) {
	var latitude = /^[-]?(\d|([1-8]\d)|(90))(\.\d*)?$/g;
	return this.optional(element) || (latitude.test(value));
}, "输入格式有误，请重新输入"); //经度格式错误

// 请输入正确的经度数180"
$.validator.addMethod("longitude", function(value, element) {
	var longitude = /^[-]?(\d|([1-9]\d)|(1[0-7]\d)|(180))(\.\d*)?$/g;
	return this.optional(element) || (longitude.test(value));
}, "输入格式有误，请重新输入"); //纬度格式错误

// 字符验证，只能包含中文、英文、数字、下划线等字符。
$.validator.addMethod("stringCheck", function(value, element) {
	return this.optional(element)
			|| /^[a-zA-Z0-9\u4e00-\u9fa5-_]+$/.test(value);
}, "输入格式有误，请重新输入"); //只能包含中文、英文、数字、下划线等字符

$.validator.addMethod("isEnglish", function(value, element) {
	return this.optional(element) || /^[A-Za-z]+$/.test(value);
}, "输入格式有误，请重新输入"); //只能包含英文字符

$.validator.addMethod("isChinese", function(value, element) {
	return this.optional(element) || /^[\u4e00-\u9fa5]+$/.test(value);
}, "输入格式有误，请重新输入");//只能包含中文字符

// 匹配中文(包括汉字和字符)
$.validator.addMethod("isChineseChar", function(value, element) {
	return this.optional(element) || /^[\u0391-\uFFE5]+$/.test(value);
}, "输入格式有误，请重新输入"); //请填写中文(包括汉字和字符) 

// 判断是否为合法字符(a-zA-Z0-9-_)
$.validator.addMethod("isRightfulString", function(value, element) {
	return this.optional(element) || /^[A-Za-z0-9_-]+$/.test(value);
}, "输入格式有误，请重新输入"); //格式错误(可包括数字、字符、—_)

//判断是否包含中英文特殊字符，除英文"-_"字符外
$.validator.addMethod("isContainsSpecialChar", function(value, element) {  
	var reg = RegExp(/[(\ )(\`)(\~)(\!)(\@)(\#)(\$)(\%)(\^)(\&)(\*)(\()(\))(\+)(\=)(\|)(\{)(\})(\')(\:)(\;)(\')(',)(\[)(\])(\.)(\<)(\>)(\/)(\?)(\~)(\！)(\@)(\#)(\￥)(\%)(\…)(\&)(\*)(\（)(\）)(\—)(\+)(\|)(\{)(\})(\【)(\】)(\‘)(\；)(\：)(\”)(\“)(\’)(\。)(\，)(\、)(\？)]+/);   
	return this.optional(element) || !reg.test(value);       
}, "输入格式有误，请重新输入");//含有中英文特殊字符

$.validator.addMethod("isDigits", function(value, element) {
	return this.optional(element) || /^\d+$/.test(value);
}, "输入格式有误，请重新输入");//只能输入0-9数字

$.validator.addMethod("isNumber", function(value, element) {
	return this.optional(element) || /^(-?[1-9]\d*|0)$/.test(value) || /^(-?[1-9]\d*\.\d+|-?0\.\d*[1-9]\d*|0\.0+)$/.test(value);
}, "输入格式有误，请重新输入");//匹配数值类型，包括整数和浮点数

$.validator.addMethod("isInteger", function(value, element) {
	return this.optional(element) || (/^(-?[1-9]\d*|0)$/.test(value));
}, "输入格式有误，请重新输入"); //匹配整数类型

$.validator.addMethod("isIntEqZero", function(value, element) {
	return this.optional(element) || (/^0$/.test(value));
}, "输入格式有误，请重新输入"); //整数必须为0

$.validator.addMethod("isIntGtZero", function(value, element) {
	return this.optional(element) || (/^[1-9]\d*$/.test(value));
}, "输入格式有误，请重新输入"); //整数必须大于0

$.validator.addMethod("isIntGteZero", function(value, element) {
	return this.optional(element) || (/^([1-9]\d*|0)$/.test(value));
}, "输入格式有误，请重新输入"); //整数必须大于或等于0

$.validator.addMethod("isIntNEqZero", function(value, element) {
	return this.optional(element) || (/^-?[1-9]\d*$/.test(value));
}, "输入格式有误，请重新输入");//整数必须不等于0

$.validator.addMethod("isIntLtZero", function(value, element) {
	return this.optional(element) || (/^-[1-9]\d*$/.test(value));
}, "输入格式有误，请重新输入");//整数必须小于0

$.validator.addMethod("isIntLteZero", function(value, element) {
	return this.optional(element) || (/^(-[1-9]\d*|0)$/.test(value));
}, "输入格式有误，请重新输入");//整数必须小于或等于0

$.validator.addMethod("isFloat", function(value, element) {
	return this.optional(element) || /^(-?[1-9]\d*\.\d+|-?0\.\d*[1-9]\d*|0\.0+)$/.test(value);
}, "输入格式有误，请重新输入");//匹配浮点数

$.validator.addMethod("isFloatEqZero", function(value, element) {
	return this.optional(element) || /^0\.0+$/.test(value);
}, "输入格式有误，请重新输入");//浮点数必须为0

$.validator.addMethod("isFloatGtZero", function(value, element) {
	return this.optional(element) || /^([1-9]\d*\.\d+|0\.\d*[1-9]\d*)$/.test(value);
}, "输入格式有误，请重新输入");//浮点数必须大于0

$.validator.addMethod("isFloatGteZero", function(value, element) {
	return this.optional(element) || /^([1-9]\d*\.\d+|0\.\d*[1-9]\d*|0\.0+)$/.test(value);
}, "输入格式有误，请重新输入");//浮点数必须大于或等于0

$.validator.addMethod("isFloatNEqZero", function(value, element) {
	return this.optional(element) || /^(-?[1-9]\d*\.\d+|-?0\.\d*[1-9]\d*)$/.test(value);
}, "输入格式有误，请重新输入");//浮点数必须不等于0

$.validator.addMethod("isFloatLtZero", function(value, element) {
	return this.optional(element) || /^(-[1-9]\d*\.\d+|-0\.\d*[1-9]\d*)$/.test(value);
}, "输入格式有误，请重新输入");//浮点数必须小于0

$.validator.addMethod("isFloatLteZero", function(value, element) {
	return this.optional(element) || /^(-[1-9]\d*\.\d+|-0\.\d*[1-9]\d*|0\.0+)$/.test(value);
}, "输入格式有误，请重新输入");//浮点数必须小于或等于0

function isBankNo(bankno) {
	var lastNum = bankno.substr(bankno.length - 1, 1);// 取出最后一位（与luhm进行比较）
	var first15Num = bankno.substr(0, bankno.length - 1);// 前15或18位
	var newArr = new Array();
	for (var i = first15Num.length - 1; i > -1; i--) { // 前15或18位倒序存进数组
		newArr.push(first15Num.substr(i, 1));
	}
	var arrJiShu = new Array(); // 奇数位*2的积 <9
	var arrJiShu2 = new Array(); // 奇数位*2的积 >9
	var arrOuShu = new Array(); // 偶数位数组
	for (var j = 0; j < newArr.length; j++) {
		if ((j + 1) % 2 == 1) {// 奇数位
			if (parseInt(newArr[j]) * 2 < 9)
				arrJiShu.push(parseInt(newArr[j]) * 2);
			else
				arrJiShu2.push(parseInt(newArr[j]) * 2);
		} else
			// 偶数位
			arrOuShu.push(newArr[j]);
	}
	var jishu_child1 = new Array();// 奇数位*2 >9 的分割之后的数组个位数
	var jishu_child2 = new Array();// 奇数位*2 >9 的分割之后的数组十位数
	for (var h = 0; h < arrJiShu2.length; h++) {
		jishu_child1.push(parseInt(arrJiShu2[h]) % 10);
		jishu_child2.push(parseInt(arrJiShu2[h]) / 10);
	}
	var sumJiShu = 0; // 奇数位*2 < 9 的数组之和
	var sumOuShu = 0; // 偶数位数组之和
	var sumJiShuChild1 = 0; // 奇数位*2 >9 的分割之后的数组个位数之和
	var sumJiShuChild2 = 0; // 奇数位*2 >9 的分割之后的数组十位数之和
	var sumTotal = 0;
	for (var m = 0; m < arrJiShu.length; m++) {
		sumJiShu = sumJiShu + parseInt(arrJiShu[m]);
	}
	for (var n = 0; n < arrOuShu.length; n++) {
		sumOuShu = sumOuShu + parseInt(arrOuShu[n]);
	}
	for (var p = 0; p < jishu_child1.length; p++) {
		sumJiShuChild1 = sumJiShuChild1 + parseInt(jishu_child1[p]);
		sumJiShuChild2 = sumJiShuChild2 + parseInt(jishu_child2[p]);
	}
	// 计算总和
	sumTotal = parseInt(sumJiShu) + parseInt(sumOuShu)
			+ parseInt(sumJiShuChild1) + parseInt(sumJiShuChild2);
	// 计算Luhm值
	var k = parseInt(sumTotal) % 10 == 0 ? 10 : parseInt(sumTotal) % 10;
	var luhm = 10 - k;
	if (lastNum == luhm) {
		console.info("Luhm验证通过");
		return true;
	} else {
		console.info("银行卡号必须符合Luhm校验");
		return false;
	}
}

// 身份证号码的验证规则
function isIdCardNo(code) {
	var city = {
		11 : "北京",
		12 : "天津",
		13 : "河北",
		14 : "山西",
		15 : "内蒙古",
		21 : "辽宁",
		22 : "吉林",
		23 : "黑龙江 ",
		31 : "上海",
		32 : "江苏",
		33 : "浙江",
		34 : "安徽",
		35 : "福建",
		36 : "江西",
		37 : "山东",
		41 : "河南",
		42 : "湖北 ",
		43 : "湖南",
		44 : "广东",
		45 : "广西",
		46 : "海南",
		50 : "重庆",
		51 : "四川",
		52 : "贵州",
		53 : "云南",
		54 : "西藏 ",
		61 : "陕西",
		62 : "甘肃",
		63 : "青海",
		64 : "宁夏",
		65 : "新疆",
		71 : "台湾",
		81 : "香港",
		82 : "澳门",
		91 : "国外 "
	};
	var tip = "";
	var pass = true;
	if (code.length != 18) {
		tip = "身份证号格式错误";
		pass = false;
	}

	if (!code
			|| !/^\d{6}(18|19|20)?\d{2}(0[1-9]|1[12])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i
					.test(code)) {
		tip = "身份证号格式错误";
		pass = false;
	}

	else if (!city[code.substr(0, 2)]) {
		tip = "地址编码错误";
		pass = false;
	} else {
		// 18位身份证需要验证最后一位校验位
		if (code.length == 18) {
			code = code.split('');
			// ∑(ai×Wi)(mod 11)
			// 加权因子
			var factor = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 ];
			// 校验位
			var parity = [ 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 ];
			var sum = 0;
			var ai = 0;
			var wi = 0;
			for (var i = 0; i < 17; i++) {
				ai = code[i];
				wi = factor[i];
				sum += ai * wi;
			}
			var last = parity[sum % 11];
			if (parity[sum % 11] != code[17]) {
				tip = "校验位错误";
				pass = false;
			}
		}
	}
	return pass;
}