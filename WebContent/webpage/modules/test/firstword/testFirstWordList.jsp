<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<html>
<head>
	<title>test_fisrt_word管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<%@ include file="testFirstWordList.js" %>
	<script type="text/javascript">
		$(document).ready(function() {
	        laydate({
	            elem: '#beginRunDate', //目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	            event: 'focus',format: 'YYYY-MM-DD hh:mm:ss', istime : true //响应事件。如果没有传入event，则按照默认的click
	        });
	        laydate({
	            elem: '#endRunDate', //目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	            event: 'focus',format: 'YYYY-MM-DD hh:mm:ss', istime : true //响应事件。如果没有传入event，则按照默认的click
	        });
					
		
		});
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>test_fisrt_word列表 </h5>
		<div class="ibox-tools">
			<a class="collapse-link">
				<i class="fa fa-chevron-up"></i>
			</a>
			<a class="dropdown-toggle" data-toggle="dropdown" href="#" style="display:none">
				<i class="fa fa-wrench"></i>
			</a>
			<ul class="dropdown-menu dropdown-user">
				<li><a href="#">选项1</a>
				</li>
				<li><a href="#">选项2</a>
				</li>
			</ul>
			<a class="close-link">
				<i class="fa fa-times"></i>
			</a>
		</div>
	</div>
    
    <div class="ibox-content">
	<sys:message content="${message}"/>
	
	<!--查询条件-->
	<div class="row">
	<div class="col-sm-12">
	<form:form id="searchForm" modelAttribute="testFirstWord" action="${ctx}/test/firstword/testFirstWord/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="ownerCode" name="ownerCode" type="hidden" value="${ownerCode}" />
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
			<div class="form-group">
			<span>时间：</span>
				<input id="beginRunDate" name="beginRunDate" type="text" maxlength="20" class="laydate-icon form-control layer-date input-sm"
					value="<fmt:formatDate value="${testFirstWord.beginRunDate}" pattern="yyyy-MM-dd HH:mm:ss" />"/> - 
				<input id="endRunDate" name="endRunDate" type="text" maxlength="20" class="laydate-icon form-control layer-date input-sm"
					value="<fmt:formatDate value="${testFirstWord.endRunDate}" pattern="yyyy-MM-dd HH:mm:ss" />"/>
			</div>
		<div class="form-group pull-right">
			<a id="search" class="btn btn-primary btn-rounded  btn-bordered btn-sm"><i class="fa fa-search"></i> 查询</a>
			<a id="reset" class="btn btn-primary btn-rounded  btn-bordered btn-sm" ><i class="fa fa-refresh"></i> 重置</a>
		</div>
	</form:form>
	<br/>
	</div>
	</div>
	
	<!-- 工具栏 -->
	<div id="toolbar">
		<shiro:hasPermission name="test:firstword:testFirstWord:add">
			<a id="add" class="btn btn-sm btn-primary" onclick="add()"><i class="glyphicon glyphicon-plus"></i> 添加</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="test:firstword:testFirstWord:edit">
		</shiro:hasPermission>
		<shiro:hasPermission name="test:firstword:testFirstWord:del">
		</shiro:hasPermission>
		<shiro:hasPermission name="test:firstword:testFirstWord:import">
		</shiro:hasPermission>
	</div>


 <!-- word操作列 -->
	 <div align="left">
		 <table border="0" width="48" cellspacing="0" cellpadding="0" >
			 <tr>
				<td><div class=up onclick=show("a0")><a class="btn btn-info btn-xs view">test</a></div><div onmouseover=high() onmouseout=low() id=a0 style="display:none" >
				<li class="btn btn-info btn-xs view"><a href="/gt_plus/webpage/modules/test/firstword/word/samples/OpenAndSave_Word.jsp"  target="_blank" > 新建Word文档</a></li>
				<li class="btn btn-info btn-xs view"><a href="/gt_plus/webpage/modules/test/firstword/word/samples/OpenAndSave_Excel.jsp"  target="_blank" >新建Excel文档</a></li>
				
				<li class="btn btn-info btn-xs view"><a href="/gt_plus/webpage/modules/test/firstword/word/samples/AddBookMark.jsp"  target="_blank" >书签操作</a></li>
				<li class="btn btn-info btn-xs view"><a href="/gt_plus/webpage/modules/test/firstword/word/samples/TraceDocumentWord.jsp"  target="_blank" >文档痕迹</a></li>
				
				<li class="btn btn-info btn-xs view"><a href="/gt_plus/webpage/modules/test/firstword/word/samples/ContentDocument.jsp"  target="_blank" >文档内容操作</a></li>
				<li class="btn btn-info btn-xs view"><a href="/gt_plus/webpage/modules/test/firstword/word/samples/CustomToolBar.jsp"  target="_blank" >显示和隐藏操作</a></li>
				
				<li class="btn btn-info btn-xs view"><a href="/gt_plus/webpage/modules/test/firstword/word/samples/TemplateBMFilling.jsp"  target="_blank" >文档模板书签填充</a></li>
				  </div></td>
			 </tr>
		 </table>
	</div> 



	<!-- 表格 -->
	<table id="testFirstWordTable"   data-toolbar="#toolbar" data-id-field="id"></table>
	
	<!-- context menu -->
    <ul id="context-menu" class="dropdown-menu" style="display:none">
    	<%-- 用于右键菜单
    	<shiro:hasPermission name="test:firstword:testFirstWord:edit">
        <li data-item="edit"><a>编辑</a></li>
        </shiro:hasPermission>
        <shiro:hasPermission name="test:firstword:testFirstWord:del">
        <li data-item="delete"><a>删除</a></li>
        </shiro:hasPermission>
        <li data-item="action1"><a>取消</a></li>
        --%>
    </ul>  
	<br/>
	<br/>
	</div>
	</div>
</div>
</body>
</html>