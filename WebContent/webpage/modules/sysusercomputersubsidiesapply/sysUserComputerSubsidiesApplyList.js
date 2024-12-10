<%@ page contentType="text/html;charset=UTF-8" %>
	<script>
$(document).ready(function() {
	$('#sysUserComputerSubsidiesApplyTable').bootstrapTable({
		method: 'get',               //请求方法
		dataType: "json",            //类型json
		showRefresh: true,           //显示刷新按钮
		showToggle: true,            //显示切换手机试图按钮
		showColumns: true,           //显示 内容列下拉框
		showExport: true,            //显示到处按钮
		showPaginationSwitch: true,  //显示切换分页按钮
		minimumCountColumns: 2,      //最低显示2行
		striped: true,               //是否显示行间隔色
		cache: false,                //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*） 
		pagination: true,            //是否显示分页（*）  
		sortOrder: "asc",            //排序方式 
		pageNumber:1,                //初始化加载第一页，默认第一页
		pageSize: 10,                //每页的记录行数（*）
		pageList: [10, 25, 50, 100], //可供选择的每页的行数（*）
		url: "${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/data/${path==null?'Todo':path}", //这个接口需要处理bootstrap table传递的固定参数,并返回特定格式的json数据
		//默认值为 'limit',传给服务端的参数为：limit, offset, search, sort, order Else
		//queryParamsType:'',   
		//查询参数,每次调用是会带上这个参数，可自定义
		queryParams : function(params) {
			var searchParam = $("#searchForm").serializeJson();
			searchParam.pageNo = params.limit === undefined? "1" :params.offset/params.limit+1;
			searchParam.pageSize = params.limit === undefined? -1 : params.limit;
			searchParam.orderBy = params.sort === undefined? "" : params.sort+ " "+  params.order;
			return searchParam;
		},
		sidePagination: "server",        //分页方式：client客户端分页，server服务端分页（*）
		contextMenuTrigger:"right",      //pc端 按右键弹出菜单
		contextMenuTriggerMobile:"press",//手机端 弹出菜单，click：单击， press：长按。
		contextMenu: '#context-menu',
		onContextMenuItem: function(row, $el){
			if($el.data("item") == "edit"){
				edit(row.id);
			}else if($el.data("item") == "delete"){
				jp.confirm('确认要删除该电脑补贴申请记录吗？', function(){
					jp.loading();
					jp.get("${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/delete?id="+row.id, function(data){
						if(data.success){
							$('#sysUserComputerSubsidiesApplyTable').bootstrapTable('refresh');
							jp.success(data.msg);
						}else{
							jp.error(data.msg);
						}
					});
				});
			}
		},
		onClickRow: function(row, $el){ },
		onLoadSuccess: function () { },
		columns: [
			{
				checkbox: true
				,visible: false
			}
			,{
				field: 'computerConfiguration',
				title: '电脑配置',
				sortable: true
				
				,formatter:function(value, row , index){
					return "<a href='javascript:edit(\""+row.id+"\",true)'>"+value+"</a>";
				}
			}
			,{
				field: 'computerSerialNumber',
				title: '电脑序列号',
				sortable: true
				
			}
			,{
				field: 'buyDate',
				title: '购买日期',
				sortable: true
				
			}
			,{
				field: 'beginDate',
				title: '起租日期',
				sortable: true
				
			}
			,{
				field: 'endDate',
				title: '终租日期',
				sortable: true
				
			}
			,{
				field: 'subsidyStandard',
				title: '补贴标准',
				sortable: true
				
			}
			,{ //操作列
				field: 'operate',
				title: '操作',
				events: {
					'click .view': function (e, value, row, index) {
						jp.openDialogView('查看电脑补贴申请', '${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/form?viewFlag=set_audit&id=' + row.id, '800px', '500px');
					},
					'click .edit': function (e, value, row, index) {
						var windowName = $(".edit").attr("dealname")+'电脑补贴申请';
						var paramStr = getParamStr(row);
						jp.openDialogFlow(windowName, '${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/form?id=' + row.id, '800px', '500px', $('#sysUserComputerSubsidiesApplyTable'), paramStr);
					},
					'click .edit2': function (e, value, row, index) {
						var windowName = $(".edit2").attr("dealname")+'电脑补贴申请';
						var paramStr = getParamStr(row);
						jp.openDialogFlow(windowName, '${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/form?viewFlag=set&id=' + row.id, '800px', '500px', $('#sysUserComputerSubsidiesApplyTable'), paramStr);
					},
					'click .del': function (e, value, row, index) {
						del(row.id);
					}
				},
				formatter: function operateFormatter(value, row, index) {
					var editLink = ' <a href="#" class="btn btn-success btn-xs edit" dealname="编辑"><i class="fa fa-edit"></i> 编辑</a>';
					var delLink = ' <a href="#"  class="btn btn-danger btn-xs del"><i class="fa fa-trash"></i> 删除</a>';
					if ("${path==null?'Todo':path}"=="Todo"){
						editLink = ' <a href="#" class="btn btn-success btn-xs edit2" dealname="办理"><i class="fa fa-edit"></i> 办理</a>';
					} else if ("${path}"=="Doing") {
						editLink = ' <a href="#" class="btn btn-success btn-xs edit2" dealname="办理"><i class="fa fa-edit"></i> 办理</a>';
					} else if ("${path}"=="Done") {
						editLink = '';
					} else if ("${path}"=="Sent") {
						editLink = '';
					}
					if (("${path}"=="Doing" || "${path==null?'Todo':path}"=="Todo") 
							&& row.procTaskPermission == "modify") {
						editLink = ' <a href="#" class="btn btn-success btn-xs edit" dealname="编辑"><i class="fa fa-edit"></i> 编辑</a>';
					}
					return [
						<shiro:hasPermission name="sysusercomputersubsidiesapply:sysUserComputerSubsidiesApply:view">
						'<a href="#" class="btn btn-info btn-xs view" ><i class="fa fa-search-plus"></i> 查看</a>',
						</shiro:hasPermission>
						<shiro:hasPermission name="sysusercomputersubsidiesapply:sysUserComputerSubsidiesApply:edit">
						editLink,
						</shiro:hasPermission>
						<shiro:hasPermission name="sysusercomputersubsidiesapply:sysUserComputerSubsidiesApply:del"> 
						delLink,  
						</shiro:hasPermission>
					].join('');
				}
			}
			<%--
			,{ //操作列
				field: 'operate',
				title: '操作',
				events: {
					'click .view': function (e, value, row, index) {
						jp.openDialogView('查看电脑补贴申请', '${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/form?viewFlag=set_audit&id=' + row.id, '800px', '500px');
					},
					'click .edit': function (e, value, row, index) {
						var windowName = $(".edit").attr("dealname")+'电脑补贴申请';
						var btns = $(".edit").attr("dealbtn");
						var params = $(".edit").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/form?id=' + row.id, '800px', '500px', $('#sysUserComputerSubsidiesApplyTable'), btns, params);
					},
					'click .edit2': function (e, value, row, index) {
						var windowName = $(".edit2").attr("dealname")+'电脑补贴申请';
						var btns = $(".edit2").attr("dealbtn");
						var params = $(".edit2").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/form?viewFlag=set&id=' + row.id, '800px', '500px', $('#sysUserComputerSubsidiesApplyTable'), btns, params);
					},
					'click .edit3': function (e, value, row, index) {
						var windowName = $(".edit3").attr("dealname")+'电脑补贴申请';
						var btns = $(".edit3").attr("dealbtn");
						var params = $(".edit3").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/form?viewFlag=set&id=' + row.id, '800px', '500px', $('#sysUserComputerSubsidiesApplyTable'), btns, params);
					},
					'click .del': function (e, value, row, index) {
						del(row.id);
					}
				},
				formatter: function operateFormatter(value, row, index) {
					var editLink = ' <a href="#" class="btn btn-success btn-xs edit" dealname="编辑"><i class="fa fa-edit"></i> 编辑</a>';
					var delLink = ' <a href="#"  class="btn btn-danger btn-xs del"><i class="fa fa-trash"></i> 删除</a>';
					
						if ("${path}"=="Todo"){
							editLink = ' <a href="#" class="btn btn-success btn-xs edit2" dealname="办理"><i class="fa fa-edit"></i> 办理</a>';
						} else if ("${path}"=="Doing") {
							editLink = ' <a href="#" class="btn btn-success btn-xs edit2" dealname="办理"><i class="fa fa-edit"></i> 办理</a>';
						} else if ("${path}"=="Done") {
							editLink = '';
						} else if ("${path}"=="Sent") {
							editLink = '';
						}
						if (row.procTaskPermission == "modify") {
							editLink = ' <a href="#" class="btn btn-success btn-xs edit" dealname="编辑"><i class="fa fa-edit"></i> 编辑</a>';
						}
					
					return [
						<shiro:hasPermission name="sysusercomputersubsidiesapply:sysUserComputerSubsidiesApply:view">
						'<a href="#" class="btn btn-info btn-xs view" ><i class="fa fa-search-plus"></i> 查看</a>',
						</shiro:hasPermission>
						<shiro:hasPermission name="sysusercomputersubsidiesapply:sysUserComputerSubsidiesApply:edit">
						editLink,
						</shiro:hasPermission>
						<shiro:hasPermission name="sysusercomputersubsidiesapply:sysUserComputerSubsidiesApply:del"> 
						delLink,  
						</shiro:hasPermission>
					].join('');
				}
			}
			--%>
			,{
				field: 'rules',
				title: '参数列',
				sortable: false,
				visible: false
				,formatter:function(value, row , index){
					return "";
				}
			}
		]
	}); //结束绑定列表数据
	
	//如果是移动端
	if(navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)){ 
		$('#sysUserComputerSubsidiesApplyTable').bootstrapTable("toggleView");
	}
	$('#sysUserComputerSubsidiesApplyTable').on('check.bs.table uncheck.bs.table load-success.bs.table ' +
		'check-all.bs.table uncheck-all.bs.table', function () {
		$('#remove').prop('disabled', ! $('#sysUserComputerSubsidiesApplyTable').bootstrapTable('getSelections').length);
		$('#edit').prop('disabled', $('#sysUserComputerSubsidiesApplyTable').bootstrapTable('getSelections').length!=1);
	});
	
	//导入数据	  
	$("#btnImport").click(function(){
		top.layer.open({
			type: 1, 
			area: [500, 300],
			title:"导入数据",
			content:$("#importBox").html() ,
			btn: ['下载模板','确定', '关闭'],
			btn1: function(index, layero){
				window.location='${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/import/template';
			},
			btn2: function(index, layero){
				var inputForm =top.$("#importForm");
				var top_iframe = top.getActiveTab().attr("name");//获取当前active的tab的iframe 
				inputForm.attr("target",top_iframe);             //表单提交成功后，从服务器返回的url在当前tab中展示
				inputForm.onsubmit = function(){
					jp.loading('  正在导入，请稍等...');
				}
				inputForm.submit();
				jp.close(index);
			},
			btn3: function(index){ 
				jp.close(index);
			}
		}); 
	});
	
	//绑定查询按扭 
	$("#search").click("click", function() { 
		$('#sysUserComputerSubsidiesApplyTable').bootstrapTable('refresh');
	});
	
	//绑定重置按扭
	$("#reset").click("click", function() {
		$("#searchForm  input[name!='ownerCode']").val("");
		$("#searchForm  select").val("");
		$("#searchForm  .select-item").html("");
		$('#sysUserComputerSubsidiesApplyTable').bootstrapTable('refresh');
	});
}); //结束document.ready

