<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>签报管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		var $table;   // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		function doSubmit(table, index, pName, pValue){
			if(validateForm.form()){
				/* var defaultPValue = '10';
		    	if (!pName && !pValue) {
					//添加
				} else {
					//保存，办理
					if (pName && pValue && $("#" + pName) && pValue != '') $("#" + pName).val(pValue);
				} */
		      
				$table = table;
				$topIndex = index;
				
				$('#flag').val(pValue);
				var param = eval('(' + pValue + ')');
				getUserList(param);
			}
			return false;
		}
		
		function realSubmit(){
			if(validateForm.form()){
				jp.loading();
				$("#inputForm").submit();
				return true;
			}
			return false;
		}
		
		$(document).ready(function() { 
			validateForm = $("#inputForm").validate({
				submitHandler: function(form){
					jp.post("${ctx}/oa/edoc/edocSign/save",$('#inputForm').serialize(),function(data){
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
				elem: '#agentDate',
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
			//流程信息初始化
			//actInit();
			ruleArgsInit();
		});

		/* function actInit() {
			if (jp.checkTaskPermission("${edocSign.procTaskPermission}")) {
				$('#commentTextarea').attr('style','');
			} else {
				$('#commentTextarea').attr('style','display: none;');
			}
		} */
		
		function ruleArgsInit(){
			if ($('#id').val() != null && $('#id').val().trim() != ''){
				var ruleArgs = eval('('+ '${edocSign.ruleArgsJson}' +')');
				if (ruleArgs != null && ruleArgs.form !=null) {
					$.each(ruleArgs.form, function(key, value){
						//==0 向上3级移除
						if (value == 0 ) {
							$('#'+key).parent().parent().parent().remove();
						} 
						//==1 向上1级可编辑
						else if (value == 1) {
							$('#'+key).parent().removeAttr('disabled');
						} 
						//==2 向上1级不可编辑
						else if (value == 2) {
							$('#'+key).parent().attr('disabled', 'disabled');
						}
						//==3 向上1级隐藏
						else if (value == 3) {
							$('#'+key).parent().attr('style', 'display: none;');
						}
						//==4 向上1级显示
						else if (value == 4) {
							$('#'+key).parent().attr('style', '');
						}
						//==5 向上1级显示&可编辑
						else if (value == 5) {
							$('#'+key).parent().removeAttr('disabled');
							$('#'+key).parent().attr('style', '');
						}
					});
				}
			}
		}
		
		function getUserList(param){
			if (jp.isNeedUserList(param.type)) {
				//异步查询用户列表
				$.ajax({
					type:'post',
					url:'${ctx}/oa/edoc/edocSign/getUserList',
					data:'id='+$('#id').val()+'&procInsId='+$('#procInsId').val()+'&act.flag='+param.flag,
					dataType:'json',
					success:function(data){
						if (data.body == null || data.body.userList == null) {
							realSubmit();
						}
						//data.body.userList.length==1,直接提交唯一用户
						else if (data.body.userList.length == 1) {
							$('#tempLoginName').val(data.body.userList[0].loginName);
							realSubmit();
						}
						//data.body.userList.length!=1,data.body.type==single,单选
						else if (data.body.userList.length != 1 && data.body.type == 'single') {
							buildSingleBox(data.body.userList);
						}
						//data.body.userList.length!=1,data.body.type==multi,多选
						else if (data.body.userList.length != 1 && data.body.type == 'multi') {
							buildMultiBox(data.body.userList);
						}
					}
				});
			} else if (jp.isAlreadyExistUser(param.type)) {
				//退回时使用目标节点用户
				realSubmit();
			} else {
				//其他情况直接提交
				realSubmit();
			}
		}
		
		function buildSingleBox(userList){
			var select = $('<select></select>')
				.attr('id', 'userSelect')
				.attr('class', 'form-control')
				.attr('onchange', '$(\'#tempLoginName\').val(this.value)')
				.append(
					$('<option></option>').attr('selected', 'selected')
				);
			for (var i = 0; i < userList.length; i++) {
				select.append(
					$('<option></option>')
						.val(userList[i].loginName)
						.html(userList[i].name)
				);
			}
			$('#userSelectDiv').html('')
				.attr('style', '')
				.attr('class', 'modal-body')
				.append(select);
			$('#modalBtn').click();
		}
		
		function buildMultiBox(userList){
			var unselect = $('<select></select>')
				.attr('id', 'unselect')
				.attr('class', 'form-control')
				.attr('style', 'height: 200px;')
				.attr('multiple','multiple');
			for (var i = 0; i < userList.length; i++) {
				unselect.append(
					$('<option></option>')
						.attr('id', userList[i].loginName)
						.attr('class', 'unselected')
						.attr('ondblclick', 'fillSelected(this);')
						/* .attr('onclick', '$("#selectedTempId").val(this.value)') */
						.val(userList[i].loginName)
						.html(userList[i].name)
				);
			}
			var selected = $('<select></select>')
				.attr('id', 'selected_')
				.attr('class', 'form-control')
				.attr('style', 'height: 200px;')
				.attr('multiple','multiple');
			var selectBtn = $('<div></div>')
				.attr('style', 'float: left; width: 8%; margin: 60px 0px 0px 2%;')
				.append(
					$('<a></a>')
						.attr('class', 'btn btn-white btn-sm')
						.attr('data-toggle', 'tooltip')
						.attr('data-placement', 'left')
						.attr('title', '选择')
						.attr('onclick', 'fillSelectedValuesForButton()')
						.append(
							$('<i></i>').attr('class', 'glyphicon glyphicon-chevron-right')
						)
				)
				.append(
					$('<a></a>')
						.attr('class', 'btn btn-white btn-sm')
						.attr('data-toggle', 'tooltip')
						.attr('data-placement', 'left')
						.attr('title', '移除')
						.attr('onclick', 'removeSelectedValuesForButton()')
						.append(
							$('<i></i>').attr('class', 'glyphicon glyphicon-chevron-left')
						)
				);
			$('#userSelectDiv')
				.attr('class', 'modal-body')
				.attr('style', 'height: 240px;')
				.html('');
			$('#userSelectDiv')
				.append(
					$('<div></div>')
						.attr('style', 'float: left; width: 45%;')
						.append(unselect)
				)
				.append(selectBtn)
				.append(
					$('<div></div>')
						.attr('style', 'float: left; width: 45%;')
						.append(selected)
				);
			$('#modalBtn').click();
		}
		
		function fillSelected(obj){
			if ($('#selected_').html().indexOf($(obj).val()) == -1) {
				$('#selected_').append(
					$('<option></option>').val($(obj).attr('id'))
						.attr('class', 'selected')
						.attr('id', $(obj).attr('id'))
						//.attr('onclick', '$(\'#removeTempId\').val($(this).attr(\'id\'))')
						.attr('ondblclick', 'removeSelectedInput(this),$(this).remove()')
						.html($(obj).html())
				)
				$('#tempLoginNameDiv').append(
					$('<input/>').val($(obj).attr('id'))
						.attr('type', 'hidden')
						.attr('id', $(obj).attr('id'))
						.attr('name', 'tempLoginName')
						.attr('class', 'selectedInput')
						.attr('ondblclick', '$(this).remove()')
				);
			}
		}
		
		function removeSelectedInput(obj){
			$('input[id=\'' + $(obj).attr('id') + '\'][class=\'selectedInput\']').dblclick();
		}
		
		function fillSelectedValuesForButton(){
			//alert($('option[id=\'' + $('#selectedTempId').val() + '\'][class=\'unselected\']').val());
			var arr = $('#unselect').val();
			for (var i = 0; i < arr.length; i++) {
				$('option[id=\'' + arr[i] + '\'][class=\'unselected\']').dblclick();
			}
		}
		
		function removeSelectedValuesForButton(){
			//$('option[id=\'' + $('#removeTempId').val() + '\'][class=\'selected\']').dblclick();
			var arr = $('#selected_').val();
			for (var i = 0; i < arr.length; i++) {
				$('option[id=\'' + arr[i] + '\'][class=\'selected\']').dblclick();
			}
		}
		
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
	<style type="text/css">
		.unit {float:left;}
		.unit1 {width:230px;float:left;}
		.unit2 {width:460px;float:left;}
		.unit3 {width:690px;float:left;}
		.unit4 {width:100%;float:left;}
		.unit-align {text-align: center;}
		.unit-font {color: green;}
		.unit-label {margin-top:8px;}
	</style>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="edocSign" class="form-horizontal">
		<form:hidden path="id"/>
		
		<input type="hidden" id="act.taskId" name="act.taskId" value="${edocSign.act.taskId }"/>
		<input type="hidden" id="act.taskName" name="act.taskName" value="${edocSign.act.taskName }"/>
		<input type="hidden" id="act.taskDefKey" name="act.taskDefKey" value="${edocSign.act.taskDefKey }"/>
		<input type="hidden" id="procInsId" name="act.procInsId" value="${edocSign.act.procInsId }"/>
		<input type="hidden" id="act.procDefId" name="act.procDefId" value="${edocSign.act.procDefId }"/>
		<input type="hidden" id="flag" name="act.flag"/>
		<input type="hidden" id="selectedTempId" />
		<input type="hidden" id="removeTempId" />
		<input type="hidden" id="tempLoginName" name="tempLoginName"/>
		<div id="tempLoginNameDiv"></div>
		
		<input type="hidden" name="dirtyUpdateDate" value="${edocSign.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
		   		<tr>
					<td colspan="4">
						<div style="float: left;">
							<input type="hidden" name="contentUrl" value="${edocSign.contentUrl }">
							<input type="hidden" id="remarks" name="remarks" value="${edocSign.remarks }">
			   				<a class="btn btn-success btn-sm" onclick="openContent();"><i class="fa fa-edit"></i> 正文</a>
							<script>
								function openContent(){
									if ("${edocSign.id}" == null || "${edocSign.id}" == ""
										|| "${edocSign.remarks}" == null || "${edocSign.remarks}" == "") {
										$('#remarks').val($('#tpl').val());
										window.open("${ctx}/oa/edoc/edocSign/content?id=${edocSign.id}&token=${token}&uploadPath=${realPath}${edocSign.contentUrl}&remarks=" + $('#remarks').val(), "正文", "", false);
									} else {
										window.open("${ctx}/oa/edoc/edocSign/content?id=${edocSign.id}&token=${token}&uploadPath=${realPath}${edocSign.contentUrl}", "正文", "", false);
									}
								}
							</script>
						</div>
						<div style="float: left; margin-left: 20px; display:<c:if test="${edocSign.id != null && edocSign.id != '' }">none;</c:if>">
							<select id="tpl" class="form-control">
								<c:forEach items="${tplList }" var="tpl">
									<option value='${tpl.fileUrl }'>${fn:substringBefore(tpl.filename,'.')}</option>
								</c:forEach>
							</select>
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="4">
						<div class="unit1">
							<div style="width:30px; float: left;"><label class="unit-label unit-align unit-font">密级</label></div>
			        		<div style="width:100px; float: left;">
			        			<fieldset class="set">
									<form:select path="dense" class="form-control ">
										<form:option value="" label=""/>
										<form:options items="${fns:getDictListEdit('oa_edoc_dense2',edocSign.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
									</form:select>
								</fieldset>
			        		</div>
			        	</div>
			        	<div class="unit1">
							<div style="width:30px; float: left;"><label class="unit-label unit-align unit-font">缓急</label></div>
			        		<div style="width:100px; float: left;">
			        			<fieldset class="set">
									<form:select path="urgency" class="form-control ">
										<form:option value="" label=""/>
										<form:options items="${fns:getDictListEdit('oa_edoc_urgency',edocSign.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
									</form:select>
								</fieldset>
			        		</div>
			        	</div>
					</td>
				</tr>
				<tr>
					<td colspan="4">
						<div class="unit4 unit-align unit-font">
			        		<h2>签报</h2>
			        	</div>
	        		</td>
				</tr>
				<tr>
					<td colspan="4">
						<div class="unit3">
							<div style="width:105px; float: left;"><label class="unit-label unit-align unit-font">国家物资储备局</label></div>
			        		<div style="width:150px; float: left;">
			        			<fieldset class="set">
									<form:input path="orgDepartment" htmlEscape="false"    class="form-control "/>
								</fieldset>
			        		</div>
							<div style="width:15px; float: left;"><label class="unit-label unit-align unit-font">处</label></div>
			        	</div>
			        	<div class="unit1">
			        		<div style="width:150px; float: left;">
			        			<fieldset class="set">
									<form:input path="docNumber" htmlEscape="false"    class="form-control "/>
									<%-- 
									<form:select path="docNumber" class="form-control ">
										<form:option value="" label=""/>
										<form:options items="${fns:getDictListEdit('oa_edoc_doc_number',edocSign.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
									</form:select>
									--%>
								</fieldset>
			        		</div>
			        	</div>
					</td>
				</tr>
				<tr>
					<td colspan="4">
						<div class="unit4">
			        		<div><label class="unit-label unit-align unit-font">局领导批示</label></div>
			        		<div style="width:900px;">
			        			<fieldset class="set">
			        				<div onclick="$('#leaderInstructions').focus();"
										style="width:900px; height: 100px;">
										${edocSign.leaderInstructions }
									</div>
									<div style="width:900px;">
										<textarea id="leaderInstructions" name="leaderInstructions" htmlEscape="false" rows="2"    class="form-control "
											style="border: 0px solid; resize:none;">
										</textarea>
									</div>
								</fieldset>
			        		</div>
			        	</div>
	        		</td>
				</tr>
				<tr>
					<td colspan="4">
						<div class="unit4">
							<div style="width:30px; float: left;"><label class="unit-label unit-align unit-font">呈送</label></div>
			        		<div style="width:150px; float: left;">
			        			<fieldset class="set">
									<form:input path="sendTo" htmlEscape="false"    class="form-control "/>
								</fieldset>
			        		</div>
			        	</div>
	        		</td>
				</tr>
				<tr>
					<td colspan="4">
						<div class="unit2">
			        		<div><label class="unit-label unit-align unit-font">呈报单位负责人</label></div>
			        		<div style="width:460px;">
			        			<fieldset class="set">
			        				<form:textarea path="sendToMainPerson" htmlEscape="false" rows="8"    class="form-control "
										style="border: 0px solid; border-right: 1px solid; resize:none;"
			        				/>
								</fieldset>
			        		</div>
			        	</div>
			        	<div class="unit2">
			        		<div><label class="unit-label unit-align unit-font">会签单位负责人</label></div>
			        		<div style="width:460px;">
			        			<fieldset class="set">
			        				<form:textarea path="signMainPerson" htmlEscape="false" rows="8"    class="form-control "
										style="border: 0px solid; resize:none;"
			        				/>
								</fieldset>
			        		</div>
			        	</div>
					</td>
				</tr>
				<tr>
					<td colspan="4">
			        	<div class="unit4">
			        		<div><label class="unit-label unit-align unit-font">标题</label></div>
			        		<div style="width:900px;">
			        			<fieldset class="set">
			        				<form:textarea path="title" htmlEscape="false" rows="2"    class="form-control "
			        					style="border: 0px solid; resize:none;"
			        				/>
								</fieldset>
			        		</div>
			        	</div>
					</td>
				</tr>
				<tr>
					<td colspan="4">
			        	<div class="unit4">
			        		<div style="width:100px; float: left;"><label class="unit-label unit-align unit-font">电子附件</label></div>
			        		<div style="width:800px; float: left;">
			        			<fieldset class="set">
			        				<input type="hidden" id="files" name="files" value='${edocSign.files}' />
									<sys:gtfinder input="files" uploadPath="/oa/edoc/edocSign"></sys:gtfinder>
								</fieldset>
			        		</div>
			        		<%-- <div><label class="unit-label unit-align unit-font">电子附件</label></div>
			        		<div style="width:900px;">
			        			<fieldset class="set">
			        				<input type="hidden" id="files" name="files" value='${edocSign.files}' />
									<sys:gtfinder input="files" uploadPath="/oa/edoc/edocSign"></sys:gtfinder>
								</fieldset>
			        		</div> --%>
			        	</div>
					</td>
				</tr>
				<tr>
					<td colspan="4">
						<div class="unit2">
			        		<div style="width:90px; float: left;"><label class="unit-label unit-align unit-font">拟稿人</label></div>
			        		<div style="width:240px; float: left;">
			        			<fieldset class="set">
									<form:input path="drafter" htmlEscape="false"    class="form-control "/>
								</fieldset>
			        		</div>
			        	</div>
			        	<div class="unit2">
			        		<div style="width:90px; float: left;"><label class="unit-label unit-align unit-font">电话</label></div>
			        		<div style="width:240px; float: left;">
			        			<fieldset class="set">
									<form:input path="phone" htmlEscape="false"    class="form-control "/>
								</fieldset>
			        		</div>
			        	</div>
					</td>
				</tr>
				<tr>
					<td colspan="4">
						<div class="unit2">
			        		<div style="width:90px; float: left;"><label class="unit-label unit-align unit-font">办公室经办人</label></div>
			        		<div style="width:240px; float: left;">
			        			<fieldset class="set">
									<form:input path="officeAgent" htmlEscape="false"    class="form-control "/>
								</fieldset>
			        		</div>
			        	</div>
			        	<div class="unit2">
			        		<div style="width:90px; float: left;"><label class="unit-label unit-align unit-font">经办日期</label></div>
			        		<div style="width:240px; float: left;">
			        			<fieldset class="set">
									<input id="agentDate" name="agentDate" type="text" maxlength="20" class="laydate-icon form-control layer-date "
										value="<fmt:formatDate value="${edocSign.agentDate}" pattern="yyyy-MM-dd" />"/>
								</fieldset>
			        		</div>
			        	</div>
					</td>
				</tr>
				<tr>
					<td colspan="4">
						<div class="unit4">
							<div style="width:60px; float: left;"><label class="unit-label unit-align unit-font">上报信息</label></div>
			        		<div style="width:100px; float: left;">
			        			<fieldset class="set">
									<form:select path="reportInfo" class="form-control ">
										<form:option value="" label=""/>
										<form:options items="${fns:getDictListEdit('oa_edoc_common_option',edocSign.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
									</form:select>
								</fieldset>
			        		</div>
			        		<div style="width:90px; float: left;"><label class="unit-label unit-align unit-font">加载机关内网</label></div>
			        		<div style="width:100px; float: left;">
			        			<fieldset class="set">
									<form:select path="officeNetwork" class="form-control ">
										<form:option value="" label=""/>
										<form:options items="${fns:getDictListEdit('oa_edoc_common_option',edocSign.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
									</form:select>
								</fieldset>
			        		</div>
			        		<div style="width:90px; float: left;"><label class="unit-label unit-align unit-font">加载系统专网</label></div>
			        		<div style="width:100px; float: left;">
			        			<fieldset class="set">
									<form:select path="systemNetwork" class="form-control ">
										<form:option value="" label=""/>
										<form:options items="${fns:getDictListEdit('oa_edoc_common_option',edocSign.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
									</form:select>
								</fieldset>
			        		</div>
			        		<div style="width:60px; float: left;"><label class="unit-label unit-align unit-font">创新事项</label></div>
			        		<div style="width:100px; float: left;">
			        			<fieldset class="set">
									<form:select path="innovationItem" class="form-control ">
										<form:option value="" label=""/>
										<form:options items="${fns:getDictListEdit('oa_edoc_common_option',edocSign.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
									</form:select>
								</fieldset>
			        		</div>
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="4">
			        	<div class="unit4">
			        		<div><label class="unit-label unit-align unit-font">批示页处理情况</label></div>
			        		<div style="width:900px;">
			        			<fieldset class="set">
			        				<form:textarea path="result" htmlEscape="false" rows="4"    class="form-control "
			        					style="border: 0px solid; resize:none;"
			        				/>
								</fieldset>
			        		</div>
			        	</div>
					</td>
				</tr>
				<tr id="commentTextarea" style="display: none;">
					<td class="width-15 active"><label class="pull-right">意见：</label></td>
					<td class="width-35" colspan="3">
						<textarea name="act.comment" class="form-control" rows="3" cols="4"></textarea>
					</td>
				</tr>
				<c:if test="${not empty edocSign.procInsId}">
				<tr>
					<td class="width-15 active"><label class="pull-right">流程日志：</label></td>
					<td class="width-35" colspan="3">
						<a class="btn btn-white btn-sm " href="javascript:$('#histoicFlowList').css('display','block');">查看日志</a>
						<a class="btn btn-white btn-sm " href="javascript:openDialog('查看流程历史','${ctx}/act/task/trace/photo/${edocSign.act.procDefId}/${edocSign.procInsId}','1000px', '600px')">流程图</a>
						<div style="margin-bottom:5px;"></div>
						<act:histoicFlowCustom procInsId="${edocSign.act.procInsId}" />
					</td>
				</tr>
				</c:if>
		 	</tbody>
		</table>
		
		<div style="display: none;">
			<a id="modalBtn" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#myModal"></a>
		</div>
		<!-- 模态框（Modal）BEGIN -->
		<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		    <div class="modal-dialog">
		        <div class="modal-content">
		            <div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		                <h4 class="modal-title" id="myModalLabel">选择用户</h4>
		            </div>
		            <div id="userSelectDiv" class="modal-body">
		            	<select id="userSelect" class="form-control " onchange="$('#tempLoginName').val(this.value)">
		            	</select>
		            </div>
		            <div class="modal-footer">
		                <button type="button" class="btn btn-primary" onclick="realSubmit();">提交</button>
		            </div>
		        </div>
		    </div>
		</div>
		<!-- 模态框（Modal）END -->
	</form:form>
</body>
</html>