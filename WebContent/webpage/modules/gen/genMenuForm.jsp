<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
    <title>菜单管理</title>

    <meta name="decorator" content="default">
    <script type="text/javascript">
        var validateForm;
        function doSubmit() {//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
            if (validateForm.form()) {
                $("#inputForm").submit();
                return true;
            }

            return false;
        }
        $(document).ready(function () {
            $("#name").focus();
            validateForm = $("#inputForm").validate({
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });
        });

    </script>

</head>
<body id="" class="hideScroll  pace-done" style="">
<div class="pace  pace-inactive">
    <div class="pace-progress" data-progress-text="100%" data-progress="99" style="width: 100%;">
        <div class="pace-progress-inner"></div>
    </div>
    <div class="pace-activity"></div>
</div>

<form id="inputForm" class="form-horizontal" action="${ctx}/gen/genScheme/createMenu" method="post" novalidate="novalidate">
    <input id="id" name="id" type="hidden" value="${menu.id}">

    <!-- 0:隐藏tip, 1隐藏box,不设置显示全部 -->
    <script type="text/javascript">top.$.jBox.closeTip();</script>
    <sys:message content="${message}"/>

    <input type="hidden" name="gen_table_id" value="${gen_table_id}">
    <table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
        <tbody>
        <tr>
            <td class="width-15 active"><label class="pull-right">上级菜单:</label></td>
            <td class="width-35">


                <input id="menuId" name="parent.id" class="form-control required" type="hidden" value="${menu.parent.id}"
                       aria-required="true">
                <div class="input-group">
                    <input id="menuName" name="parent.name" readonly="readonly" type="text" value="${menu.parent.name}"
                           data-msg-required="" class="form-control required" style="" aria-required="true">
                    <span class="input-group-btn">
	       		 <button type="button" id="menuButton" class="btn   btn-primary  "><i class="fa fa-search"></i>
	             </button>
       		 </span>

                </div>
                <label id="menuName-error" class="error" for="menuName" style="display:none"></label>
                <script type="text/javascript">
                    $("#menuButton, #menuName").click(function () {
                        // 是否限制选择，如果限制，设置为disabled
                        if ($("#menuButton").hasClass("disabled")) {
                            return true;
                        }
                        // 正常打开
                        top.layer.open({
                            type: 2,
                            area: ['300px', '420px'],
                            title: "选择菜单",
                            ajaxData: {selectIds: $("#menuId").val()},
                            content: "${ctx}/tag/treeselect?url=" + encodeURIComponent("/sys/menu/treeData") + "&module=&checked=&extId=&isAll=",
                            btn: ['确定', '关闭']
                            , yes: function (index, layero) { //或者使用btn1
                                var tree = layero.find("iframe")[0].contentWindow.tree;//h.find("iframe").contents();
                                var ids = [], names = [], nodes = [];
                                if ("" == "true") {
                                    nodes = tree.getCheckedNodes(true);
                                } else {
                                    nodes = tree.getSelectedNodes();
                                }
                                for (var i = 0; i < nodes.length; i++) {//
                                    ids.push(nodes[i].id);
                                    names.push(nodes[i].name);//
                                    break; // 如果为非复选框选择，则返回第一个选择
                                }
                                $("#menuId").val(ids.join(",").replace(/u_/ig, ""));
                                $("#menuName").val(names.join(","));
                                $("#menuName").focus();
                                top.layer.close(index);
                            },
                            cancel: function (index) { //或者使用btn2
                                //按钮【按钮二】的回调
                            }
                        });

                    });
                </script>
            </td>
            <td class="width-15 active"><label class="pull-right"><font color="red">*</font> 名称:</label></td>
            <td class="width-35">
                <input id="name" name="name" class="required form-control valid" type="text"
                       value="${menu}" maxlength="50" aria-required="true" aria-invalid="false"></td>
        </tr>

        <tr>
            <td class="width-15 active"><label class="pull-right">图标:</label></td>
            <td class="width-35">


                <i id="iconIcon" class="icon- hide"></i>&nbsp;<span id="iconIconLabel">无</span>&nbsp;
                <input id="icon" name="icon" type="hidden" value="${menu.icon}"><a id="iconButton" href="javascript:"
                                                                       class="btn btn-primary">选择</a>&nbsp;&nbsp;
                <input id="iconclear" class="btn btn-default" type="button" value="清除" onclick="clear()">
                <script type="text/javascript">
                    $("#iconButton").click(function () {

                        top.layer.open({
                            type: 2,
                            title: "选择图标",
                            area: ['700px', $(top.document).height() - 180 + "px"],
                            content: '${ctx}/tag/iconselect?value="+$("#icon").val()',
                            btn: ['确定', '关闭'],
                            yes: function (index, layero) { //或者使用btn1
                                var icon = layero.find("iframe")[0].contentWindow.$("#icon").val();
                                $("#iconIcon").attr("class", "fa " + icon);
                                $("#iconIconLabel").text(icon);
                                $("#icon").val(icon);
                                top.layer.close(index);
                            }, cancel: function (index) { //或者使用btn2

                            }
                        });
                    });
                    $("#iconclear").click(function () {
                        $("#iconIcon").attr("class", "icon- hide");
                        $("#iconIconLabel").text("无");
                        $("#icon").val("");

                    });
                </script>
            </td>
            <td class="width-15 active"><label class="pull-right">排序:</label></td>
            <td class="width-35"><input id="sort" name="sort" class="required digits form-control " type="text"
                                        value="2030" maxlength="50" aria-required="true">
                <span class="help-inline">排列顺序，升序。</span></td>
        </tr>

        </tbody>
    </table>
</form>

<script type="text/javascript">//<!-- 无框架时，左上角显示菜单图标按钮。
if (!(self.frameElement && self.frameElement.tagName == "IFRAME")) {
    $("body").prepend("<i id=\"btnMenu\" class=\"icon-th-list\" style=\"cursor:pointer;float:right;margin:10px;\"></i><div id=\"menuContent\"></div>");
    $("#btnMenu").click(function () {

        top.layer.open({
            type: 2,
            area: ['300px', '350px'],
            content: 'get:${ctx}/sys/menu/treeselect;JSESSIONID=51660cb4202a4bb69e57e073609a8f33' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
        });
        //top.$.jBox('get:/a/sys/menu/treeselect;JSESSIONID=51660cb4202a4bb69e57e073609a8f33', {title:'选择菜单', buttons:{'关闭':true}, width:300, height: 350, top:10});
        //if ($("#menuContent").html()==""){$.get("/a/sys/menu/treeselect", function(data){$("#menuContent").html(data);});}else{$("#menuContent").toggle(100);}
    });
}//-->
</script>

</body>
</html>