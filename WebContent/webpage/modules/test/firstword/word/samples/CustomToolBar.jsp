<%@ page contentType="text/html; charset=utf-8" %>
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
<title>自定义工具栏</title>
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
			WebOfficeObj.FileName = "sample.doc";
		    WebOfficeObj.FileType = ".doc";            //FileType:文档类型  .doc  .xls
		    WebOfficeObj.DebugMode = false;						//开启or关闭调试模式  true：开启  false：关闭
		    WebOfficeObj.ShowWindow = false;					//显示/隐藏进度条
		    WebOfficeObj.ShowMenu = 1;
		    WebOfficeObj.ShowToolBar = 1;
		    
			//AppendTools:Index的值根据 OnCommand回调函数来调用, Caption = 自定义工具栏按钮文字 , 
		    //Icon = 前4个字符为http时，表示按钮图标的url路径；前4个字符为hex:时，表示按钮图标的二进制数据；也可以是本地路径例如，C:\\icon.png。图片格式为png。
			var URL = WebOfficeObj.WebUrl.substring(0, WebOfficeObj.WebUrl.lastIndexOf("/"));
			WebOfficeObj.AppendTools("301", "打开本地文档","");
			WebOfficeObj.AppendTools("302", "保存本地文档","");
			WebOfficeObj.AppendTools("303", "新建文档","303");
			
			WebOfficeObj.AppendMenu("1","打开本地文件");
			WebOfficeObj.AppendMenu("2","保存本地文件");
			WebOfficeObj.AppendMenu("3","新建文件");
			
		    WebOfficeObj.SetCaption(WebOfficeObj.UserName + "正在编辑文档");
		    SetGraySkin();			//设置控件皮肤
			  if(WebOfficeObj.WebOpen())
			  {
			  	
			  }
			}
			catch(e){
 	     	alert(e.description);
 	    }
		}

		function CustomToolBarsVisibled(mValue){
			WebOfficeObj.ShowCustomToolBar(mValue);
		}

		//烟枪灰皮肤
 		function SetGraySkin(){
 			//参数顺序依次为：控件标题栏颜色、自定义菜单开始颜色、自定义工具栏按钮开始颜色、自定义工具栏按钮结束颜色、
 			//自定义工具栏按钮边框颜色、自定义工具栏开始颜色、控件标题栏文本颜色（默认值为：0x000000）
 			if (!WebOfficeObj.WebSetSkin(0xdbdbdb, 0xeaeaea, 0xeaeaea, 0xdbdbdb, 0xdbdbdb, 0xdbdbdb, 0x000000))
 				alert(WebOfficeObj.Status);
		}
		
 		//显示/隐藏Office工具栏
 		function OfficeToolBar(mValue){
 	   	WebOfficeObj.ShowToolBars(mValue);
		}

		//显示/隐藏Office状态栏
		function OfficeStatusBar(mValue){
			WebOfficeObj.ShowStatusBar(mValue);
		}
		
		//显示/隐藏菜单栏
		function CustomMenu(mValue){
			WebOfficeObj.ShowMenuBar(mValue);
		}
		
</script>

<!--  响应工具栏事件  -->
<script language="javascript" for=WebOffice event="OnCommand(ID, Caption, bCancel)">
	switch(ID){
		    case 301: WebOfficeObj.WebOpenLocal(); break;//打开本地文件
			case 302: WebOfficeObj.WebSaveLocal(); break;//另存本地文件
			case 303: WebOfficeObj.CreateFile(); break;//新建文档
			case 304: WebOfficeObj.PrintPreview(); break;//启用
			case 305: WebOfficeObj.PrintPreviewExit(); WebOfficeObj.ShowField(); break;//启用
			
		    case 1: WebOfficeObj.WebOpenLocal(); break;//打开本地文件
		    case 2: WebOfficeObj.WebSaveLocal(); break;//另存本地文件
			case 3: WebOfficeObj.CreateFile(); break;//新建文档
			case 4: WebOfficeObj.PrintPreview(); break;//启用
			case 5: WebOfficeObj.PrintPreviewExit(); WebOfficeObj.ShowField(); break;//启用
				
			default: ; return;
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
  		<input type=button value="显示自定义工具栏" onclick="CustomToolBarsVisibled(true)">
  		<input type=button value="隐藏自定义工具栏" onclick="CustomToolBarsVisibled(false)">
  		<br><br>
  		
  		<input type=button value="显示Office工具栏" onclick="OfficeToolBar(true)">
  		<input type=button value="隐藏Office工具栏" onclick="OfficeToolBar(false)">
  		<input type=button value="显示Office状态栏" onclick="OfficeStatusBar(true)">
  		<input type=button value="隐藏Office状态栏" onclick="OfficeStatusBar(false)">
  		<br><br>
  		
  		<input type=button value="显示菜单栏" onclick="CustomMenu(true)">
  		<input type=button value="隐藏菜单栏" onclick="CustomMenu(false)">
  		<br><br>
  		
		<input id="StatusBar" type="text" name="StatusBar" readonly  style="WIDTH:75%">&nbsp;|←状态信息
  	</p>
		<script src="../js/iWebOffice2015.js"></script>
	</form>
</body>
</html>