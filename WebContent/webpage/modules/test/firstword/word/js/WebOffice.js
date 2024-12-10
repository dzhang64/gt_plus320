function WebOffice2015() {
	this.dVersion = "4.0.0.4";
	this.dUpdateDate = "2017-10-17";
	//高级功能：全文检索、文档对比

	// *****************************************************************************************************************************
	// 内部变量和方法
	this.PASSWORD = "123456";
	var ISO;// 服务器返回值
	var root;
	var rootValue;
	this.Ashell = null;
    this.BookMark = "";
    this.ImageName = "";
	this.obj; // obj：object 对象
	this.DownloadedFileTempPathName = "";
	this.DOWN = "\\WebOffice\\Down\\" // 指定隐藏路径并设置名称
	this.UP = "\\WebOffice\\UP\\"//指定隐藏路径并设置名称
	this.FilePath = null; // 文档路径
	this.TmpFile = null; // 临时下载的文档名称（含完整路径）
	this.tempInsertName = ""; // 插入文件的文件名称
	this.DocTypeValue = {
		DOC : 0,
		DOCX : 16,
		XLS : 56,
		XLSX : 51
	}; // 枚举所有的文档类型值用户保存用
	this.DocSuffixType = {
		0 : ".doc",
		12 : ".docx",
		56 : ".xls",
		51 : ".xlsx"
	}; // 根据打开文档类型，来获取后缀名称
	this.setVersion = -1;
	this.OfficeVersion = {
		v2003 : 1,
		vOther : 0
	} // 判断Office版本，这个只要用于保存到本地
	this.sendMode = null // 设置异步调用模式
	this.iWebOfficeTempName = "iWebOfficeTempName.doc"
	// *****************************************************************************************************************************

	// *****************************************************************************************************************************
	// 对外公共属性
	this.WebUrl = ""; // 服务器应用程序Url路径 				（该功能已完整实现）
	this.RecordID = ""; // 文档的纪录号					（该功能已完整实现）
	this.Template = ""; // 模板编号					（该功能已完整实现）
	this.FileName = ""; // 文档名称					（该功能已完整实现）
	this.UserName = ""; // 操作文档用户名				（该功能已完整实现）
	this.FileType = ""; // 文档类型 .doc .xls .wps		（该功能已完整实现）
	this.EditType = ""; //文档编辑类型					（该功能已完整实现）
	this.ExtParam = ""; // 客户自定义的参数				（未实现）
	this.EditType = 0; // 设置文档编辑状态				（该功能已完整实现）
	this.PenColor = "#FF0000"; // 默认手写批注的颜色		（手写功能暂时不需要实现）
	this.PenWidth = "3"; // 写字的笔宽度					（手写功能暂时不需要实现）
	this.Print = "1"; // 设置是否允许打印 "1": 允许打印 "0": 禁止打印	（未实现）
	this.WebObject = null; // 设置WebObject对象，方便调用VBA 方法（）
	// 设置是否显示整个控件工具栏，包括OFFICE的工具栏
	// 0 : 自定义工具栏=false, Office工具栏=true;
	// 1 : 自定义工具栏=true, Office工具栏=true;
	// 2 : 自定义工具栏=false, Office工具栏=false;
	// 3 : 自定义工具栏=true, Office工具栏=false;
	this.ShowToolBar = 1;		//					（该功能已完整实现）
	this.ShowType = "1"; // 设置载入文档后的界面模式：1文字批注；2手写批注；0文档核稿      （手写功能暂时不需要实现）
	this.Modify = false; // 返回当前文件中的信息是否被修改						（未实现）
	this.CopyType = "1"; // 设置拷贝类型 "1": 允许拷贝 "0": 禁止拷贝			（该功能已完整实现）
	// 该属性控制的是操作系统的粘贴板，一旦设置为禁止拷贝则整个操作系统的拷贝也被禁止。
	this.ShowMenu = "1"; // 设置是否显示整个菜单 "1": 显示菜单 "0": 不显示菜单		（该功能已完整实现）
	this.Language = "CH"; // 暂没实现									（该功能暂不实现）
	this.Status = ""; // Status：状态信息								（该功能已完整实现）
	this.WindowStatus = "1"; // 获得当前控件窗口状态，返回“1”表示小窗口，“0”表示全屏幕  （需要控件修改代码）
	this.ToolsSpace = 1; // 是否保留工具栏空间 1: 保留 0：不保留					（需要控件修改代码）
	// “工具栏空间”即当所有OFFICE工具栏都隐藏时控件故意产生一个灰色条占住空间，在OFFICE2000、2003下有此功能
	this.MaxFileSize = 8 * 1024; // 设置文件最大允许值，单位k，默认为8M			（该功能已完整实现）
	this.ShowWindow = true; // 设置是否显示保存及打开窗口 True: 显示 False: 不显示    
	// 设置为显示，在打开文档和保存文档时会出现进度窗口
	this.Office2007Ribbon = 0; // 设置OFFICE2007功能区状态最小化			（未实现）
	this.RibbonUIXML = ""; // 用来详细设置Office2007选项卡工具界面			（该功能已完整实现）
	this.ChangeSize = false; // 是否允许在全屏下更改窗口大小 True: 允许在全屏下更改窗口大小 False:  （需要控件修改代码）
	// 不允许在全屏下更改窗口大小
	this.ShortCutKey = ""; // 用来产生组合快捷键的OnToolsClick事件。组合键之间用“;”分隔。在WebOpen()之前进行设置。 （需要控件修改代码）
	this.AllowEmpty = true; // 用于设置保存文档时是否文档内容是空白的，用于控制文档被保存成空白内容的异常问题		（未实现）
	this.UIControl = false; // 控制OFFICE2010环境中是否可以使用另存为和保存功能，用于控制客户使用自定义快捷键和快捷工具栏的另存为功能下载文件	（该功能已完整实现）
	this.Compatible = false; // 控制保存文档时是否保存为兼容格式。true，保存为兼容格式；false，按FileType指定的格式保存	（需要控件修改代码）
	this.ShowStatus = false; // 是否显示EXCEL计算用状态栏。true，显示计算用状态栏；false，不显示（默认）				（需要控件修改代码）
	// 状态栏只针对EXCEL选择多列求和计算用，在WORD文档中无作用。
	this.ClearTempFileByDays = 7; // 在正常退出时，控件会自动清除该客户端近七天内临时目录<Temporary				（需要控件修改代码）
	// Internet Files\Content.Word>中的 ~*.doc 和
	// ~*.tmp 文件。通过该接口可以设置另外的天数
	this.ChangeWindowSize = true; // 是否允许双击标题栏进入全屏 true: 开启双击进入全屏功能 false:					（需要控件修改代码）
	// 关闭双击进入全屏功能
	this.LocalTempPath = ""; // 用来更改控件保存临时目录的路径												（未实现）
	this.OpenFileType = ""; // 用来设置打开本地文档的类型													（需要控件修改代码）
	this.DebugMode = false; // 是否开启调试模式，true：开启 false：不开启，默认false
	this.HiddenDirectory = false;		//是否开启将文档保存在隐藏目录中  true：开启  false： 不开启 默认：false
	this.DelFileAfterSave = true;		//保存文档后删除该目录中的文档   true：删除  false：不删除  默认：true
	// ******************************************************************************************************************************

	// ******************************************************************************************************************************//
	// ---------------------------------------------------对外接口-------------------------------------------------------------------//
	// 设置控件标题（该功能已完整实现）
	this.SetCaption = function(captionName) 
	{
		this.obj.Caption = captionName;// this.Caption;
	}

    
	/* 从服务器上取文档并打开，打开RecordID指定的文件 */   //该功能已完整实现
	this.WebOpen = function(mBoolean) 
	{
		this.ConsoleOut("<WebOpen> 开始...");
		this.Status = "";
		var httpclient = this.obj.Http; // 设置http对象
		httpclient.Clear();
		this.WebSetMsgByName("USERNAME", this.UserName); // 加载UserName
		this.WebSetMsgByName("FILENAME", this.FileName); // 加载FileName
		this.WebSetMsgByName("FILETYPE", this.FileType); // 加载FileType
		this.WebSetMsgByName("RECORDID", this.RecordID); // 加载RecordID
		this.WebSetMsgByName("EDITTYPE", this.EditType); // 加载RecordID
		this.WebSetMsgByName("OPTION", "LOADFILE"); // 发送请求LOADFILE
		httpclient.AddForm("FormData", this.GetMessageString()); // 这里是自定义json
		// 传输格式。
		this.WebClearMessage(); // 清除所有WebSetMsgByName参数
		this.sendMode = "OpenFile";
		this.ConsoleOut("<WebOpen> 开始下载文档...");
		this.NewShowMenu();  //控制菜单栏是否可以显示
		this.NewShowToolBar(); //控制Office工具栏和自定义工具栏
		if (this.LOADFILE(httpclient)) // Http下载服务器文件
		{
			this.NewCopyType();  //控制是否可以复制
			this.NewUIControl();  //控制 2010保存跟另存为
			this.ConsoleOut("<WebOpen> 成功...");
			var httpclient = this.obj.Http;
			var ISO = httpclient.GetResponseHeader("ISO");// 获取返回值
			this.obj.ActiveDocument.Application.UserName = this.UserName; //设置当前痕迹（修订）人
			this.setEditType(this.EditType);	//设置文档编辑权限   0 、只读不能复制  1、无痕迹打开 2、有痕迹打开  
			this.Status = "打开文档成功"; // Status：状态信息
			return true;
		} else {
			//this.Status = "打开文档失败"; // Status：状态信息 由This.LOADFILE返回
			return false;
		}
	}

	/* 保存文件 */		//（该功能已完整实现）
	this.WebSave = function() 
	{
		this.ConsoleOut("<WebSave> 开始...");
		this.Status = "";
		var httpclient = this.obj.Http; // 设置http对象
		httpclient.Clear();
		this.WebSetMsgByName("USERNAME", this.UserName);
		this.WebSetMsgByName("RECORDID", this.RecordID);
		this.WebSetMsgByName("TEMPLATE", this.Template);
		this.WebSetMsgByName("SUBJECT", this.Subject);
		this.WebSetMsgByName("AUTHOR", this.Author);
		this.WebSetMsgByName("HTMLPATH", this.HTMLPath);
		this.WebSetMsgByName("FILETYPE", this.FileType);
		this.WebSetMsgByName("OPTION", "SAVEFILE");
		this.WebSetMsgByName("FILENAME", this.FileName); // 加载FileName
		this.ConsoleOut("<WebSave> 开始将文档保存到本地...");
		var mSaveResult = this.WebSaveLocalFile(this.getFilePath()
				+ this.FileName);
		if (!(mSaveResult == 0)) {
			this.ConsoleOut("<WebSave> 保存本地文档失败！错误代码为：" + mSaveResult);
			this.Status = "保存本地文档失败！错误代码为：" + mSaveResult;
			return false;
		}
		this.ConsoleOut("<WebSave> 将文档保存到本地成功...");
		this.sendMode = "SaveFile";
		this.ConsoleOut("<WebSave> 开始将文档保存到服务器...");
			// 判断本地文件是否大于指定的文件大小，如果大于不保存
			if (this.WebSetMaxFileSize(this.FilePath + this.FileName)) {
				if (this.SAVEFILE(httpclient, this.FilePath + this.FileName)) {
					this.ConsoleOut("<WebSave> 成功将文档保存到服务器...");
					var ISO = httpclient.GetResponseHeader("ISO");// 获取返回值
					this.Close();
					if (this.DelFileAfterSave)
					{
						this.ClearDirectory(); //清除临时文件
					}
					return true;
				} else {
					//STATUS 由this.SAVEFILE返回
					return false;
				}
			} else {
			this.Status = "保存失败：MaxFileSize只能允许保存：<" + this.MaxFileSize / 1024
					+ ">" + "M";
			return false;
		}
	}

	/* 关闭文档 */
	this.WebClose = function() 
	{
		this.Close();
	}
	//	（该功能已完整实现）
	this.WebSetMsgByName = function(FieldName, FieldValue) 
	{
		this.ht.Add(FieldName, FieldValue);
	}
	//  （该功能已完整实现）
	this.WebGetMsgByName = function(FieldName) 
	{
		return this.ht.Get(FieldName);
	}

	/* 按json格式发送数据 */	//（该功能已完整实现）
	this.WebSendMessage = function() {
		this.ConsoleOut("<SendMessage> 开始...");
		var httpclient = this.obj.Http; // 设置http对象
		httpclient.Clear();
		this.WebSetMsgByName("OPTION", "SENDMESSAGE");
		this.sendMode = "SendMessage";
		httpclient.ShowProgressUI = this.ShowWindow;
		httpclient.AddForm("FormData", this.GetMessageString());
		this.ConsoleOut("<SendMessage> 开始发到服务器...");
		if (httpclient.Open(this.HttpMethod.Post, this.WebUrl, false)) // true 异步方式 false同步
		{
			if (httpclient.Send()) 
			{
				this.ConsoleOut("<SendMessage> 开始获取服务器上的数据");		
				this.GetDataToSend(); //得到服务器setMsgByName的值并发送到前台
				this.ConsoleOut("<SendMessage> 获取成功，发送到前台");	
				httpclient.Clear();
				this.Status = "接收数据成功";
				return true;
			} else {
				this.ConsoleOut("<SendMessage> 数据包发送失败");	
				this.Status = "数据包发送失败";
				return false;
			}
		}else{
			this.ConsoleOut("<SendMessage> 网络通信异常...");	
			this.Status = "网络通信异常";               //Status：状态信息
			return false;
		 }
		return this.ht.toString();
	}

	
	/* @deprecated:清除由WebSetMsgByName设置的值 */
	this.WebClearMessage = function() 
	{
		this.ht.Clear();
	}

	// 保存为PDF	// （该功能已完整实现）
	this.WebSavePDF = function() 
	{
		this.ConsoleOut("<WebSavePDF> 开始...");
		var httpclient = this.obj.Http; // 设置http对象
		httpclient.Clear();
		this.WebSetMsgByName("RECORDID", this.RecordID);
		this.WebSetMsgByName("OPTION", "SAVEPDF");
		this.WebSetMsgByName("FILENAME", this.RecordID + ".pdf");// 加载FileName
		this.ConsoleOut("<WebSavePDF> 开始保存PDF到本地...");
		this.SaveAsPdf(this.getFilePath() + this.RecordID + ".pdf");
		this.ConsoleOut("<WebSavePDF> 开始保存PDF到服务器...");
		if (this.SAVEFILE(httpclient, this.getFilePath() + this.RecordID
				+ ".pdf")) {
			this.ConsoleOut("<WebSavePDF> 保存保存PDF到服务器...");
			this.Status = "保存PDF文件成功";
			return true;
		} else {
			this.ConsoleOut("<WebSavePDF> 保存PDF失败...");
			this.Status = "保存PDF文件失败";
			return false;
		}
	}

	
	// 控件待实现 功能不完整待老姜提供接口
	this.WebSaveAsHtml = function() 
	{
/*		var httpclient = this.obj.Http; // 设置http对象
		httpclient.Clear();
		this.WebSetMsgByName("RECORDID", this.RecordID);
		this.WebSetMsgByName("OPTION", "SAVEHTML");
		this.WebSetMsgByName("FILENAME", this.RecordID + ".html");// 加载FileName
		if (this.SaveAsHtml(this.getFilePath() + this.RecordID + ".html")) {
			if (this.SAVEFILE(httpclient, this.getFilePath() + this.RecordID
					+ ".html")){
				this.Status = "保存HTML文件成功";
				return true;
			} else {
				this.Status = "保存HTML文件失败";
				return false;
			}
		}*/
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 保存定稿文档到服务器	   （未实现）
	this.WebUpdateFile = function() 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 打开版本保存窗口，录入版本信息，保存版本文件	（未实现）	
	this.WebSaveVersion = function() 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 不打开提示窗口保存版本文件  （未实现）
	this.WebSaveVersionByFileID = function() 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 打开显示版本窗口，显示版本列表，选择打开某个版本文件 （未实现）
	this.WebOpenVersion = function() 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	//保存模板文件   （该功能已完整实现）
    this.WebSaveTemplate = function()
    {
    	this.ConsoleOut("<WebSaveTemplate> 开始...");
    	this.Status = "";
    	var httpclient = this.obj.Http; //设置http对象 
    	httpclient.Clear();
    	this.WebSetMsgByName("OPTION", "SAVETEMPLATE");
    	this.WebSetMsgByName("TEMPLATE", this.Template);
    	this.ConsoleOut("<WebSaveTemplate> 开始将文档保存到本地...");
    	var mSaveResult = this.WebSaveLocalFile(this.getFilePath() + this.FileName);
    	if (!(mSaveResult == 0))
    	{
    		this.ConsoleOut("<WebSaveTemplate> 保存本地文档失败！错误代码为：" + mSaveResult);
    		this.Status = "保存本地文档失败！错误代码为：" + mSaveResult;
    		return false;
    	}
    	this.ConsoleOut("<WebSaveTemplate> 将文档保存到本地成功...");
    	
    	this.sendMode = "WebSaveTemplate";
    	this.ConsoleOut("<WebSaveTemplate> 开始将文档保存到服务器...");
    	if (this.SAVEFILE(httpclient, this.FilePath + this.FileName)) 
    	{
    		this.ConsoleOut("<WebSaveTemplate> 成功将文档保存到服务器...");
    		this.Status="保存模板文件成功";
    		return true;
    	}
    	else
    	{
    		//STATUS 由this.SAVEFILE返回
    		return false;        	
    	}
    }
    
    //调入由Template指定的模版，该功能主要用于模版管理或模板套红  （该功能已完整实现、已修改）
    this.WebLoadTemplate = function()
    {
    	this.ConsoleOut("<WebLoadTemplate> 开始...");	
    	var httpclient = this.obj.Http; //设置http对象
    	this.ClearRevisions() //清除正文痕迹的目的是为了避免痕迹状态下出现内容异常问题。
    	this.WebSetMsgByName("OPTION", "LOADTEMPLATE");
    	this.WebSetMsgByName("TEMPLATE", this.Template);
    	httpclient.AddForm("FormData", this.GetMessageString()); //这里是自定义json 传输格式。
    	this.WebClearMessage(); //清除所有WebSetMsgByName参数
    	this.ConsoleOut("<WebLoadTemplate> 将当前文档保存到本地...");	
    	var mSaveResult = this.WebSaveLocalFile(this.getFilePath() + this.iWebOfficeTempName);//将当前文档保存下来
    	if (!(mSaveResult == 0)){
    		this.ConsoleOut("<WebLoadTemplate> 保存本地文档失败！错误代码为：" + mSaveResult);
    		this.Status = "保存本地文档失败！错误代码为：" + mSaveResult;
    		this.ConsoleOut("<WebLoadTemplate> 结束...");	
    		return false;
    	}
    	this.ConsoleOut("<WebLoadTemplate> 开始打开套红文档...");	
    	if(this.LOADFILE(httpclient)){
    		this.ConsoleOut("<WebLoadTemplate> 打开套红文档成功...");	
    		this.ConsoleOut("<WebLoadTemplate> 开始插入正文...");
    		if(this.VBAInsertFile("Content",this.getFilePath() + this.iWebOfficeTempName)){
    			this.ConsoleOut("<WebLoadTemplate> 插入正文成功...");	
    			this.Status = "套红成功";
    			this.ConsoleOut("<WebLoadTemplate> 结束...");	
    			return true;
    		}else{
    			this.ConsoleOut("<WebLoadTemplate> 插入正文失败...");	
    			//this.Status = "套红失败"; //STATUS 由this.VBAInsertFile返回
    			return false;
    		}
    	}else{
    		this.ConsoleOut("<WebLoadTemplate> 打开套红文档失败...");	
			this.Status = "打开文档失败";               //Status：状态信息
			this.ConsoleOut("<WebLoadTemplate> 结束...");	
			return false;
    	}
    }

	// 将当前文档作为图片格式保存在服务器上  （未实现）
	this.WebSaveImage = function (ImageType, ImageName) 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}
    //  插入服务器上执行的文档   （该功能已完整实现、已做修改）
	this.WebInsertFile = function () 
    {
        	var httpclient = this.obj.Http;
    		this.ConsoleOut("<WebInsertFile> 开始...");
			this.WebSetMsgByName("TEMPLATE", this.Template); //在接口获取模板名称
			this.WebSetMsgByName("OPTION", "INSERTFILE");     //发送请求LOADFILE
			httpclient.AddForm("FormData", this.GetMessageString()); //这里是自定义json 传输格式。
			this.WebClearMessage();                         //清除所有WebSetMsgByName参数
			var URL = this.WebUrl.substring(0, this.WebUrl.lastIndexOf("/"));
			httpclient.ShowProgressUI = this.ShowWindow;//隐藏进度条
			this.ConsoleOut("<WebInsertFile> 开始打开连接...");
			if (httpclient.Open(this.HttpMethod.Post, URL + "/Document/" + this.Template, false)) 									//Http下载服务器文件
			{  
				if(httpclient.Send()){
	        		this.ConsoleOut("<WebInsertFile> 数据包发送成功...");
	        		this.ConsoleOut("<WebInsertFile> 判断接收数据是否异常...");
	        		if (httpclient.Status == 200) {
	        			this.ConsoleOut("<WebInsertFile> 开始将后台文件保存到本地...");
	        			if(this.hiddenSaveLocal(httpclient,this,false,false,this.Template)){
	        				this.ConsoleOut("<WebInsertFile> 后台文件保存到本地成功...");
	        				this.ConsoleOut("<WebInsertFile> 插入书签正文...")
	        				if(this.obj.ActiveDocument.BookMarks.Exists(this.BookMark)){
		        				if(this.VBAInsertFile(this.BookMark,this.DownloadedFileTempPathName)){
		        	    			this.ConsoleOut("<WebInsertFile> 插入正文成功...");	
		        	    			this.Status = "插入成功";               //Status：状态信息
		        	    			httpclient.Clear();
		        	    			this.ConsoleOut("<WebInsertFile> 结束...");	
		        	    			return true;
		        				}else{
		        	    			this.ConsoleOut("<WebInsertFile> 插入正文失败...");	
		        	    			//状态信息由 this.VBAInsertFILE返回
		        	    			this.ConsoleOut("<WebInsertFile> 结束...");
		        	    			return false;
		        				}
	        				}else{
	        					this.ConsoleOut("<WebInsertFile> 文档里没有对应书签...");
	        					this.Status = "文档里没有对应书签";
	        					this.ConsoleOut("<WebInsertFile> 结束...");
	        					return false;
	        				}
	        			}else{
	        				this.ConsoleOut("<WebInsertFile> 文档保存到本地失败...");
        					this.Status = "文档保存到本地失败";
        					this.ConsoleOut("<WebInsertFile> 结束...");
        					return false;
	        			}
	                }else{
	                	this.ConsoleOut("<WebInsertFile> 数据接收误...");
    					this.Status = "数据接收错误";
    					this.ConsoleOut("<WebInsertFile> 结束...");
    					return false;
	                }
	        	}else{
	        		this.ConsoleOut("<WebInsertFile> 数据包发送失败...");	
					this.Status = "数据包发送失败";               //Status：状态信息
					this.ConsoleOut("<WebInsertFile> 结束...");
					return false;
	        	}
    		}else{
					this.ConsoleOut("<WebInsertFile> 打开连接失败...");	
					this.Status = "打开连接失败";               //Status：状态信息
					this.ConsoleOut("<WebInsertFile> 结束...");
					return false;
				 }
    }

    //在指定的标签位置插入服务器上的图片，并决定是否做透明处理  （该功能已完整实现、已做修改）
    this.WebInsertImage = function(BookMark, ImageName) //插入远程图片：BookMark插入的书签的位置;ImageName远程图片的名称
    {
    	this.ConsoleOut("<WebInsertImage> 开始...");
    	var httpclient = this.obj.Http;
    	this.BookMark = BookMark;
    	this.ImageName = ImageName;
    	var URL = this.WebUrl.substring(0, this.WebUrl.lastIndexOf("/"));
    	httpclient.ShowProgressUI = this.ShowWindow;//隐藏进度条
    	this.ConsoleOut("<WebInsertImage> 开始打开连接...");
	    if(httpclient.Open(this.HttpMethod.Post, URL + "/Document/" + ImageName, false)){
	    	this.ConsoleOut("<WebInsertImage> 开始向后台发送数据包...");
        	if(httpclient.Send()){
        		this.ConsoleOut("<WebInsertImage> 数据包发送成功...");
        		this.ConsoleOut("<WebInsertImage> 判断接收数据是否异常...");
        		if (httpclient.Status == 200) {
        			this.ConsoleOut("<WebInsertImage> 开始将后台文件保存到本地...");
        			if(this.hiddenSaveLocal(httpclient,this,false,false,this.ImageName)){
        				this.ConsoleOut("<WebInsertImage> 后台文件保存到本地成功...");
        				this.ConsoleOut("<WebInsertImage> 运行书签插入图片...");
        				if(this.InsertImageByBookMark()){
        					this.ConsoleOut("<WebInsertImage> 本地内容加载到控件成功...");
        					this.ImageName = null;
        					this.BookMark = null;
        					httpclient.Clear();
        					return true;
        				}else{
        					this.ConsoleOut("<WebInsertImage> 书签插入图片失败...");
        					this.Status = "书签插入图片失败";
        					this.ConsoleOut("<WebInsertImage> 结束...");
        					return false;
        				}
        			}else{
        				this.ConsoleOut("<WebInsertImage> 文档保存到本地失败...");
    					this.Status = "文档保存到本地失败";
    					this.ConsoleOut("<WebInsertImage> 结束...");
    					return false;
        			}
                }else{
                	this.ConsoleOut("<WebInsertImage> 数据接收误...");
					this.Status = "数据接收错误";
					this.ConsoleOut("<WebInsertImage> 结束...");
					return false;
                }
        	}else{
        		this.ConsoleOut("<WebInsertImage> 发送数据包失败...");
        		this.Status = "发送数据包失败";               //Status：状态信息
    			this.ConsoleOut("<WebInsertImage> 结束...");
    			return false;
        	}
        }else{
        	this.ConsoleOut("<WebInsertImage> 打开连接失败...");	
			this.Status = "打开连接失败";               //Status：状态信息
			this.ConsoleOut("<WebInsertImage> 结束...");
			return false;
        }
    }

	// 保存文档中所有的书签名称。该功能主要把模板中所使用的书签都保存到数据库里  （该功能已完整实现）
	this.WebSaveBookMarks = function() 
	{
		this.ConsoleOut("<WebSaveBookMarks> 开始...");
		var httpclient = this.obj.Http;
    	httpclient.Clear();
    	var BKCount = this.obj.ActiveDocument.Bookmarks.Count; //获取书签数量
    	for(var i = 1; i <= BKCount; i++){
    		var BookName = this.obj.ActiveDocument.Bookmarks.Item(i).Name;
    		var	BookValue =this.obj.ActiveDocument.Bookmarks.Item(BookName).Range.text;
    		this.WebSetMsgByName(BookName,BookValue); //存入书签名和值
    	}
    	this.WebSetMsgByName("OPTION", "SAVEBOOKMARKS"); 
    	this.WebSetMsgByName("TEMPLATE", this.RecordID); //在接口获取模板名称
    	httpclient.AddForm("FormData", this.GetMessageString()); //这里是自定义json 传输格式。
    	this.WebClearMessage();
    	httpclient.ShowProgressUI = this.ShowWindow;//隐藏进度条
    	this.ConsoleOut("<WebSaveBookMarks> 开始打开连接...");
        if(httpclient.Open(this.HttpMethod.Post, this.WebUrl, false)){
        	this.ConsoleOut("<WebSaveBookMarks> 开始向后台发送数据包...");
        	if(httpclient.Send()){
        		this.ConsoleOut("<WebSaveBookMarks> 数据包发送成功...");
        		this.Status = "书签保存成功";
        		this.ConsoleOut("<WebSaveBookMarks> 结束...");
        		httpclient.Clear();
        		return true;
        	}else{
        		this.ConsoleOut("<WebSaveBookMarks> 数据包发送失败...");
        		this.Status = "数据包发送失败";
        		this.ConsoleOut("<WebSaveBookMarks> 结束...");
        		httpclient.Clear();
        		return false;
        	}
        }else{
        	this.ConsoleOut("<WebSaveBookMarks> 打开连接失败...");
        	this.Status = "打开连接失败";
    		this.ConsoleOut("<WebSaveBookMarks> 结束...");
    		httpclient.Clear();
    		return false;
        }
	}

	// 打开书签管理窗口，显示书签列表，可以用于添加、删除和定位书签	（该功能已完整实现）
	this.WebOpenBookMarks = function() 
	{
		var FunctionString = "ActiveObject.ActiveDocument.Application.Dialogs(168).Show()";
		this.blnIE() ? this.obj.ActiveDocument.Application.Dialogs(168).Show()
				: this.ExecuteScript("WebPageCode", FunctionString);
	}

	// 取数据库内容填充文档中书签的值	（该功能已完整实现）
	this.WebLoadBookMarks = function() {
		this.ConsoleOut("<WebLoadBookMarks> 开始...");
		var httpclient = this.obj.Http;
    	httpclient.Clear();
    	this.WebSetMsgByName("RECORDID", this.RecordID); //在接口获取模板名称
    	this.WebSetMsgByName("TEMPLATE", this.Template); //在接口获取模板名称
    	this.WebSetMsgByName("FILENAME", this.FileName); //在接口获取模板名称
    	this.WebSetMsgByName("FILETYPE", this.FileType); //在接口获取模板名称
    	this.WebSetMsgByName("OPTION", "LOADBOOKMARKS"); //在接口获取模板名称
    	httpclient.AddForm("FormData", this.GetMessageString()); //这里是自定义json 传输格式。
        this.WebClearMessage();                         //清除所有WebSetMsgByName参数
        this.sendMode = "WebLoadBookMarks";
        httpclient.ShowProgressUI = this.ShowWindow;//隐藏进度条
        this.ConsoleOut("<WebLoadBookMarks> 开始打开连接...");
        if(httpclient.Open(this.HttpMethod.Post, this.WebUrl, false)){
        	this.ConsoleOut("<WebLoadBookMarks> 开始向后台发送数据包...");
        	if(httpclient.Send()){
        		this.ConsoleOut("<WebLoadBookMarks> 获取书签内容插入到书签...");
        		var ReturnValue = httpclient.GetResponseHeader("RName");// 获取返回值
        		var jsonObj = eval('(' + ReturnValue + ')'); 
        		try {
        			for(var i  in jsonObj){
        				var BookName = i;
        				var BookValue = jsonObj[i];
        				if(this.obj.ActiveDocument.BookMarks.Exists(BookName)){
        					var mBookRange = this.obj.ActiveDocument.Bookmarks.Item(BookName).Range;
        					mBookRange.text = BookValue;
        				}else{
        					this.ConsoleOut("<WebLoadBookMarks> 没有找到" + BookName + "书签...");
        					this.Status = "没有找到" + BookName + "书签";
        					return false;
        				}
        			}
        		} catch (e) {
        			this.ConsoleOut("<WebLoadBookMarks> 插入书签出现异常" + e.description);
        			this.Status = e.description;
        			return false;
        		}
        		this.ConsoleOut("<WebLoadBookMarks> 书签内容已插入到书签...");
        		this.Status = "书签内容已插入到书签";
                return true;
        	}else{
        		this.ConsoleOut("<WebLoadBookMarks> 向后台发送数据包错误...");
        		this.Status = "向后台发送数据包错误";
        		return false;
        	}
        }else{
        	this.ConsoleOut("<WebLoadBookMarks> 打开链接错误...");
        	this.Status = "打开链接错误";
    		return false;
        }
	}

	// 打开签名窗口，允许用户输入密码，来获取已经保存起来的印章或签名信息。确定签名后的信息将保存在服务器的数据库中以便将来验证使用  （需要控件修改代码）
	this.WebOpenSignature = function() 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 不打开签名或印章检验窗口，检验签名或印章是否合法有效	（需要控件修改代码）
	this.WebCheckSignature = function() 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 打开签名或印章检验窗口，检验签名或印章是否合法有效	    （需要修改控件代码）
	this.WebShowSignature = function() 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

    //下载服务器上的文件并保存在本地	（该功能已完整实现、已做修改）
    this.WebGetFile = function(LocalFile, RemoteFile)
    {
    	this.ConsoleOut("<WebGetFile> 开始...");
    	var httpclient = this.obj.Http; //设置http对象
		httpclient.Clear();
		this.WebSetMsgByName("RECORDID", this.RecordID); //加载RecordID
		this.WebSetMsgByName("LOCALFILE", LocalFile); //取得本地文件名称
		this.WebSetMsgByName("REMOTEFILE", RemoteFile); //取得远程文件名称
		this.WebSetMsgByName("OPTION", "GETFILE");     //发送请求LOADFILE
		httpclient.AddForm("FormData", this.GetMessageString()); //这里是自定义json 传输格式。
		this.WebClearMessage();                         //清除所有WebSetMsgByName参数
		 httpclient.ShowProgressUI = true;   //隐藏进度条
		 this.ConsoleOut("<WebGetFile> 开始打开链接...");
		 if (httpclient.Open(this.HttpMethod.Post, this.WebUrl, false)) {//这里采用同步方式打开文档。我要返回值
			 this.ConsoleOut("<WebGetFile> 开始向后台发送数据包...");
			 if (httpclient.Send()) {
				 this.ConsoleOut("<WebGetFile> 数据包发送成功...");
                 if (httpclient.GetResponseHeader("MsgError") == "404") { //判断服务器是否存在文件
                	 this.ConsoleOut("<WebGetFile> 后台未找到对应文档");
                	 this.Status = "后台未找到对应文档"; 
                	 httpclient.Clear();
                	 this.ConsoleOut("<WebGetFile> 结束...");
                	 return false;
                 }
                 httpclient.Clear();
                 var fs = this.obj.FileSystem;
                 this.ConsoleOut("<WebGetFile> 创建本地路径");
                 fs.CreateDirectory(LocalFile);
                 this.ConsoleOut("<WebGetFile> 文档下载到本地");
                 httpclient.ResponseSaveToFile(LocalFile);
                 this.ConsoleOut("<WebGetFile> 文档下载成功");
                 this.Status = "文档下载到本地成功";
                 httpclient.Clear();
                 this.ConsoleOut("<WebGetFile> 结束...");
                 return true;
             }else{
            	 this.ConsoleOut("<WebGetFile> 数据包发送失败...");
            	 this.Status = "数据包发送失败";               //Status：状态信息
            	 this.ConsoleOut("<WebGetFile> 结束...");
            	 return false;
             }
         }else{
        	 this.ConsoleOut("<WebGetFile> 打开连接失败...");	
        	 this.Status = "打开连接失败";               //Status：状态信息
        	 httpclient.Clear();
        	 this.ConsoleOut("<WebGetFile> 结束...");
        	 return false;
         }
    }
    
    //将本地文件上传到服务器上，并保存为远程文件  （该功能已完整实现）
    //(httpclient.AddFile无法判断本地文档是否存在，导致上传时找不到文档情况下会生成空白文档)
    this.WebPutFile = function(LocalFile, RemoteFile)
    {
    	this.ConsoleOut("<WebPutFile> 开始...");
    	var httpclient = this.obj.Http; //设置http对象 
        httpclient.Clear();   
		this.WebSetMsgByName("REMOTEFILE", RemoteFile); //取得远程文件名称
		this.WebSetMsgByName("OPTION", "PUTFILE");     //发送请求LOADFILE
		httpclient.AddForm("FormData", this.GetMessageString());
		httpclient.AddFile("FileData", LocalFile);    //需要上传的文件
		this.WebClearMessage();
		httpclient.ShowProgressUI = false;           //隐藏进度条
		this.ConsoleOut("<WebPutFile> 开始打开链接...");
		if (httpclient.Open(this.HttpMethod.Post, this.WebUrl, false)) ////true 异步方式 false同步
		{
			this.ConsoleOut("<WebPutFile> 开始向后台发送数据包...");
			if (!httpclient.Send()) {
				this.ConsoleOut("<WebPutFile> 数据包发送失败...");
				this.Status = "文档上传失败";
				httpclient.Clear();
				this.ConsoleOut("<WebPutFile> 结束...");
				return false;
			}else{    
				this.ConsoleOut("<WebPutFile> 文档上传成功...");
				this.Status = "文档上传成功";
				httpclient.Clear();
				this.ConsoleOut("<WebPutFile> 结束...");
				return true;
			}
		}else{
			this.ConsoleOut("<WebPutFile> 打开链接失败...");
			this.Status = "打开链接失败";
			httpclient.Clear();
			this.ConsoleOut("<WebGetFile> 结束...");
			return false;
		}
    }
    
    //删除指定的本地文件或服务器上的文件	   （该功能已完整实现）
    this.WebDelFile = function(LocalFile, RemoteFile)
    {
    	this.ConsoleOut("<WebDelFile> 开始...");
    	if(LocalFile == ""){
    		if(RemoteFile != ""){
    			this.ConsoleOut("<WebDelFile> 开始删除远程文档...");
    			var httpclient = this.obj.Http; //设置http对象 
    	        httpclient.Clear();   
    			this.WebSetMsgByName("REMOTEFILE", RemoteFile); //取得远程文件名称
    			this.WebSetMsgByName("OPTION", "DELFILE");     //发送请求LOADFILE
    			httpclient.AddForm("FormData", this.GetMessageString());
    			this.WebClearMessage();
    			httpclient.ShowProgressUI = false;           //隐藏进度条
    			this.ConsoleOut("<WebDelFile> 开始打开链接...");
    			if (httpclient.Open(this.HttpMethod.Post, this.WebUrl, false)) ////true 异步方式 false同步
    			{
    				this.ConsoleOut("<WebDelFile> 连接成功，开始向后台发送数据包...");
    				if (!httpclient.Send()) {
    					this.ConsoleOut("<WebDelFile> 向后台发送数据包失败...");
    					this.Status = "向后台发送数据包失败";
    					httpclient.Clear();
    					this.ConsoleOut("<WebDelFile> 结束...");
    					return false;
    				}else{
    					this.ConsoleOut("<WebDelFile> 连接数据包成功...");
    					if(this.WebGetMsgByName("DelFileState") == "成功" ){
    						this.ConsoleOut("<WebDelFile> 文档删除成功...");
    						this.Status = "文档删除成功";
    						httpclient.Clear();
    						this.ConsoleOut("<WebDelFile> 结束...");
    						return true;
    					}else{
    						this.ConsoleOut("<WebDelFile> 文档不存在...");
    						this.Status = "文档不存在";
    						httpclient.Clear();
    						this.ConsoleOut("<WebDelFile> 结束...");
    						return false;
    					}
    				}
    			}else{
    				this.ConsoleOut("<WebDelFile> 打开链接失败...");
    				this.Status = "打开链接失败";
    				this.ConsoleOut("<WebDelFile> 结束...");
    				return false;
    			}     
    		}else{
    			this.ConsoleOut("<WebDelFile> 文件名为空，请输入要删除的文件名...");
    			this.Status = "文件名为空，请输入要删除的文件名";
    			this.ConsoleOut("<WebDelFile> 结束...");
    			return false;
    		}
	    }else{
	    	var fs = this.obj.FileSystem;
	    	this.ConsoleOut("<WebDelFile> 删除本地文档...");
	    	if(fs.DeleteFile(LocalFile)){
	    		this.ConsoleOut("<WebDelFile> 删除本地文档成功...");
	    		this.Status = "删除本地文件成功";
	    		this.ConsoleOut("<WebDelFile> 结束...");
	    		return true;
	    	}else{
	    		this.ConsoleOut("<WebDelFile> 该文档不存在或者没有权限删除...");
	    		this.Status = "该文档不存在或者没有权限删除";
	    		this.ConsoleOut("<WebDelFile> 结束...");
	    		return false;
	    	}
	    }
    }

	// 取得或设置本地时间为服务器时间 true: 表示取得服务器时间并修改本机时间 false: 表示取得服务器时间并不修改本机时间  （未实现）
	this.WebDateTime = function(LocalTime) 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 建立新文件	（该功能已完整实现）
	this.CreateFile = function() 
	{
		var docType = this.getDocType(this.FileType); // 获取文档类型
		switch (docType) {
		case this.DocType.WORD:
			this.obj.CreateNew("Word.Document"); // 创建word
			break;
		case this.DocType.EXECL:
			this.obj.CreateNew("Excel.Sheet"); // 创建execl
			break;
		case this.DocType.PICTURE:
			this.obj.CreateNew("iWebPicture.iWebPictureMain"); // 创建
			break;
		default:
			this.obj.CreateNew("Word.Document"); // 默认创建word文档
			break;
		}
	}

	// 使当前编辑区全屏显示	   		（该功能已完整实现）
	this.FullSize = function(mValue) 
	{
		this.obj.FullSize = mValue;
	}

	// 文档保存到本地（有对话框）		（该功能已完整实现）
	this.WebSaveLocal = function() 
	{
		this.ConsoleOut("<WebSaveLocal> 开始...");
		this.setShowDialog(this.ShowDialog.DialogSaveCopyAs);
		this.ConsoleOut("<WebSaveLocal> 文档保存到本地成功...");
	}

	// 打开本地文档（有对话框）		（该功能已完整实现）
	this.WebOpenLocal = function() 
	{
		this.ConsoleOut("<WebOpenLocal> 开始...");
		this.setShowDialog(this.ShowDialog.DialogOpen, this
				.getOpenDocSuffix(this.FileType));
		this.ConsoleOut("<WebOpenLocal> 打开本地文件成功...");
	}

	// 显示保存到本地对话框			（未实现）
	this.WebSaveLocalDialog = function() 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 显示打开本地文档对话框		（未实现）
	this.WebOpenLocalDialog = function() 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 文档保存到本地			（该功能已完整实现）
	this.WebSaveLocalFile = function(FileName) 
	{
		this.ConsoleOut("<WebSaveLocalFile> 开始...");
		try {
			this.ConsoleOut("<WebSaveLocalFile> 开始删除之前的文件...");
			this.WebDelLocalFile(FileName);
			this.ConsoleOut("<WebSaveLocalFile> 删除之前的文档成功...");
			return this.Save(FileName, this.getOfficeVersion(), this.FileType
					.substring(1).toUpperCase());
			this.ConsoleOut("<WebSaveLocalFile> 保存本地文件成功...");
		} catch (e) {
			this.Status = e.description;
			this.ConsoleOut("<WebSaveLocalFile> 保存本地文件失败..."+e.description);
			return -1;
		}
	}

	// 打开本地文档				（该功能已完整实现）
	this.WebOpenLocalFile = function(filePath) 
	{
		this.ConsoleOut("<WebOpenLocalFile> 开始...");
		if (this.getDocType(this.FileType) == this.DocType.PICTURE) {
			try {
				this.obj.ActiveDocument.WebOpenLocalFile(filePath);
				return 0;
			} catch (e) {
				return -1;
			}
		}
		return this.obj.Open(filePath);
		this.ConsoleOut("<WebOpenLocalFile> 打开本地文件成功...");
	}

	// 取得书签的值				（该功能已完整实现）
	this.WebGetBookMarks = function(BMarksName) 
	{
		this.ConsoleOut("<WebGetBookMarks> 开始...");
		this.ConsoleOut("<WebGetBookMarks> 判断书签是否存在...");
		if (this.obj.ActiveDocument.BookMarks.Exists(BMarksName)) // 判断是否存在该书签
		{
			return this.obj.ActiveDocument.Bookmarks.Item(BMarksName).Range.Text;
			this.ConsoleOut("<WebGetBookMarks> 获取书签的值成功...");
		} else {
			this.ConsoleOut("<WebGetBookMarks> "+"名称为<" + BMarksName + ">的书签在文档中不存在");
			this.Status = "名称为<" + BMarksName + ">的书签在文档中不存在";
			return "";
		}
	}

	// 光标处添加书签(新增功能)			（该功能已完整实现）
	this.WebAddBookMarks = function(BMarksName, BMarksValue) 
	{
		this.ConsoleOut("<WebAddBookMarks> 开始...");
		this.ConsoleOut("<WebAddBookMarks> 判断书签是否存在...");
		if (!this.obj.ActiveDocument.BookMarks.Exists(BMarksName)) // 判断是否存在该书签
		{
			this.ConsoleOut("<WebAddBookMarks> 获取书签的长度...");
			var BMVLength = BMarksValue.length;
			this.ConsoleOut("<WebAddBookMarks> 插入内容...");
			this.obj.ActiveDocument.Application.Selection.TypeText(BMarksValue);// 插入内容
			this.obj.ActiveDocument.Application.Selection.MoveLeft(Unit = 1,
					Count = BMVLength);
			this.ConsoleOut("<WebAddBookMarks> 获取书签的原始位置...");
			var StartR = this.obj.ActiveDocument.Application.Selection.Start;
			this.ConsoleOut("<WebAddBookMarks> 获取书签的最终位置...");
			var EndR = this.obj.ActiveDocument.Application.Selection.Start
					+ BMVLength;
			this.obj.ActiveDocument.Range(Start = StartR, End = EndR).Select();
			this.ConsoleOut("<WebAddBookMarks> 添加书签...");
			this.obj.ActiveDocument.Bookmarks.Add(BMarksName);// 添加书签
			this.ConsoleOut("<WebAddBookMarks> 结束...");
			return true;
		} else {
			this.ConsoleOut("<WebAddBookMarks> "+"名称为<" + BMarksName + ">的书签在文档中不存在");
			this.Status = "名称为<" + BMarksName + ">的书签在文档中已存在";
			this.WebFindBookMarks(BMarksName);
			return false;
		}
	}

	// 书签定位			（该功能已完整实现）
	this.WebFindBookMarks = function(BMarksName) 
	{
		this.ConsoleOut("<WebFindBookMarks> 开始...");
		this.ConsoleOut("<WebFindBookMarks> 判断书签是否存在...");
		if (this.obj.ActiveDocument.BookMarks.Exists(BMarksName)) // 判断是否存在该书签
		{
			var range = this.obj.Range;
			range = this.obj.ActiveDocument.Bookmarks(BMarksName).Range;
			range.Select();
			this.ConsoleOut("<WebFindBookMarks> 书签定位成功...");
			return true;
		} else {
			this.ConsoleOut("<WebFindBookMarks> "+"名称为<" + BMarksName + ">的书签在文档中不存在");
			this.Status = "名称为<" + BMarksName + ">的书签在文档中不存在";
			return false;
		}
	}

	// 删除书签(新增功能)			（该功能已完整实现）
	this.WebDelBookMarks = function(BMarksName) 
	{
		this.ConsoleOut("<WebDelBookMarks> 开始...");
		this.ConsoleOut("<WebDelBookMarks> 判断书签是否存在...");
		if (this.obj.ActiveDocument.BookMarks.Exists(BMarksName)) // 判断是否存在该书签
		{
			this.ConsoleOut("<WebDelBookMarks> 开始删除书签...");
			this.obj.ActiveDocument.Bookmarks(BMarksName).Delete();// 删除书签
			this.ConsoleOut("<WebDelBookMarks> 删除书签成功...");
			return true;
		} else {
			this.ConsoleOut("<WebDelBookMarks> "+"名称为<" + BMarksName + ">的书签在文档中不存在");
			this.Status = "名称为<" + BMarksName + ">的书签在文档中不存在";
			return false;
		}
	}

	// 将信息设置到书签位置				（该功能已完整实现）
	this.WebSetBookmarks = function(vbmName, vbmValue) 
	{
		this.ConsoleOut("<WebSetBookmarks> 开始...");
		try {
			this.ConsoleOut("<WebSetBookmarks> 判断书签是否存在...");
			if (this.obj.ActiveDocument.BookMarks.Exists(vbmName)) {// 判断是否存在该书签
				var vRange = this.obj.ActiveDocument.Bookmarks.Item(vbmName).Range;
				vRange.text = vbmValue;
				this.obj.ActiveDocument.Bookmarks.Add(vbmName, vRange);
				this.ConsoleOut("<WebSetBookmarks> 书签赋值成功...");
			}
		} catch (e) {
			this.Status = "名称为<" + BMarksName + ">的书签在文档中不存在";
			return false;
		}
	}

	// 设置显示或隐藏工具栏				（需要修改控件代码）
	this.WebToolsVisible = function(ToolName, Visibled) 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 设置工具栏按钮是否有效			（需要修改控件代码）
	this.WebToolsEnable = function(ToolName, ToolIndex, Enabled) 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 打印文档					
	this.WebOpenPrint = function() 
	{
		this.setShowDialog(this.ShowDialog.DialogPrint);
	}

	// 打开插入图片窗口				（未实现）
	this.WebOpenPicture = function() 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 使文档重新获得焦点刷新文档			（该功能已完整实现）
	this.WebRefresh = function() 
	{
		this.obj.Activate(true);
	}

	// 设置文档痕迹保留的状态		（该功能已完整实现）
	this.WebSetRevision = function(Show, Track, Print, Tool) 
	{
		this.ConsoleOut("<WebSetRevision> 开始...");
		  var strCustomUI;
		    strCustomUI = '<customUI xmlns="http://schemas.microsoft.com/office/2006/01/customui" onLoad="OnLoad" loadImage="LoadImage"> '+
				 ' <ribbon startFromScratch="false"> '+
	             '   <tabs>'+
	              '      <tab idMso="TabReviewWord" visible="false">'+    //关闭审阅工具蓝
	             '     </tab> '+
	            '    </tabs> '+
	            '  </ribbon> '+
	           ' </customUI>';
		    this.ConsoleOut("<WebSetRevision> 判断是不是Word格式...");    
		if (this.getDocType(this.FileType) == this.DocType.WORD
				&& this.obj.ActiveDocument.ProtectionType == "-1") {
			this.obj.ActiveDocument.TrackRevisions = Track; // 显示标记和隐藏标记
			this.ConsoleOut("<WebSetRevision> 设置标记成功...");
			this.obj.ActiveDocument.ShowRevisions = Show; // 显示痕迹或隐藏
			this.ConsoleOut("<WebSetRevision> 设置痕迹成功...");
			this.obj.ActiveDocument.PrintRevisions = Print; //控制是否打印痕迹
			this.ConsoleOut("<WebSetRevision> 设置打印痕迹成功...");
			if(Tool){
				this.obj.RibbonCustomUI =	strCustomUI;
				this.ConsoleOut("<WebSetRevision> 设置审阅工具栏成功...");
				this.Status = "设置痕迹成功";
				return true;
			}else{
				this.ConsoleOut("<WebSetRevision> 设置审阅工具栏成功...");
				this.Status = "设置痕迹成功";
				return true;
			}
			
		}else{
			this.ConsoleOut("<WebSetRevision> 非word文档，无法执行。。。 ");
			this.Status = "非word文档，无法执行";
			return false;
		}

	}

	// 使文档保护状态		（该功能已完整实现）
	this.WebSetProtect = function(Boolean, password) 
	{
		var docType = this.getDocType(this.FileType);
		if (password == "") {
			password = this.PASSWORD;
		}
		Boolean ? this.VBAProtectDocument(docType, password) : this
				.VBAUnProtectDocument(docType, password);
	}



	// 下载由Url指定的文件，保存为FileName路径的本地文件	（该功能已完整实现）
	this.WebDownLoadFile = function(Url,FileName) 
	{
		var kwoHttpGet = 0;
		var kwoHttpPost = 1;
		var colse_begin = new Date().getTime();
		var httpclient = WebOffice.WebObject.Http;
		httpclient.Clear();
		httpclient.ShowProgressUI = true;
		httpclient.Hidden = false;
		// 异步下载先调用OnSendEnd(),再调用OnRecEnd()
		this.ConsoleOut("<WebDownLoadFile> 开始...");
		this.ConsoleOut("<WebDownLoadFile> 开始打开服务器...");
		var info = httpclient.Open(kwoHttpGet,Url, false);
		if (info) {
			var send = httpclient.Send();
			if (send) {
				if (httpclient.Status == 200) {
					httpclient.ResponseSaveToFile(FileName);
					this.ConsoleOut("<WebDownLoadFile>下载成功");
					this.Status = "下载成功";
					return true;
				}else{
					this.ConsoleOut("<WebDownLoadFile>下载失败");
					this.Status = "下载失败请检查URL是否正确";
					return false;
				}
			}else{
	           	 this.ConsoleOut("<WebDownLoadFile> 数据包发送失败...");
	        	 this.Status = "数据包发送失败";               // Status：状态信息
	        	 this.ConsoleOut("<WebDownLoadFile> 结束...");
	        	 return false;
			}
		}else{
	       	 this.ConsoleOut("<WebDownLoadFile> 打开连接失败...");	
	    	 this.Status = "打开连接失败";               // Status：状态信息
	    	 httpclient.Clear();
	    	 this.ConsoleOut("<WebDownLoadFile> 结束...");
	    	 return false;
		}
	}



	// 建立本地目录		（该功能已完整实现）
	this.WebMkDirectory = function(DirName) 
	{
		this.ConsoleOut("<WebMkDirectory> 开始建立本地目录...");
		var fs = this.obj.FileSystem; // 创建file对象：
		if(fs.CreateDirectory(DirName)){
			this.ConsoleOut("<WebMkDirectory> 建立本地目录成功...");
			this.Status = "创建目录成功";
			return true;
		}else{
			this.ConsoleOut("<WebMkDirectory> 建立本地目录失败...");
			this.Status = "创建目录失败检查是否已经有此目录或者路径不对";
			return false;
		}
	}

	// 删除本地目录		（该功能未完全实现）
	this.WebRmDirectory = function(DirName) 
	{
		this.ConsoleOut("<WebRmDirectory> 删除本地目录...");
		var fs = this.obj.FileSystem; // 创建file对象：
		fs.ClearDirectory (DirName);  //此接口无返回值
		this.ConsoleOut("<WebRmDirectory> 删除本地目录成功...");
		this.Status="删除目录成功";
		return true;
/*		if(fs.ClearDirectory(DirName)){
			this.Status="删除目录成功";
		}else{
			this.Status="删除目录失败";
		}*/
	}

	// 判断本地文件是否存在		(需要控件修改代码)
	this.WebFileExists = function(FileName) 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 得到某文件的大小
	this.WebFileSize = function(FileName) 
	{
		this.ConsoleOut("<WebFileSize> 得到某文件的大小...");
		var fs = this.obj.FileSystem; // 创建file对象：
		var fsize = fs.GetFileSize(FileName);
	    if(fsize == 0){
	    	this.ConsoleOut("<WebFileSize> 获取文件失败，请检查对应路径...");
	    	this.Status = "请检查文件路径是否有误";
	    	return 0;
	    }else{
	    	this.ConsoleOut("<WebFileSize> 获取文件成功大小："+fsize+"字节");
	    	this.Status = "获取成功大小为"+fsize+"字节";
	    	return fsize;
	    }
	}
	

	// 得到某文件的修改日期		（需要控件修改代码）
	this.WebFileDate = function(FileName) 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 禁止指定菜单项		（需要控件修改代码）
	this.DisableMenu = function(MenuName) 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 允许菜单项有效		（需要控件修改代码）
	this.EnableMenu = function(MenuName) 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 禁止指定快捷键的功能		（需要控件修改代码）
	this.DisableKey = function(KeyName) 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	 //建立注册表对象 （已完全实现）
    this.WebOpenKey = function(mRoot, mValue)
    {
    	this.ConsoleOut("<WebOpenKey> 开始...");
    	root = mRoot;
    	rootValue = mValue;
    	var register = this.obj.Register;
    	this.ConsoleOut("<WebOpenKey> 建立注册表对象...");
    	var res = register.QueryStringValue(root, rootValue, "");
    	if(!res){
    		this.ConsoleOut("<WebOpenKey> 建立注册对象失败，注册表对象不存在，开始新建注册表对象...");
    		register.SetStringValue(root, rootValue, "", "");
    		this.ConsoleOut("<WebOpenKey> 新建注册表对象成功...");
    		this.Status = "建立注册对象失败，已新建注册表对象";
    		this.ConsoleOut("<WebOpenKey> 结束...");
    		return res;
    	}
    	this.ConsoleOut("<WebOpenKey> 建立注册对象成功...");
    	this.Status = "建立注册对象成功";
    	this.ConsoleOut("<WebOpenKey> 结束...");
    	return true;
    }
    
    //写入由Name指定的注册表项的值Value（字符串类型的值）（已完全实现）
    this.WebWriteString = function(mKey, mValue)
    {
    	this.ConsoleOut("<WebWriteString> 开始...");
    	var register = this.obj.Register;
    	if(root != undefined && rootValue != undefined){
    		this.ConsoleOut("<WebWriteString> 开始写入注册表项的值...");
    		var res = register.SetStringValue(root, rootValue, mKey, mValue);
    		this.ConsoleOut("<WebWriteString> 写入注册表成功...");
    		this.Status = "写入注册表成功";
    		this.ConsoleOut("<WebWriteString> 结束...");
    		return res;
    	}
    	this.ConsoleOut("<WebWriteString> 写入注册表失败，请先调用WebOpenKey接口...");
    	this.Status = "写入注册表失败，请先调用WebOpenKey接口";
    	this.ConsoleOut("<WebWriteString> 结束...");
    	return false;
    }
    
    //读取由Name指定的注册表项的值（字符串类型的值）（已完全实现）
    this.WebReadString = function(mKey)
    {
    	this.ConsoleOut("<WebReadString> 开始...");
    	var register = this.obj.Register;
    	if(root != undefined && rootValue != undefined){
    		this.ConsoleOut("<WebReadString> 开始读取由注册表项的值...");
    		var res = register.QueryStringValue(root, rootValue, mKey);
    		if(res != null || res != ""){
    			this.ConsoleOut("<WebReadString> 读取注册表成功...");
    			this.Status = "读取注册表成功";
    			this.ConsoleOut("<WebReadString> 结束...");
    			return res;
    		}else{
    			this.ConsoleOut("<WebReadString> 读取失败，没有对应的注册表...");
    			this.Status = "读取失败，没有对应的注册表";
    			this.ConsoleOut("<WebReadString> 结束...");
    			return res;
    		}
    	}
    	this.ConsoleOut("<WebReadString> 写入注册表失败，请先调用WebOpenKey接口...");
    	this.Status = "读取注册表失败，请先调用WebOpenKey接口";
    	this.ConsoleOut("<WebReadString> 结束...");
    	return false;
    }

    
     //写入由Name指定的注册表项的值Value（已完全实现）
    this.WebWriteInteger = function(mKey, mValue)
    {
    	this.ConsoleOut("<WebWriteInteger> 开始...");
    	var register = this.obj.Register;
    	if(root != undefined && rootValue != undefined){
    		this.ConsoleOut("<WebWriteInteger> 开始写入注册表项的值...");
    		var res = register.SetDWordValue(root, rootValue, mKey, mValue);
    		this.ConsoleOut("<WebWriteInteger> 写入注册表成功...");
    		this.Status = "写入注册表成功";
    		this.ConsoleOut("<WebWriteInteger> 结束...");
    		return res;
    	}
    	this.ConsoleOut("<WebWriteInteger> 写入注册表失败，请先调用WebOpenKey接口...");
    	this.Status = "写入注册表失败，请先调用WebOpenKey接口";
    	this.ConsoleOut("<WebWriteInteger> 结束...");
    	return false;
    }
    
    //读取由Name指定的注册表项的值Value（已完全实现）
    this.WebReadInteger = function(mKey)
    {
    	this.ConsoleOut("<WebReadInteger> 开始...");
    	var register = this.obj.Register;
    	if(root != undefined && rootValue != undefined){
    		this.ConsoleOut("<WebReadInteger> 开始读取注册表项的值...");
    		var res = register.QueryDWORDValue(root, rootValue, mKey);
    		if(res != null || res != ""){
    			this.ConsoleOut("<WebReadInteger> 读取注册表成功...");
    			this.Status = "读取注册表成功";
    			this.ConsoleOut("<WebReadInteger> 结束...");
    			return res;
    		}else{
    			this.ConsoleOut("<WebReadInteger> 读取失败，没有对应的注册表...");
    			this.Status = "读取失败，没有对应的注册表";
    			this.ConsoleOut("<WebReadInteger> 结束...");
    			return res;
    		}
    	}
    	this.ConsoleOut("<WebReadInteger> 读取注册表失败，请先调用WebOpenKey接口...");
    	this.Status = "读取注册表失败，请先调用WebOpenKey接口";
    	this.ConsoleOut("<WebReadInteger> 结束...");
    	return false;
    }
    
    //关闭注册表操作对象（该功能未完全实现）
    this.WebCloseKey = function()
    {
    	this.ConsoleOut("<WebReadInteger> 开始关闭注册表作对象...");
    	root = undefined;
    	rootValue =undefined;
    	this.ConsoleOut("<WebReadInteger> 关闭注册表对象结束...");
    	return true;
    }


	// 盖章时自动载入日期信息附加在所盖的印章上	（需要控件修改代码）
	this.ShowDate = function(DateString, Align) 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 查看某个文档的编辑器是否存在		（未实现）
	this.WebApplication = function(FileType) 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 将文档中的印章变为黑白或彩色		（需要控件修改代码）
	this.SignatureColor = function(Flag) 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 返回当前文档中的印章或签名个数		（需要控件修改代码）
	this.SignatureCount = function(Flag) 
	{
		this.Status = "该功能暂未提供!";
		return 0;
	}

	// 用来定位某用户的最后一个印章。光标定位到该印章上	（需要控件修改代码）
	this.GetSignature = function(UserName) 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	 //接受当前文档中所有的痕迹	（该功能已完全实现）
    this.ClearRevisions = function()
    {
    	this.ConsoleOut("<ClearRevisions> 接受当前文档中所有的痕迹...");
    	try
    	{
    		this.obj.Activate(true);
    		this.ConsoleOut("<ClearRevisions> 激活当前文档焦点成功...");
    		this.obj.ActiveDocument.Revisions.AcceptAll();
    		this.ConsoleOut("<ClearRevisions> 接受痕迹成功...");
    		this.Status = "痕迹接受成功";
    		return this.obj.ActiveDocument.Revisions.Count >= 0 ? true : false;
    	} 
    	catch(e)
    	{
    		this.ConsoleOut("<ClearRevisions> 接受痕迹失败..."+e.description);
    		this.Status = "痕迹接受失败，错误原因：" + e.description;
    		return false;
    	}
    	
    }

    //用于删除目录下的文件		（该功能未完全实现）
    this.WebDelTree = function(Directory)
    {
    	this.ConsoleOut("<WebDelTree> 删除目录下的文件...");
    	var fs = this.obj.FileSystem;
    	fs.ClearDirectory (Directory); //此接口无返回值
    	this.ConsoleOut("<WebDelTree> 删除成功...");
    	return true;
    }

	// 用于取得文件内容		（未实现）
	this.WebGetFileContent = function(FileName) 
	{
		this.Status = "该功能暂未提供!";
		return "";
	}

	// 用于取得当前控件的版本号信息		（该功能已完整实现）
	this.Version = function() 
	{
		return this.obj.Version;
	}

	// 用于取得当前控件的生产版本信息		（需要控件修改代码）
	this.VersionEx = function() 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 按设置的打印份数进行打印（该功能完全实现）
	this.PrintByCopies = function(Copies, Show) {
		this.ConsoleOut("<PrintByCopies> 开始...");
		var mAPIObj;
    	var mApiName;
    	var mAppName;
    	mAppName = this.obj.ActiveDocument.Application.Name;
    	this.ConsoleOut("<PrintByCopies> 判断文档是否是word类型...");
    	if(mAppName == "Microsoft Word"){
    		this.ConsoleOut("<PrintByCopies> 调用打印机功能...");
    		mAPIObj = this.obj.ActiveDocument.Application.Dialogs.Item(88);
    		this.ConsoleOut("<PrintByCopies> 是否调用打印机窗口...");
    		if(Show){
    			this.ConsoleOut("<PrintByCopies> 启用打印机窗口...");
    			if(mAPIObj.Display == -1){
    				this.ConsoleOut("<PrintByCopies> 设置带窗口的打印份数...");
    				mAPIObj.NumCopies = Copies;
    				this.ConsoleOut("<PrintByCopies> 开始打印...");
    				mAPIObj.Execute;
    				this.ConsoleOut("<PrintByCopies> 打印成功...");
    				this.Status = "打印成功";
    				this.ConsoleOut("<PrintByCopies> 结束...");
    				return true;
    			}else{
    				this.ConsoleOut("<PrintByCopies> 启用打印机窗口失败...");
    				this.Status = "启用打印机窗口失败";
    				this.ConsoleOut("<PrintByCopies> 结束...");
    				return false;
    			}
    		}else{
    			this.ConsoleOut("<PrintByCopies> 不启用打印机窗口...");
    			this.ConsoleOut("<PrintByCopies> 设置不带窗口打印份数...");
    			mAPIObj.NumCopies = Copies;
    			this.ConsoleOut("<PrintByCopies> 开始打印...");
    			mAPIObj.Execute;
    			this.Status = "打印成功";
    			this.ConsoleOut("<PrintByCopies> 结束...");
    			return true;
    		}
    	}else{
    		this.ConsoleOut("<PrintByCopies> 文档不是word类型...");
    		this.Status = "文档不是word类型";
    		this.ConsoleOut("<PrintByCopies> 结束...");
    		return false;
    	}
	}

	// 用于获取中央处理器CUPID			（需要控件修改代码）
	this.WebGetCpuID = function() 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 用于获取硬盘序列号IDEID			（需要控件修改代码）
	this.WebGetIdeID = function() 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 将HTML文本生成Word可识别的格式，之后带格式可插入Word文档	（需要控件修改代码）
	this.BuildContentFromHTML = function(HTMLText) 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 执行宏
	this.WebRunMacro = function(MarcroName, MacroValue) 
	{
		try {
			var VBAStr = MacroValue;
			var VBCom = this.obj.ActiveDocument.VBProject.VBComponents.Add(1);
			VBCom.CodeModule.AddFromString(VBAStr);
			this.obj.ActiveDocument.Application.Run(MarcroName);
			return true;
		} catch (e) {
			return false;
		}
	}

	// 文档对比	（该功能已完全实现）
	this.WebDocumentCompare = function(FileName1, FileName2) 
	{
		// 与当前打开的文档进行对比
		this.ConsoleOut("<WebDocumentCompare> 文档对比开始...");
		this.Status = '';
		var fs = this.obj.FileSystem; // 创建file对象：
		var filePath = this.DownFilePath(); //获取文件保存临时路劲
		var saveFilePath = filePath + Math.round(Math.random() * 100000000)
				+ '.doc';
		if ((FileName1 == null) || (FileName1 == '')) {
			if ((FileName2 == null) || (FileName2 === '')) {
				this.Status = '【FileName2】不能为空!';
				return false;
			} else {
				// 开始下载FileName2
				this.ConsoleOut("<WebDocumentCompare>与当前文档进行对比 ");
				this.ConsoleOut("<WebDocumentCompare> 开始下载..."+FileName2);
				if (this.WebDownloadFile_int(FileName2)) {
					this.ConsoleOut("<WebDocumentCompare> "+'文件【' + FileName2 + '】下载成功');
					FileName1 = this.FileName;
					this.WebSaveLocalFile(filePath + FileName1);
					this.ConsoleOut("<WebDocumentCompare> 开始把当前文档下载到本地...");
					this.WebClose();
					this.ConsoleOut("<WebDocumentCompare> 开始文档对比...");
					var bRet = this.obj.FuncExtModule.WebDocumentCompare(
							filePath + FileName1,this.DownloadedFileTempPathName,
							saveFilePath);
					if (bRet == true) {
						this.ConsoleOut("<WebDocumentCompare> 文档对比成功");
						this.obj.Open(saveFilePath);
						this.ConsoleOut("<WebDocumentCompare> 文档对比成功打开最终文档...");
						this.obj.ActiveDocument.Application.ActiveWindow.View.type = 3;
						this.obj.ActiveDocument.Application.ActiveWindow.View.SplitSpecial = 20;
						return true;
					} else {
						this.ConsoleOut("<WebDocumentCompare> 文档对比失败,请确认待对比的文档是否能够正常打开!");
						this.Status = '文档对比失败,请确认待对比的文档是否能够正常打开!';
						return false;
					}
				} else {
					this.ConsoleOut("<WebDocumentCompare> "+'文件【' + FileName2 + '】下载失败，该文档可能在服务器不存在!');
					this.Status = '文件【' + FileName2 + '】下载失败，该文档可能在服务器不存在!';
					return false;
				}
			}
		}
		// 与指定的后台两个文档进行对比
		else {
			this.ConsoleOut("<WebDocumentCompare> 后台两个文档进行对比 ");
			if ((FileName2 == null) || (FileName2 == '')) {
				this.Status = '【FileName2】不能为空!';
				return false;
			} else {
				// 开始分别下载指定的两个文件
				this.ConsoleOut("<WebDocumentCompare> 分别下载两个文档 ");
				if (this.WebDownloadFile_int(FileName1)) {
					this.ConsoleOut("<WebDocumentCompare> "+'文件【' + FileName1 + '】下载成功');
					if ((this.WebDownloadFile_int(FileName2))) {
						this.ConsoleOut("<WebDocumentCompare> "+'文件【' + FileName2 + '】下载成功');
						this.WebClose();
						var bRet = this.obj.FuncExtModule.WebDocumentCompare(
								filePath + FileName1, filePath + FileName2,
								saveFilePath);
						this.ConsoleOut("<WebDocumentCompare> 开始文档对比 ");
						if (bRet == true) {
							this.ConsoleOut("<WebDocumentCompare> 文档对比成功...");
							this.obj.Open(saveFilePath);
							this.ConsoleOut("<WebDocumentCompare> 文档对比成功打开最终文档...");
							this.obj.ActiveDocument.Application.ActiveWindow.View.type = 3 //
							this.obj.ActiveDocument.Application.ActiveWindow.View.SplitSpecial = 20;
							return true;
						} else {
							this.ConsoleOut("<WebDocumentCompare> 文档对比失败,请确认待对比的文档是否能够正常打开!");
							this.Status = '文档对比失败,请确认待对比的文档是否能够正常打开!';
							return false;
						}
					} else {
						this.ConsoleOut("<WebDocumentCompare> "+'文件【' + FileName2 + '】下载失败，该文档可能在服务器不存在!');
						this.Status = '文件【' + FileName2 + '】下载失败，该文档可能在服务器不存在!';
						return false;
					}
				} else {
					this.ConsoleOut("<WebDocumentCompare> "+'文件【' + FileName1 + '】下载失败，该文档可能在服务器不存在!');
					this.Status = '文件【' + FileName1 + '】下载失败，该文档可能在服务器不存在!';
					return false;
				}
			}
		}
	}

	// 设置控件皮肤	（该功能已完整实现）
	this.WebSetSkin = function(titleBarColor/* 控件标题栏颜色 */,
			menuBarStartColor/* 自定义菜单开始颜色 */,
			menuBarButtonStartColor/* 自定义工具栏按钮开始颜色 */,
			menuBarButtonEndColor/* 自定义工具栏按钮结束颜色 */,
			menuBarButtonFrameColor/* 自定义工具栏按钮边框颜色 */,
			CustomToolbarStartColor/* 自定义工具栏开始颜色 */, TitleBarTextColor/* 控件标题栏文本颜色 */) {
		this.Status = '';
		var style = this.obj.Style;
		try {
			style.TitleBarColor = titleBarColor;
			if ((TitleBarTextColor == undefined) || (TitleBarTextColor == '')) // 设置默认标题文字颜色
			{
				style.TitleBarTextColor = 0x000000; // 黑色
			} else {
				style.TitleBarTextColor = TitleBarTextColor;
			}
			style.MenuBarStartColor = menuBarStartColor;
			style.MenuBarEndColor = 0xFFFFFF;
			style.MenuBarTextColor = 0x000000;
			style.MenuBarHighlightTextColor = 0x000000;
			style.MenuBarButtonStartColor = menuBarButtonStartColor;
			style.MenuBarButtonEndColor = menuBarButtonEndColor;
			style.MenuBarButtonFrameColor = menuBarButtonFrameColor;
			style.CustomToolbarStartColor = CustomToolbarStartColor;
			style.CustomToolbarEndColor = CustomToolbarStartColor;
			style.Invalidate();
			return true;
		} catch (e) {
			this.Status = '皮肤设置错误，错误信息为：' + e.description;
			return false;
		}
	}

	// 是否启用iWebOffice对象内的拷贝功能。非控件的不受影响	（该功能已完整实现）
	this.WebEnableCopy = function(mValue) 
	{
		this.ConsoleOut("<WebEnableCopy> 开始 ");
		switch (mValue) {
		case 0:
		case false:
		case "0":
			this.obj.CopyEnabled = false;
			this.ConsoleOut("<WebEnableCopy> 不允许Copy ");
			break; // 启用
		case 1:
		case "1":
		case true:
			this.obj.CopyEnabled = true;
			this.ConsoleOut("<WebEnableCopy> 允许Copy ");
			break; // 关闭
		default:
			;
			return;
		}
	}

	// 显示痕迹和隐藏痕迹	（该功能已完整实现）
	this.VBAShowRevisions = function(mValue) 
	{
		this.ConsoleOut("<VBAShowRevisions> 开始隐藏痕迹。。。 ");
		if (this.getDocType(this.FileType) == this.DocType.WORD
				&& this.obj.ActiveDocument.ProtectionType == "-1") {
			this.obj.ActiveDocument.TrackRevisions = mValue; // 显示标记和隐藏标记
			this.obj.ActiveDocument.ShowRevisions = mValue; // 显示痕迹或隐藏
			this.ConsoleOut("<VBAShowRevisions> 隐藏痕迹成功 ");
			this.Status = "隐藏痕迹成功";
			return true;
		} else {
			this.ConsoleOut("<VBAShowRevisions> 非Office文档或文档已被锁定，无法执行操作 ");
			this.Status = "非Office文档或文档已被锁定，无法执行操作";
			return false;
		}
	}

	// 根据文档保护文档	（该功能已完整实现）
	this.VBAProtectDocument = function(docType, password) 
	{
		this.ConsoleOut("<VBAProtectDocument> 开始。。。 ");
		if (docType == this.DocType.WORD) // word 保护模式
		{
			if (this.obj.ActiveDocument.ProtectionType == "-1") {
				this.obj.ActiveDocument.Protect(2, false, password); //第一个参数： 3可以复制  2不能复制
				this.ConsoleOut("<VBAProtectDocument> 文档锁定成功。。。 ");
				this.Status = "文档已被锁定";
				return true;
			} else {
				this.ConsoleOut("<VBAProtectDocument> 文档锁定失败。。。 ");
				this.Status = "文档锁定失败";
				return false;
			}
		} else if (docType == this.DocType.EXECL) // word
		// 保护模式，这里只保护表单1的其他的安自己需求编写
		{
			if (!this.obj.ActiveDocument.Application.Sheets(1).ProtectContents) // 判断表单是否是保护的
			{
				this.obj.ActiveDocument.Application.Sheets(1).Protect(password);
				this.ConsoleOut("<VBAProtectDocument> 文档锁定成功。。。 ");
				this.Status = "文档锁定成功";
			} else {
				this.ConsoleOut("<VBAProtectDocument> 文档锁定失败。。。 ");
				this.Status = "文档锁定失败";
				return false;
			}
		} else {
			this.ConsoleOut("<VBAProtectDocument> 非Office文档，无法执行锁定操作。。。 ");
			this.Status = "非Office文档，无法执行锁定操作";
			return false;
		}
	}

	// 根据密码解除保护		（该功能已完整实现）
	this.VBAUnProtectDocument = function(docType, password) 
	{
		this.ConsoleOut("<VBAUnProtectDocument> 开始。。。 ");
		var docType = this.getDocType(this.FileType);
		if (docType == this.DocType.WORD) // word 保护模式
		{
			this.obj.ActiveDocument.Unprotect(password);
			this.ConsoleOut("<VBAUnProtectDocument> WORD保护成功。。。 ");
			return true;
		} else if (docType == this.DocType.EXECL) {
			this.obj.ActiveDocument.Application.Sheets(1).Unprotect(password);
			this.ConsoleOut("<VBAUnProtectDocument> EXCEL保护成功。。。 ");
			return true;
		} else {
			this.ConsoleOut("<VBAUnProtectDocument> 非Office文档，无法执行解锁操作。。。 ");
			this.Status = "非Office文档，无法执行解锁操作";
			return false;
		}
	}
	//把文档插入到指定的书签位置  （该功能已完整实现）
	this.VBAInsertFile = function(Position, FileName) 
	{
		this.ConsoleOut("<VBAInsertFile> 开始。。。 ");
		try {
			var docType = this.getDocType(this.FileType);
			this.ConsoleOut("<VBAInsertFile> 判断是否是WORD。。。 ");
			if (docType == this.DocType.WORD) {
				this.ConsoleOut("<VBAInsertFile> 判断书签是否存在。。。 ");
				if (this.obj.ActiveDocument.BookMarks.Exists(Position)) {
					this.obj.ActiveDocument.Application.Selection.GoTo(-1, 0, 0, Position);
					this.ConsoleOut("<VBAInsertFile> 跳转到书签指定位置。。。 ");
				}
				this.obj.Activate(true);
				this.ConsoleOut("<VBAInsertFile> 激活当前对象。。。 ");
				this.obj.ActiveDocument.Application.Selection.InsertFile(FileName, "", false, false, false);
				this.ConsoleOut("<VBAInsertFile> 插入。。。 "+FileName+"...成功");
				return true;
			} else {
				this.Status = "非Office文档，无法执行插入文档操作";
				this.ConsoleOut("<VBAInsertFile> 非Office文档，无法执行插入文档操作。。。 ");
				return false;
			}
		} catch (e) {
			this.Status = "插入文档失败，错误原因：" + e.description;
			this.ConsoleOut("插入文档失败，错误原因：" + e.description);
			return false;
		}
	}

	// 接受本地文档中的所有痕迹 （该功能未实现）
	this.WebAcceptAllRevisions = function() 
	{
		this.Status = "该功能暂未提供!";
		return false;
	}

	// 设置WORD用户名	（该功能已完整实现）
	this.VBASetUserName = function(UserName) 
	{
		this.ConsoleOut("<VBASetUserName> 开始。。。 ");
		try {
			this.obj.ActiveDocument.Application.UserName = UserName;
			this.ConsoleOut("<VBASetUserName> 设置WORD编辑名成功。。。 ");
			return true;
		} catch (e) {
			return false;
		}
	}

	// 设置域 	（该功能已完整实现）
	this.ShowField = function() 
	{
		try {
			this.obj.ActiveDocument.ActiveWindow.View.ShowDrawings = true;
			return true;
		} catch (e) {
			this.Status = "设置域失败，错误原因：" + e.description;
			return false;
		}
	}

	// 区域保护 	（该功能已完整实现）
	this.WebAreaProtect = function(BMarksName) 
	{
		this.ConsoleOut("<WebAreaProtect> 开始。。。 ");
		if (docType == this.DocType.WORD) // word 保护模式
		{
			if (!this.obj.ActiveDocument.BookMarks.Exists(BMarksName)) {
				this.ConsoleOut("<WebAreaProtect> 文件内没有名称为'" + BMarksName + "'的书签 ,请先添加书签!");
				this.Status = "文件内没有名称为'" + BMarksName + "'的书签 ,请先添加书签!";
				return false;
			}

			var range = this.obj.Range;
			range = this.obj.ActiveDocument.Bookmarks(BMarksName).Range;
			range.Select();
			this.ConsoleOut("<WebAreaProtect> 选中书签位置。。。 ");
			this.obj.ActiveDocument.bookmarks(BMarksName).range.editors.add(-1); // 常量：wdeditoreveryone=-1
			this.obj.ActiveDocument.Protect(3, false, "123", false, false);// 常量：wdAllowOnlyReading=3
			this.ConsoleOut("<WebAreaProtect> 保护书签内容。。。 ");
			this.obj.ActiveDocument.Application.Selection.MoveLeft(Unit = 1,
					Count = 1);
			this.obj.ActiveDocument.ActiveWindow.View.ShadeEditableRanges = false;// 取消"突出显示可编辑区域"
			this.ConsoleOut("<WebAreaProtect> 突出显示可编辑区域。。。 ");
			return true;
		} else {
			this.ConsoleOut("<WebAreaProtect> 非Office文档，无法执行区域保护操作! ");
			this.Status = "非Office文档，无法执行区域保护操作!";
			return false;
		}
	}

	// 取消区域保护		（该功能已完整实现）
	this.WebAreaUnprotect = function(BMarksName) 
	{
		this.ConsoleOut("<WebAreaUnprotect> 开始。。。 ");
		if (docType == this.DocType.WORD) {
			this.ConsoleOut("<WebAreaUnprotect> 判断书签是否存在。。。 ");
			if (this.obj.ActiveDocument.BookMarks.Exists(BMarksName)) // 判断是否存在该书签
			{
				try {
					this.ConsoleOut("<WebAreaUnprotect> 解保护。。。 ");
					this.obj.ActiveDocument.Unprotect("123");// 解保护
					var range = this.obj.Range;
					range = this.obj.ActiveDocument.Bookmarks(BMarksName).Range;
					range.Select();// 选定书签内容
					this.ConsoleOut("<WebAreaUnprotect> 选中书签内容。。。 ");
					this.obj.ActiveDocument.DeleteAllEditableRanges(-1); // 去掉突出显示
					this.ConsoleOut("<WebAreaUnprotect> 去掉突出显示。。。 ");
					return true;
				} catch (e) {
					this.ConsoleOut("<WebAreaUnprotect> 执行取消区域保护时出现错误，错误原因：" + e.description);
					this.Status = "执行取消区域保护时出现错误，错误原因：" + e.description;
					return false;
				}
			} else {
				this.ConsoleOut("<WebAreaUnprotect> 文档中不存在<" + BMarksName + ">的书签");
				this.Status = "文档中不存在<" + BMarksName + ">的书签";
				return false;
			}
		} else {
			this.ConsoleOut("<WebAreaUnprotect> 非Office文档，无法执行取消区域保护操作!");
			this.Status = "非Office文档，无法执行取消区域保护操作!";
			return false;
		}
	}

	// 获取焦点	（该功能已完整实现）
	this.Activate = function(blnValue) 
	{
		this.obj.Activate(blnValue);
	}

	// 设置word的页码 ,IE和其他浏览器有区别，所以封装起来	（该功能已完整实现）
	this.WebPageCode = function() 
	{
		var FunctionString = "ActiveObject.ActiveDocument.Application.Dialogs(294).Show()";
		this.blnIE() ? this.obj.ActiveDocument.Application.Dialogs(294).Show()
				: this.ExecuteScript("WebPageCode", FunctionString);
	}

	/* ExecuteScript可将同步函数转换为异步，解决在FireFox、Chrome浏览器中有弹框或运行时间过长导致弹出不响应提示框的问题。 */
	this.ExecuteScript = function(mValue, StringObject) 
	{
		this.obj.ExecuteScript(mValue, StringObject);
	}

	/* 控制2015标题栏 */		// （该功能已完整实现）
	this.ShowTitleBar = function(mValue) 
	{
		var style = this.obj.Style;
		style.ShowTitleBar = mValue;
	}

	/* 控制2015自定义菜单栏 */	// （该功能已完整实现）
	this.ShowCustomToolBar = function(mValue) 
	{
		var style = this.obj.Style;
		style.ShowCustomToolbar = mValue;
	}

	/* 控制2015菜单栏 */		// （该功能已完整实现）
	this.ShowMenuBar = function(mValue) 
	{
		var style = this.obj.Style;
		style.ShowMenuBar = mValue;
	}

	/* 控制Office工具栏 */	// （该功能已完整实现）
	this.ShowToolBars = function(mValue)
	{
		var style = this.obj.Style;
		style.ShowToolBars = mValue;
	}

	/* 控制2015状态栏 */		// （该功能已完整实现）
	this.ShowStatusBar = function(mValue)
	{
		var style = this.obj.Style;
		style.ShowStatusBar = mValue;
	}
	
    //显示/隐藏手写签批工具栏	// （该功能已完整实现）
    this.ShowCustomToolbar = function(bVal)
    {
    	this.obj.Style.ShowCustomToolbar=bVal;
    }
	
	/*
	 * 显示和隐藏痕迹 隐藏痕迹时之前的痕迹不受影响
	 */		// （该功能已完整实现）
	this.WebShow = function(blnValue) 
	{
		if (this.getDocType(this.FileType) == this.DocType.WORD) {
			this.VBAShowRevisions(blnValue);
		}
	}

	// 是否启用iWebOffice对象内文档的保存功能		// （该功能已完整实现）
	this.SaveEnabled = function(mBoolean)
	{
		this.obj.SaveEnabled = mBoolean;
	}

	// 是否启用iWebOffice对象内文档的打印功能		// （该功能已完整实现）
	this.PrintEnabled = function(mBoolean) 
	{
		this.obj.PrintEnabled = mBoolean;
	}

	this.HookEnabled = function() // 解决在Firefox和chrome浏览器下调用iWebPlugin崩溃崩溃的问题需要在load()函数下加如下代码   // （该功能已完整实现）
	{
		if (this.getDocType(this.FileType) == this.DocType.WORD) {
			this.obj.Style.ShowToolSpace = true;
			this.obj.SelectionInformationEnabled = false;
		}
		if (!((window.ActiveXObject != undefined)
				|| (window.ActiveXObject != null) || "ActiveXObject" in window)) {
			this.obj.HookEnabled = false;
		}
	}
	// ---------------------------------------------------对外接口-------------------------------------------------------------------//
	// ******************************************************************************************************************************
	// ******************************************************************************************************************************

	// ******************************************************************************************************************************//
	// ---------------------------------------------------内部方法-------------------------------------------------------------------//
	
	//清理本地临时文件	
    this.ClearDirectory = function()
    {
    	this.ConsoleOut("<ClearDirectory> 开始...");
    	var fs = this.obj.FileSystem;
    	/*老版本插件临时目录*/
    	fs.ClearDirectory(this.DownFilePath());
    	this.ConsoleOut("<ClearDirectory> 清除Down里面文件成功...");
    	fs.ClearDirectory(this.getFilePath());  //此接口无返回值
    	this.ConsoleOut("<ClearDirectory> 清除UP里面文件成功...");
    	this.ConsoleOut("<ClearDirectory> 结束...");
    }
    
	//删除本地文件		// （该功能已完整实现）
    this.WebDelLocalFile = function (FileName) 
    {
        var fs = this.obj.FileSystem;
        fs.DeleteFile(FileName);
    }
    
	/* 获取json格式数据包 */	// （该功能已完整实现）
	this.GetMessageString = function()
	{
		return this.ht.toString();
	}
	
	// 设置控件Ribbon		// （该功能已完整实现）
	this.WebSetRibbonUIXML = function(strCustomUI)
	{
		this.obj.RibbonCustomUI = strCustomUI;
	}
	
	// 设置控件保存文档的限制		// （该功能已完整实现）
	this.WebSetMaxFileSize = function(mFileName)
	{
	if(this.WebFileSize(mFileName) > this.MaxFileSize*1024){
		return false;
	}else{
		return true;
	}
	}
	
	//设置临时目录路径(DOWN)
	this.DownFilePath = function()
	{
		var fs = this.obj.FileSystem;
		var NewDownFilePath;
	    /*老版本插件临时目录*/
	  if (this.HiddenDirectory)	//将文档保存在隐藏目录
	  {
	  	NewDownFilePath = fs.GetSpecialFolderLocation(0x20) + this.DOWN
				+ fs.GetFolderEncryptCode(1) + "\\" ; // 设置临时路径
	  }
	  else
	  {
	  	NewDownFilePath = fs.GetSpecialFolderPath(0x1a) + this.DOWN; //设置临时路径
	  } 
		return NewDownFilePath;
	}
	//设置临时目录路径(UP)
	/* 获取临时路径 */	// （该功能已完整实现）
	this.getFilePath = function() 
	{
		var fs = this.obj.FileSystem; // 创建file对象：
		var filePath;
		if (this.HiddenDirectory)	//将文档保存在隐藏目录
		{
			filePath = fs.GetSpecialFolderLocation(0x20) + this.UP
				+ fs.GetFolderEncryptCode(1) + "\\"; // 设置临时路径
		}
		else
		{
			filePath = fs.GetSpecialFolderLocation(0x1a) + this.UP;
		}
		
		fs.CreateDirectory(filePath); // 创建路径
		this.FilePath = filePath; // 给对象赋值，方便删除和打开
		return this.FilePath;
	}
	
	// 设置是否保存空文档		（未完全实现待修改）
	this.WebSetAllowEmpty = function(mFileName)
	{
		var AllowEmpty = this.AllowEmpty;
		if(this.WebFileSize(mFileName) == 0){
			if(AllowEmpty){
				return true;
			}else{
		        if (confirm("文件监测到异常（可能文件内容为空）是否继续保存？")) {  
		            return true;
		        }  
		        else {  
		            return false;
		        }
			}
		}else{
			return true;
		}
	}
	
	/*控制是否可以复制*/	// （该功能已完整实现）
	this.NewCopyType = function()
    {
    	var mValue = this.CopyType;
    	switch(mValue)
    	{
    		case 0:
    		case false:
    		case "0": this.obj.CopyEnabled = false; break; //启用
    		case 1:
    		case "1":
    		case true: this.obj.CopyEnabled = true; break; //关闭
    		default: ; return;
    	}
    }

	/*控制2015菜单栏*/		// （该功能已完整实现）
    this.NewShowMenu = function()
    {
    	var style = this.obj.Style;
    	var mValue = this.ShowMenu;
    	switch(mValue)
    	{
	    	case 0:
			case false:
			case "0": style.ShowMenuBar = false; break; //隐藏菜单栏
			case 1:
			case "1":
			case true: style.ShowMenuBar = true; break; //显示菜单栏
			default: ; return;
    	}
    }

    /*设置是否显示整个控件工具栏，包括OFFICE的工具栏*/  // （该功能已完整实现）
    this.NewShowToolBar = function() 
    {
    	var style = this.obj.Style;
    	var mValue = this.ShowToolBar;
    	switch(mValue)
    	{
    		case false : style.ShowCustomToolbar = false; style.ShowToolBars = true; break; //自定义工具栏隐藏，office工具栏显示
    		case true : style.ShowCustomToolbar = true; style.ShowToolBars = true; break; //自定义工具栏显示，office工具栏显示
    		case 0: style.ShowCustomToolbar = false; style.ShowToolBars = true; break; //自定义工具栏隐藏，office工具栏显示
    		case 1: style.ShowCustomToolbar = true; style.ShowToolBars = true; break; //自定义工具栏栏显示，office工具栏显示
    		case 2: style.ShowCustomToolbar = false; style.ShowToolBars = false; break; //自定义工具栏隐藏，office工具栏隐藏
    		case 3: style.ShowCustomToolbar = true; style.ShowToolBars = false; break; //自定义工具栏显示，office工具栏隐藏
    	}
    }

  // 控制OFFICE另存为和保存功能		// （该功能已完整实现）
    this.NewUIControl = function()
    {
    	var strCustomUI;
    	var mValue = this.UIControl;
    	switch(mValue)
    	{
    	case false: strCustomUI = '<customUI xmlns="http://schemas.microsoft.com/office/2006/01/customui" onLoad="OnLoad" loadImage="LoadImage"> \
			            <commands> \
			      	    	<command idMso="FileSave" enabled="false" "/> \
			      	  		<command idMso="FileSaveAs" enabled="false" /> \
			            </commands> \
		    		</customUI>';break; //启用
    	case true: strCustomUI = '<customUI xmlns="http://schemas.microsoft.com/office/2006/01/customui" onLoad="OnLoad" loadImage="LoadImage"> \
				        <commands> \
				  	    	<command idMso="FileSave" enabled="true" /> \
				  	  		<command idMso="FileSaveAs" enabled="true" /> \
				        </commands> \
					</customUI>';break; //启用		
        
    	}
        this.obj.RibbonCustomUI = strCustomUI;
    }
	

	// 枚举显示系统和控件定义的相关对话框内容		// （该功能已完整实现）
	this.ShowDialog = 
	{
		DialogNew : 0, // 新建对象
		DialogOpen : 1, // 打开
		DialogSaveAs : 2, // 另存为
		DialogSaveCopyAs : 3, // 另存为拷贝
		DialogPrint : 4, // 打印
		DialogPageSetup : 5, // 打印设置
		DialogProperties : 6
	// 文档属性
	}

	this.setObj = function(object) // 设置2015对象
	{
		this.obj = object;
		this.WebObject = this.obj; // 设置VBA调用对象
	}

	this.DocType = // 枚举所有文档类型这里只列举word 0,execl 1
	{
		WORD : 0,
		EXECL : 1
	};

	this.HttpMethod = 
	{
		Get : 0, // Http对象get方式
		Post : 1
	// Http对象post方式
	};

	this.SaveAsPdf = function(FilePath) 
	{
		// var FilePath=this.getFilePath() + this.RecordID + ".pdf";
		this.ConsoleOut("<WebSavePDF> 开始把PDF保存到本地...");
		if ((this.FileType == ".doc") || (this.FileType == ".docx")
				|| (this.FileType == ".wps")) {
			try {
				this.obj.ActiveDocument.ExportAsFixedFormat(FilePath, 17,
						false, 0, 0, 1, 1, 0, true, true, 0, true, true, true);
						this.ConsoleOut("<WebSavePDF> 保存到本地成功...");
			} catch (e) {
				this.ConsoleOut("<WebSavePDF> 保存异常..."+e.description);
				this.Status = e.description;
				return false;
			}
			this.Status = "保存到本地成功";
			return true;
		}
	}

	// 保存为html		（该功能未完全实现）
	this.SaveAsHtml = function(FilePath) 
	{
		if ((this.FileType == ".doc") || (this.FileType == ".docx")
				|| (this.FileType == ".wps")) {
			try {
				var ret = this.obj.ActiveDocument.SaveAs(FilePath, 8, false,
						"", false, "", false, false, false, false, false, 0);
				this.obj.ActiveDocument.Application.ActiveWindow.View.type = 3;//3是页面视图  
				return true;
			} catch (e) {
				this.Status = e.description;
				return false;
			}
		}
	}

	/* 用来存储Http发送的表单数据 */
	this.ArrayList = function() 
	{
		this.ObjArr = {}; // 列表
		this.Count = 0; // 数量
		this.Add = function(key, value) // 添加
		{
			this.ObjArr[key] = value;
			this.Count++;
			return true;
		}

		this.Get = function(key) {
			return this.ObjArr[key];
		}

		this.Clear = function() // 清空
		{
			this.ObjArr = {};
			this.Count = 0;
		}

		// 按json格式输出
		this.toString = function() 
		{
			var newArray = new Array(); // 存储json字符串
			var i = 0;
			for ( var i in this.ObjArr) {
				newArray.push("'" + i + "':'" + this.ObjArr[i] + "'");
			}
			return "{" + newArray + "}";
		}
	}
	this.ht = new this.ArrayList();

	// 获取打开窗口的后缀
	this.getOpenDocSuffix = function(fileType) 
	{
		if (fileType.length == 5) {
			fileType = fileType.substring(0, 4);
		}
		var exts;
		exts = "";
		if (this.isWPS() || !this.getOfficeVersion()) // 如果是office2003是不支持x格式的文档
		{
			exts += "*" + fileType + "x|*" + fileType + "x|";
		}
		exts += "*" + fileType + "|*" + fileType + "|";
		exts += this.getOpenSuffixName(fileType) + "(*" + fileType;
		if (this.isWPS() || !this.getOfficeVersion()) // 如果是office2003是不支持x格式的文档
		{
			exts += ";*" + fileType + "x";
		}
		exts += ")|*" + fileType;
		if (this.isWPS() || !this.getOfficeVersion()) // 如果是office2003是不支持x格式的文档
		{
			exts += ";*" + fileType + "x";
		}
		exts += "|";
		return exts;
	}
	
	// 对比文档里面所用到的 下载文档方法  （该功能已完整实现）
	this.WebDownloadFile_int = function(fn) 
	{
		this.Status = '';
		var httpclient = this.obj.Http; // 设置http对象
		httpclient.Clear();
		this.WebSetMsgByName("USERNAME", this.UserName); // 加载UserName
		this.WebSetMsgByName("FILENAME", fn); // 加载FileName
		this.WebSetMsgByName("FILETYPE", this.FileType); // 加载FileType
		this.WebSetMsgByName("RECORDID", this.RecordID); // 加载RecordID
		this.WebSetMsgByName("OPTION", "LOADFILE"); // 发送请求LOADFILE
		httpclient.AddForm("FormData", this.GetMessageString()); // 这里是自定义json
		// 传输格式。
		this.WebClearMessage(); // 清除所有WebSetMsgByName参数
		httpclient.ShowProgressUI = this.ShowWindow;// 显示进度条
		if (httpclient.Open(this.HttpMethod.Post, this.WebUrl, false)) // 这里采用同步方式打开文档。我要返回值
		{
			if (httpclient.Send()) {
				if (httpclient.GetResponseHeader("MsgError") == "404") // 判断服务器是否存在文件
				{
					this.Status = '文档【' + fn + '】下载失败，请确认该文档在服务器上是否存在';
					httpclient.Clear();
					return false;
				}
				httpclient.Clear();
				if (this.hiddenSaveLocal(httpclient, this, false, false, fn)) // 下载成功时
				{
					this.Status = '文档下载成功';
					return true;
				} else {
					this.Status = '文档下载失败';
					return false;
				}
			}
		}
	}
	
	//保存文件至服务   （该功能已完整实现）
	this.SAVEFILE = function(httpclient, FileName) // 时发生交互OPTION值为：SAVEFILE
	{
		httpclient.AddForm("FormData", this.GetMessageString());
		httpclient.AddFile("FileData", FileName); // 需要上传的文件
		this.WebClearMessage();
		httpclient.ShowProgressUI = this.ShowWindow; // 隐藏进度条
		this.ConsoleOut("<SAVEFILE> 开始打开链接...");
		if (httpclient.Open(this.HttpMethod.Post, this.WebUrl, false)) //true 异步方式 false同步
		{
			this.ConsoleOut("<SAVEFILE> 开始向后台发送数据包...");
			if (!httpclient.Send()) {
				this.ConsoleOut("数据包发送失败！请检查链接<" + this.WebUrl
						+ ">是否正确或网络是否畅通。");
				this.Status = "数据包发送失败！请检查链接<" + this.WebUrl + ">是否正确或网络是否畅通。";
				return false;
			} else {
				this.ConsoleOut("<SAVEFILE> 数据包发送成功，文档已成功保存到后台...");
				return true;
			}
		} else {
			this.ConsoleOut("打开链接<" + this.WebUrl + ">失败！请检查网络是否畅通。");
			this.Status = "打开链接<" + this.WebUrl + ">失败！请检查网络是否畅通。";
			return false;
		}
	}
	
	// 加载服务上的文档    （该功能已完整实现）
	this.LOADFILE = function(httpclient) 
	{
		this.Status = "";
		httpclient.ShowProgressUI = this.ShowWindow;
		this.ConsoleOut("<LOADFILE> 开始打开链接...");
		if (httpclient.Open(this.HttpMethod.Post, this.WebUrl, false)) // true
		// 异步方式
		// false同步
		{
			this.ConsoleOut("<LOADFILE> 链接打开成功，开始进行数据包发送...");
			// 这里采用异步方式打开文档
			if (httpclient.Send()) {
				this.ConsoleOut("<LOADFILE> 数据包发送成功...");
				if (httpclient.GetResponseHeader("MsgError") == "404") {
					this.ConsoleOut("<LOADFILE> 后台未找到对应文档，开始创建一个空白文档...");
					this.CreateFile();
					this.ConsoleOut("<LOADFILE> 空白文档创建成功...");
					this.getOfficeVersion();// 打开文档后，判断当前office版本
					httpclient.Clear();
					return true;
				}

				this.ConsoleOut("<LOADFILE> 开始将后台文档保存到本地...");
				/*
				 * , Math.round(Math.random() * 100000)
				 */
				if (this.hiddenSaveLocal(httpclient, this, false, false)) {
					this.ConsoleOut("<LOADFILE> 后台文档保存到本地成功，开始打开本地文档，路径为："
							+ this.DownloadedFileTempPathName + "...");
					var mSaveResult = this
							.WebOpenLocalFile(this.DownloadedFileTempPathName);
					if (mSaveResult == 0) { // 打开本地磁盘文件
						this.ConsoleOut("<LOADFILE> 文档打开成功...");
						this.getOfficeVersion();// 打开文档后，判断当前office版本
						return true;
					} else {
						this
								.ConsoleOut("<LOADFILE> 打开文档错误，可能当前编辑软件不支持当前格式！错误码为： "
										+ mSaveResult);
						var windows = window
								.confirm("打开文档错误，可能当前编辑软件不支持当前格式！错误码为： "
										+ mSaveResult
										+ "\r\r单击“确定”关闭。单击“取消”继续。");
						this.Status = "打开文档错误，可能当前编辑软件不支持当前格式！错误码为： "
								+ mSaveResult;
						if (windows == 1) {
							window.close();
						}
						return false;
					}
				} else {
					// 失败后，this.Status的值由hiddenSaveLocal返回
					return false;
				}
			} else {
				this.Status = "数据包发送失败！请检查链接<" + this.WebUrl + ">是否正确或网络是否畅通。";
				return false;
			}
		} else {
			this.Status = "打开链接<" + this.WebUrl + ">失败！请检查网络是否畅通。";
			return false;
		}
	}

	/* 保存到本地 isHidden 是否隐藏文件；isInsertFile是否是插入文件 */		// （该功能已完整实现）
	this.hiddenSaveLocal = function(httpclient, webOffice, isHidden,
			isInsertFile, OtherName) {
		try {
			this.ConsoleOut("<hiddenSaveLocal> 开始保存文档到本地...");
			this.Status = "";
			if (isHidden) {
				httpclient.Hidden = true; // 隐藏文件
			}
			var tempName = "";
			var fs = webOffice.obj.FileSystem; // WebOffice外面对象名称：
			var filePath = this.DownFilePath(); //获取临时文件保存路径
			fs.CreateDirectory(filePath);
			/*
			 * try { if (!fs.CreateDirectory(filePath)) { this.Status =
			 * "目录创建失败，请检查确认当前用户是否有创建目录的权限。"; return false; } } catch(e) {
			 * this.Status = "目录创建失败，请检查确认当前用户是否有创建目录的权限。错误信息为：" +
			 * e.description; return false; }
			 */
			webOffice.FilePath = filePath; // 这个保存的路径方便打开的时候再取。
			if (isInsertFile == undefined || isInsertFile == true) {
				tempName = "temp" + webOffice.RecordID;
				webOffice.tempInsertName = tempName + webOffice.FileName;
			}
			if (OtherName == undefined || OtherName == "") {
				OtherName = webOffice.FileName;
				OtherName = Math.random() * 100000 + this.FileType;
			}
			this.DownloadedFileTempPathName = filePath + tempName + OtherName;
			httpclient.ResponseSaveToFile(this.DownloadedFileTempPathName); // 临时文件
			httpclient.Clear();
			this.ConsoleOut("<hiddenSaveLocal> 保存文档到本地结束...");
			return true;
		} catch (e) {
			this.ConsoleOut("<hiddenSaveLocal> 保存本地文档失败，错误信息为： "
					+ e.description);
			return false;
		}
	}

	/* office2003的接口和其他的不一样，所有保存到本地要区分开来 */		// （该功能已完整实现）
	this.Save = function(FileName, is2003, FileType) {
		if (this.getDocType(this.FileType) == this.DocType.PICTURE) {
			if (this.obj.ActiveDocument.WebSaveLocalFile(FileName)) {
				return 0;
			} else {
				return -1
			}
		}
		if (is2003) {
			return this.obj.Save(FileName);
		}
		return this.obj.Save(FileName, eval("this.DocTypeValue." + FileType),
				true);
	}

	/* 判断浏览器类型 */	// （该功能已完整实现）
	this.blnIE = function() 
	{
		return (window.ActiveXObject != undefined)
				|| (window.ActiveXObject != null)
				|| ("ActiveXObject" in window)
	}

	/* 判断是否是WPS */		// （该功能已完整实现）
	this.isWPS = function() 
	{
		return this.FileType.toUpperCase() == ".WPS"
				|| this.FileType.toUpperCase() == ".ET";
	}

	// 获取office版本信息		// （该功能未完全实现）
	this.getOfficeVersion = function() 
	{
		var getVersion = 0.0;
		try {
			if (this.setVersion == -1) {
				getVersion = parseFloat(this.obj.ActiveDocument.Application.Version);
				this.setVersion = getVersion;
			} else {
				getVersion = this.setVersion;
			}
			if (getVersion == 11.0) {
				return this.OfficeVersion.v2003;
			} else {
				return this.OfficeVersion.vOther;
			}
		} catch (e) {
			return this.OfficeVersion.v2003;
		}
	}
	
	/*
	 * 获取文档类型
	 */		// （该功能已完整实现）
	this.getDocType = function(fileType) {
		if (fileType == ".doc" || fileType == ".docx" || fileType == ".wps") {
			return this.DocType.WORD;
		}
		if (fileType == ".xls" || fileType == ".xlsx" || fileType == ".et") {
			return this.DocType.EXECL;
		}

		if (fileType == ".tif" || fileType == ".jpg") {
			return this.DocType.PICTURE;
		}
	}

	// 获取打开文档类型名称		// （该功能已完整实现）
	this.getOpenSuffixName = function(fileType) 
	{
		var openSuffixName;
		switch (fileType) {
		case this.DocType.WORD:
			openSuffixName = "Word Files";
			break; // 创建word后缀名称
		case this.DocType.EXECL:
			openSuffixName = "Excel Files";
			break; // 创建execl后缀名称
		default:
			openSuffixName = "Word Files";
			break;
		}
		return openSuffixName;
	}

	// 设置打开窗口的类型，这里统一设置		// （该功能已完整实现）
	this.setShowDialog = function(thisType, exts) 
	{
		switch (thisType) {
		case this.ShowDialog.DialogOpen:
			this.obj.ShowDialog(thisType, exts, 0);
			break; // 打开本地文档
		case this.ShowDialog.DialogNew: // 新建对象
		case this.ShowDialog.DialogPageSetup: // 打印设置
		case this.ShowDialog.DialogPrint: // 打印
		case this.ShowDialog.DialogProperties: // 文档属性
		case this.ShowDialog.DialogSaveCopyAs: // 另存为拷贝
		case this.ShowDialog.DialogSaveAs: // 另存为
			this.blnIE() ? this.obj.ShowDialog(thisType) : this.obj
					.ExecuteScript(thisType + "", "ActiveObject.ShowDialog("
							+ thisType + ")");
			break; // 打开窗口
		default:
			;
			break;
		}
	}

	// 设置文档的保护状态		// （该功能已完整实现）
	this.setEditType = function(type) 
	{
		try {
			switch (type) {
			case "0":
				this.VBAProtectDocument(this.getDocType(this.FileType), "123");
				break;
			case "1":
				this.WebShow(false);
				break;
			case "2":
				this.WebShow(true);
				break;
			default:
				;
			}
		} catch (e) {
			return false;
		}
	}

	// 返回当前编辑器	（该功能未完全实现）	
	this.getEditVersion = function() 
	{
		return this.obj.AppName;
	}

	// 设置手写签批用户	（该功能已完整实现）
	this.SetUser = function(username) 
	{
		this.obj.User = username;
	}
	
	// 检测异常文件的接口（金山）  （该功能未完全实现）
	this.CheckFile = function(vaul) 
	{
		this.obj.bCheckFile = vaul;
	}
	
	//禁用wps混合签章按钮		（该功能未完全实现）	
	this.EnableSignature = function(vaul)
	{
		try {
			this.obj.ActiveDocument.Application.COMAddIns
					.Item("iSignatureWord.WordApp").Connect = vaul;
		} catch (e) {
			return;
		}
	}

	// 关闭文档	（该功能已完整实现）
	this.Close = function() 
	{
		this.obj.Close();
	}

	// 控制台日志输出	（该功能已完整实现）
	this.ConsoleOut = function(value) 
	{
		if (this.DebugMode) {
			console.log(value);
		}
	}
	
	// ******************************************************************************************************************************//
	// ******************************************************************************************************************************//
	// ******************************************************************************************************************************//

	/** ****一下是打开本地窗口代码***** */
	/** ****End 打开本地窗口代码***** */

	/* 2015自带有窗口的页面设置 */	//（该功能已完整实现）
	this.WebPageSetup = function() 
	{
		this.setShowDialog(this.ShowDialog.DialogPageSetup);
	}
	
	/* 2015自带有窗口的打印设置 */		//（该功能已完整实现）
	this.WebOpenPrint = function()
	{
		this.setShowDialog(this.ShowDialog.DialogPrint);
	}

	// 打印预览		//（该功能已完整实现）
	this.PrintPreview = function() 
	{
		this.obj.PrintPreview();
	}
	
	// 退出打印预览		//（该功能已完整实现）
	this.PrintPreviewExit = function() 
	{
		this.obj.PrintPreviewExit();
		this.obj.ActiveDocument.ActiveWindow.View.ShowFieldCodes = false;
	}
	
	//增加自定义工具栏按钮		（该功能完全实现）
    this.AppendTools = function(Index, Caption, Icon)
    {
    	parseInt(Index);
    	var customtoolbar = this.obj.CustomToolbar;
    	customtoolbar.AddToolButton(Index, Caption, Icon, Caption, 0); //Icon为图片路径
    }


	// 按钮是否有效（需要修改iWebOffice2015核心代码）
	this.DisableTools = function(Caption, Flag) 
	{
		
	}

   //自定义工具栏按钮是否显示（需要修改iWebOffice2015核心代码）
    this.VisibleTools = function(Caption, Flag)
    {
    	var customtoolbar = this.obj.CustomToolbar;
    	customtoolbar.DeleteToolButton(Caption);
    }
	
    //增加菜单（该功能未完全实现）
    var MenuFile;
    this.AppendMenu = function(Index, Caption)
    {
    	var custommenu = this.obj.CustomMenu;
    	if (MenuFile == undefined || MenuFile == null) 
    	{
    		custommenu.Clear();
    		MenuFile = custommenu.CreatePopupMenu();
    		custommenu.Add(MenuFile, "文件(&F)");
    	}
    	custommenu.AppendMenu(MenuFile, Index, false, Caption);
    	custommenu.Update();
    }

	/* 获取当前文档打开类型，以后缀名来区别 */		//（该功能已完整实现）
	this.WebGetDocSuffix = function() 
	{
		try {
			var docType = this.getDocType(this.FileType); // 判断是文档还是表格
			var FileTypeValue = 0; // 判断打开文档的值 0：doc，12：docx，51：xls，56：xlsx
			if (docType == this.DocType.WORD) // word 获取vba值的方法
			{
				this.Activate(true);
				FileTypeValue = this.obj.ActiveDocument.SaveFormat;
			}
			if (docType == this.DocType.EXECL) { // Execl 获取方法
				this.obj.ExitExcelEditMode(); // 退出当前编辑模式
				FileTypeValue = this.obj.ActiveDocument.FileFormat;
				if (FileTypeValue < 0) { // 2003不支持该属性
					FileTypeValue = 56;
				}
			}
			return this.DocSuffixType[FileTypeValue];
		} catch (e) {
			return this.FileType;
		}
	}
	
	// iWebOffice打开的文档全路径。	//（该功能已完整实现）
	this.WebFullName = function() 
	{
		return this.obj.FullName;
	}

	// 更加书签插入图片	//（该功能已完整实现）
	this.InsertImageByBookMark = function() 
	{
		if(this.obj.ActiveDocument.BookMarks.Exists(this.BookMark)){
        	this.obj.ActiveDocument.Bookmarks(this.BookMark).Select();
        }
		this.obj.ActiveDocument.Application.Selection.InlineShapes
				.AddPicture(this.FilePath + this.ImageName);
		this.obj.ActiveDocument.InlineShapes.Item(1).ConvertToShape(); // 转为浮动型
		this.obj.ActiveDocument.Shapes.Item(1).WrapFormat.Type = 5; // 0:四周型 1：紧密型 2：穿越型环绕 3：浮于文字上方 4：上下型环绕 5：衬于文字下方 6：浮于文字上方
		return true;
	}

	// 下载文档		//（该功能已完整实现）
	this.DownloadToFile = function(DownFileName, SavePathName) 
	{
		var httpclient = this.obj.Http;
		var URL = this.WebUrl.substring(0, this.WebUrl.lastIndexOf("/"));
		httpclient.ShowProgressUI = this.ShowWindow;// 隐藏进度条
		if (httpclient.Open(this.HttpMethod.Get, URL + "/Document/"
				+ DownFileName, false)) {// 指定下载模板的名称
			if (httpclient.Send()) {
				if (httpclient.Status == 200) {
					httpclient.ResponseSaveToFile(SavePathName + DownFileName);
					httpclient.Clear();
					return true;
				}
			}
		}
		httpclient.Close();
		return false;
	}

	// 手写签批		//（该功能已完整实现）
	this.HandWriting = function(penColor, penWidth) 
	{
		var handwritting = this.obj.Handwritting;
		var handsetting = handwritting.DrawingSetting;
		handsetting.PenThicker = penWidth;
		handsetting.PenColor = penColor;
		handwritting.AnnotateDraw();
		this.ShowMenuBar(false);// 签批时隐藏菜单栏
		this.ShowToolBars(false);// 签批时隐藏工具栏

	}
	
	// 停止手写签批		//（该功能已完整实现）
	this.StopHandWriting = function() 
	{
		var handwritting = this.obj.Handwritting;
		handwritting.StopAnnotate();
		this.ShowMenuBar(true);// 停止签批时显示菜单栏
		this.ShowToolBars(true);// 停止签批时显示工具栏
	}
	
	// 文字签名		//（该功能已完整实现）
	this.TextWriting = function() 
	{
		var handwritting = this.obj.Handwritting;
		var textsetting = handwritting.TextSetting;
		textsetting.TextSize = 32;
		textsetting.TextColor = 0xbb00ff;
		textsetting.FontName = "宋体";
		handwritting.AnnotateText();
		this.ShowMenuBar(false);// 签批时隐藏菜单栏
		this.ShowToolBars(false);// 签批时隐藏工具栏
	}
	
	// 图形签批		//（该功能已完整实现）
	this.ShapeWriting = function() 
	{
		var handwritting = this.obj.Handwritting;
		var shapesetting = handwritting.ShapeSetting;
		shapesetting.ShapeType = 0;
		shapesetting.BackgroundColor = 0xffffff;
		shapesetting.BorderColor = 0xff0000;
		shapesetting.BorderWidth = 6;
		handwritting.AnnotateShape();
		this.ShowMenuBar(false);// 签批时隐藏菜单栏
		this.ShowToolBars(false);// 签批时隐藏工具栏
	}
	
	// 取消上一次签批		//（该功能已完整实现）
	this.RemoveLastWriting = function() 
	{
		var handwritting = this.obj.Handwritting;
		handwritting.RemoveLast();
	}
	
	// 显示某用户的签批		//（该功能已完整实现）
	this.ShowWritingUser = function(bVal, username) 
	{
		var strxml = this.obj.GetAnnotations();
		var json = eval('(' + strxml + ')');
		if (username != "" && username != null && username != undefined) {
			for (var i = 0; i < json.Annotations.length; i++) {
				if (json.Annotations[i].Annotation.User != username) {
					var id = json.Annotations[i].Annotation.ID;
					this.obj.GetAnnotationByID(id).Visible = bVal;
				}
			}
		} else {
			for (var i = 0; i < json.Annotations.length; i++) {
				var id = json.Annotations[i].Annotation.ID;
				this.obj.GetAnnotationByID(id).Visible = bVal;
			}
		}
	}
	
	//得到服务器setMsgByName的值并发送到前台		//（该功能已完整实现）
	this.GetDataToSend= function()
	{
		var httpclient = this.obj.Http; // 设置http对象
		httpclient.Clear();
		var ReturnValue = httpclient.GetResponseHeader("RName");// 获取返回值
		var jsonObj = eval('(' + ReturnValue + ')');    
		for(var i  in jsonObj){
			this.WebSetMsgByName(i,jsonObj[i]);
		}
	}
	
	//手写签批控件属性和方法
	this.IWR = null;
	//创建手写签批控件对象
	this.CreateRevision = function()
	{
		//判断iWebOffice2015对象是否存在
		if(this.obj == null){
			this.Status = "iWebOffice2015对象未初始化";
			return false;
		}
		//判断手写签批控件是否已创建
		if(this.IWR != null){
			this.Status = "手写组件对象已存在";
			return false;
		}
		//创建手写签批控件
		var ret = this.obj.CreateNew("iWebRevision.iWebRevisionCtrl.1");
		if(ret != 0){
			this.Status = "创建手写组件对象失败";
			return false;
		}
		//隐藏标题栏、状态栏、菜单栏、自定义工具栏
		this.ShowTitleBar(false);
		this.ShowMenuBar(false);
		this.ShowStatusBar(false);
		this.ShowCustomToolbar(false);
		this.obj.Style.BorderStyle = 0;
		//获取手写控件对象
		this.IWR = this.obj.Application;
		return true;
	}
}