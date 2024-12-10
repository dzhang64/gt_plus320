<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
<title>系统配置管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {

		$("#inputForm").validate({
			rules : {
				mailName : {
					required : true,
					email : true
				}
			}
		});

	});
	function modifyMail() {
		if ($("#inputForm").valid() == false) {
			return;
		}
		$.ajax({
			async : false,
			url : "${ctx}/sys/systemConfig/save",
			data : {
				"smtp" : $("#smtp").val(),
				"port" : $("#port").val(),
				"mailName" : $("#mailName").val(),
				"mailPassword" : $("#mailPassword").val()
			},
			dataType : "json",
			success : function(data) {
				if (data.success) {
					top.layer.alert("更新成功！");
				} else {
					top.layer.alert("更新失败！");
				}
			}
		});
	}

	function modifySms() {
		$.ajax({
			async : false,
			url : "${ctx}/sys/systemConfig/save",
			data : {
				"smsName" : $("#smsName").val(),
				"smsPassword" : $("#smsPassword").val()
			},
			dataType : "json",
			success : function(data) {
				if (data.success) {
					top.layer.alert("更新成功！");
				} else {
					top.layer.alert("更新失败！");
				}
			}
		});
	}
</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
		<div class="row animated fadeInRight">
			<div class="col-sm-6">
				<form:form id="inputForm" modelAttribute="systemConfig"
					action="${ctx}/sys/systemConfig/save" method="post"
					class="form-horizontal">
					<div class="ibox float-e-margins">
						<div class="ibox-title">
							<h5>系统邮箱设置</h5>
							<div class="ibox-tools">
								<a href="#" onclick="modifyMail()" style="color:#1C84C6"> <i
									style="font-size: 20px" class="fa  fa-save"></i> </a>
							</div>
						</div>
						<div class="ibox-content">
							<div class="row">
								<div class="col-sm-12">
									<table class="table table-bordered">
										<tbody>
											<tr>
												<td class="width-15"><label class="pull-right">邮箱服务器地址：</label>
												</td>
												<td class="width-35"><form:input path="smtp"
														htmlEscape="false" maxlength="64" class="form-control " /></td>
											</tr>
											<tr>
												<td class="width-15"><label class="pull-right">邮箱服务器端口：</label>
												</td>
												<td class="width-35"><form:input path="port"
														htmlEscape="false" maxlength="64" class="form-control " /></td>
											</tr>
											<tr>
												<td class="width-15"><label class="pull-right">系统邮箱地址：</label>
												</td>
												<td class="width-35"><form:input path="mailName"
														htmlEscape="false" maxlength="64" class="form-control " /></td>
											</tr>
											<tr>
												<td class="width-15"><label class="pull-right">系统邮箱密码：</label>
												</td>
												<td class="width-35"><form:password path="mailPassword"
														htmlEscape="false" maxlength="64"
														class="form-control required" /></td>
											</tr>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</form:form>
			</div>
			<div class="col-sm-6">
				<form:form id="inputForm1" modelAttribute="systemConfig"
					action="${ctx}/sys/systemConfig/save" method="post"
					class="form-horizontal">
					<div class="ibox float-e-margins">
						<div class="ibox-title">
							<h5>短信设置（企信通）</h5>
							<div class="ibox-tools">
								<a href="#" onclick="modifySms()" style="color:#1C84C6"> <i
									style="font-size: 20px" class="fa  fa-save"></i> </a>
							</div>
						</div>
						<div class="ibox-content">
							<div class="row">
								<div class="col-sm-12">
									<table class="table table-bordered">
										<tr>
											<td class="width-15"><label class="pull-right">短信用户名：</label>
											</td>
											<td class="width-35"><form:input path="smsName"
													htmlEscape="false" maxlength="64" class="form-control " /></td>
										</tr>
										<tr>
											<td class="width-15"><label class="pull-right">短信密码：</label>
											</td>
											<td class="width-35"><form:password path="smsPassword"
													htmlEscape="false" maxlength="64" class="form-control " /></td>
										</tr>
									</table>
								</div>
							</div>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</body>
</html>