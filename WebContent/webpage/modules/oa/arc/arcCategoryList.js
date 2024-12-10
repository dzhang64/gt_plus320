<%@ page contentType="text/html;charset=UTF-8" %>
<script>
	var $arcCategoryTreeTable=null;  
	$(document).ready(function() {
		$arcCategoryTreeTable=$('#arcCategoryTreeTable').treeTable({  
			theme:'vsStyle',	           
			expandLevel : 5,
			column:0,
			checkbox: false,
			url:'${ctx}/oa/arc/arcCategory/getChildren?parentId=',  
			callback:function(item) { 
				var treeTableTpl= $("#arcCategoryTreeTableTpl").html();
				var result = laytpl(treeTableTpl).render({
					row: item
				});
				return result;                   
			},  
			beforeClick: function($arcCategoryTreeTable, id) { 
				//异步获取数据 这里模拟替换处理  
				$arcCategoryTreeTable.refreshPoint(id);  
			},  
			beforeExpand : function($arcCategoryTreeTable, id) {   
			},  
			afterExpand : function($arcCategoryTreeTable, id) {  
			},  
			beforeClose : function($arcCategoryTreeTable, id) {    
		            	
			}  
		});
		$arcCategoryTreeTable.initParents('${parentIds}', "0");//在保存编辑时定位展开当前节点
	});
	
	//添加   
	function add(){
		jp.openDialog('添加档案目录', "${ctx}/oa/arc/arcCategory/form",'800px', '500px', $arcCategoryTreeTable);
	}
		
	function del(con,id){  
		jp.confirm('确认要删除档案目录吗？', function(){
			jp.loading();
			$.get("${ctx}/oa/arc/arcCategory/delete?id="+id, function(data){
				if(data.success){
					$arcCategoryTreeTable.del(id);
					jp.success(data.msg);
				}else{
					jp.error(data.msg);
				}
			});
		});
	} 
		
	function refresh(){//刷新
		var index = jp.loading("正在加载，请稍等...");
		$arcCategoryTreeTable.refresh();
		jp.close(index);
	}
</script>
<script type="text/html" id="arcCategoryTreeTableTpl">
			<td><a  href="#" onclick="jp.openDialogView('查看档案目录', '${ctx}/oa/arc/arcCategory/form?viewFlag=set_audit&id={{d.row.id}}','800px', '500px')">
				{{d.row.name}}
			</a></td>
			<td>
				{{d.row.sort}}
			</td>
			<td>
				<div class="btn-group">
			 		<button type="button" class="btn  btn-primary btn-xs dropdown-toggle" data-toggle="dropdown">
						<i class="fa fa-cog"></i>
						<span class="fa fa-chevron-down"></span>
					</button>
				  <ul class="dropdown-menu" role="menu">
					<shiro:hasPermission name="oa:arc:arcCategory:view">
						<li><a href="#" onclick="jp.openDialogView('查看档案目录', '${ctx}/oa/arc/arcCategory/form?viewFlag=set_audit&id={{d.row.id}}','800px', '500px')"><i class="fa fa-search-plus"></i> 查看</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="oa:arc:arcCategory:edit">
						<li><a href="#" onclick="jp.openDialog('编辑档案目录', '${ctx}/oa/arc/arcCategory/form?id={{d.row.id}}','800px', '500px', $arcCategoryTreeTable)"><i class="fa fa-edit"></i> 编辑</a></li>
		   			</shiro:hasPermission>
		   			<shiro:hasPermission name="oa:arc:arcCategory:del">
		   				<li><a  onclick="return del(this, '{{d.row.id}}')"><i class="fa fa-trash"></i> 删除</a></li>
					</shiro:hasPermission>
		   			<shiro:hasPermission name="oa:arc:arcCategory:add">
						<li><a href="#" onclick="jp.openDialog('添加下级档案目录', '${ctx}/oa/arc/arcCategory/form?parent.id={{d.row.id}}','800px', '500px', $arcCategoryTreeTable)"><i class="fa fa-plus"></i> 添加下级档案目录</a></li>
					</shiro:hasPermission>
				  </ul>
				</div>
			</td>
	</script>