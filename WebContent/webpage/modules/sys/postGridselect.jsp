<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
				var che = $(".iCheck-helper").attr("onclick","checkedId(this)");
				var checks = $("#contentTable tbody tr td input.i-checks:checkbox").parent().parent().parent().find(".id");
				var ids = $("#checkId").val();
				for(var i = 0;i < checks.length;i++){
					if(ids.indexOf(checks[i].value) != -1){
						$("#"+checks[i].value).parent().attr("class","icheckbox_square-green checked");
						$("#"+checks[i].value).attr("checked","checked");
					}
				}

			    $('#contentTable thead tr th input.i-checks').on('ifChecked', function(event){ //ifCreated 事件应该在插件初始化之前绑定 
			    	  $('#contentTable tbody tr td input.i-checks').iCheck('check');
			    	});

			    $('#contentTable thead tr th input.i-checks').on('ifUnchecked', function(event){ //ifCreated 事件应该在插件初始化之前绑定 
			    	  $('#contentTable tbody tr td input.i-checks').iCheck('uncheck');
			    	});
			    
			    
		});
		
		function checkedId(obj){
			var id = $(obj).parent().parent().parent().find(".id").val();
			var name = $(obj).parent().parent().parent().find(".names").val();
			var ids = $("#checkId").val();
			var names = $("#checkName").val();
			//id
			if(ids != null && ids != ""){
				var newIds = "";
				if(ids.indexOf(id) != -1){
					newIds = ids.replace(id,"");
				}else{
					newIds = ids + "," + id;
				}
				//重新排序
				var splIds = newIds.split(",");
				var idsAdd = "";
				for(var i = 0;i < splIds.length;i++){
					
					if(splIds[i] != null && splIds[i] != ""){
						if(idsAdd == "") idsAdd = splIds[i];
						else idsAdd = idsAdd + "," + splIds[i];
						
					}
				}
				$("#checkId").val(idsAdd);
			}else{
				$("#checkId").val(id);
			}
			//name
			if(names != null && names != ""){
				var newNames = "";
				if(names.indexOf(name) != -1){
					newNames = names.replace(name,"");
				}else{
					newNames = names + "," + name;
				}
				//重新排序
				var splNames = newNames.split(",");
				var namesAdd = "";
				for(var i = 0;i < splNames.length;i++){
					if(splNames[i] != null && splNames[i] != ""){
						if(i == 0) namesAdd = splNames[i];
						else namesAdd = namesAdd + "," + splNames[i];
						
					}
				}
				$("#checkName").val(namesAdd);
			}else{
				$("#checkName").val(name);
			}
		}

		function getSelectedItem(){
				var id =   $("#checkId").val();//getReturnStr($("#contentTable tbody tr td input.i-checks:checkbox:checked").parent().parent().parent().find(".id"));
			    var label =  $("#checkName").val();//getReturnStr($("#contentTable tbody tr td input.i-checks:checkbox:checked").parent().parent().parent().find(".names"));
				return id+"_item_"+label;
		}
		function getReturnStr(obj){
			var result = "";
			for (var i = 0; i < obj.length; i++) {
				result += obj[i].value;
				if (i != obj.length -1) {
					result += ",";
				}
			}
			return result;
		}
		function page(n,s){
			if(n) $("#pageNo").val(n);
			if(s) $("#pageSize").val(s);
			$("#searchForm").attr("action","${url}");
			$("#searchForm").submit();
	    	return false;
	    }
		function monitorBody(item){
			
		}
	</script>
</head>
<body class="gray-bg">
	<div class="">
    <div class="ibox-content">
		<!-- 查询条件 -->
	<div class="row">
	<div class="col-sm-12">
	<form:form id="searchForm" modelAttribute="obj" action="${url}" method="post" class="form-inline">
		<input type="hidden" name="url" value="${url}"/>
		<input type="hidden" name="fieldLabels" value="${fieldLabels}"/>
		<input type="hidden" name="fieldKeys" value="${fieldKeys}"/>
		<input type="hidden" name="searchLabel" value="${searchLabel}"/>
		<input type="hidden" name="searchKey" value="${searchKey}"/>
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="checkId" name="postParamId" type="hidden" value="${postParamId}"/>
		<input id="checkName" name="checkName" type="hidden" value="${checkName}"/>
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
		<div class="form-group">
			<span>${searchLabel }</span>
				<form:input path="${searchKey}" htmlEscape="false" maxlength="50" class=" form-control input-sm"/>
		
		 </div>	
	</form:form>
	<br/>
	</divbtn-rounded btn-border栏 -->
	<div class="row">
	<div class="col-sm-12">
		<div class="pull-right">
			<button  class="btn btbtn-rounded btn-bordertn-outline btn-sm " onclick="search()" ><i class="fa fa-search"></i> 查询</button>
			<button  class="btn btn-primary btn-rounded btn-outline btn-sm " onclick="reset()" ><i class="fa fa-refresh"></i> 重置</button>
		</div>
	</div>
	</div>
	
	<table id="contentTable" class="table table-striped table-bordered table-hover table-condensed dataTables-example dataTable">
		<thead>
			<tr>
				<th><input type="checkbox" class="i-checks"></th>
				<c:forEach items="${labelNames}" var="name"  varStatus="status">
					<th>${name}</th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="obj">
			<tr>
				<td> <input type="checkbox" id="${fns:getValue(obj, 'id')}" class="i-checks" value="${fns:getValue(obj, 'id')}"></td>
				<td>${fns:getValue(obj, 'name')}</td> 
				<input type="hidden" class="names" value="${fns:getValue(obj, 'name')}"/>
				<input type="hidden" class="id" value="${fns:getValue(obj, 'id')}"/>
				
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<table:page page="${page}"></table:page>
	<br/>
	<br/>
	</div>
</div>
</body>
</html>