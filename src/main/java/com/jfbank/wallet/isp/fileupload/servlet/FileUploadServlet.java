package com.jfbank.wallet.isp.fileupload.servlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfbank.wallet.isp.fileupload.util.JsonUtil;
import com.jfbank.wallet.isp.fileupload.util.PropUtil;

/**
 * 文件上传服务
 */
public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(FileUploadServlet.class);

	private static final String SAVE_PATH = "/app/9f_cdn_data/";
	
	private static final long FILE_MAX_SIZE = 1024 * 1024 * 200;
	
	private static final String prefix = PropUtil.getValue("force_prefix");

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		logger.info("request remote : " + request.getRemoteAddr() + " | " + request.getRemoteHost() + " | "
				+ request.getRemotePort() + " | " + request.getRemoteUser());
		RequestContext req = new ServletRequestContext(request);
		Map<String, String> results = new HashMap<String, String>();
        if (FileUpload.isMultipartContent(req)) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload fileUpload = new ServletFileUpload(factory);
            fileUpload.setHeaderEncoding("UTF-8");
            fileUpload.setFileSizeMax(FILE_MAX_SIZE);
            List<FileItem> items = new ArrayList<FileItem>();
            try {
                items = fileUpload.parseRequest(request);
	            String fileName = "";
	            File newFile = null;
	            Iterator<FileItem> it = items.iterator();
	            while (it.hasNext()) {
	                FileItem fileItem = (FileItem) it.next();
	                if (fileItem.isFormField()) {
	                	if("uploadFileName".equals(fileItem.getFieldName())) {
	                		fileName = new String(fileItem.getString().getBytes("ISO-8859-1"), "UTF-8");
	                		if(prefix==""){
	                			
	                		}
	                		else{
	                			if(fileName.indexOf(prefix) == -1) {
	                				throw new Exception("文件url不正确");
		                		}
	                		}
	                		
	                	}
	                } else {
	                	logger.info(fileItem.getFieldName() + " | "
	                            + fileItem.getName() + " | " + fileItem.isInMemory()
	                            + " | " + fileItem.getContentType() + " | "  + fileItem.getSize());
	                    if (fileItem.getName() != null && fileItem.getSize() != 0) {
	                        File fullFile = new File(fileItem.getName());
	                        newFile = new File(fullFile.getName());
							fileItem.write(newFile);
	                    } else {
	                    	logger.info("no file choosen or empty file");
	                    }
	                }
	            }
	            saveFiles(fileName, newFile);
	            results.put("code", "0000");
	            results.put("msg", "ok");
            } catch (Exception e) {
            	logger.error(e.getMessage());
            	results.put("code", "1111");
            	results.put("msg", e.getMessage());
            	e.printStackTrace();
            }
        }
		response.getWriter().append(JsonUtil.toJson(results));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private void saveFiles(String fileName, File file) throws IOException {
		String savePath = fileName.substring(fileName.indexOf(prefix) + prefix.length()); //��������
		savePath = SAVE_PATH + savePath.substring(0, savePath.indexOf(file.getName()));
		// 查看是不存在目录如果不存在，则创建
				File dir = new File(savePath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File newFile = new File(savePath + "/" + file.getName());
				if (newFile.exists()) {
					throw new IOException("已存在文件名为【" + file.getName() + "]的文件");
				}
				FileUtils.copyFileToDirectory(file, dir);
				logger.info("文件保存成功： " + savePath + "/" + file.getName());
			}
}