<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
<title>主页</title>
<meta name="decorator" content="default" />
<%@ include file="/webpage/include/bootstraptable.jsp"%>
<%@ include file="edocReceiveList.js"%>
<%@ include file="edocSendList.js"%>
<%@ include file="edocSignList.js"%>
<%@ include file="meetingPendingList.js"%>
<script type="text/javascript">
	$(document).ready(function() {
	});
</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="row">
			<div class="col-sm-12">
				<div class="ibox">
					<!-- <div class="ibox-title">
						<h5>公文拟文</h5>
						<div class="ibox-tools">
							<a class="collapse-link"> <i class="fa fa-chevron-up"></i> </a> <a
								class="dropdown-toggle" data-toggle="dropdown" href="#"
								style="display:none"> <i class="fa fa-wrench"></i> </a>
							<ul class="dropdown-menu dropdown-user">
								<li><a href="#">选项1</a></li>
								<li><a href="#">选项2</a></li>
							</ul>
							<a class="close-link"> <i class="fa fa-times"></i> </a>
						</div>
					</div> -->
					<div class="ibox-content">
						<shiro:hasPermission name="oa:edoc:edocSend:add">
							<a id="add1" class="btn btn-sm btn-primary" onclick="add1()"><i class="glyphicon glyphicon-plus"></i> 发文</a>
							<script>
								function add1(){
									var params = '{"button":"暂存","type":"save","flag":"0"};' 
								   			   + '{"button":"提交","type":"saveAndStart","flag":"1"};'
								   			   + '{"button":"取消","type":"","flag":""};';
									jp.openDialogFlow('发文拟文', "${ctx}/oa/edoc/edocSend/form",'1000px', '100%', $('#edocSendTable'), params);
								}
							</script>
						</shiro:hasPermission>
						<shiro:hasPermission name="oa:edoc:edocReceive:add">
							<a id="add2" class="btn btn-sm btn-primary" onclick="add2()"><i class="glyphicon glyphicon-plus"></i> 收文</a>
							<script>
								function add2(){
									var params = '{"button":"暂存","type":"save","flag":"0"};' 
								   			   + '{"button":"提交","type":"saveAndStart","flag":"1"};'
								   			   + '{"button":"取消","type":"","flag":""};';
									jp.openDialogFlow('收文分发', "${ctx}/oa/edoc/edocReceive/form",'1000px', '100%', $('#edocReceiveTable'), params);
								}
							</script>
						</shiro:hasPermission>
						<shiro:hasPermission name="oa:edoc:edocSign:add">
							<a id="add3" class="btn btn-sm btn-primary" onclick="add3()"><i class="glyphicon glyphicon-plus"></i> 签报</a>
							<script>
								function add3(){
									var params = '{"button":"暂存","type":"save","flag":"0"};' 
								   			   + '{"button":"提交","type":"saveAndStart","flag":"1"};'
								   			   + '{"button":"取消","type":"","flag":""};';
									jp.openDialogFlow('签报拟文', "${ctx}/oa/edoc/edocSign/form",'1000px', '100%', $('#edocSignTable'), params);
								}
							</script>
						</shiro:hasPermission>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<div class="ibox">
					<div class="ibox-title">
						<h5>待办发文<span id="sendSpan" style="color: red;"></span></h5>
						<div class="ibox-tools">
							<a class="collapse-link"> <i class="fa fa-chevron-up"></i> </a> <a
								class="dropdown-toggle" data-toggle="dropdown" href="#"
								style="display:none"> <i class="fa fa-wrench"></i> </a>
							<ul class="dropdown-menu dropdown-user">
								<li><a href="#">选项1</a></li>
								<li><a href="#">选项2</a></li>
							</ul>
							<a class="close-link"> <i class="fa fa-times"></i> </a>
						</div>
					</div>
					<div class="ibox-content">
						<sys:message content="${message}" />

						<!-- 表格 -->
						<table id="edocSendTable" data-toolbar="#toolbar"
							data-id-field="id"></table>

						<!-- context menu -->
						<ul id="context-menu" class="dropdown-menu" style="display:none">
							<%-- 用于扩展右键菜单 --%>
						</ul>
						<br /> <br />
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<div class="ibox">
					<div class="ibox-title">
						<h5>待办收文<span id="receiveSpan" style="color: red;"></span></h5>
						<div class="ibox-tools">
							<a class="collapse-link"> <i class="fa fa-chevron-up"></i> </a> <a
								class="dropdown-toggle" data-toggle="dropdown" href="#"
								style="display:none"> <i class="fa fa-wrench"></i> </a>
							<ul class="dropdown-menu dropdown-user">
								<li><a href="#">选项1</a></li>
								<li><a href="#">选项2</a></li>
							</ul>
							<a class="close-link"> <i class="fa fa-times"></i> </a>
						</div>
					</div>
					<div class="ibox-content">
						<sys:message content="${message}" />

						<!-- 表格 -->
						<table id="edocReceiveTable" data-toolbar="#toolbar"
							data-id-field="id"></table>

						<!-- context menu -->
						<ul id="context-menu" class="dropdown-menu" style="display:none">
							<%-- 用于扩展右键菜单 --%>
						</ul>
						<br /> <br />
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<div class="ibox">
					<div class="ibox-title">
						<h5>待办签报<span id="signSpan" style="color: red;"></span></h5>
						<div class="ibox-tools">
							<a class="collapse-link"> <i class="fa fa-chevron-up"></i> </a> <a
								class="dropdown-toggle" data-toggle="dropdown" href="#"
								style="display:none"> <i class="fa fa-wrench"></i> </a>
							<ul class="dropdown-menu dropdown-user">
								<li><a href="#">选项1</a></li>
								<li><a href="#">选项2</a></li>
							</ul>
							<a class="close-link"> <i class="fa fa-times"></i> </a>
						</div>
					</div>
					<div class="ibox-content">
						<sys:message content="${message}" />

						<!-- 表格 -->
						<table id="edocSignTable" data-toolbar="#toolbar"
							data-id-field="id"></table>

						<!-- context menu -->
						<ul id="context-menu" class="dropdown-menu" style="display:none">
							<%-- 用于扩展右键菜单 --%>
						</ul>
						<br /> <br />
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<div class="ibox">
					<div class="ibox-title">
						<h5>待开会议<span id="pendingSpan" style="color: red;"></span></h5>
						<div class="ibox-tools">
							<a class="collapse-link"> <i class="fa fa-chevron-up"></i> </a> <a
								class="dropdown-toggle" data-toggle="dropdown" href="#"
								style="display:none"> <i class="fa fa-wrench"></i> </a>
							<ul class="dropdown-menu dropdown-user">
								<li><a href="#">选项1</a></li>
								<li><a href="#">选项2</a></li>
							</ul>
							<a class="close-link"> <i class="fa fa-times"></i> </a>
						</div>
					</div>
					<div class="ibox-content">
						<sys:message content="${message}" />

						<!-- 表格 -->
						<table id="pendingTable" data-toolbar="#toolbar"
							data-id-field="id"></table>

						<!-- context menu -->
						<ul id="context-menu" class="dropdown-menu" style="display:none">
							<%-- 用于扩展右键菜单 --%>
						</ul>
						<br /> <br />
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>