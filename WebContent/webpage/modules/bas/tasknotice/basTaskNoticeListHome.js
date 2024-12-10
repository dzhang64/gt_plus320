<%@ page contentType="text/html;charset=UTF-8" %>
<script>
$(document).ready(function() {
	$('#basTaskNoticeTable').bootstrapTable({
		method: 'get',               //请求方法
		dataType: "json",            //类型json
		showRefresh: true,           //显示刷新按钮
		showToggle: true,            //显示切换手机试图按钮
		showColumns: true,           //显示 内容列下拉框
		showExport: false,           //显示导出按钮
		showPaginationSwitch: true,  //显示切换分页按钮
		minimumCountColumns: 2,      //最低显示2行
		striped: true,               //是否显示行间隔色
		cache: false,                //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*） 
		pagination: true,           //是否显示分页（*）  
		sortOrder: "asc",            //排序方式 
		pageNumber:1,                //初始化加载第一页，默认第一页
		pageSize: 5,                //每页的记录行数（*）
		pageList: [5, 10, 25, 50, 100], //可供选择的每页的行数（*）
		url: "${ctx}/bas/tasknotice/basTaskNotice/dataPrivate", //这个接口需要处理bootstrap table传递的固定参数,并返回特定格式的json数据
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
				jp.confirm('确认要删除该任务通知记录吗？', function(){
					jp.loading();
					jp.get("${ctx}/bas/tasknotice/basTaskNotice/delete?id="+row.id, function(data){
						if(data.success){
							$('#basTaskNoticeTable').bootstrapTable('refresh');
							jp.success(data.msg);
						}else{
							jp.error(data.msg);
						}
					});
				});
			}
		},
		onClickRow: function(row, $el){ },
		columns: [
			{
				checkbox: true
			}
			,{
				field: 'createDate',
				title: '通知时间',
				sortable: true
			}
			,{
				field: 'content',
				title: '通知内容',
				sortable: true
				,formatter:function(value, row , index){
					return '关于“' + value + '”的任务通知';
				}
				
			}
			,{
				field: 'createUserName',
				title: '提交人',
				sortable: true
			}
			,{
				field: 'officeName',
				title: '提交人归属机构',
				sortable: true
				
			}
			,{
				field: 'userName',
				title: '接收人',
				sortable: true,
				visible: false
				
			}
			,{
				field: 'status',
				title: '状态',
				sortable: true,
				
				formatter:function(value, row , index){
					if (value == '0') {
						return '<font color="red">' + jp.getDictLabel(${fns:toJson(fns:getDictList('oa_notify_read'))}, value, "-") + '</font>';
					} else {
						return '<font color="green">' + jp.getDictLabel(${fns:toJson(fns:getDictList('oa_notify_read'))}, value, "-") + '</font>';
					}
				}
			}
			,{ //操作列
				field: 'operate',
				title: '操作',
				events: {
					'click .view': function (e, value, row, index) {
						jp.openDialogView('查看任务通知', '${ctx}/bas/tasknotice/basTaskNotice/form?viewFlag=set_audit&id=' + row.id,'800px', '500px');
					},
					'click .edit': function (e, value, row, index) {
						var windowName = $(".edit").attr("dealname")+'任务通知';
						var btns = $(".edit").attr("dealbtn");
						var params = $(".edit").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/bas/tasknotice/basTaskNotice/form?id=' + row.id,'800px', '500px', $('#basTaskNoticeTable'), btns, params);
					},
					'click .edit2': function (e, value, row, index) {
						var windowName = $(".edit2").attr("dealname")+'任务通知';
						var btns = $(".edit2").attr("dealbtn");
						var params = $(".edit2").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/bas/tasknotice/basTaskNotice/form?viewFlag=set&id=' + row.id,'800px', '500px', $('#basTaskNoticeTable'), btns, params);
					},
					'click .edit3': function (e, value, row, index) {
						var windowName = $(".edit3").attr("dealname")+'任务通知';
						var btns = $(".edit3").attr("dealbtn");
						var params = $(".edit3").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/bas/tasknotice/basTaskNotice/form?viewFlag=set&id=' + row.id,'800px', '500px', $('#basTaskNoticeTable'), btns, params);
					},
					'click .edit4': function (e, value, row, index) {
						var windowName = $(".edit4").attr("dealname")+'任务通知';
						var btns = $(".edit4").attr("dealbtn");
						var params = $(".edit4").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/bas/tasknotice/basTaskNotice/form?viewFlag=set&id=' + row.id,'800px', '500px', $('#basTaskNoticeTable'), btns, params);
					},
					'click .edit5': function (e, value, row, index) {
						var windowName = $(".edit5").attr("dealname")+'任务通知';
						var btns = $(".edit5").attr("dealbtn");
						var params = $(".edit5").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/bas/tasknotice/basTaskNotice/form?viewFlag=set&id=' + row.id,'800px', '500px', $('#basTaskNoticeTable'), btns, params);
					},
					'click .del': function (e, value, row, index) {
						del(row.id);
					}
				},
				formatter: function operateFormatter(value, row, index) {
					var editLink = ' <a href="#" class="btn btn-success btn-xs edit" dealname="编辑"><i class="fa fa-edit"></i> 编辑</a>';
					var delLink = ' <a href="#"  class="btn btn-danger btn-xs del"><i class="fa fa-trash"></i> 删除</a>';
					return [
						<shiro:hasPermission name="bas:tasknotice:basTaskNotice:view">
						"<a href='javascript:edit(\"1\",false,\"" + row.id + "\",\"" + row.status + "\");jp.viewTask(\"${ctx}" + row.remarks + "\",\"" + row.content + "\")' class='btn btn-info btn-xs'><i class='fa fa-search-plus'></i> 查看任务</a>",
						</shiro:hasPermission>
						<shiro:hasPermission name="bas:tasknotice:basTaskNotice:edit">
						//editLink,
						</shiro:hasPermission>
						<shiro:hasPermission name="bas:tasknotice:basTaskNotice:del"> 
						//delLink,  
						</shiro:hasPermission>
					].join('');
				}
			}
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
		$('#basTaskNoticeTable').bootstrapTable("toggleView");
	}
	$('#basTaskNoticeTable').on('check.bs.table uncheck.bs.table load-success.bs.table ' +
		'check-all.bs.table uncheck-all.bs.table', function () {
		$('#remove').prop('disabled', ! $('#basTaskNoticeTable').bootstrapTable('getSelections').length);
		$('#edit').prop('disabled', $('#basTaskNoticeTable').bootstrapTable('getSelections').length!=1);
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
				window.location='${ctx}/bas/tasknotice/basTaskNotice/import/template';
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
		$('#basTaskNoticeTable').bootstrapTable('refresh');
	});
	
	//绑定重置按扭
	$("#reset").click("click", function() {
		$("#searchForm  input").val("");
		$("#searchForm  select").val("");
		$("#searchForm  .select-item").html("");
		$('#basTaskNoticeTable').bootstrapTable('refresh');
	});
}); //结束document.ready

