<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/webpage/include/taglib.jsp"%>

<html>
<title>正文</title>
<link href="${ctxStatic}/plugin/jquery-jbox/2.3/Skins/Bootstrap/jbox.min.css" rel="stylesheet" />
<link href="${ctxStatic}/plugin/iCheck/custom.css" rel="stylesheet" />
<link href="${ctxStatic}/plugin/dataTables/dataTables.bootstrap.css" rel="stylesheet" />
<link href="${ctxStatic}/common/gt_plus.css" type="text/css" rel="stylesheet" />
<link href="${ctxStatic}/plugin/bootstrap/3.3.4/css_default/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="${ctxStatic}/plugin/awesome/4.4/css/font-awesome.min.css" rel="stylesheet" />
<link href="${ctxStatic}/common/css/style.css?v=3.2.0" type="text/css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/plugin/toastr/toastr.css">
<link rel="stylesheet" type="text/css" href="${ctxStatic}/plugin/webuploader-0.1.5/webuploader.css">
<link href="${ctxStatic}/common/css/login.css" type="text/css" rel="stylesheet" />
<%-- 
<meta http-equiv="Content-Type" content="text/html;charset=gb2312"> 
<link href="${ctxStatic}/plugin/weboffice/officeCss.css" type="text/css" rel="stylesheet">
--%>
<script language="javascript">
	//作用：显示操作状态
	function StatusMsg(mString) {
		StatusBar.innerHTML = mString;
	}

	//初始化对象
	function initObject() {
		WebOffice.WebUrl = "${ctxStatic}/plugin/weboffice/OfficeServer.jsp"; //后台处理页路径，用来执行后台数据处理业务。本属性支持相对路径
		WebOffice.RecordID = "${uploadPath}"; //文档记录号
		WebOffice.UserName = "${userName}";   //当前操作员
		
		WebOffice.Template = "";              //模板记录号
		WebOffice.FileName = "${filename}";      //文档名称
		WebOffice.FileType = "${filetype}";          //文档类型  .doc  .xls
		WebOffice.EditType = "${editType}";   //编辑状态：第一位可以为0,1,2,3其中：0不可编辑；1可以编辑,无痕迹；2可以编辑,有痕迹,不能修订；3可以编辑,有痕迹,能修订。
		WebOffice.ShowMenu = "1";             //是否显示菜单：1显示；0不显示
		WebOffice.MaxFileSize = 20 * 1024;    //最大的文档大小控制，默认是8M，现在设置成20M。
		LoadDocument();
		/* if ("${id}"==""||"${id}"==null) {
			WebOffice.CreateFile(); //创建空白文档
		} else {
			WebOffice.LoadDocument();
		} */
	}

	//作用：打开服务器文档
	function LoadDocument() {
		if (!WebOffice.WebOpen()) {            //打开该文档    交互OfficeServer的OPTION="LOADFILE"
			StatusMsg(WebOffice.Status);       //显示状态，从OfficeServer中读取
		} else {
			StatusMsg(WebOffice.Status);       //显示状态，从OfficeServer中读取
		}
	}

	//作用：保存服务器文档
	function SaveDocument() {
		if (!WebOffice.WebSave()) {            //交互OfficeServer的OPTION="SAVEFILE"
			alert("保存失败");
			//StatusMsg(WebOffice.Status);
		} else {
			alert("保存成功");
			//StatusMsg(WebOffice.Status);
		}
	}

	//作用：创建空白文档
	function CreateFile() {
		WebOffice.CreateFile();
		StatusMsg(WebOffice.Status);
	}

	//作用：打开本地文件
	function WebOpenLocal() {
		var result = WebOffice.WebOpenLocalFile();
		if (result) {
			StatusMsg("打开本地文档成功！");
		} else {
			StatusMsg(WebOffice.Status);
		}
	}

	//作用：保存文件到本地
	function WebSaveLocal() {
		var result = WebOffice.WebSaveLocalFile();
		if (result) {
			StatusMsg("保存文档到本地成功！");
		} else {
			StatusMsg(WebOffice.Status);
		}
	}

	//作用：获取文档页数（VBA扩展应用）
	function WebDocumentPageCount() {
		if (WebOffice.FileType == ".doc") {
			var intPageTotal = WebOffice.WebObject.Application.ActiveDocument
					.BuiltInDocumentProperties(14);
			alert("文档页总数：" + intPageTotal);
		}

		if (WebOffice.FileType == ".wps") {
			var intPageTotal = WebOffice.WebObject.PagesCount();
			alert("文档页总数：" + intPageTotal);
		}
	}

	//作用：接受文档中全部痕迹（VBA扩展应用）
	function WebAcceptAllRevisions() {
		WebOffice.WebObject.Application.ActiveDocument.AcceptAllRevisions();
		var mCount = WebOffice.WebObject.Application.ActiveDocument.Revisions.Count;
		if (mCount > 0) {
			StatusMsg("接受痕迹失败！");
			return false;
		} else {
			StatusMsg("文档中的痕迹已经全部接受！");
			return true;
		}
	}

	//作用：退出iWebOffice
	function UnLoad() {
		try {
			if (!WebOffice.WebClose()) {
				StatusMsg(WebOffice.Status);
			} else {
				StatusMsg("关闭文档...");
			}
		} catch (e) {
			alert(e.description);
		}
	}
