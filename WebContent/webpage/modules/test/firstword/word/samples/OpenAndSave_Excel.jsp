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
<title>在线打开/保存Excel文档</title>
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
//	      WebOfficeObj.FileName = "gt_plus/webpage/modules/test/firstword/word/Document/sample.xlsx";
		    WebOfficeObj.FileType = ".xlsx";            //FileType:文档类型  .doc  .xls
		    WebOfficeObj.DebugMode = false;						//开启or关闭调试模式  true：开启  false：关闭
		    WebOfficeObj.ShowWindow = false;					//显示/隐藏进度条
		    WebOfficeObj.ShowMenu = 0;
		    WebOfficeObj.ShowToolBar = 0;
		    WebOfficeObj.SetCaption(WebOfficeObj.UserName + "正在编辑文档");
		    SetGraySkin();	
		    //设置控件皮肤
		    
//			  if(WebOfficeObj.WebOpen())
//			  {
//			  	StatusMsg(WebOfficeObj.Status);
//			  }
		    
			}
			catch(e){
 	     	StatusMsg(e.description);
 	    }
		}

	  //保存文档到本地
		function Save()
		{
		  if (WebOfficeObj.WebSaveLocal()){    //交互OfficeServer的OPTION="SAVEFILE"
	   //  	alert("文档保存成功");
		    WebOfficeObj.WebClose();
		    window.close();
	  	}else{
	     	StatusMsg(WebOfficeObj.Status);
	  	}
		}

		 	//设置页面中的状态值
 		 function StatusMsg(mValue) {
		  try {
			  document.getElementById('StatusBar').value = mValue;
		  } catch (e) {
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

		//打开本地路径的文档
 		function Open() {
 			if (WebOfficeObj.WebOpenLocal()){    
 		     	StatusMsg(WebOfficeObj.Status);
 		  	}
 		}
		
 		//皮肤颜色值为BGR格式，即：RGB中的R与B值进行互调
		//紫罗兰皮肤
 		function SetPurpleSkin(){
 			//参数顺序依次为：控件标题栏颜色、自定义菜单开始颜色、自定义工具栏按钮开始颜色、自定义工具栏按钮结束颜色、
 			//自定义工具栏按钮边框颜色、自定义工具栏开始颜色、控件标题栏文本颜色（默认值为：0x000000）
 			if (!WebOfficeObj.WebSetSkin(0xfdd6e5, 0xfdd8e6, 0xfdd8e6, 0xfdd6e5, 0xfdd6e5, 0xfdd6e5, 0x000000))
 				alert(WebOfficeObj.Status);
		}
		
		//紫罗兰皮肤
 		function SetPurpleSkin1(){
 			//参数顺序依次为：控件标题栏颜色、自定义菜单开始颜色、自定义工具栏按钮开始颜色、自定义工具栏按钮结束颜色、
 			//自定义工具栏按钮边框颜色、自定义工具栏开始颜色、控件标题栏文本颜色（默认值为：0x000000）
 			if (!WebOfficeObj.WebSetSkin(0xb15d7d, 0xfdd8e6, 0xfdd8e6, 0xfdd6e5, 0xfdd6e5, 0xfdd6e5, 0xFFFFFF))
 				alert(WebOfficeObj.Status);
		}
		
		//胭脂红皮肤
 		function SetRedSkin(){
 			//参数顺序依次为：控件标题栏颜色、自定义菜单开始颜色、自定义工具栏按钮开始颜色、自定义工具栏按钮结束颜色、
 			//自定义工具栏按钮边框颜色、自定义工具栏开始颜色、控件标题栏文本颜色（默认值为：0x000000）
 			if (!WebOfficeObj.WebSetSkin(0xcfd9fa, 0xdce3fb, 0xdce3fb, 0xcfd9fa, 0xcfd9fa, 0xcfd9fa, 0x000000))
 				alert(WebOfficeObj.Status);
		}
		
		//胭脂红皮肤
 		function SetRedSkin1(){
 			//参数顺序依次为：控件标题栏颜色、自定义菜单开始颜色、自定义工具栏按钮开始颜色、自定义工具栏按钮结束颜色、
 			//自定义工具栏按钮边框颜色、自定义工具栏开始颜色、控件标题栏文本颜色（默认值为：0x000000）
 			if (!WebOfficeObj.WebSetSkin(0x3055f4, 0xdce3fb, 0xdce3fb, 0xcfd9fa, 0xcfd9fa, 0xcfd9fa, 0xffffff))
 				alert(WebOfficeObj.Status);
		}

		//烟枪灰皮肤
 		function SetGraySkin(){
 			//参数顺序依次为：控件标题栏颜色、自定义菜单开始颜色、自定义工具栏按钮开始颜色、自定义工具栏按钮结束颜色、
 			//自定义工具栏按钮边框颜色、自定义工具栏开始颜色、控件标题栏文本颜色（默认值为：0x000000）
 			if (!WebOfficeObj.WebSetSkin(0xdbdbdb, 0xeaeaea, 0xeaeaea, 0xdbdbdb, 0xdbdbdb, 0xdbdbdb, 0x000000))
 				alert(WebOfficeObj.Status);
		}
		
		//烟枪灰皮肤
 		function SetGraySkin1(){
 			//参数顺序依次为：控件标题栏颜色、自定义菜单开始颜色、自定义工具栏按钮开始颜色、自定义工具栏按钮结束颜色、
 			//自定义工具栏按钮边框颜色、自定义工具栏开始颜色、控件标题栏文本颜色（默认值为：0x000000）
 			if (!WebOfficeObj.WebSetSkin(0xa0a0a3, 0xeaeaea, 0xeaeaea, 0xdbdbdb, 0xdbdbdb, 0xdbdbdb, 0xffffff))
 				alert(WebOfficeObj.Status);
		}

		//蓝色皮肤
 		function SetBlueSkin(){
 			//参数顺序依次为：控件标题栏颜色、自定义菜单开始颜色、自定义工具栏按钮开始颜色、自定义工具栏按钮结束颜色、
 			//自定义工具栏按钮边框颜色、自定义工具栏开始颜色、控件标题栏文本颜色（默认值为：0x000000）
 			if (!WebOfficeObj.WebSetSkin(0xfee6d3, 0xfee6d3, 0xfee6d3, 0xfee6d3, 0xfee6d3, 0xfee6d3, 0x000000))
 				alert(WebOfficeObj.Status);
		}
		
		//蓝色皮肤
 		function SetBlueSkin1(){
 			//参数顺序依次为：控件标题栏颜色、自定义菜单开始颜色、自定义工具栏按钮开始颜色、自定义工具栏按钮结束颜色、
 			//自定义工具栏按钮边框颜色、自定义工具栏开始颜色、控件标题栏文本颜色（默认值为：0x000000）
 			if (!WebOfficeObj.WebSetSkin(0xe26602, 0xfee6d3, 0xfee6d3, 0xfee6d3, 0xfee6d3, 0xfee6d3, 0xffffff))
 				alert(WebOfficeObj.Status);
		}

		//柠檬黄皮肤
 		function SetYellowSkin(){
 			//参数顺序依次为：控件标题栏颜色、自定义菜单开始颜色、自定义工具栏按钮开始颜色、自定义工具栏按钮结束颜色、
 			//自定义工具栏按钮边框颜色、自定义工具栏开始颜色、控件标题栏文本颜色（默认值为：0x000000）
 			if (!WebOfficeObj.WebSetSkin(0x5edffe, 0xb7effd, 0xb7effd, 0xb7effd, 0xb7effd, 0x5edffe, 0x000000))
 				alert(WebOfficeObj.Status);
		}
		
		//柠檬黄皮肤
 		function SetYellowSkin1(){
 			//参数顺序依次为：控件标题栏颜色、自定义菜单开始颜色、自定义工具栏按钮开始颜色、自定义工具栏按钮结束颜色、
 			//自定义工具栏按钮边框颜色、自定义工具栏开始颜色、控件标题栏文本颜色（默认值为：0x000000）
 			if (!WebOfficeObj.WebSetSkin(0x00baff, 0xb7effd, 0xb7effd, 0xb7effd, 0xb7effd, 0x5edffe, 0x000000))
 				alert(WebOfficeObj.Status);
		}
		
 		//锁定/解锁文档 。对Excel没有用
		function WebSetProtect(Boolean, PassWord){
			WebOfficeObj.WebSetProtect(Boolean,PassWord);
			StatusMsg(WebOfficeObj.Status);
		}

		//允许/禁止拷贝文档
		function WebEnableCopy(Boolean){
			WebOfficeObj.WebEnableCopy(Boolean);
			StatusMsg(WebOfficeObj.Status);
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
			<!-- 新建、打开、保存文档 -->
			<input type=button value="新建文档" onclick="WebOfficeObj.CreateFile()">
			<input type=button value="打开本地文档" onclick="Open()">
	    	<input type=button value="保存文档" onclick="Save()">
	    	<br><br>
	    	
	    	<!-- 换肤 -->
	    	<input type=button value="紫罗兰" onclick="SetPurpleSkin()">
	  		<input type=button value="紫罗兰(深)" onclick="SetPurpleSkin1()">
	  		<input type=button value="烟枪灰" onclick="SetGraySkin()">
	  		<input type=button value="烟枪灰(深)" onclick="SetGraySkin1()">
	  		<input type=button value="天空蓝" onclick="SetBlueSkin()">
	  		<input type=button value="天空蓝(深)" onclick="SetBlueSkin1()">
	  		<input type=button value="柠檬黄" onclick="SetYellowSkin()">
	  		<input type=button value="柠檬黄(深)" onclick="SetYellowSkin1()">
	  		<input type=button value="胭脂红" onclick="SetRedSkin()">
	  		<input type=button value="胭脂红(深)" onclick="SetRedSkin1()">
	    	<br><br>
	    	
		 	<!--锁定/解锁文档  允许/禁止拷贝  -->
		<!-- 	<input type=button value="锁定文档" onclick="WebSetProtect(true, '123456')">
				<input type=button value="解锁文档" onclick="WebSetProtect(false, '123456')">
		对Excel文档没有作用-->	
			<input type=button value="禁止拷贝" onclick="WebEnableCopy(false)">
			<input type=button value="允许拷贝" onclick="WebEnableCopy(true)">
	
	    	<br><br>
			<input id="StatusBar" type="text" name="StatusBar" readonly  style="WIDTH:75%">&nbsp;|←状态信息
	  	</p>
		<script src="../js/iWebOffice2015.js"></script>
	</form>
</body>
</html>