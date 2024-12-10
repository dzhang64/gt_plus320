<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>测试校验功能管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		var $table;   // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		function doSubmit(table, index, pName, pValue){
		  if(validateForm.form()){
			  var defaultPValue = '10';
			  if (pName && $("#" + pName)) {
		      	$("#" + pName).val(defaultPValue);
		      } else {
		      	if ($("#status") && $("#status").val() == '') $("#status").val(defaultPValue);
		      }
		      if (pName && pValue && $("#" + pName) && pValue != '') $("#" + pName).val(pValue);
		      
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
					jp.post("${ctx}/test/validation/testValidation/save",$('#inputForm').serialize(),function(data){
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
			
			laydate({
				elem: '#newDate', //目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
				event: 'focus' //响应事件。如果没有传入event，则按照默认的click
			});
		});
	</script>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="testValidation" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" name="dirtyUpdateDate" value="${testValidation.updateDate}" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>num：</label></td>
			
			        <td class="width-35">
			
						<form:input path="num" htmlEscape="false"    class="form-control required number"/>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>num2：</label></td>
			
			        <td class="width-35">
			
						<form:input path="num2" htmlEscape="false"    class="form-control required digits"/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>str：</label></td>
			
			        <td class="width-35">
			
						<form:input path="str" htmlEscape="false"    class="form-control required"/>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>email：</label></td>
			
			        <td class="width-35">
			
						<form:input path="email" htmlEscape="false"    class="form-control required email"/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>url：</label></td>
			
			        <td class="width-35">
			
						<form:input path="url" htmlEscape="false"    class="form-control required url"/>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>new_date：</label></td>
			
			        <td class="width-35">
			
						<input id="newDate" name="newDate" type="text" maxlength="20" class="laydate-icon form-control layer-date required"
							value="<fmt:formatDate value="${testValidation.newDate}" pattern="yyyy-MM-dd" />"/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">remarks：</label></td>
			
					<td class="width-35" colspan="3">
			
						<form:textarea path="remarks" htmlEscape="false" rows="4"    class="form-control "/>
					</td>
				</tr>
		 	</tbody>
		</table>
	</form:form>
</body>
</html>