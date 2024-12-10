<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>流程测试管理</title>
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
			  	if ($("#status")) $("#status").val(defaultPValue);
			  } else {
			  	//保存，办理
			  	if (pName && pValue && $("#" + pName) && pValue != '') $("#" + pName).val(pValue);
			  }
			  
			  //办理
			  /*
			  if (pName && $("#" + pName)) {
		      	$("#" + pName).val(defaultPValue);
		      } else {
		      	if ($("#status") && $("#status").val() == '') $("#status").val(defaultPValue);
		      }
		      if (pName && pValue && $("#" + pName) && pValue != '') $("#" + pName).val(pValue);
		      */
		      
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
					jp.post("${ctx}/test/testworkflow/testWorkflow/save",$('#inputForm').serialize(),function(data){
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
				elem: '#submitDate', //目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
				event: 'focus' //响应事件。如果没有传入event，则按照默认的click
			});
			//如果存在审核信息，创建审核信息历史列表
			if($("#preAudits")) bindAudits($("#preAudits").val());
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
		//创建审核信息历史列表
		function bindAudits(data) {
			data = "{'list':[" + data + "]}";
			var audit = eval("(" + data+ ")");
			if (audit.list.length > 0) {
				var html = '';
				//输出table
				html += '<table class="table table-striped table-bordered table-condensed" style="margin-bottom:0px;">';
				html += '<thead>';
				html += '<th>执行人</th>';
				html += '<th>执行时间</th>';
				html += '<th>操作信息</th>';
				html += '</thead>';
				html += '<tbody>';
				for(var i=0; i<audit.list.length; i++) {
					html += '<tr ' + ((i > 0) ? 'class="more" style="display:none"' : '') + '>';
					html += '<td>' + audit.list[i].auditUser + '</td>';
					html += '<td>' + audit.list[i].auditTime + '</td>';
					html += '<td style="color:' + audit.list[i].auditColor + '">' + audit.list[i].auditComments + '</td>';
					html += '</tr>';
				}
				html += '</tbody>';
				html += '</table>';
				$("#auditDiv").html(html);
			}
		}
		
	</script>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="testWorkflow" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="status" />
		<form:hidden path="preStatus" />
		<form:hidden path="preAudits" />
		<input type="hidden" name="dirtyUpdateDate" value="${testWorkflow.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>名称：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="name" htmlEscape="false"    class="form-control required"/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">提交日期：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="submitDate" name="submitDate" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${testWorkflow.submitDate}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">状态：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
					${fns:getDictLabel(testWorkflow.status, 'bas_3workflow', '')}
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">审核信息：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="audit">
						<textarea id="audits" name="audits" htmlEscape="false" rows="2" class="form-control"></textarea>
						</fieldset>
						<a class="btn btn-white btn-sm" onclick="($('.more').attr('style') == '') ? $('.more').attr('style','display:none') : $('.more').attr('style','')" title="操作历史" style="margin-top:4px;"><i class="fa fa-edit"></i> 操作历史</a>
						<div id="auditDiv" style="margin-top:5px;"></div>
					</td>
				</tr>
		 	</tbody>
		</table>
	</form:form>
</body>
</html>