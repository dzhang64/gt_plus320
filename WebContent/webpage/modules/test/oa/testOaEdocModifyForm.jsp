<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>发文管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		function doSubmit(){
			if(validateForm.form()){
			  $("#inputForm").submit();
			  return true;
		  }
		  return false;
		}
		
		$(document).ready(function() { 
			validateForm = $("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
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
			laydate({
				elem: '#runDate', //目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
				event: 'focus' //响应事件。如果没有传入event，则按照默认的click
			});
		});
	</script>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="testOaEdoc" action="${ctx}/test/oa/testOaEdoc/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="act.taskId"/>
		<form:hidden path="act.taskName"/>
		<form:hidden path="act.taskDefKey"/>
		<form:hidden path="act.procInsId"/>
		<form:hidden path="act.procDefId"/>
		<form:hidden id="flag" path="act.flag"/>
		<input type="hidden" name="dirtyUpdateDate" value="${testOaEdoc.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>日期：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="runDate" name="runDate" type="text" maxlength="20" class="laydate-icon form-control layer-date required"
							value="<fmt:formatDate value="${testOaEdoc.runDate}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>标题：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="title" htmlEscape="false"    class="form-control required"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">正文：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
						<form:textarea path="content" htmlEscape="false" rows="4"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
		 	</tbody>
		</table>
		<div class="layui-layer-btn" style="text-align: center;">
			<a class="layui-layer-btn0" onclick="$('#flag').val('claim'), $('#inputForm').submit();">暂存</a>
			<a class="layui-layer-btn0" onclick="$('#flag').val('yes'), $('#inputForm').submit();">重申</a>
			<a class="layui-layer-btn0" onclick="$('#flag').val('no'), $('#inputForm').submit();">销毁</a>
			<a class="layui-layer-btn1" onclick="history.go(-1)">取消</a>
		</div>
		<c:if test="${not empty testOaEdoc.id}">
			<act:flowChart procInsId="${testOaEdoc.act.procInsId}"/>
			<act:histoicFlow procInsId="${testOaEdoc.act.procInsId}" />
		</c:if>
	</form:form>
</body>
</html>