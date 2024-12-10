<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/webpage/include/taglib.jsp"%>
<%@ attribute name="input" type="java.lang.String" required="true" description="附件属性名称"%>
<%@ attribute name="uploadPath" type="java.lang.String" required="true" description="上传文件路径"%>
<div class="set">
	<input id="${input}File" name="file" type="file" multiple="multiple" onchange="${input}Validate();" style="display: none;"/>
	<a class="btn btn-white btn-sm" data-toggle="tooltip" data-placement="left" onclick="${input}Select();" title="添加文件"><i class="fa fa-plus"></i> 添加文件</a>
</div>
<div id="${input}FileListDiv" style="margin: 5px 0px 5px 3px;"></div>
<script type="text/javascript">
$(function(){
	if ($("#${input}").val() != null && $("#${input}").val() != "") {
		var data = eval ("("+$("#${input}").val()+")");
			$("#${input}FileListDiv").html(
				"<span id='" + data.uuid + "' name='${input}Allow'>"
					+"<img src='"+data.fileUrl+"' class='img-rounded'>"
					+ "<a onclick='${input}Remove(\"" + data.uuid + "\");' class='set'>删除</a>"
				+ "</span>");	
	}
	if ($("#viewFlag")) {
		var viewFlag = $("#viewFlag").val();
		if (viewFlag.indexOf("set") != -1) {
			$("a[class='set']").attr("onclick","javascript:;").html("");
			$("div[class='set']").html("");
		}
	}
});
function ${input}Select(){
	$("#${input}File").click();
}
function ${input}Validate(){
	if($("#${input}File").val()!=""){
		var fileList = document.getElementById("${input}File").files;
		var length = $("span[name='${input}Allow']").length;
		if(length + fileList.length > ${fns:getConfig('allowedNums')}){
			jp.error("最多可上传" + ${fns:getConfig('allowedNums')} + "个文件");
			return;
		} 
		for(var i = 0; i < fileList.length; i++){
			var formData = new FormData();
			formData.append('file',fileList[i]);
			//formData.append('toPdf',${toPdf});
			var filename = fileList[i].name;
			${input}AjaxUpload(formData,filename);
		}
	}
	$("#${input}File").val("");
}
function ${input}AjaxUpload(formData,filename){

	$.ajax({
		type:"post",
		url:"${ctx}${uploadPath}/fileUpload",
		data:formData,
		processData : false,     
  		contentType : false,  
		dataType:"json",
		success:function(data){
			if(data=="ExceedSize"){
				$("#${input}FileListDiv").html(
						$("#${input}FileListDiv").html()
						+ "<span style='color:red;'>" + filename + "&emsp;所选文件超过10M无法上传" + "</span>"
						+ "<br>"
				);
			 }else if(data=="IllegalExtension"){
				$("#${input}FileListDiv").html(
						$("#${input}FileListDiv").html()
						+ "<span style='color:red;'>" + filename + "&emsp;所选文件类型无法上传" + "</span>"
						+ "<br>"
				);
			}else{
				$("#${input}FileListDiv").html(
						$("#${input}FileListDiv").html()
						+ "<span id='" + data.uuid + "' name='${input}Allow'>"
							+ "<span style='color:#676A6C;'>" + data.filename + "</span>"
							+ "&emsp;"
							+ "<span style='color:#AAA;'>" + data.fileSize + "</span>"
							+ "&emsp;"
							+ "<a href='" + data.fileUrl + "'>下载</a>"
							+ "&nbsp;"
							+ "<a onclick='${input}Remove(\"" + data.uuid + "\");'>删除</a>" 
							+ "<br>"
						+ "</span>"
				);
				//json拼接	开始
				var hiddenJson = "";
				var newHiddenJson = "{\"fileSize\":\"" + data.fileSize
					 + "\",\"filename\":\"" + data.filename
					 + "\",\"uuid\":\"" + data.uuid
					 + "\",\"fileUrl\":\"" + data.fileUrl
					 + "\"}";
				var oldHiddenJson = $("#${input}").val(); 
				if(oldHiddenJson != null && oldHiddenJson != ""){
					oldHiddenJson = oldHiddenJson.substring(0,oldHiddenJson.lastIndexOf("]"));
					hiddenJson = oldHiddenJson + "," + newHiddenJson + "]";
				}else{
					hiddenJson = "[" + newHiddenJson + "]";
				}
				$("#${input}").val(newHiddenJson);
				//json拼接	结束
			}
		},
		error:function(data){
			jp.error("文件过大或网络异常");
		}
	});
}
function ${input}Remove(uuid){
	//json片段删除	开始
	var hiddenJson = "";
	var data = eval('(' + $("#${input}").val() + ')');
	if(data.length > 1){
		hiddenJson = "[";
		for(var i = 0; i < data.length; i++){
		if(data[i].uuid!=uuid){
			hiddenJson += "{\"fileSize\":\"" + data[i].fileSize
					 + "\",\"filename\":\"" + data[i].filename
					 + "\",\"uuid\":\"" + data[i].uuid
					 + "\",\"fileUrl\":\"" + data[i].fileUrl
					 + "\"},";
			}
		}
		hiddenJson = hiddenJson.substring(0,hiddenJson.lastIndexOf(",")) + "]";
	}
	$("#${input}").val(hiddenJson);
	//json片段删除	结束
	$("span#"+uuid).remove();
}
</script>