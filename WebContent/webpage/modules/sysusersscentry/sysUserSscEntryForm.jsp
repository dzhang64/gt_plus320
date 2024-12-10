<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>社保统筹补录入管理</title>
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
					jp.post("${ctx}/sysusersscentry/sysUserSscEntry/save",$('#inputForm').serialize(),function(data){
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
		//gridselect选择后执行
		function getGridSelectItem(objstr) {
		 $("#officeName").val(objstr[3]); 
		 $("#nameCode").val(objstr[2]); 
		}
	</script>
</head>
<body class="hideScroll">
        <input type="hidden" id="userKey" value="" />
		<form:form id="inputForm" modelAttribute="sysUserSscEntry" class="form-horizontal">
		<form:hidden path="id"/>
		
		
		<input type="hidden" name="dirtyUpdateDate" value="${sysUserSscEntry.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">员工姓名：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<sys:gridselectcustomU url="${ctx}/userinfo/userInfo/selectUserInfo" id="name" name="name.id"  value="${sysUserSscEntry.name.id}"  title="选择员工姓名" labelName="name.name" 
						 labelValue="${sysUserSscEntry.name.name}" cssClass="form-control required" fieldLabels="姓名|工号|部门|区域|员工状态|员工类型" fieldKeys="name|nameCode|office|area|userStatus|userType" fieldTypes="0|0|0|0|sys_user_status|sys_user_type" filter="" searchLabel="姓名" searchKey="name"></sys:gridselectcustomU>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">工号：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="nameCode" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">部门：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="officeName" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">社保交纳地：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<sys:treeselect id="ssc" name="ssc.id" value="${sysUserSscEntry.ssc.id}" labelName="ssc.name" labelValue="${sysUserSscEntry.ssc.name}"
							title="区域" url="/sys/area/treeData" cssClass="form-control " allowClear="true" notAllowSelectParent="true"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">统筹号：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="coordinateNum" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">公积金账号：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="providentNum" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
		 	</tbody>
		</table>
		
	</form:form>
</body>
</html>