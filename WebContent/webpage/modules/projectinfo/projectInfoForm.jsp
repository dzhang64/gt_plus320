<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>项目信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		var $table;   // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		function doSubmit(table,index,pName,pValue){
			if(validateForm.form()){

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
					jp.post("${ctx}/projectinfo/projectInfo/save",$('#inputForm').serialize(),function(data){
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
				elem: '#startTime',
				event: 'focus'
			});
			laydate({
				elem: '#endTime',
				event: 'focus'
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
	</script>
</head>
<body class="hideScroll">
        <input type="hidden" id="userKey" value="" />
		<form:form id="inputForm" modelAttribute="projectInfo" class="form-horizontal">
		<form:hidden path="id"/>
		
		
		<input type="hidden" name="dirtyUpdateDate" value="${projectInfo.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">项目名称：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="projectName" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">项目编号：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="projectNum" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">项目性质：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="projectAttribute" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('project_attribute',projectInfo.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">项目类型：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="projectType" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('project_type',projectInfo.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">产品类型：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="productType" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('product',projectInfo.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">区域：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<sys:treeselect id="area" name="area.id" value="${projectInfo.area.id}" labelName="area.name" labelValue="${projectInfo.area.name}"
							title="区域" url="/sys/area/treeData" cssClass="form-control " allowClear="true" notAllowSelectParent="true"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">执行部门：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<sys:treeselect id="office" name="office.id" value="${projectInfo.office.id}" labelName="office.name" labelValue="${projectInfo.office.name}"
							title="部门" url="/sys/office/treeData?type=2" cssClass="form-control " allowClear="true" notAllowSelectParent="true"/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">设备商：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="deviceCompany" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('device_company_type',projectInfo.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">合作对象：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="cooperative" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('cooperative',projectInfo.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">运营商：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="operator" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('operator',projectInfo.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">合同额：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="contractAmount" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">开工日期：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="startTime" name="startTime" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${projectInfo.startTime}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">完工日期：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="endTime" name="endTime" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${projectInfo.endTime}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">项目经理：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<sys:gridselectcustom url="${ctx}/userinfo/userInfo/selectUserInfo" id="projectmanager" name="projectmanager.id"  value="${projectInfo.projectmanager.id}"  title="选择项目经理" labelName="projectmanager.name" 
						 labelValue="${projectInfo.projectmanager.name}" cssClass="form-control required" fieldLabels="姓名|工号|部门|区域|员工状态|员工类型" fieldKeys="name|nameCode|office|area|userStatus|userType" fieldTypes="0|0|0|0|sys_user_status|sys_user_type" filter="" searchLabel="姓名" searchKey="name"></sys:gridselectcustom>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">附件：</label></td>
			
					<td class="width-35" colspan="3">
						
						<input type="hidden" id="attachfile" name="attachfile" value='${projectInfo.attachfile}' />
						<sys:gtfinder input="attachfile" uploadPath="/projectinfo/projectInfo"></sys:gtfinder>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">简介：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
						<form:textarea path="attachmessage" htmlEscape="false" rows="4"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
		 	</tbody>
		</table>
		
	</form:form>
</body>
</html>