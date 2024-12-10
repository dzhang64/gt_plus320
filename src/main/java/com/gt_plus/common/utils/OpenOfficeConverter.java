package com.gt_plus.common.utils;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.gt_plus.common.config.Global;

@Service
@Lazy(false)
public class OpenOfficeConverter {
	
	//static{
	//	init();
	//}
	
	private static Logger log = LoggerFactory.getLogger(OpenOfficeConverter.class);
	
    public static void startService(){
    	if (false == StringUtils.isEmpty(Global.getConfig("OfficeHome"))) {
    		DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
            try {
            	log.debug("准备启动服务....");
            	//设置OpenOffice.org安装目录
                configuration.setOfficeHome(Global.getConfig("OfficeHome"));
                //设置转换端口，默认为8100
                configuration.setPortNumber(Integer.parseInt(Global.getConfig("PortNumber")));
                //设置任务执行超时为5分钟
                configuration.setTaskExecutionTimeout(Long.parseLong(Global.getConfig("TaskExecutionTimeout")));
                //设置任务队列超时为24小时
                configuration.setTaskQueueTimeout(Long.parseLong(Global.getConfig("TaskQueueTimeout")));

                officeManager = configuration.buildOfficeManager();
                officeManager.start();    //启动服务
                log.debug("office转换服务启动成功!");
            } catch (Exception ce) {
            	log.debug("office转换服务启动失败!详细信息:" + ce);
            }
    	}
    }
    
    public static void stopService(){
    	log.debug("关闭office转换服务....");
        if (officeManager != null) {
            officeManager.stop();
        }
        log.debug("关闭office转换成功!");
   }
    
    public static boolean convertToPdf(String inputFile, String outPath,String fileName) {
    	//if(openOfficeConverter == null) init();
    	boolean isOK = false;
    	if (officeManager == null) {
    		log.debug("office转换服务未启动，转PDF失败！");
    	} else {
    		String outputFile = generateByFilename(outPath,fileName, PDF);
            File tempFile = null;
            if(inputFile.endsWith(".txt")){
                String odtFile = FileUtils.getFilePrefix(inputFile)+".odt";
                tempFile=new File(odtFile);
                if(tempFile.exists()){
                	log.debug("odt文件已存在！");
                    inputFile = odtFile;
                }else{
                    try {
                        FileUtils.copyFile(inputFile,odtFile);
                        tempFile=new File(odtFile);
                        inputFile = odtFile;
                    } catch (Exception e) {
                    	log.debug("文档不存在！");
                        e.printStackTrace();
                    }
                }
            }
            OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
            File output=new File(outputFile);
            converter.convert(new File(inputFile),output);
            isOK = true;
            if(tempFile!=null && tempFile.exists()){
            	tempFile.delete();
            }
    	}
        return isOK;
    }
    
    @PostConstruct
    public static void init(){
    	OpenOfficeConverter coverter = new OpenOfficeConverter();
    	coverter.startService();
		openOfficeConverter=coverter;
		//test
		//convertToPdf("D:/test/33.txt", "D:/test/", "new");
    }
    
    @PreDestroy
    public static void destroy(){
    	openOfficeConverter.stopService();
    }
    
    public static void main(String[] args){
        OpenOfficeConverter  cov = new OpenOfficeConverter();
        Long s1=System.currentTimeMillis();
        cov.startService();
        log.debug("启动消耗时间+"+(System.currentTimeMillis()-s1)/1000.0);
        
        cov.convertToPdf("D:/test/33.txt", "D:/test/", "new");
        
        log.debug("转换消耗时间"+(System.currentTimeMillis()-s1)/1000.0);
        cov.stopService();
        log.debug("停止消耗时间"+(System.currentTimeMillis()-s1)/1000.0);
    }

    private static OpenOfficeConverter openOfficeConverter;
    private static OfficeManager officeManager; 
    public static final String HTML = "html";
    public static final String PDF = "pdf";
    public static final String TXT = "txt";
    public static final String DOC = "doc";
    public static final String DOCX = "docx";
    public static final String XLS = "xls";
    public static final String XLSX= "xlsx";
    public static final String PPT = "ppt";
    public static final String PPTX = "pptx";
    public static final String WPS = "wps";
    private static String officeHome;
    private static int port;

	public OpenOfficeConverter() {
		super();
	}
	
	public OpenOfficeConverter(String officeHome, int port) {
		super();
		this.officeHome = officeHome;
		this.port = port;
	}
	
	public String getOfficeHome() {
		return officeHome;
	}

	public void setOfficeHome(String officeHome) {
		this.officeHome = officeHome;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	private static String generateByFilename(String path,String fileName, String ext) {
		return path + fileName + "." + ext;
	}
}
