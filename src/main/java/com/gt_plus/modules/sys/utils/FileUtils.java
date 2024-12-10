package com.gt_plus.modules.sys.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.utils.OpenOfficeConverter;

public class FileUtils {
	/**
	 * 校验文件扩展名
	 */
	public static boolean checkExtension(MultipartFile file){
		String allowedExtensions = Global.getConfig("allowedExtensions");
		String[] fileSplit = file.getOriginalFilename().split("\\.");
		String extension = fileSplit[fileSplit.length-1];
		return allowedExtensions.contains(extension);
	}
	
	/**
	 * 获取UUID
	 */
	public static UUID getUuid(){
		return UUID.randomUUID();
	}
	
	/**
	 * 获取UUID.toString
	 */
	public static String getUuidToString(){
		return UUID.randomUUID().toString();
	}
	
	/**
	 * 获取文件上传路径
	 */
	public static String getUploadPath(HttpServletRequest request, String randomUUID){
		return getRealPath(request) + getRelativePath(request, randomUUID);
	}
	
	/**
	 * 获取物理路径
	 */
	public static String getRealPath(HttpServletRequest request){
		return request.getSession().getServletContext().getRealPath(Global.getConfig("fileUploadFolder"));
		
	}
	
	/**
	 * 获取相对路径
	 */
	public static String getRelativePath(HttpServletRequest request, String randomUUID){
		return new StringBuffer().append(request.getRequestURI())
				.append(new SimpleDateFormat(Global.getConfig("uploadPathDateFormat"))
				.format(new Date())).append(randomUUID).append("/").toString();
		
	}
	
	/**
	 * 获取文件下载路径
	 */
	public static String getDownloadPath(HttpServletRequest request, String randomUUID){
		return new StringBuffer().append(request.getContextPath()).append(Global.getConfig("fileUploadFolder"))
				.append(request.getRequestURI()).append(new SimpleDateFormat(Global.getConfig("uploadPathDateFormat"))
				.format(new Date())).append(randomUUID).append("/").toString();
	}
	
	/**
	 * 文件上传
	 */
	//public static String fileUpload(HttpServletRequest request, MultipartFile file){
	//	return fileUpload(request, file, false);
	//}
	
	/**
	 * 文件上传，是否转PDF
	 */
	public static String fileUpload(HttpServletRequest request, MultipartFile file, boolean toPdf){
		//boolean toPdf = true;
		try {
			if(file!=null){
				if(checkExtension(file)){
					String randomUUID = getUuidToString();
					String uploadPath = getUploadPath(request, randomUUID);
					String downloadPath = getDownloadPath(request, randomUUID);
					new File(uploadPath).mkdirs();
					IOUtils.copy(file.getInputStream(), new FileOutputStream(uploadPath + file.getOriginalFilename()));
					Map<String, String> map = new HashMap<String, String>();
					map.put("uuid", randomUUID);
					map.put("filename", file.getOriginalFilename());
					map.put("fileSize", getFileSize(file));
					map.put("fileUrl", downloadPath + file.getOriginalFilename());
					
					//转PDF
					if (toPdf) {
						String inputFile = uploadPath + file.getOriginalFilename();
						String fileName = file.getOriginalFilename();
						fileName = fileName.substring(0, fileName.lastIndexOf("."));
						if (OpenOfficeConverter.convertToPdf(inputFile, uploadPath, fileName)) {
							map.put("pdfUrl", downloadPath + fileName + ".pdf");
						} else {
							map.put("pdfUrl", "");
						}
					} else {
						map.put("pdfUrl", "");
					}
					
					return new Gson().toJson(map);
				}else{
					return new Gson().toJson("IllegalExtension");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取文件大小
	 */
	public static String getFileSize(MultipartFile multipartFile){
		long size = multipartFile.getSize();
		if(size / 1024 < 1024){
			return "(" + new DecimalFormat("0.0").format(size/1024D) + "K)";
		}else{
			return "(" + new DecimalFormat("0.0").format(size/1024D/1024D) + "M)";
		}
	}
	
}
