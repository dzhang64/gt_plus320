<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>会议安排管理</title>
	<meta name="decorator" content="default"/>
	<link href="${ctxStatic}/plugin/summernote/summernote.css" rel="stylesheet">
	<link href="${ctxStatic}/plugin/summernote/summernote-bs3.css" rel="stylesheet">
	<script src="${ctxStatic}/plugin/summernote/summernote.min.js"></script>
	<script src="${ctxStatic}/plugin/summernote/summernote-zh-CN.js"></script>
	<style type="text/css">
		.unit {float:left;}
		.unit1 {width:238px;float:left;}
		.unit2 {width:476px;float:left;}
		.unit3 {width:714px;float:left;}
		.unit4 {width:100%;float:left;}
		.unit-label {margin-top:8px;}
		.unit-body {width:990px;margin:auto;}
	</style>
	<script type="text/javascript">
		var validateForm;
		var $table;   // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		function doSubmit(table, index, pName, pValue){
			if(validateForm.form()){
				$("#docFile").val($("#rich_docFile").next().find(".note-editable").html());//取富文本的值
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
			var meetingId = $("#meetingId").val();
			if(meetingId != null && meetingId != ''){
				participation();	
			} 
			//alert($("#participationName").val());
			if(validateForm.form()){
				jp.loading();
				$("#inputForm").submit();
				return true;
			} 
			return false;
		}
		//提交当前代办人是否参与会议
		function participation(){
			var participation = $("input[name='participation'][type='radio']:checked").val();  //单选内容
			var participationName = $("#participationName").val();		 //历史回执信息
			if(participationName != null && participationName != ''){
				participationName = participationName + '&' +  $("#userName").val() + ':' + participation;
			}else{
				participationName = $("#userName").val() + ':' + participation;
			}
			$("#participationName").val(participationName);
		}
		//解析与会人员
		function toMeetingUser(){
			var participationName = $("#participationName").val();
			//participationName = '王雪健:canj&金晓东:bucj&梁星:daid&刘远:canj;
			if(participationName != null && participationName != ''){
				var canj = '';
				var bucj = '';
				var daid = '';
				var partics = participationName.split('&');
				if(partics.length > 0){
					for(var i = 0;i < partics.length;i++){
						var jsonMap =  partics[i].split(':');
						var pName = jsonMap[0] + ' ';
						var pType = jsonMap[1];
						if(pType == 'canj') canj += pName;
						else if(pType == 'bucj') bucj += pName;
						else if(pType == 'daid') daid += pName;
					}
					$("#canj").append(canj);
					$("#bucj").append(bucj);
					$("#daid").append(daid);
				}
			}	
		}
		
		$(document).ready(function() { 
			toMeetingUser();
			if($("#viewFlag").val() == 'set_audit'){
				$("#trHide").hide();
			}
			validateForm = $("#inputForm").validate({
				submitHandler: function(form){
					jp.post("${ctx}/oa/meeting/meeting/save",$('#inputForm').serialize(),function(data){
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
				elem: '#meetingStartDate',
				event: 'focus',format: 'YYYY-MM-DD hh:mm:ss', istime : true
			});
			laydate({
				elem: '#meetingEndDate',
				event: 'focus',format: 'YYYY-MM-DD hh:mm:ss', istime : true
			});
				//富文本初始化
			$('#rich_docFile').summernote({
                lang: 'zh-CN',
                height: 500
            });
			$("#rich_docFile").next().find(".note-editable").html(  $("#docFile").val());
			$("#rich_docFile").next().find(".note-editable").html(  $("#rich_docFile").next().find(".note-editable").text());
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
			if (jp.checkTaskPermission("${meeting.procTaskPermission}")) {
				$('#commentTextarea').attr('style','');
			} else {
				$('#commentTextarea').attr('style','display: none;');
			}
		} */
		
		function ruleArgsInit(){
			if ($('#id').val() != null && $('#id').val().trim() != ''){
				var ruleArgs = eval('('+ '${meeting.ruleArgsJson}' +')');
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
					url:'${ctx}/oa/meeting/meeting/getUserList',
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
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="meeting" class="form-horizontal">
		<form:hidden path="id"/>
		
		<input type="hidden" id="act.taskId" name="act.taskId" value="${meeting.act.taskId }"/>
		<input type="hidden" id="act.taskName" name="act.taskName" value="${meeting.act.taskName }"/>
		<input type="hidden" id="act.taskDefKey" name="act.taskDefKey" value="${meeting.act.taskDefKey }"/>
		<input type="hidden" id="procInsId" name="act.procInsId" value="${meeting.act.procInsId }"/>
		<input type="hidden" id="act.procDefId" name="act.procDefId" value="${meeting.act.procDefId }"/>
		<input type="hidden" id="flag" name="act.flag"/>
		<input type="hidden" id="tempLoginName" name="tempLoginName"/>
		<input type="hidden" id="participationName" name="participationName" value="${meeting.participationName }"/>
		<input type="hidden" id="userName" value="${userName }"/>
		<input type="hidden" id="meetingId"  value="${meeting.id }"/>
		<div id="tempLoginNameDiv"></div>
		
		<input type="hidden" name="dirtyUpdateDate" value="${meeting.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>会议名称：</label></td>
			
			        <td class="width-35" colspan="3">
						<fieldset class="set">
						<form:input path="meetingName" htmlEscape="false"    class="form-control required"/>
						</fieldset>
					</td>
					
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>开始时间：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="meetingStartDate" name="meetingStartDate" type="text" maxlength="20" class="laydate-icon form-control layer-date required"
							value="<fmt:formatDate value="${meeting.meetingStartDate}" pattern="yyyy-MM-dd HH:mm:ss" />"/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>结束时间：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="meetingEndDate" name="meetingEndDate" type="text" maxlength="20" class="laydate-icon form-control layer-date required"
							value="<fmt:formatDate value="${meeting.meetingEndDate}" pattern="yyyy-MM-dd HH:mm:ss" />"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">会议地点：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="meetingSite" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">主持人：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="compere" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				
				<c:if test="${not empty meeting.id}">
					<tr>
						<td  class="width-15 active"><label class="pull-right">与会人员</label></td>
						<td class="width-15" colspan="4">
							<div id="canj" class="unit1">参加：</div>
							<div id="bucj" class="unit1">不参加：</div>
							<div id="daid" class="unit1">待定：</div>
						</td>
					</tr>
					<tr id='trHide'>
						<td class="width-15 active"><label class="pull-right">回执：</label></td>
						<td class="width-35" colspan="3">
							<input type="radio" name="participation" value="canj" class="i-checks " checked="checked"/>参加&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<input type="radio"  name="participation" value="bucj" class="i-checks "/>不参加&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<input type="radio"  name="participation" value="daid" class="i-checks "/>待定
						</td>
					</tr>
				</c:if>
				<tr>
					<td class="width-15 active"><label class="pull-right">文件：</label></td>
			
					<td class="width-35" colspan="3">
						
						<input type="hidden" id="file" name="file" value='${meeting.file}' />
						<sys:gtfinder input="file" uploadPath="/oa/meeting/meeting"></sys:gtfinder>
					</td>
				</tr>
				<tr>
					<td class="width-35" colspan="4">
						<fieldset class="set">
						<form:hidden path="docFile"/>
						<div id="rich_docFile">
                           
							
                        </div>
						</fieldset>
					</td>
				</tr>
				<tr id="commentTextarea" style="display: none;">
					<td class="width-15 active"><label class="pull-right">意见：</label></td>
					<td class="width-35" colspan="3">
						<textarea name="act.comment" class="form-control" rows="3" cols="4"></textarea>
					</td>
				</tr>
				<c:if test="${not empty meeting.procInsId}">
				<tr>
					<td class="width-15 active"><label class="pull-right">流程日志：</label></td>
					<td class="width-35" colspan="3">
						<a class="btn btn-white btn-sm " href="javascript:$('#histoicFlowList').css('display','block');">查看日志</a>
						<a class="btn btn-white btn-sm " href="javascript:openDialog('查看流程历史','${ctx}/act/task/trace/photo/${meeting.act.procDefId}/${meeting.procInsId}','1000px', '600px')">流程图</a>
						<div style="margin-bottom:5px;"></div>
						<act:histoicFlowCustom procInsId="${meeting.act.procInsId}" />
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