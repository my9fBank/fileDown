package com.jfbank.wallet.isp.pos.common.util;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

public class SendFileUtil {
	private static Logger logger = Logger.getLogger(SendFileUtil.class);
	public static String sendFile(String reqURL, File file, String uploadFileName){
		logger.debug("请求接口地址："+reqURL);
		logger.debug("文件原名车："+file.getName());
		logger.debug("文件读取路径"+uploadFileName);
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpPost httpPost = new HttpPost(reqURL);
		String responseContent = "{\"code\":\"5555\",\"msg\":\"图片上传服务不能调用\"}";
		try{
			HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)  
	                .addPart("uploadFile", new FileBody(file)) //uploadFile对应服务端类的同名属性<File类型>  
	                .addPart("uploadFileName", new StringBody(uploadFileName,ContentType.create("text/plain", "UTF-8")))  
	                .setCharset(CharsetUtils.get("UTF-8")).build(); 
			httpPost.setEntity(reqEntity);
			HttpResponse response = httpClient.execute(httpPost);
			logger.debug("响应========"+response);
			HttpEntity entity = response.getEntity();
			logger.debug("服务器响应结果==="+entity);
			if (null != entity) {
				responseContent = EntityUtils.toString(entity, Charset.forName("UTF-8"));
				EntityUtils.consume(entity);
				logger.debug("调用图片返回结果========"+responseContent);
				return responseContent;
			}
			logger.debug("接口返回数据："+responseContent);
		}catch(Exception e){
			logger.error("与[" + reqURL + "]通信过程中发生异常,堆栈信息如下", e);
			
			responseContent = "{\"code\":\"5555\",\"msg\":\"图片上传与服务器通信过程发生异常\"}";
		}finally{
			httpClient.getConnectionManager().shutdown();
		}
		logger.debug("调用图片返回结果========"+responseContent);
		return responseContent;
	}
}