//获取选择列id		
function getIdSelections() {
	return $.map($("#basTaskNoticeTable").bootstrapTable('getSelections'), function (row) {
		return row.id
	});
}

//批量删除
function deleteAll(){
	jp.confirm('确认要删除选中任务通知记录吗？', function(){
		//jp.loading();  	
		jp.get("${ctx}/bas/tasknotice/basTaskNotice/deleteAll?ids=" + getIdSelections(), function(data){
			if(data.success){
				$('#basTaskNoticeTable').bootstrapTable('refresh');
				jp.success(data.msg);
			}else{
				jp.error(data.msg);
			}
		});
	});
}

//添加   
function add(){
	jp.openDialog('添加任务通知', "${ctx}/bas/tasknotice/basTaskNotice/form",'800px', '500px', $('#basTaskNoticeTable'));
}

//设置已读
function edit(status,isBat,id,preStatus){
	if (preStatus && preStatus == status) return; //已经为已读状态，则不需要重复设置
	if(id == undefined){
		id = getIdSelections();
	}
	jp.get("${ctx}/bas/tasknotice/basTaskNotice/setRead?ids=" + id + "&status=" + status, function(data){
		if(data.success){
			$('#basTaskNoticeTable').bootstrapTable('refresh');
			if (isBat) jp.success(data.msg);
		}else{
			jp.error(data.msg);
		}
	});
}

//单条删除
function del(id){
	jp.confirm('确认要删除该任务通知吗？',  function(){
		jp.get("${ctx}/bas/tasknotice/basTaskNotice/delete?id=" + id, function(data){
			if(data.success){
				$('#basTaskNoticeTable').bootstrapTable('refresh');
				jp.success(data.msg);
			}else{
				jp.error(data.msg);
			}
		});
	});
}

</script>