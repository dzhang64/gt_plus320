<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>权限管理管理</title>
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
					jp.post("${ctx}/oa/setting/taskPermission/save",$('#inputForm').serialize(),function(data){
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

		//gridselect选择后执行
		function afterGridSelect(callbackKey, item) {
			<%--
			//callbackKey，区分多个gridselect
			//if(callbackKey == 'gridselect_01') {
			//
			//} else if (callbackKey == 'gridselect_02') {
			//
			//}
			--%>
		}
		
		function changeButtonType(value){
			//权限位置==起始节点
			if (value=='1') {
				$('#zc').val('暂存_saveAndClaim');
				$('#tj').val('提交_saveAndComplete');
				
			}
			//权限位置==处理节点
			else if (value=='2') {
				$('#zc').val('暂存_save');
				$('#tj').val('提交_saveAndStart');
			}
		}
	</script>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="taskPermission" class="form-horizontal">
		<form:hidden path="id"/>
		
		
		<input type="hidden" name="dirtyUpdateDate" value="${taskPermission.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>权限名称：</label></td>
			
			        <td class="width-35" colspan="3">
						<fieldset class="set">
						<form:input path="name" htmlEscape="false"    class="form-control required"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>权限类型：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="type" class="form-control required">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('oa_task_permission_type',taskPermission.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>权限类别：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="category" class="form-control required">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('oa_task_permission_category',taskPermission.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>权限位置：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="position" class="form-control required" onchange="changeButtonType(this.value);">
							<form:options items="${fns:getDictListEdit('oa_edoc_position',taskPermission.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>是否启用：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="isInuse" class="form-control required">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('yes_no',taskPermission.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>高级操作：</label></td>
			
					<td class="width-35" colspan="3">
						<input id="zc" type="checkbox" class="i-checks" name="operation" value="暂存_saveAndClaim" <c:if test="${fn:contains(taskPermission.operation, '暂存_save') or fn:contains(taskPermission.operation, '暂存_saveAndClaim')}">checked</c:if>/>
						暂存
						<input id="tj" type="checkbox" class="i-checks" name="operation" value="提交_saveAndComplete" <c:if test="${fn:contains(taskPermission.operation, '提交_saveAndStart') or fn:contains(taskPermission.operation, '提交_saveAndComplete')}">checked</c:if>/>
						提交
						<input type="checkbox" class="i-checks" name="operation" value="回退_saveAndReject" <c:if test="${fn:contains(taskPermission.operation, '回退_saveAndReject')}">checked</c:if>/>
						回退
						<input type="checkbox" class="i-checks" name="operation" value="指定回退_saveAndRejectTo" <c:if test="${fn:contains(taskPermission.operation, '指定回退_saveAndRejectTo')}">checked</c:if> disabled="disabled"/>
						指定回退
						<input type="checkbox" class="i-checks" name="operation" value="终止_saveAndTerminate" <c:if test="${fn:contains(taskPermission.operation, '终止_saveAndTerminate')}">checked</c:if>/>
						终止
						<input type="checkbox" class="i-checks" name="operation" value="取消_cancel" <c:if test="${fn:contains(taskPermission.operation, '取消_cancel')}">checked</c:if>/>
						取消
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">描述：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
						<form:textarea path="describe" htmlEscape="false" rows="4"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
		 	</tbody>
		</table>
		
	</form:form>
</body>
</html>