</script>

<body onLoad="initObject();" onunload="UnLoad()">
	<div style="margin:auto;width:1000px;">
		<div style="height:10px;"></div>
		<div class="pull-left">
			使用帮助：非IE浏览器，请下载<a href="${ctxStatic}/plugin/weboffice/IE_Tab_Multi_extension_1_0_0_1.crx">[IE支持插件]</a>并安装（在浏览器右上角点击<img src="${ctxStatic}/plugin/weboffice/ipa.png" />图标）；
			如果未自动安装，请下载<a href="${ctxStatic}/plugin/weboffice/InstallClient.zip">[WebOffice插件]</a>并安装。
		</div>
		<div class="pull-right" style="margin-bottom:10px;">
			<a class="btn btn-success btn-sm" onclick="WebAcceptAllRevisions();" style="${editType ne '3' ? 'display:none' : ''}"><i class="fa fa-edit"></i> 接受痕迹</a>
			<a class="btn btn-success btn-sm" onclick="SaveDocument();"  style="${editType eq '0' ? 'display:none' : ''}"><i class="fa fa-edit"></i> 保存正文</a>
		</div>
	</div>
	<div id="StatusBar" style="display:none;"></div>
	<%--
	<input type=button class=button value="新建文件" onclick="CreateFile()">
	<input type=button class=button value="编辑留痕" onClick="WebOffice.EditType='2'">
	<input type=button class=button value="编辑不留痕" onClick="WebOffice.EditType='1'"> 
	‖
	<input type=button class=button value="打开本地文件" onclick="WebOpenLocal()">
	<input type=button class=button value="保存本地文件" onclick="WebSaveLocal()">
	<input type=button class=button value="打开远程文件" onClick="LoadDocument()">
	<!--系统将通过WebUrl指定的程序到服务器上调入文件, 调入由RecordID指定的文件-->
	<input type=button class=button value="保存远程文件" onClick="SaveDocument()">
	<!--系统将通过WebUrl指定的程序保存本文件到服务器上,保存由RecordID指定的文件-->
	‖
	<input type=button class=button value="文档页数" onClick="WebDocumentPageCount()">
	<input type=button class=button value="接受痕迹" onClick="WebAcceptAllRevisions()">
	<table>
		<tr>
			<td height="28" style="font-size:12px;color:#0000FF">状态栏：</td>
			<td id=StatusBar style="font-size:12px;color:#FF0000">操作状态信息</td>
		</tr>
	</table>
	 --%>
	 
	<!--调用iWebOffice，注意版本号，可用于升级-->
	<div id="DivID">
		<object id="WebOffice" width="100%" height="800px"
			classid="clsid:23739A7E-2000-4D1C-88D5-D50B18F7C347"
			codebase="${ctxStatic}/plugin/weboffice/iWebOffice2000.ocx#version=7,2,6,0">
		</object>
	</div>
</body>
</html>