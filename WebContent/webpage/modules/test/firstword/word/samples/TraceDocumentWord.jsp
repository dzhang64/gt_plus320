﻿<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.io.*,java.text.*,java.util.*,java.sql.*,java.text.SimpleDateFormat,java.text.DateFormat,java.util.Date,javax.servlet.*,javax.servlet.http.*,DBstep.iDBManager2000.*" %>
<%
  //自动获取OfficeServer和OCX文件完整URL路径
  String mHttpUrlName=request.getRequestURI();
  String mScriptName=request.getServletPath();
  String mServerName="OfficeServer";
  String mServerUrl="http://"+request.getServerName()+":"+request.getServerPort()+mHttpUrlName.substring(0,mHttpUrlName.lastIndexOf(mScriptName))+"/"+mServerName;//取得OfficeServer文件的完整URL
%>

<html>
<head>
<title>文档痕迹</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script src="../js/WebOffice.js"></script>


<script type="text/javascript">
 	var WebOfficeObj = new WebOffice2015(); //创建WebOffice对象
</script>

<script language="javascript">
	  function Load()
		{
			try
			{
			  WebOfficeObj.WebUrl = "<%=mServerUrl%>";
			  WebOfficeObj.UserName = "演示人";
	      WebOfficeObj.FileName = "TraceDocument.doc";
		    WebOfficeObj.FileType = ".doc";            //FileType:文档类型  .doc  .xls
		    WebOfficeObj.DebugMode = false;						//开启or关闭调试模式  true：开启  false：关闭
		    WebOfficeObj.ShowWindow = false;					//显示/隐藏进度条
		    WebOfficeObj.ShowMenu = 0;
		    WebOfficeObj.ShowToolBar = 0;
		    WebOfficeObj.SetCaption(WebOfficeObj.UserName + "正在编辑文档");
		    SetGraySkin();			//设置控件皮肤
			  if(WebOfficeObj.WebOpen())
			  {
			  	StatusMsg(WebOfficeObj.Status);
			  }
			}
			catch(e){
 	     	StatusMsg(e.description);
 	    }
		}

		 	//设置页面中的状态值
 		function StatusMsg(mValue){
 	   	try{
	   		document.getElementById('StatusBar').value = mValue;
	   	}catch(e){
	     	return false;
	   	}
		}
		
		//烟枪灰皮肤
 		function SetGraySkin(){
 			//参数顺序依次为：控件标题栏颜色、自定义菜单开始颜色、自定义工具栏按钮开始颜色、自定义工具栏按钮结束颜色、
 			//自定义工具栏按钮边框颜色、自定义工具栏开始颜色、控件标题栏文本颜色（默认值为：0x000000）
 			if (!WebOfficeObj.WebSetSkin(0xdbdbdb, 0xeaeaea, 0xeaeaea, 0xdbdbdb, 0xdbdbdb, 0xdbdbdb, 0x000000))
 				alert(WebOfficeObj.Status);
		}
		//隐藏/显示痕迹
		function ShowRevision(Boolean){
		if (Boolean){
			 WebOfficeObj.WebShow(true);
			 StatusMsg("显示痕迹...");
		  }else{
			 WebOfficeObj.WebShow(false);
			 StatusMsg("隐藏痕迹...");
		  }
		}

		//清理痕迹
		function ClearRevisions(){
			WebOfficeObj.ClearRevisions();
			 StatusMsg("清理痕迹...");
		}

		
		//打开本地的文档
 		function Open() {
 			if (WebOfficeObj.WebOpenLocal()){   
 		     	StatusMsg(WebOfficeObj.Status);
 		  	}
 		}
		
</script>

<!--以下是多浏览器的事件方法 -->
<script>
function OnReady(){
 WebOfficeObj.setObj(document.getElementById('WebOffice'));//给2015对象赋值
 //Load();//避免页面加载完，控件还没有加载情况
 window.onload = function(){Load();} //IE和谷歌可以直接调用Load方法，火狐要在页面加载完后去调用
}
</script>


<script language="javascript" for=WebOffice event="OnReady()">
	 WebOfficeObj.setObj(document.getElementById('WebOffice'));
   Load();//避免页面加载完，控件还没有加载情况
</script>
</head>

<body onUnload="WebOfficeObj.WebClose()">
	<form name="webform" method="post">  <!--保存iWebOffice后提交表单信息-->
		<p>
			<input type=button value="打开本地文档" onclick="Open()">
			<br>
			
			<input type=button value="显示痕迹" onclick="ShowRevision(true)">
			<input type=button value="隐藏痕迹" onclick="ShowRevision(false)">
			<input type=button value="清除痕迹" onclick="ClearRevisions()">
			<input id="StatusBar" type="text" name="StatusBar" readonly  style="WIDTH:75%">&nbsp;|←状态信息
  		</p>
		<script src="../js/iWebOffice2015.js"></script>
	</form>
</body>
</html>