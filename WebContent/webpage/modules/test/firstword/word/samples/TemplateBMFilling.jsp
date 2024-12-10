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
<title>模板书签填充/书签套红</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script src="../js/WebOffice.js"></script>
<script type="text/javascript">
 	var WebOfficeObject = new WebOffice2015(); //创建WebOffice对象
</script>

<script language="javascript">
	  function Load()
		{
			try
			{
			  WebOfficeObject.WebUrl = "<%=mServerUrl%>";
	      WebOfficeObject.FileName = "template.doc";   //套红成功后要保存的文档名称（文档存在后台服务器磁盘）
		    //WebOfficeObject.RecordID = "123";				//套红成功后的记录号（文档存在数据库中）
		    WebOfficeObject.FileType = ".doc";            //FileType:文档类型  .doc  .xls
			  if(WebOfficeObject.WebOpen())
			  {
			  }
			}
			catch(e){
 	     	alert(e.description);
 	    }
		}

		//文档模板书签填充
		function WebTemplateBMFilling()
		{
			//document.getElementById("wordform").submit();
			var rg;
			try {
				//开始遍历当前打开的文档中存在的书签数，然后根据对应的书签名进行填充
				for (var i = 1; i <= WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Count; i++) {
					if (WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Item(i).Name == "Caption") {
						rg = WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Item("Caption").Range;
					//	rg.Text = "关于信息化建设的题案";
						rg.Text =document.getElementById("Caption").value;
						WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Add("Caption", rg);
						document.getElementById("Caption").value = "";
					}
					else if (WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Item(i).Name == "Content") {
						rg = WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Item("Content").Range;
					//	rg.Text = "公文内容：请详细填写";
						rg.Text =document.getElementById("Content").value;
						WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Add("Content", rg);
						document.getElementById("Content").value = "";
					}
					else if (WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Item(i).Name == "Description") {
						rg = WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Item("Description").Range;
					//	rg.Text = "计算机、信息、网络";
						rg.Text =document.getElementById("Description").value;
						WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Add("Description", rg);
						document.getElementById("Description").value = "";
					}
					else if (WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Item(i).Name == "Sec") {
						rg = WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Item("Sec").Range;
					//	rg.Text = "绝密";
						rg.Text =document.getElementById("Sec").value;
						WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Add("Sec", rg);
						document.getElementById("Sec").value = "";
					}
					else if (WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Item(i).Name == "Subject") {
						rg = WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Item("Subject").Range;
					//	rg.Text = "内部公文";
						rg.Text =document.getElementById("Subject").value;
						WebOfficeObject.WebObject.ActiveDocument.Bookmarks.Add("Subject", rg);
						document.getElementById("Subject").value = "";
					}
				}
				StatusMsg("文档模板书签填充成功");
			} catch(e) {
				StatusMsg("文档模板书签填充失败");
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
	 WebOfficeObject.setObj(document.getElementById('WebOffice'));
   Load();//避免页面加载完，控件还没有加载情况
</script>
</head>

<body onUnload="WebOfficeObject.WebClose()">

	<form id="wordform"  method="post">
		  公文密机： <input type="text" name="公文密机" id="Sec" />
		  标题： <input type="text" name="标题" id="Caption" />
		 公文主题词:  <input type="text" name="公文主题词" id="Subject" />
		 公文内容:  <input type="text" name="公文内容" id="Content" />
		 公文说明或描述:  <input type="text" name="公文说明或描述" id="Description" />
		 
	</form>
	
	<form name="webform" method="post">  <!--保存iWebOffice后提交表单信息-->
		<p>
			 <input type=button value="书签填充" onclick="WebTemplateBMFilling()">
		 	<br><br>
		 
			<input id="StatusBar" type="text" name="StatusBar" readonly  style="WIDTH:75%">&nbsp;|←状态信息
 	 	</p>
		<script src="../js/iWebOffice2015.js"></script>
	</form>
</body>
</html>