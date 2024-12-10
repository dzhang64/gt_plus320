<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>任务通知管理</title>
	<meta name="decorator" content="default"/>
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
					jp.post("${ctx}/bas/tasknotice/basTaskNotice/save",$('#inputForm').serialize(),function(data){
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
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="basTaskNotice" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" name="dirtyUpdateDate" value="${basTaskNotice.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">消息ID：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="messageId" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">通知内容：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="content" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">接收人：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="userName" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">接收人归属机构：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="officeName" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">状态：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
						<form:select path="status" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictList('oa_notify_read')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
				</tr>
		 	</tbody>
		</table>
	</form:form>
</body>
</html>