<%@page contentType="text/html; charset=utf-8"%>
<%@page import="java.io.*"%>
<%@page import="java.text.*"%>
<%@page import="java.util.*"%>
<%@page import="DBstep.iMsgServer2000.*"%>
<%!
  public class iWebOffice {
    private String mOption;
    private String mRecordID;
    private String mUserName;
    private String mFileType;
    private String mFilePath;
    private DBstep.iMsgServer2000 MsgObj;
    public void ExecuteRun(HttpServletRequest request, HttpServletResponse response) {
      mOption = "";
      mRecordID = "";
      mUserName = "";
      mFileType = "";
      mFilePath = request.getSession().getServletContext().getRealPath("");   //取得服务器路径
      MsgObj = new DBstep.iMsgServer2000();                                   //创建服务对象
      
      //System.out.println("ReadPackage");      
      MsgObj.Load(request);                                                   //解析客户端发送的数据

      if (MsgObj.GetMsgByName("DBSTEP").equalsIgnoreCase("DBSTEP")) {         //如果是合法的信息包
        mOption = MsgObj.GetMsgByName("OPTION");                              //取得操作信息
        mUserName = MsgObj.GetMsgByName("USERNAME");                          //取得系统用户
        //System.out.println(mOption);                                          //打印出调试信息
        if (mOption.equalsIgnoreCase("SAVEFILE")) {                           //下面的代码为保存为全文批注格式文件
          mRecordID = MsgObj.GetMsgByName("RECORDID");                        //取得文档编号
          mFileType = MsgObj.GetMsgByName("FILETYPE");                        //取得文档类型
          //mFileBody=MsgObj.MsgFileBody();			                              //取得文档内容 mFileBody可以保存到数据库中，类型byte[]
          MsgObj.MsgTextClear();                                              //清除文本信息
          mFilePath = mFilePath + "/Document/" + mRecordID + mFileType;       //文档的完整路径
          if (MsgObj.MsgFileSave(mFilePath)) {                                //保存文档
            MsgObj.SetMsgByName("STATUS", "保存文档成功!");                   //设置状态信息
            MsgObj.MsgError("");                                              //清除错误信息
          }
          else {
            MsgObj.MsgError("保存文档失败!");                                 //设置错误信息
          }
          MsgObj.MsgFileClear();
        }

        else if (mOption.equalsIgnoreCase("LOADFILE")) {                      //下面的代码为调入全文批注格式文件
          mRecordID = MsgObj.GetMsgByName("RECORDID");                        //取得文档编号
          mFileType = MsgObj.GetMsgByName("FILETYPE");                        //取得文档类型
          MsgObj.MsgTextClear();                                              //清除文本信息
          mFilePath = mFilePath + "/Document/" + mRecordID + mFileType;       //全文批注文件的完整路径
          System.out.println(mFilePath);  
          if (MsgObj.MsgFileLoad(mFilePath)) {                                //调入文档内容
            //MsgObj.MsgFileBody(mFileBody);			                            //将文件信息打包，mFileBody为从数据库中读取，类型byte[]
            MsgObj.SetMsgByName("STATUS", "打开文档成功!");                   //设置状态信息
            MsgObj.MsgError("");                                              //清除错误信息
          }
          else {
            MsgObj.MsgError("打开文档失败!");                                 //设置错误信息
          }
        }
      }
      else {
        MsgObj.MsgError("客户端发送数据包错误!");
        MsgObj.MsgTextClear();
        MsgObj.MsgFileClear();
      }
      //System.out.println("SendPackage");
      MsgObj.Send(response);                                                  //将结果数据包返回给客户端
    }
  }
%>
<%
    iWebOffice officeServer = new iWebOffice();
    officeServer.ExecuteRun(request, response);
	out.clear();  
	out = pageContext.pushBody();  
%>