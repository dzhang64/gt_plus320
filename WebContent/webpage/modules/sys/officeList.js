<%@ page contentType="text/html;charset=UTF-8" %>
<script>
$(document).ready(function() {
});

//添加   
function add(){
	jp.openDialog('添加组织机构', '${ctx}/sys/office/form?parent.id=' + $("#id").val(),'800px', '620px', officeTable);
}

//编辑，没有权限时，不显示保存按钮
function edit(id,isView){
	if(id == undefined){
		return;
	}
	if (isView) {
		jp.openDialogView('查看组织机构', "${ctx}/sys/office/form?viewFlag=set&id=" + id,'800px', '620px');
	} else {
	<shiro:hasPermission name="sys:office:edit">
		jp.openDialog('编辑组织机构', "${ctx}/sys/office/form?id=" + id,'800px', '620px', officeTable);
	</shiro:hasPermission>
	<shiro:lacksPermission name="sys:office:view">
		jp.openDialogView('查看组织机构', "${ctx}/sys/office/form?viewFlag=set&id=" + id,'800px', '620px');
	</shiro:lacksPermission>
	}
}

//单条删除
function del(id){
	jp.confirm('确认要删除该组织机构及所有子组织机构吗？',  function(){
		jp.get("${ctx}/sys/office/delete?id=" + id, function(data){
			if(data.success){
				window.parent.refreshTree();
				bindTable();
				bindTable();
				//officeTable.refresh();
				jp.success(data.msg);
			}else{
				jp.error(data.msg);
			}
		});
	});
}
</script>