<%@page contentType="text/html;charset=gb2312"%>
<HTML>
<title>金格科技iWebOffice2000实例程序</title>
<meta http-equiv="Content-Type" content="text/html;charset=gb2312">
<LINK href="test.css" type="text/css" rel="stylesheet">

<script language="javascript">
//作用：显示操作状态
function StatusMsg(mString){
  StatusBar.innerHTML=mString;
}

//初始化对象
function initObject(){
  WebOffice.WebUrl="OfficeServer.jsp";      //后台处理页路径，用来执行后台数据处理业务。本属性支持相对路径
  WebOffice.RecordID="1234";                //文档记录号
  WebOffice.Template="";                    //模板记录号
  WebOffice.FileName="1234.doc";            //文档名称
  WebOffice.FileType=".doc";                //文档类型  .doc  .xls
  WebOffice.UserName="文档编辑人";          //当前操作员
  WebOffice.EditType="1";                   //编辑状态：第一位可以为0,1,2,3其中：0不可编辑；1可以编辑,无痕迹；2可以编辑,有痕迹,不能修订；3可以编辑,有痕迹,能修订。

  WebOffice.MaxFileSize = 4 * 1024;         //最大的文档大小控制，默认是8M，现在设置成4M。
  WebOffice.ShowMenu="1";                   //是否显示菜单：1显示；0不显示

  WebOffice.CreateFile();                   //创建空白文档
}

function WebSave(){
	WebOffice.WebSave();
}
function WebOpen(){
	WebOffice.WebOpen();
}

//作用：打开服务器文档
function LoadDocument(){
  if (!WebOffice.WebOpen()){                //打开该文档    交互OfficeServer的OPTION="LOADFILE"
    StatusMsg(WebOffice.Status);            //显示状态，从OfficeServer中读取
  }else{
    StatusMsg(WebOffice.Status);            //显示状态，从OfficeServer中读取
  }
}

//作用：保存服务器文档
function SaveDocument(){
  if (!WebOffice.WebSave()){     //交互OfficeServer的OPTION="SAVEFILE"
    StatusMsg(WebOffice.Status);
  }else{
    StatusMsg(WebOffice.Status);
  }
}

//作用：创建空白文档
function CreateFile(){
  WebOffice.CreateFile();
  StatusMsg(WebOffice.Status);
}

//作用：打开本地文件
function WebOpenLocal(){
  var result = WebOffice.WebOpenLocalFile();
  if(result){
    StatusMsg("打开本地文档成功！");
  }else{
    StatusMsg(WebOffice.Status);
  }
}

//作用：保存文件到本地
function WebSaveLocal(){
  var result = WebOffice.WebSaveLocalFile();
  if(result){
    StatusMsg("保存文档到本地成功！");
  }else{
    StatusMsg(WebOffice.Status);
  }
}

//作用：获取文档页数（VBA扩展应用）
function WebDocumentPageCount(){
  if (WebOffice.FileType==".doc"){
    var intPageTotal = WebOffice.WebObject.Application.ActiveDocument.BuiltInDocumentProperties(14);
    alert("文档页总数："+intPageTotal);
  }
  
  if (WebOffice.FileType==".wps"){
    var intPageTotal = WebOffice.WebObject.PagesCount();
    alert("文档页总数："+intPageTotal);
  }
}

//作用：接受文档中全部痕迹（VBA扩展应用）
function WebAcceptAllRevisions(){
  WebOffice.WebObject.Application.ActiveDocument.AcceptAllRevisions();
  var mCount = WebOffice.WebObject.Application.ActiveDocument.Revisions.Count;
  if(mCount>0){
    StatusMsg("接受痕迹失败！");
    return false;
  }else{
    StatusMsg("文档中的痕迹已经全部接受！");
    return true;
  }
}

//作用：退出iWebOffice
function UnLoad(){
  try{
    if (!WebOffice.WebClose()){
      StatusMsg(WebOffice.Status);
    }
    else{
      StatusMsg("关闭文档...");
    }
  }
  catch(e){
    alert(e.description);
  }
}


</script>

<BODY onLoad="initObject();" onunload="UnLoad()"><!--创建新文件-->
  <div style="text-align:center;font-size:18px">iWebOffice2000网络文档中间件[免费版]实例程序</div>
  <hr size=1>  
  <div style="font-size:12px;color:#FF0000;">
    　注：iWebOffice2000控件能在线打开、编辑和保存服务器上的文档。如要演示本示例您需要：<br>
    　　　1.请安装Office2000以上版本的软件并使用纯IE浏览器，运行页面时安装iWebOffice2000插件。<br>
    　　　2.如果不能自动安装iWebOffice2000插件，请在这里下载<a href="InstallClient.zip">[本地安装程序]</a>并安装。
  </div>
  <hr size=1>
  <input type=button class=button value="新建文件" onclick="CreateFile()">
  <input type=button class=button value="编辑留痕" onClick="WebOffice.EditType='2'">
  <input type=button class=button value="编辑不留痕" onClick="WebOffice.EditType='1'">
‖<input type=button class=button value="打开本地文件" onclick="WebOpenLocal()">
  <input type=button class=button value="保存本地文件" onclick="WebSaveLocal()">
  <input type=button class=button value="打开远程文件" onClick="LoadDocument()"><!--系统将通过WebUrl指定的程序到服务器上调入文件, 调入由RecordID指定的文件-->
  <input type=button class=button value="保存远程文件" onClick="SaveDocument()"><!--系统将通过WebUrl指定的程序保存本文件到服务器上,保存由RecordID指定的文件-->
‖<input type=button class=button value="文档页数" onClick="WebDocumentPageCount()">
  <input type=button class=button value="接受痕迹" onClick="WebAcceptAllRevisions()">
  <input type=button class=button value="保存文件" onClick="WebSave()">
  <input type=button class=button value="打开文件" onClick="WebOpen()">
  
  <table>
    <tr>
      <td height="28" style="font-size:12px;color:#0000FF">状态栏：</td>
      <td id=StatusBar style="font-size:12px;color:#FF0000">操作状态信息</td>
    </tr>
  </table>
  <!--调用iWebOffice，注意版本号，可用于升级-->
  <script src="iWebOffice2000.js"></script>
</BODY>
</HTML>