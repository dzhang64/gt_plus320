<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>

<html>
<head>
	<title>项目预算行管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/webpage/include/treeview.jsp" %>
	<script type="text/javascript">
		var validateForm;
		var $table;   // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		function doSubmit(table,index,pName,pValue){
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
					jp.post("${ctx}/projectbg/bgcolumn/projectBgColumn/save",$('#inputForm').serialize(),function(data){
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

		//gridselect选择后执行
/* 		function afterGridSelect() {
		$("#projectname").val(item[2]);
		$("#projectarea").val(item[3]);
		} */
		
		function getGridSelectItem(objstr) {
		 $("#projectname").val(objstr[2]); 
		 $("#projectarea").val(objstr[3]); 
		}
		
		
	   function resetColumnNo() {
            $("#contentTable1 tbody tr").each(function (b, c) {
            
                 $(this).find("button").val(b);
                $(this).find("span[name*=columnList],select[name*=columnList],input[name*=columnList]").each(function () {
                    var a = $(this).attr("name"), c = a.split(".")[1], c = "columnList[" + b + "]." + c;
                    $(this).attr("name", c);
                    0 <= a.indexOf(".sort") && ($(this).val(b), $(this).next().text(b));
                });       
                
                 $(this).find("input[id*=bgSort],input[id*=bgType]").each(function () {
                    var a = $(this).attr("id"), c = a.split("[")[0], c = c+ "[" + b + "]";
                    $(this).attr("id", c);
                });
                
                 $(this).find("input[name*='projectInfo']").each(function () {
                     c = "columnList["+b+"].projectInfo.id";
                    $(this).attr("name", c);
                });
            });
        };
        
       function addColumn() {
            var b = $("#template1").clone();
            b.find("input:text,input:hidden").each(function (){$(this).val("");});
            b.removeAttr("style");
            b.removeAttr("id");
            $("#contentTable1 tbody").append(b);
            b.find("input:checkbox").iCheck({
                checkboxClass: "icheckbox_square-green",
                radioClass: "iradio_square-blue",
                increaseArea: "20%"
            });
            resetColumnNo();
            return false;
        };

       function delColumn() {
            $("input[name='ck']:checked").closest("tr").each(function () {
                var b = $(this).find("input[name*=name]").attr("name");
                $(this).remove();
            });
            resetColumnNo();
            return false;
        };
        
      
	</script>
</head>
<body class="hideScroll">
        <!-- 插入行模板 -->
  
    <table style="display:none">
      <tbody>
        <tr id="template1" style="display:none">
            <td>
                <input type="hidden" name="columnList[0].sort" value="0" maxlength="200"
                       class="form-control required   digits">
                <label>0</label>
                <input type="hidden" name="projectInfo.id" value="0" maxlength="200" class="form-control">
              
            </td>
            <td>
                <input type="checkbox" class="form-control  " name="ck" value="1">
            </td>
            <td>
          	<div class="input-group">
          	    <input type="hidden" class="form-control required" name="columnList[0].bgSort" value="" readonly="readonly">
                <input type="text" class="form-control required" id="bgSort[0]" value="" readonly="readonly">
                 <span class="input-group-btn">
	       		 <button type="button" value ="1" onclick="showModel(this.value)" class="btn   btn-primary  "><i class="fa fa-search"></i></button> 
       		 </span>
       		  </div>
            </td>
            <td>
                <input type="hidden" class="form-control required" name="columnList[0].bgType" value="" maxlength="200" readonly ="true">
                 <input type="text" class="form-control required" id="bgType[0]" value="" maxlength="200" readonly ="true">
            </td>
            <td>
				 <input type="text" class="form-control required" name="columnList[0].bgNum" value="" maxlength="200">
			</td>
            <td>
               <input type="text" class="form-control required" name="columnList[0].bgCount" value="" maxlength="200">
            </td>
              <td>
               <input type="text" class="form-control required" name="columnList[0].bgColumnPrice" value="" maxlength="200">
            </td>
              <td>
               <input type="text" class="form-control " name="columnList[0].bgColumnMonry" value="" maxlength="200" readonly ="true">
            </td>
            
        </tr>
      </tbody>
    </table>
    
    <table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td width= "10%" bgcolor="#f5f5f5"><label class="pull-right">项目信息：</label></td>
			
			        <td width= "23%" bgcolor="#f5f5f5">
						<fieldset class="set">
						<sys:gridselectcustomU url="${ctx}/projectinfo/projectInfo/selectprojectInfo" id="projectInfo" name="projectInfo.id"  value=""  title="选择项目信息" labelName="projectInfo.projectNum" 
						 labelValue="" cssClass="form-control required" fieldLabels="项目名称|项目编号|执行部门" fieldKeys="projectName|projectNum|office" fieldTypes="0|0|0" filter="" searchLabel="项目编号" searchKey="projectNum"></sys:gridselectcustomU>
						</fieldset>
					</td>
					<td width= "10%" bgcolor="#f5f5f5"><label class="pull-right">项目编号：</label></td>
			
			        <td width= "20%" bgcolor="#f5f5f5">
						<fieldset class="set">
						<input id="projectname" htmlEscape="false"    class="form-control "  value=""  readonly ="true" aria-required="true"/>
						</fieldset>
					</td>
					
					<td width= "10%" bgcolor="#f5f5f5"><label class="pull-right">执行部门：</label></td>
			
			        <td width= "20%" bgcolor="#f5f5f5">
						<fieldset class="set"> 
						<input id="projectarea" htmlEscape="false"    class="form-control "  value=""  readonly ="true" aria-required="true"/>
						</fieldset>
					</td>
				</tr>
		 	</tbody>
		</table>
		
		<button class="btn btn-white btn-sm" onclick="return addColumn()"><i class="fa fa-plus"> 增加</i></button>
        <button class="btn btn-white btn-sm" onclick="return delColumn()"><i class="fa fa-minus"> 删除</i></button>
        <input type="hidden" id="userKey" value="" />
        
		<form:form id="inputForm" modelAttribute="projectBg" class="form-horizontal">
		<sys:message content="${message}"/>	
		
		
			<table id="contentTable1"  class="table table-striped table-bordered table-hover  dataTables-example dataTable">
		        <thead>
		        <tr style="cursor: move;">
		            <th width= "5%" bgcolor="#f5f5f5">序号</th>
		            <th width= "5%" bgcolor="#f5f5f5">操作</th>
		            <th width= "25%" bgcolor="#f5f5f5">预算分类</th>
		            <th width= "20%" bgcolor="#f5f5f5">预算类型</th>
		            <th width= "10%" bgcolor="#f5f5f5">数量</th>
		            <th width= "10%" bgcolor="#f5f5f5">月/天/周期</th>
		            <th width= "12%" bgcolor="#f5f5f5">单价</th>
		            <th width= "13%" bgcolor="#f5f5f5">总金额</th>
		            <!-- <th title="字段是否可为空值，不可为空字段自动进行空值验证">可空</th> -->
		            <!--<th title="选中后该字段被加入到insert语句里">插入</th>
		            <th title="选中后该字段被加入到update语句里">编辑</th>  -->
		        </tr>
		        </thead>
		        <tbody>
		        <c:choose>
		            <c:when test="${not empty columnList}">
		                <c:forEach items="${columnList}" var="column" varStatus="vs">
		                    <tr ${column.delFlag eq '1'?' class="error" title="已删除的列，保存之后消失！"':''}
		                            style="cursor: move;">
		                        <td>
		                            <input type="hidden" name="columnList[${vs.index}].sort"
		                                   value="${column.sort}" maxlength="200"
		                                   class="form-control required   digits" aria-required="true">
		                            <label>${vs.index}</label>
		                            <input type="hidden" name="columnList[${vs.index}].isInsert"
		                                   value="${column.isInsert}">
		                            <input type="hidden" name="columnList[${vs.index}].isEdit"
		                                   value="${column.isEdit}">
		                        </td>
		                        <td>
		                            <div class="icheckbox_square-green" style="position: relative;">
		                                <input type="checkbox" class="i-checks " name="ck" value="1"
		                                       style="position: absolute; opacity: 0;">
		                            </div>
		                        </td>
		                        <td>
		                        	<input type="hidden" name="columnList[${vs.index}].id" value="${column.id}">
		                        	<input type="hidden" name="columnList[${vs.index}].delFlag" value="${column.delFlag}">
		                        	<input type="hidden" name="columnList[${vs.index}].genTable" value="${column.genTable.id}">
		                            <input type="text" class="form-control"
		                                   name="columnList[${vs.index}].name" value="${column.name}">
		                        </td>
		                        <td>
		                            <input type="text" class="form-control"
		                                   name="columnList[${vs.index}].comments"
		                                   value="${column.comments}"
		                                   maxlength="200">
		                        </td>
		                        <td>
		                            <span name="columnList[${vs.index}].jdbcType" class="required"
		                                  value="${column.jdbcType}"></span>
		                        </td>
		                        <td>
		                            <div class="icheckbox_square-green" style="position: relative;">
		                                <input type="checkbox" class="i-checks"
		                                       name="columnList[${vs.index}].isPk" value="${column.isPk}"
		                                       <c:if test="${column.isPk == 1}">checked=""</c:if>
		                                       style="position: absolute; opacity: 0;">
		                            </div>
		                        </td>
		                    </tr>
		                </c:forEach>
		            </c:when>
		            <c:otherwise>
		        
		            </c:otherwise>
		        </c:choose>
		        </tbody>
		    </table>
				
			</form:form>
	<input type="hidden" id="treeDate" value="${projectBgDate}" />
    <input type="hidden" id="selectId" value="" />
    
	<!-- 预算类型选择 -->
	<div class="modal fade" id="treeModal" tabindex="-1" role="dialog" aria-labelledby="treeModalLabel" aria-hidden="true">
     <div class="modal-dialog">
        <div class="modal-content">
           <!--  <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">预算类型选择</h4>
            </div> -->
            <div class="modal-body">
               <ul id="tree" class="ztree" style="padding:15px 20px;"  value="zdy">zdy</ul>
            </div>
          <!--   <div class="modal-footer">
                <button type="button" class="btn btn-primary">确认</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div> -->
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
   </div>
<!-- /.modal -->
<script type="text/javascript">

    var tree,zNodes;
			
	$.get("${ctx}/projectbg/bgcolumn/projectBgColumn/getBgTreeDate", function(zNode){zNodes=zNode;});
	
	/* 显示模组，条用treemk构造树 */
	function showModel(rowid){
	          $('#treeModal').modal({
	                keyboard: true
	             });     
	      treemk(rowid);
        };
	
	/* 树的节点展开	 */	
	function expandNodes(nodes) 
		{
			if (!nodes) return;
			for (var i=0, l=nodes.length; i<l; i++) {
				tree.expandNode(nodes[i], true, false, false);
				if (nodes[i].isParent && nodes[i].zAsync) {
					expandNodes(nodes[i].children);
				}
			}
		}
		
		

	/* 构造树	 */
    function treemk(rowid)
		{
		
		     var setting = 
	         	{
				        view:{selectedMulti:false,dblClickExpand:false},
				        check:{enable:true,nocheckInherit:true}, 
						data:{simpleData:{enable:true}},
				        callback:
				        {
							onClick:function(event, treeId, treeNode){
								tree.expandNode(treeNode);
							},
							onCheck: function(e, treeId, treeNode){
								var nodes = tree.getCheckedNodes(true);
								for (var i=0, l=nodes.length; i<l; i++) {
									tree.expandNode(nodes[i], true, false, false);
								}
								return false;
							},
							onAsyncSuccess: function(event, treeId, treeNode, msg){
								var nodes = tree.getNodesByParam("pId", treeNode.id, null);
								for (var i=0, l=nodes.length; i<l; i++) {
									try{tree.checkNode(nodes[i], treeNode.checked, true);}catch(e){}
								}
								selectCheckNode();
							},
							
							/* 后期这里需要修改来传递参数 */
							onDblClick: function(event, treeId, treeNode) {
                                setform(treeNode,rowid);
                            }
						}
			      };
		   
     		tree = $.fn.zTree.init($("#tree"), setting, zNodes);
				// 默认展开一级节点
			 	var nodes = tree.getNodesByParam("level", 0);
				for(var i=0; i<nodes.length; i++) {
					tree.expandNode(nodes[i], true, false, false);
				} 
		 }
		 
		 /* 树的双击调用的修改form的函数 */
		 function setform(treeNode,rowid){
		 
		   if(treeNode.isParent==false){ 
		   
		     var fatherNode =  treeNode.getParentNode();
		     
		     $("input[name='columnList["+rowid+"].projectInfo.id']").val($("input[id='projectInfoId']").val());
	
		     $("input[name='columnList["+rowid+"].sort']").val(rowid);
		     
		     $("input[name='columnList["+rowid+"].bgSort']").val(fatherNode.id);
		     
		     $("input[id='bgSort["+rowid+"]").val(fatherNode.name);
             
             $("input[name='columnList["+rowid+"].bgType']").val(treeNode.id);
             
             $("input[id='bgType["+rowid+"]").val(treeNode.name);
             
             $('#treeModal').modal('hide');
             

		   } 
		 
		 }
		 
		 
	 
       
 
</script>
	
</body>
</html>