<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<html>
<head>
    <title>表单管理</title>

    <meta name="decorator" content="default">
    <%@ include file="/webpage/include/combox.jsp" %>
    <script type="text/javascript">
        var validateForm;
        function doSubmit() {
            return validateForm.form() ? ($("#inputForm").submit(), true) : false;
        }
        ;
        $(document).ready(function () {
            validateForm = $("#inputForm").validate({
                ignore: "", submitHandler: function (form) {
                    loading("正在提交，请稍等...");
                    $("input[type=checkbox]").each(function () {
                        $(this).after('<input type="hidden" name="' + $(this).attr("name") + '" value="' + ($(this).attr("checked") ? "1" : "0") + '"/>');
                        $(this).attr("name", "_" + $(this).attr("name"));
                    });
                    form.submit();
                }, errorContainer: "#messageBox"
                , errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
                }
            });
            resetColumnNo();
            $("#tableType").change(function () {
                "3" == $("#tableType").val() ? addForTreeTable() : removeForTreeTable();
            });
            $("#isProcessDefinition").change(function () {
                "1" == $("#isProcessDefinition").val() ? addForActTable() : removeForActTable();
            });
            var b, c;
            $("#contentTable1").tableDnD({
                onDragClass: "myDragClass", onDrop: function (a, d) {
                    c = $(d).index();
                    var f = $("#tab-2 #contentTable2 tbody tr:eq(" + c + ")"), e = $("#tab-2 #contentTable2 tbody tr:eq(" + b + ")");
                    b < c ? e.insertAfter(f) : e.insertBefore(f);
                    f = $("#tab-3 #contentTable3 tbody tr:eq(" + c + ")");
                    e = $("#tab-3 #contentTable3 tbody tr:eq(" + b + ")");
                    b < c ? e.insertAfter(f) : e.insertBefore(f);
                    f = $("#tab-4 #contentTable4 tbody tr:eq(" + c + ")");
                    e = $("#tab-4 #contentTable4 tbody tr:eq(" + b + ")");
                    b < c ? e.insertAfter(f) : e.insertBefore(f);
                    resetColumnNo();
                }, onDragStart: function (a, c) {
                    b = $(c).index();
                }
            });
        });
        function resetColumnNo() {
            $("#tab-1 #contentTable1 tbody tr").each(function (b, c) {
                $(this).find("span[name*=columnList],select[name*=columnList],input[name*=columnList]").each(function () {
                    var a = $(this).attr("name"), c = a.split(".")[1], c = "columnList[" + b + "]." + c;
                    $(this).attr("name", c);
                    0 <= a.indexOf(".sort") && ($(this).val(b), $(this).next().text(b));
                });
            });
        };
        
        function addColumn() {
            var b = $("#template1").clone();
            b.removeAttr("style");
            b.removeAttr("id");
            $("#tab-1 #contentTable1 tbody").append(b);
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
<body id="" class=" pace-done" style="">
<div class="pace  pace-inactive">
    <div class="pace-progress" data-progress-text="100%" data-progress="99" style="width: 100%;">
        <div class="pace-progress-inner"></div>
    </div>
    <div class="pace-activity"></div>
</div>


<!-- 锁定 -->
<div class="wrapper wrapper-content">

<!-- 插入行模板 -->
    <table style="display:none">
      <tbody>
        <tr id="template1" style="display:none">
            <td>
                <input type="hidden" name="columnList[0].sort" value="0" maxlength="200"
                       class="form-control required   digits">
                <label>0</label>
                <input type="hidden" class="form-control" name="columnList[0].isInsert" value="1">
                <input type="hidden" class="form-control" name="columnList[0].isEdit" value="1">
            </td>
            <td>
                <input type="checkbox" class="form-control  " name="ck" value="1">
            </td>
            <td>
                <input type="text" class="form-control required" name="columnList[0].name" value="">
            </td>
            <td>
                <input type="text" class="form-control required" name="columnList[0].comments" value="" maxlength="200">
            </td>
            <td>
				<span name="columnList[0].jdbcType" class="required" value="varchar(64)"></span>
			</td>
            <td>
                <input type="checkbox" class="form-control" name="columnList[0].isPk" value="1">
            </td>
        </tr>
      </tbody>
    </table>

    <!-- 业务表添加 -->
    <form:form id="inputForm" modelAttribute="genTable" class="form-horizontal" action="${ctx}/gen/genTable/save" method="post"
          novalidate="novalidate">
        <input id="id" name="id" type="hidden" value="${genTable.id}">
        <input id="isSync" name="isSync" type="hidden" value="${genTable.isSync}">


        <!-- 0:隐藏tip, 1隐藏box,不设置显示全部 -->
        <script type="text/javascript">top.$.jBox.closeTip();</script>
        <sys:message content="${message}"/>

      <table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
            <tbody>
            <tr>
            	<td class="width-15 active"><label class="pull-right">数据源：</label></td>
                <td class="width-35">
                    <form:select path="datasource" class="form-control m-b"> 
                    	<form:option value="" label="系统数据源"/>
                        <form:options items="${fns:getDictList('sys_datasource')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
                    </form:select>
                </td>
                <td class="width-15 active"><label class="pull-right"><font color="red">*</font>表名：</label></td>
                <td class="width-35">
                    <input id="name" name="name" class="form-control required" type="text" value="${genTable.name}"
                           maxlength="200"
                           aria-required="true">
                </td>
            </tr>
            <tr>
            	<td class="width-15 active"><label class="pull-right"><font color="red">*</font>类名：</label></td>
                <td class="width-35">
                    <input id="className" name="className" class="form-control required" type="text" value="${genTable.className}" maxlength="200" aria-required="true">
                </td>
                <td class="width-15 active"><label class="pull-right"><font color="red">*</font>说明：</label></td>
                <td class="width-35">
                    <input id="comments" name="comments" class="form-control required" type="text"
                           value="${genTable.comments}"
                           maxlength="200" aria-required="true">
                </td>
            </tr>
            <tr>
                <td class="width-15 active"><label class="pull-right">表类型：</label></td>
                <td class="width-35">
                    <select id="tableType" name="tableType" class="form-control m-b" style="width:200px;">
                        <option value="0" ${genTable.tableType=='0'?'selected':''}>单表</option>
                        <option value="1" ${genTable.tableType=='1'?'selected':''}>主表</option>
                        <option value="2" ${genTable.tableType=='2'?'selected':''}>附表</option>
                        <option value="3" ${genTable.tableType=='3'?'selected':''}>树结构表</option>
                    </select>
                    <span class="help-inline">如果是附表，请指定主表表名和当前表的外键</span>
                </td>
                <td class="width-15 active"><label class="pull-right">版本化数据：</label></td>
                <td class="width-35">
                    <form:select path="isVersion" class="form-control m-b" style="width:100px;">
                    	<form:option value="" label="" />
                        <form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
                    </form:select>
                </td>
            </tr>
            <tr>
                <td class="width-15 active"><label class="pull-right">主表表名：</label></td>
                <td class="width-35">
                    <select id="parentTable" name="parentTable" class="form-control">
                        <option value="" selected="selected">无</option>
                        <c:forEach items="${tableList}" var="table">
                            <option value="${table.name}" ${genTable.parentTable eq table.name ? 'selected' : ''}>${table.nameAndComments}</option>
                        </c:forEach>
                    </select>
                </td>
                <td class="width-15 active"><label class="pull-right">当前表外键：</label></td>
                <td class="width-35">
                    <input id="parentTableFk" name="parentTableFk" class="form-control" type="text"
                           value="${genTable.parentTableFk}"
                           maxlength="200">
                </td>
            </tr>
			<tr>
				<td class="width-15 active"><label class="pull-right">流程表单：</label></td>
                <td class="width-35">
                    <form:select path="isProcessDefinition" class="form-control m-b" style="width:100px;">
                    	<form:option value="" label="" />
                        <form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
                    </form:select>
                </td>
                <td class="width-15 active"><label class="pull-right">流程定义：</label></td>
                <td class="width-35">
                    <select id="processDefinitionKey" name="processDefinitionKey" class="form-control">
                        <option value=""></option>
                        <c:forEach items="${processDefinitionList}" var="processDefinition">
                            <option value="${processDefinition.key}" ${genTable.processDefinitionKey eq processDefinition.key ? 'selected' : ''}>${processDefinition.key}:${processDefinition.name}</option>
                        </c:forEach>
                    </select>
                </td>
			</tr>
            </tbody>
     </table>
        
        <button class="btn btn-white btn-sm" onclick="return addColumn()"><i class="fa fa-plus"> 增加</i></button>
        <button class="btn btn-white btn-sm" onclick="return delColumn()"><i class="fa fa-minus"> 删除</i></button>
        <br>

     <table id="contentTable1"  class="table table-striped table-bordered table-hover  dataTables-example dataTable">
        <thead>
        <tr style="cursor: move;">
            <th width="40px">序号</th>
            <th>操作</th>
            <th title="数据库字段名">列名</th>
            <th title="默认读取数据库字段备注">说明</th>
            <th title="数据库中设置的字段类型及长度">物理类型</th>
            <th title="是否是数据库主键">主键</th>
            <!-- <th title="字段是否可为空值，不可为空字段自动进行空值验证">可空</th> -->
            <!--<th title="选中后该字段被加入到insert语句里">插入</th>
            <th title="选中后该字段被加入到update语句里">编辑</th>  -->
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${not empty genTable.columnList}">
                <c:forEach items="${genTable.columnList}" var="column" varStatus="vs">
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
                <tr style="cursor: move;">
                    <td>
                        <input type="hidden" name="columnList[0].sort" value="0" maxlength="200"
                               class="form-control required   digits" aria-required="true">
                        <label>0</label>
                        <input type="hidden" name="columnList[0].isInsert" value="1">
                        <input type="hidden" name="columnList[0].isEdit" value="0">
                    </td>
                    <td>
                        <div class="icheckbox_square-green" style="position: relative;"><input
                                type="checkbox" class="i-checks " name="ck" value="1"
                                style="position: absolute; opacity: 0;">
                        </div>
                    </td>
                    <td>
                        <input type="text" class="form-control" name="columnList[0].name"
                               value="id">
                    </td>
                    <td>
                        <input type="text" class="form-control" name="columnList[0].comments"
                               value="主键" maxlength="200">
                    </td>
                    <td>
                        <span name="columnList[0].jdbcType" class="required"
                              value="varchar(64)"></span>
                    </td>
                    <td>
                        <div class="icheckbox_square-green" style="position: relative;">
                            <input type="checkbox" class="i-checks" name="columnList[0].isPk"
                                   value="1" checked="" style="position: absolute; opacity: 0;">
                        </div>
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>
        </tbody>
      </table>
    </form:form>
</div>


<script type="text/javascript">//<!-- 无框架时，左上角显示菜单图标按钮。
if (!(self.frameElement && self.frameElement.tagName == "IFRAME")) {
    $("body").prepend("<i id=\"btnMenu\" class=\"icon-th-list\" style=\"cursor:pointer;float:right;margin:10px;\"></i><div id=\"menuContent\"></div>");
    $("#btnMenu").click(function () {

        top.layer.open({
            type: 2,
            area: ['300px', '350px'],
            content: 'get:/gt_plus/a/sys/menu/treeselect;JSESSIONID=23d215b3762e4e9cbb5142b56cb36aa4' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
        });
        //top.$.jBox('get:/gt_plus/a/sys/menu/treeselect;JSESSIONID=23d215b3762e4e9cbb5142b56cb36aa4', {title:'选择菜单', buttons:{'关闭':true}, width:300, height: 350, top:10});
        //if ($("#menuContent").html()==""){$.get("/gt_plus/a/sys/menu/treeselect", function(data){$("#menuContent").html(data);});}else{$("#menuContent").toggle(100);}
    });
}//-->
</script>

</body>
</html>