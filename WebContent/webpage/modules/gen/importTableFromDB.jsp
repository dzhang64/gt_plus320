<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<html>
<head>
<title>业务表管理</title>
<meta name="decorator" content="default">

    <%@ include file="/webpage/include/combox.jsp"%>
<script type="text/javascript">
    function doSubmit() {//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
        if ($("#inputForm").valid()) {
            $("#inputForm").submit();
            return true;
        }

        return false;
    }
</script>

</head>
<body id="" class="hideScroll  pace-done" style="">
<div class="pace  pace-inactive">
    <div class="pace-progress" data-progress-text="100%" data-progress="99" style="width: 100%;">
        <div class="pace-progress-inner"></div>
    </div>
    <div class="pace-activity"></div>
</div>


<div class="wrapper wrapper-content">


    <!-- create table from db -->

    <form:form id="inputForm" modelAttribute="genTable" action="${ctx}/gen/genTable/importTableFromDB" method="post" class="form-horizontal">
        <input id="id" name="id" type="hidden" value="">

        <sys:message content="${message}"/>
        <br/>
        <script type="text/javascript">top.$.jBox.closeTip();</script>

        <div class="control-group">
            <label class="control-label">表名:</label>
            <div class="controls">
                <form:select path="name" class="form-control">
                    <form:options items="${tableList}" itemLabel="nameAndComments" itemValue="name" htmlEscape="false"/>
                </form:select>
            </div>
        </div>
    </form:form>

</div>

<script type="text/javascript">//<!-- 无框架时，左上角显示菜单图标按钮。
if (!(self.frameElement && self.frameElement.tagName == "IFRAME")) {
    $("body").prepend("<i id=\"btnMenu\" class=\"icon-th-list\" style=\"cursor:pointer;float:right;margin:10px;\"></i><div id=\"menuContent\"></div>");
    $("#btnMenu").click(function () {

        top.layer.open({
            type: 2,
            area: ['300px', '350px'],
            content: 'get:${ctx}/sys/menu/treeselect;JSESSIONID=8a1d216542c34f8882ae8d9fa6e5be06' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
        });
        //top.$.jBox('get:/gt_plus/a/sys/menu/treeselect;JSESSIONID=8a1d216542c34f8882ae8d9fa6e5be06', {title:'选择菜单', buttons:{'关闭':true}, width:300, height: 350, top:10});
        //if ($("#menuContent").html()==""){$.get("/gt_plus/a/sys/menu/treeselect", function(data){$("#menuContent").html(data);});}else{$("#menuContent").toggle(100);}
    });
}//-->
</script>

</body>
</html>