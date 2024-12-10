<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>档案管理</title>
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
						jp.post("${ctx}/oa/arc/archive/save",$('#inputForm').serialize(),function(data){
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
		<form:form id="inputForm" modelAttribute="archive" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" name="dirtyUpdateDate" value="${archive.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>标题：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
						<form:input path="title" htmlEscape="false"    class="form-control required"/>
						</fieldset>
					</td>
				</tr>
				<tr>
				<td class="width-15 active"><label class="pull-right"><font color="red">*</font>档案目录：</label></td>
				
			        <td class="width-35"  >
						<fieldset class="set">
						<%-- <sys:gridselect url="${ctx}/oa/arc/archive/selectarc" id="arc" name="arc.id"  value="${archive.arc.id}"  title="选择档案目录" labelName="arc.name" 
						 labelValue="${archive.arc.name}" cssClass="form-control required" fieldLabels="目录名称" fieldKeys="name" searchLabel="目录名称" searchKey="name" ></sys:gridselect> --%>
						<sys:treeselect id="arc" name="arc.id" value="${archive.arc.id}" labelName="arc.name" labelValue="${archive.arc.name}"
						title="档案栏目" url="/oa/arc/arcCategory/treeData?type=2" cssClass="form-control required" allowClear="true" notAllowSelectParent="true" />
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>内容类型：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<%-- <form:input path="oaArcTypeCode" htmlEscape="false"    class="form-control required"/> --%>
						<form:select path="oaArcTypeCode" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('archive_type',archive.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>	
				</tr>
				<%-- <tr>
					<td class="width-15 active"><label class="pull-right">阅读量：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="readCount" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					
				</tr> --%>
				
				<tr>
					<td class="width-15 active"><label class="pull-right">备注信息：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
						<form:textarea path="remarks" htmlEscape="false" rows="4"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">附件：</label></td>
			
					<td class="width-35" colspan="3">
						
						<input type="hidden" id="files" name="files" value='${archive.files}' />
						<sys:gtfinder input="files" uploadPath="/oa/arc/archive"></sys:gtfinder>
					</td>
				</tr>
		 	</tbody>
		</table>
	</form:form>
</body>
</html>