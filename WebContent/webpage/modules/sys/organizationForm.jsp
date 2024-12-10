<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>组织管理管理</title>
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
			rules: {
					orgNumber: {remote: "${ctx}/sys/organization/checkorgNumber?oldorgNumber=" + encodeURIComponent('${organization.orgNumber}')}//设置了远程验证，在初始化时必须预先调用一次。
				},
				messages: {
					orgNumber: {remote: "组织编号已存在"}
				},
				submitHandler: function(form){
					jp.post("${ctx}/sys/organization/save",$('#inputForm').serialize(),function(data){
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
		<form:form id="inputForm" modelAttribute="organization" class="form-horizontal">
		<form:hidden path="id"/>
		
		
		<input type="hidden" name="dirtyUpdateDate" value="${organization.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>组织编号：</label></td>
			
			        <td class="width-35">
						<input id="oldorgNumber" name="oldorgNumber" type="hidden" value="${organization.orgNumber}">
						<form:input path="orgNumber" htmlEscape="false"    class="form-control required"/>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>组织名称：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="orgName" htmlEscape="false"    class="form-control required"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>组织负责人：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<sys:treeselect id="primaryPerson" name="primaryPerson.id" value="${organization.primaryPerson.id}" labelName="primaryPerson.name" labelValue="${organization.primaryPerson.name}"
							title="用户" url="/sys/office/treeData?type=3" cssClass="form-control required" allowClear="true" notAllowSelectParent="true"/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>是否有效：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="orgEffect" class="form-control required">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('yes_no',organization.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">序号：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
						<form:input path="orgSequenceNumber" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">备注信息：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
						<form:textarea path="remarks" htmlEscape="false" rows="4"    class="form-control"/>
						</fieldset>
					</td>
				</tr>
		 	</tbody>
		</table>
		
	</form:form>
</body>
</html>