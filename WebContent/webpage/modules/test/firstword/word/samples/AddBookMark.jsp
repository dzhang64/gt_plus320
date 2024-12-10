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
<title>书签操作</title>
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
	  
		//打开书签窗口
		function WebOpenBookMarks(){	
			WebOfficeObj.WebOpenBookMarks();
		 }

		 //作用：设置书签值  vbmName:标签名称，vbmValue:标签值   标签名称注意大小写
		function SetBookmarks(){
			try{
				var mText=window.prompt("请输入书签名称和书签值，以','隔开。","例如：book1,book2");
				var mValue = mText.split(",");
				BookMarkName = mValue[0];
				BookMarkValue = mValue[1];
				WebOfficeObj.WebSetBookmarks(BookMarkName,BookMarkValue);
				StatusMsg(WebOfficeObj.Status);
				return true;
			}catch(e){
				StatusMsg(WebOfficeObj.Status);
				return false;
			}
		}
		
		//根据书签名定位书签
		function WebFindBookMarks(){
			var mText=window.prompt("请输入定位的书签名称","例如：book1");
			WebOfficeObj.WebFindBookMarks(mText);
			StatusMsg(WebOfficeObj.Status);

		}
		
		 //删除书签(不会删除书签对应的值)
		function WebDelBookMarks(){//书签名称，
			var mText=window.prompt("请输入删除的书签名称","例如：book1");
			WebOfficeObj.WebDelBookMarks(mText);//删除书签
			StatusMsg(WebOfficeObj.Status);
		 }
		 //根据光标添加添加书签名和值
		function WebAddBookMarks(){//书签名称，书签值
			var mText=window.prompt("请输入书签名称和书签值，以','隔开。","例如：book1,book2");
			var mValue = mText.split(",");
			BookMarkName = mValue[0];
			BookMarkValue = mValue[1];
			WebOfficeObj.WebAddBookMarks(BookMarkName,BookMarkValue);
			StatusMsg(WebOfficeObj.Status);
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
		<!-- 对书签进行操作 -->
    	<input type=button value="书签填充" onclick="SetBookmarks()">
		<input type=button value="打开书签窗体" onclick="WebOpenBookMarks()">
		<!-- 书签套红在此基础上处理 -->
		<input type=button value="添加书签" onclick="WebAddBookMarks()">
		<input type=button value="删除书签" onclick="WebDelBookMarks()">
		<input type=button value="定位书签" onclick="WebFindBookMarks()">
		<br><br>
		
		
		<br><br>
		
		<input id="StatusBar" type="text" name="StatusBar" readonly  style="WIDTH:75%">&nbsp;|←状态信息
  		</p>
		<script src="../js/iWebOffice2015.js"></script>
	</form>
</body>
</html>