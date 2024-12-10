<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<html>
<head>
    <title>代码生成</title>
    <meta name="decorator" content="default">
    <script type="text/javascript">
        var validateForm;
        function doSubmit(){//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
            if(validateForm.form()){
                $("#inputForm").submit();
                return true;
            }

            return false;
        }
        $(document).ready(function() {
            $("#name").focus();
            validateForm = $("#inputForm").validate({
                submitHandler: function(form){
                    loading('正在提交，请稍等...');
                    form.submit();
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
        });
    </script>
</head>
<body id="" class="hideScroll  pace-done" style=""><div class="pace  pace-inactive"><div class="pace-progress" data-progress-text="100%" data-progress="99" style="width: 100%;">
    <div class="pace-progress-inner"></div>
</div>
    <div class="pace-activity"></div></div>

<sys:message content="${message}"/>

<form id="inputForm" class="form-horizontal" action="${ctx}/gen/genTable/genCode" method="post" novalidate="novalidate">

    <input name="id" value="${genScheme.id}" type="hidden">
	
    <!-- 0:隐藏tip, 1隐藏box,不设置显示全部 -->

    <script type="text/javascript">top.$.jBox.closeTip();</script>

    <div class="control-group">
    </div>
    <div class="control-group">
        <label class="control-label"><font color="red">*</font>代码风格:</label>
        <div class="controls">
            <select id="category" name="category" class="required form-control" aria-required="true">
                <option value="curd" ${genScheme.category=='curd'?'selected':''}>增删改查（单表）</option>
                <option value="curd_many" ${genScheme.category=='curd_many'?'selected':''}>增删改查（一对多）</option>
                <option value="treeTable" ${genScheme.category=='treeTable'?'selected':''}>树结构表（一体）</option>
            </select>
            <span class="help-inline">
					生成结构：{包名}/{模块名}/{分层(dao,entity,service,web)}/{子模块名}/{java类}
				</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label"><font color="red">*</font>生成包路径:</label>
        <div class="controls">
            <input id="packageName" name="packageName" class="required form-control" type="text"
                   value="${genScheme.packageName}" maxlength="500" aria-required="true">
            <span class="help-inline">建议模块包：com.gt_plus.modules</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label"><font color="red">*</font>生成模块名:</label>
        <div class="controls">
            <input id="moduleName" name="moduleName" class="required form-control valid" type="text"
                   value="${genScheme.moduleName}"
                   maxlength="500" aria-required="true" aria-invalid="false">
            <span class="help-inline">可理解为子系统名，例如 sys</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">生成子模块名:</label>
        <div class="controls">
            <input id="subModuleName" name="subModuleName" class="form-control valid" type="text" value="${genScheme.subModuleName}"
                   maxlength="500" aria-invalid="false">
            <span class="help-inline">可选，分层下的文件夹，例如 </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label"><font color="red">*</font>生成功能描述:</label>
        <div class="controls">
            <input id="functionName" name="functionName" class="required form-control valid" type="text" value="${genScheme.functionName}"
                   maxlength="500" aria-required="true" aria-invalid="false">
            <span class="help-inline">将设置到类描述</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label"><font color="red">*</font>生成功能名:</label>
        <div class="controls">
            <input id="functionNameSimple" name="functionNameSimple" class="required form-control valid" type="text"
                   value="${genScheme.functionNameSimple}" maxlength="500" aria-required="true" aria-invalid="false">
            <span class="help-inline">用作功能提示，如：保存“某某”成功</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label"><font color="red">*</font>生成功能作者:</label>
        <div class="controls">
            <input id="functionAuthor" name="functionAuthor" class="required form-control" type="text"
                   value="${genScheme.functionAuthor}"
                   maxlength="500" aria-required="true">
            <span class="help-inline">功能开发者</span>
        </div>
    </div>

    <input type="hidden" name="genTable.id" value="${genScheme.genTable.id}">
    <!--
    <div class="control-group">
        <label class="control-label"><font color="red">*</font>业务表名:</label>
        <div class="controls">
            <select id="genTable.id" name="genTable.id" class="required form-control">
                <option value="020a21edacfb4c0e99471e561e385555" selected="selected">t_dynamic_comment  :  动态评论表</option><option value="70218034d413442f8daf1e0714295f8a">t_login  :  登陆信息表</option><option value="5f2608f08f1c403ab154fcf108831e93">t_message_history  :  聊天记录</option><option value="6bcf3c9480fa4a72b5a9c1990d5c0a56">t_musang  :  动态打赏表</option><option value="0d08136faea04204a93171b3a963226c">t_order_pay  :  支付表信息·</option><option value="27717810ce2b428f94f2a4acd2a1aa6b">t_rent_item  :  出租内容</option><option value="b297fb7e13954493ab193bb7e7851a30">t_rent_order  :  交易订单</option><option value="bb6bc4eb9801458da07fdfa65d1187a7">t_space_dynamic  :  空间动态表</option><option value="f11d35a4d1e846d8ae5e6ec4d460f860">t_user_account  :  用户账户表</option><option value="f6cfba880a11498dbf1b84f0b9bef881">t_user_focus  :  关注表</option><option value="e9f5ca08985d44ad8364506127915c83">t_user_friend  :  好友关系表</option><option value="9923286b21314b03a22f7bab95ff2a72">t_user_info  :  用户信息表</option>
            </select>
            <span class="help-inline">生成的数据表，一对多情况下请选择主表。</span>
        </div>
    </div>
     -->
</form>


<script type="text/javascript">//<!-- 无框架时，左上角显示菜单图标按钮。
if(!(self.frameElement && self.frameElement.tagName=="IFRAME")){
    $("body").prepend("<i id=\"btnMenu\" class=\"icon-th-list\" style=\"cursor:pointer;float:right;margin:10px;\"></i><div id=\"menuContent\"></div>");
    $("#btnMenu").click(function(){

        top.layer.open({
            type: 2,
            area:['300px','350px'],
            content: 'get:${ctx}/sys/menu/treeselect;JSESSIONID=c228acb4bd934d27b2a90846f698bccd' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
        });
        //top.$.jBox('get:/a/sys/menu/treeselect;JSESSIONID=c228acb4bd934d27b2a90846f698bccd', {title:'选择菜单', buttons:{'关闭':true}, width:300, height: 350, top:10});
        //if ($("#menuContent").html()==""){$.get("/a/sys/menu/treeselect", function(data){$("#menuContent").html(data);});}else{$("#menuContent").toggle(100);}
    });
}//-->
</script>

</body>
</html>
