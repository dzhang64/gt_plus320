<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>机构管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		var $table;   // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		function doSubmit(table, index){//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
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
			$("#name").focus();
			validateForm = $("#inputForm").validate({
				submitHandler: function(form){
					jp.post("${ctx}/sys/office/save",$('#inputForm').serialize(),function(data){
						if(data.success){
	                    	$table.refresh();
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
			}
		});
		function monitorBody(){
			
		}
	</script>
</head>
<body class="hideScroll">
	<form:form id="inputForm" modelAttribute="office" class="form-horizontal">
		<fieldset class="set">
		<form:hidden path="id"/>
		<form:hidden path="viewFlag"/>
		<input type="hidden" name="rootId" value="${rootId}" />
		<sys:message content="${message}"/>
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
		      <tr>
		         <td class="width-15 active"><label class="pull-right">上级机构:</label></td>
		         <td class="width-35"><sys:treeselect id="office" name="parent.id" value="${office.parent.id}" labelName="parent.name" labelValue="${office.parent.name}"
					title="机构" url="/sys/office/treeData" extId="${office.id}"  cssClass="form-control" allowClear="${office.currentUser.admin}"/></td>
		         <td  class="width-15"  class="active"><label class="pull-right"><font color="red">*</font>归属区域:</label></td>
		         <td class="width-35"><sys:treeselect id="area" name="area.id" value="${office.area.id}" labelName="area.name" labelValue="${office.area.name}"
					title="区域" url="/sys/area/treeData" cssClass="form-control required"/></td>
		      </tr>
		      <tr>
		         <td  class="width-15"  class="active"><label class="pull-right">机构编码:</label></td>
		         <td class="width-35"><form:input path="code" htmlEscape="false" maxlength="50" class="form-control"/></td>
		         <td class="width-15 active"><label class="pull-right"><font color="red">*</font>机构名称:</label></td>
		         <td class="width-35"><form:input path="name" htmlEscape="false" maxlength="50" class="form-control required"/></td>
		      </tr>
		      <tr>
		       	 <td class="width-15 active"><label class="pull-right"><font color="red">*</font>机构简称:</label></td>
		         <td class="width-35"><form:input path="shortName" htmlEscape="false" maxlength="50" class="form-control required"/></td>
		         <td class="width-15 active"><label class="pull-right">机构类型:</label></td>
		         <td class="width-35"><form:select path="type" class="form-control">
					<form:options items="${fns:getDictList('sys_office_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select></td>
		      </tr>
		      <tr>
		         <td  class="width-15"  class="active"><label class="pull-right">机构级别:</label></td>
		         <td class="width-35"><form:select path="grade" class="form-control">
					<form:options items="${fns:getDictList('sys_office_grade')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select></td>
		         <td class="width-15"  class="active"><label class="pull-right">主负责人:</label></td>
		         <td class="width-35"><sys:treeselect id="primaryPerson" name="primaryPerson.id" value="${office.primaryPerson.id}" labelName="office.primaryPerson.name" labelValue="${office.primaryPerson.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="form-control" allowClear="true" notAllowSelectParent="true"/></td>
		      </tr>
		      <tr>	 
		      	 <td class="width-15 active"><label class="pull-right">副负责人:</label></td>
		         <td class="width-35"><sys:treeselect id="deputyPerson" name="deputyPerson.id" value="${office.deputyPerson.id}" labelName="office.deputyPerson.name" labelValue="${office.deputyPerson.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="form-control" allowClear="true" notAllowSelectParent="true"/></td>
		         <td class="width-15 active"><label class="pull-right">是否可用:</label></td>
		         <td class="width-35"><form:select path="useable" class="form-control">
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
				 </td>
			  </tr>
			  <tr>
			  <td class="width-15 active"><label class="pull-right">岗位：</label></td>
			  
			  	<td class="width-35" colspan="3">
						<fieldset class="set">
						<sys:gridselectcustomquery url="${ctx}/sys/office/selectpost" id="postParam" name="postParam.id"  value="${office.postParam.id}"  title="选择岗位" labelName="postParam.name" 
						queryName="postParamId" queryId="postParamId" checkName="postParamName" 
						 labelValue="${office.postParam.name}" cssClass="form-control" cssStyle="width:610px;" fieldLabels="岗位名称" fieldKeys="name" searchLabel="岗位名称" searchKey="name" ></sys:gridselectcustomquery>
						</fieldset>
					</td>
			  </tr>
		      <tr>
				 <td  class="width-15" class="active"><label class="pull-right">联系人:</label></td>
		         <td class="width-35"><form:input path="master" htmlEscape="false" maxlength="50" cssClass="form-control" /></td>
		         <td class="width-15 active"><label class="pull-right">电话:</label></td>
		         <td class="width-35"><form:input path="phone" htmlEscape="false" maxlength="50" cssClass="form-control" /></td>
		      </tr>
		      <tr>   
		         <td  class="width-15"  class="active"><label class="pull-right">传真:</label></td>
		         <td class="width-35"><form:input path="fax" htmlEscape="false" maxlength="50" cssClass="form-control" /></td>
		         <td class="width-15 active"><label class="pull-right">邮政编码:</label></td>
		         <td class="width-35"><form:input path="zipCode" htmlEscape="false" maxlength="50" cssClass="form-control" /></td>
		      </tr>
		      <tr>
		         <td class="width-15 active"><label class="pull-right">邮箱:</label></td>
		         <td class="width-35"><form:input path="email" htmlEscape="false" maxlength="50" cssClass="form-control" /></td>
		         <td class="width-15"  class="active"><label class="pull-right">序号:</label></td>
		         <td class="width-35"><form:input path="sort" htmlEscape="false" maxlength="50" cssClass="form-control" /></td>
		      </tr>
		      <tr>
		         <td class="width-15"  class="active"><label class="pull-right">联系地址:</label></td>
		         <td class="width-35" colspan="3"><form:input path="address" htmlEscape="false" maxlength="50" cssClass="form-control" /></td>
		      </tr>
		      <tr>
		         <td  class="width-15" class="active"><label class="pull-right">备注:</label></td>
		         <td class="width-35" colspan="3"><form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="200" class="form-control"/></td>
		      </tr>
	      </tbody>
	      </table>
	      </fieldset>
	</form:form>
</body>
</html>