//获取选择列id		
function getIdSelections() {
	return $.map($("#sysUserComputerSubsidiesApplyTable").bootstrapTable('getSelections'), function (row) {
		return row.id
	});
}

//批量删除
function deleteAll(){
	jp.confirm('确认要删除选中电脑补贴申请记录吗？', function(){
		//jp.loading();  	
		jp.get("${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/deleteAll?ids=" + getIdSelections(), function(data){
			if(data.success){
				$('#sysUserComputerSubsidiesApplyTable').bootstrapTable('refresh');
				jp.success(data.msg);
			}else{
				jp.error(data.msg);
			}
		});
	});
}

//添加   
function add(){
	var btns = '暂存,提交,取消';
	var params = 'flag:暂存,提交';
	jp.openDialog('添加电脑补贴申请', "${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/form",'800px', '500px', $('#sysUserComputerSubsidiesApplyTable'), btns, params);
	<%--
	jp.openDialog('添加电脑补贴申请', "${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/form",'800px', '500px', $('#sysUserComputerSubsidiesApplyTable'));
	--%>
}

//编辑，没有权限时，不显示保存按钮
function edit(id,isView){
	if(id == undefined){
		id = getIdSelections();
	}
	if (isView) {
		jp.openDialogView('查看电脑补贴申请', "${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/form?viewFlag=set_audit&id=" + id, '800px', '500px');
	} else {
	<shiro:hasPermission name="sysusercomputersubsidiesapply:sysUserComputerSubsidiesApply:edit">
		jp.openDialog('编辑电脑补贴申请', "${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/form?id=" + id, '800px', '500px', $('#sysUserComputerSubsidiesApplyTable'));
	</shiro:hasPermission>
	<shiro:lacksPermission name="sysusercomputersubsidiesapply:sysUserComputerSubsidiesApply:edit">
		jp.openDialogView('查看电脑补贴申请', "${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/form?viewFlag=set_audit&id=" + id, '800px', '500px');
	</shiro:lacksPermission>
	}
}

//单条删除
function del(id){
	jp.confirm('确认要删除该电脑补贴申请吗？',  function(){
		jp.get("${ctx}/sysusercomputersubsidiesapply/sysUserComputerSubsidiesApply/delete?id=" + id, function(data){
			if(data.success){
				$('#sysUserComputerSubsidiesApplyTable').bootstrapTable('refresh');
				jp.success(data.msg);
			}else{
				jp.error(data.msg);
			}
		});
	});
}

function getParamStr(row){
	var paramStr = '';
	if (row.procTaskPermission == null || row.procTaskPermission.operation == null
			|| row.procInsId == null) {
		paramStr = '{"button":"暂存","type":"save","flag":"save"};' 
			   	 + '{"button":"提交","type":"saveAndStart","flag":"saveAndStart"};'
			   	 + '{"button":"取消","type":"cancel","flag":"cancel"};';
	} else {
		var btns = row.procTaskPermission.operation.split(',');
		for (var i = 0; i < btns.length; i++) {
			paramStr += '{"button":"' + btns[i].split('_')[0] 
			+ '","type":"' + btns[i].split('_')[1] 
			+ '","flag":"' + btns[i].split('_')[1] 
			+ '"};';
		}
	}
	return paramStr;
}
</script>