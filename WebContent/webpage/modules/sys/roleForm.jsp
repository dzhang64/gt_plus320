<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>角色管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/webpage/include/treeview.jsp" %>
	<script type="text/javascript">
	 	var validateForm;
		function doSubmit(table, index){
			if(validateForm.form()){	      
				$table = table;
				$topIndex = index;
				jp.loading();
				$("#inputForm").submit();
				return true;
			}
			return false;
		}
		$(document).ready(function(){
			$("#name").focus();
			validateForm= $("#inputForm").validate({
				rules: {
					name: {remote: "${ctx}/sys/role/checkName?oldName=" + encodeURIComponent("${role.name}")},//设置了远程验证，在初始化时必须预先调用一次。
					enname: {remote: "${ctx}/sys/role/checkEnname?oldEnname=" + encodeURIComponent("${role.enname}")}
				},
				messages: {
					name: {remote: "角色名已存在"},
					enname: {remote: "英文名已存在"}
				},
				submitHandler: function(form){
					//var ids = [], nodes = tree.getCheckedNodes(true);
					//for(var i=0; i<nodes.length; i++) {
					//	ids.push(nodes[i].id);
					//}
					//$("#menuIds").val(ids);
					/*
					var ids2 = [], nodes2 = tree2.getCheckedNodes(true);
					for(var i=0; i<nodes2.length; i++) {
						ids2.push(nodes2[i].id);
					}
					$("#officeIds").val(ids2);
					loading('正在提交，请稍等...');
					form.submit();
					*/
					var ids2 = [], nodes2 = tree2.getCheckedNodes(true);
					for(var i=0; i<nodes2.length; i++) {
						ids2.push(nodes2[i].id);
					}
					$("#officeIds").val(ids2);
					jp.post("${ctx}/sys/role/save",$('#inputForm').serialize(),function(data){
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
			}
			
			//在ready函数中预先调用一次远程校验函数，是一个无奈的回避案。(David）
			//否则打开修改对话框，不做任何更改直接submit,这时再触发远程校验，耗时较长，
			//submit函数在等待远程校验结果然后再提交，而layer对话框不会阻塞会直接关闭同时会销毁表单，因此submit没有提交就被销毁了导致提交表单失败。
			$("#inputForm").validate().element($("#name"));
			$("#inputForm").validate().element($("#enname"));
		
			var setting = {check:{enable:true,nocheckInherit:true},view:{selectedMulti:false},
					data:{simpleData:{enable:true}},callback:{beforeClick:function(id, node){
						tree.checkNode(node, !node.checked, true, true);
						return false;
					}}};
			
			
			// 用户-机构
			var zNodes2=[
					<c:forEach items="${officeList}" var="office">{id:"${office.id}", pId:"${not empty office.parent?office.parent.id:0}", name:"${office.name}"},
		            </c:forEach>];
			// 初始化树结构
			var tree2 = $.fn.zTree.init($("#officeTree"), setting, zNodes2);
			// 不选择父节点
			tree2.setting.check.chkboxType = { "Y" : "ps", "N" : "s" };
			// 默认选择节点
			var ids2 = "${role.officeIds}".split(",");
			for(var i=0; i<ids2.length; i++) {
				var node = tree2.getNodeByParam("id", ids2[i]);
				try{tree2.checkNode(node, true, false);}catch(e){}
			}
			// 默认展开全部节点
			tree2.expandAll(true);
			// 刷新（显示/隐藏）机构
			refreshOfficeTree();
			$("#dataScope").change(function(){
				refreshOfficeTree();
			});
		});
		function refreshOfficeTree(){
			if($("#dataScope").val()==9){
				$("#officeTree").show();
			}else{
				$("#officeTree").hide();
			}
		}

	</script>
</head>
<body class="hideScroll">
	<form:form id="inputForm" modelAttribute="role" autocomplete="off" class="form-horizontal" >
		<form:hidden path="id"/>
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>
		<fieldset class="set">
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
		      <tr>
		      	 <td class="width-15 active"><label class="pull-right"><font color="red">*</font>归属子系统：</label></td>
		         <td class="width-35"><form:select path="subSystem.code"  items="${subSystemList}" itemValue="code" itemLabel="name" class="form-control ">
					</form:select>
				 </td>
		         <td class="width-15 active"><label class="pull-right">归属机构：</label></td>
		         <td class="width-35"> <sys:treeselect id="office" name="office.id" value="${role.office.id}" labelName="office.name" labelValue="${role.office.name}"
					title="机构" url="/sys/office/treeData" cssClass="form-control"/></td>
		      </tr>
		      <tr>
		         <td  class="width-15" class="active"><label class="pull-right"><font color="red">*</font>角色名称：</label></td>
		         <td class="width-35"><input id="oldName" name="oldName" type="hidden" value="${role.name}">
					<form:input path="name" htmlEscape="false" maxlength="50" class="form-control required"/></td>
		         <td class="width-15 active"><label class="pull-right"><font color="red">*</font>英文名称：</label></td>
		         <td class="width-35"><input id="oldEnname" name="oldEnname" type="hidden" value="${role.enname}">
					<form:input path="enname" htmlEscape="false" maxlength="50" class="form-control required"/></td>
		      </tr>
		      <tr>
		         <td  class="width-15" class="active"><label class="pull-right">角色类型：</label></td>
		         <td class="width-35"><%--
					<form:input path="roleType" htmlEscape="false" maxlength="50" class="required"/>
					<span class="help-inline" title="activiti有3种预定义的组类型：security-role、assignment、user 如果使用Activiti Explorer，需要security-role才能看到manage页签，需要assignment才能claim任务">
						工作流组用户组类型（security-role：管理员、assignment：可进行任务分配、user：普通用户）</span> --%>
					<form:select path="roleType" class="form-control ">
						<form:option value="assignment">任务分配</form:option>
						<form:option value="security-role">管理角色</form:option>
						<form:option value="user">普通角色</form:option>
					</form:select>
					<span class="help-inline" title="activiti有3种预定义的组类型：security-role、assignment、user 如果使用Activiti Explorer，需要security-role才能看到manage页签，需要assignment才能claim任务">
						工作流组用户组类型（任务分配：assignment、管理角色：security-role、普通角色：user）</span></td>
		         <td class="width-15 active"><label class="pull-right">是否系统数据：</label></td>
		         <td class="width-35"><form:select path="sysData" class="form-control ">
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					<span class="help-inline">“是”代表此数据只有超级管理员能进行修改，“否”则表示拥有角色修改人员的权限都能进行修改</span></td>
		       </tr>
		       <tr> 
		         <td  class="width-15" class="active"><label class="pull-right">是否可用：</label></td>
		         <td class="width-35"><form:select path="useable" class="form-control ">
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					<span class="help-inline">“是”代表此数据可用，“否”则表示此数据不可用</span></td>
		     
		         <td class="width-15 active"><label class="pull-right">数据范围：</label></td>
		         <td class="width-35"><form:select path="dataScope" class="form-control ">
					<form:options items="${fns:getDictList('sys_data_scope')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					<span class="help-inline">特殊情况下，设置为“按明细设置”，可进行跨机构授权</span>
					<div class="controls">
						<div id="officeTree" class="ztree" style="margin-top:3px;"></div>
						<form:hidden path="officeIds"/>
					</div></td>
			  </tr>
		      <tr>	 
				 <td class="width-15 active"><label class="pull-right">备注：</label></td>
		         <td class="width-35" colspan="3"><form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="200" class="form-control "/></td>
		      </tr>
			</tbody>
		</table>
		<form:hidden path="menuIds"/>
		</fieldset>
	</form:form>
</body>
</html>