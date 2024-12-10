<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<%@ page import="java.io.*,java.text.*,java.util.*,java.sql.*,java.text.SimpleDateFormat,java.text.DateFormat,java.util.Date,javax.servlet.*,javax.servlet.http.*,DBstep.iDBManager2000.*" %>
<%
  //自动获取OfficeServer和OCX文件完整URL路径
  String mHttpUrlName=request.getRequestURI();
  String mScriptName=request.getServletPath();
  String mServerName="OfficeServer";
  String mServerUrl="http://"+request.getServerName()+":"+request.getServerPort()+mHttpUrlName.substring(0,mHttpUrlName.lastIndexOf(mScriptName))+"/"+mServerName;//取得OfficeServer文件的完整URL
%>
<script src="/webpage/modules/firstword/word/js/WebOffice.js"></script>
<html>
<head>
	<title>协同事项管理</title>
	<meta name="decorator" content="default"/>
	
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
//	      WebOfficeObj.FileName = "gt_plus/webpage/modules/test/firstword/word/Document/sample.doc";
		    WebOfficeObj.FileType = ".doc";            //FileType:文档类型  .doc  .xls
		    WebOfficeObj.DebugMode = false;						//开启or关闭调试模式  true：开启  false：关闭
		    WebOfficeObj.ShowWindow = false;					//显示/隐藏进度条
		    WebOfficeObj.ShowMenu = 0;
		    WebOfficeObj.ShowToolBar = 0;
		    WebOfficeObj.SetCaption(WebOfficeObj.UserName + "正在编辑文档");
		    SetGraySkin();			//设置控件皮肤
		    
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
	    // 	alert("文档保存成功");
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
 		
 		//打开本地的文档
 		function Open() {
 			if (WebOfficeObj.WebOpenLocal()){   
 		     	StatusMsg(WebOfficeObj.Status);
 		  	}
 		}
		
 		//保存为pdf到服务器
 		//未完整实现
 		  function WebSavePDF(){
 			 var rid = WebOfficeObj.RecordID = "PDFname";
 			 var PdfName = rid+".pdf"
 			  WebOfficeObj.WebSavePDF();
 			  StatusMsg(WebOfficeObj.Status + ",文件路径/PDF/" + PdfName);
 		  }

 		  //保存为pdf到本地
 		  function SaveAsPDF(){
 		//	  var PdfPath = WebOfficeObj.DownFilePath();
 		//	  var PDFname = "pdfName.pdf";
 	 			var PdfPath=prompt("请选择保存路径？");
 	 			var PDFname=prompt("请输入保存的名字？(不带后缀)");
 			  if(WebOfficeObj.SaveAsPdf(PdfPath + PDFname+".pdf")){
 				  StatusMsg(WebOfficeObj.Status + ",文件路径" + PdfPath + PDFname+".pdf");
 			  }else{
 				  StatusMsg(WebOfficeObj.Status);
 			  }
 			  
 		  }
 		  
 	 	//锁定/解锁文档
 			function WebSetProtect(Boolean, PassWord){
 				WebOfficeObj.WebSetProtect(Boolean,PassWord);
 				StatusMsg(WebOfficeObj.Status);
 			}

 			//允许/禁止拷贝文档
 			function WebEnableCopy(Boolean){
 				WebOfficeObj.WebEnableCopy(Boolean);
 				StatusMsg(WebOfficeObj.Status);
 			}
 			
 			//模板套红
 			function WebUseTemplate(fileName){
 				WebOfficeObj.Template = fileName;  //套红模板名称
 				WebOfficeObj.WebLoadTemplate();
 				StatusMsg(WebOfficeObj.Status);
 			}
 			
</script>	
<script language="javascript" for="WebOffice" event="OnReady()">
   WebOfficeObj.setObj(document.getElementById('WebOffice'));//给2015对象赋值
   Load();//避免页面加载完，控件还没有加载情况
</script>

<!--以下是多浏览器的事件方法 -->
<script>
function OnReady(){
 WebOfficeObj.setObj(document.getElementById('WebOffice'));//给2015对象赋值
 //Load();//避免页面加载完，控件还没有加载情况
 window.onload = function(){Load();} //IE和谷歌可以直接调用Load方法，火狐要在页面加载完后去调用
}
</script>
	
	
	<script type="text/javascript">
		var validateForm;
		var $table;   // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		function doSubmit(table, index, pName, pValue){
			if(validateForm.form()){
				var defaultPValue = '10';
		    	if (!pName && !pValue) {
					//添加
				} else {
					//保存，办理
					if (pName && pValue && $("#" + pName) && pValue != '') $("#" + pName).val(pValue);
				}
		      
				$table = table;
				$topIndex = index;
				jp.loading();
				$("#inputForm").submit();
				return true;
			}
			return false;
		}
		
		$(document).ready(function() { 
			validateForm = $("#inputForm").validate({
				submitHandler: function(form){
					jp.post("${ctx}/oa/matter/synergyMatter/save",$('#inputForm').serialize(),function(data){
						if(data.success){
	                    	$table.bootstrapTable('refresh');
	                    	jp.success(data.msg);
	                    	jp.close($topIndex);//关闭dialog
	                    }else{
            	  			jp.error(data.msg);
	                    }
					});
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			//控制Form组件是否可编辑
			if ($("#viewFlag")) {
				var viewFlag = $("#viewFlag").val();
				if (viewFlag.indexOf("set") != -1) {
					$("fieldset[class='set']").attr("disabled","disabled");
				}
				if (viewFlag.indexOf("audit") != -1) {
					$("fieldset[class='audit']").attr("disabled","disabled");
				}
			}
		});
	</script>
	
</head>
<body class="hideScroll" onUnload="WebOfficeObj.WebClose()">
		<form:form id="inputForm" modelAttribute="synergyMatter" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" name="dirtyUpdateDate" value="${synergyMatter.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">标题：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="subject" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">预归档：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<sys:treeselect id="arcCategory" name="arcCategory" value="${synergyMatter.arcCategory.id}" labelName="arcCategory.name" labelValue="${synergyMatter.arcCategory.name}"
							title="档案栏目" url="/oa/arc/arcCategory/treeData?type=2" cssClass="form-control " allowClear="true" notAllowSelectParent="true"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">附件：</label></td>
			
					<td class="width-35" colspan="3">
						
						<input type="hidden" id="file" name="file" value='${synergyMatter.file}' />
						<sys:gtfinder input="file" uploadPath="/oa/matter/synergyMatter"></sys:gtfinder>
					</td>
				</tr>
					
		 	</tbody>
		</table>
		<input type=button value="新建文档" onclick="WebOfficeObj.CreateFile()">
		<script src="/webpage/modules/firstword/word/js/iWebOffice2015.js"></script>

<!-- 		<iframe id="officeContent" name="officeContent" src="/gt_plus/WebContent/webpage/modules/firstword/word/samples/OpenAndSave_Word.jsp" width="100%" height="100%" frameborder="0"></iframe>
 -->		<li class="btn btn-info btn-xs view"><a href="/gt_plus/WebContent/webpage/modules/firstword/word/samples/OpenAndSave_Word.jsp"  target="_blank" > 新建Word文档</a></li>
	</form:form>
</body>
</html>