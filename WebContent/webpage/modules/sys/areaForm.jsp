<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>区域管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		var $table;   // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		function doSubmit(table, index, pName, pValue){
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
					jp.post("${ctx}/sys/area/save",$('#inputForm').serialize(),function(data){
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
	</script>
</head>
<body class="hideScroll">
	<form:form id="inputForm" modelAttribute="area" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" name="dirtyUpdateDate" value="${area.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
			<tr>
				<td class="width-15 active"><label class="pull-right">上级上级区域：</label></td>
			    <td class="width-35">
						<fieldset class="set">
					<sys:treeselect id="parent" name="parent.id" value="${area.parent.id}" labelName="parent.name" labelValue="${area.parent.name}"
						title="上级区域" url="/sys/area/treeData" extId="${area.id}" cssClass="form-control " allowClear="true"/>
					</fieldset>
				</td>
				<td class="width-15 active"><label class="pull-right">区域名称：</label></td>
			    <td class="width-35">
						<fieldset class="set">
					<form:input path="name" htmlEscape="false"    class="form-control "/>
					</fieldset>
				</td>
			</tr>
			<tr>
				<td class="width-15 active"><label class="pull-right"><font color="red">*</font>区域编码：</label></td>
			    <td class="width-35">
						<fieldset class="set">
					<form:input path="code" htmlEscape="false"    class="form-control required"/>
					</fieldset>
				</td>
				<td class="width-15 active"><label class="pull-right">区域类型：</label></td>
			    <td class="width-35">
						<fieldset class="set">
					<form:select path="type" class="form-control ">
						<form:option value="" label=""/>
						<form:options items="${fns:getDictListEdit('sys_area_type',area.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					</fieldset>
				</td>
			</tr>
			<tr>
				<td class="width-15 active"><label class="pull-right">备注信息：</label></td>
				<td class="width-35" colspan="3">
						<fieldset class="set">
					<form:textarea path="remarks" htmlEscape="false" rows="4"    class="form-control "/>
					</fieldset>
				</td>
			</tr>
		</tbody>
	</table>
	</form:form>
</body>
</html>