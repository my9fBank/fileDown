package com.jfbank.wallet.isp.fileupload.util;

import java.io.InputStream;
import java.util.Properties;

public class PropUtil
{

	private static PropUtil instance = null;
	private Properties props = null ;
	private static String FILEPATH = "/smart.properties";
	
	private static synchronized PropUtil getInstatance(){
		if(instance == null){
			instance = new PropUtil();
		}
		return instance;
	}
	
	private PropUtil(){
		loadProps();
	}

	private void loadProps() {
		props = new Properties();
		InputStream in = null;
		try {
			in = getClass().getResourceAsStream(FILEPATH);
			props.load(in);
		}
		catch (Exception e) {
			//此处可根据你的日志框架进行记录
			System.err.println("Error reading conf properties in PropertyManager.loadProps() " + e);
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				//此处可根据你的日志框架进行记录
			}
		}
	}
	
	private String getProp(String key) {
		String value = props.getProperty(key);
		return value == null ? "" : value.trim();
	}
	
	/**
	 * 根据key获取对应value
	 * @param key
	 * @return
	 */
	public static String getValue(String key){
		return getInstatance().getProp(key);
	}
	
	/**
	 * 根据key获取对应value，如果为空则返回默认的value
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getValue(String key,String defaultValue){
		String value = getInstatance().getProp(key);
		return "".equals(value) ? defaultValue : value;
	}
	/**
	 * 根据key获取对应value，如果为空则返回默认的value;特别注意：该方法可以解析properties文件中的${}
	 * @param key
	 * @param defaultValue
	 * @param isAnalysis 是否解析${}部分
	 * @return
	 */
	public static String getValue(String key,String defaultValue,boolean isAnalysis){
		String value = getInstatance().getProp(key);
		if("".equals(value)){
			value=defaultValue;
		}else if(isAnalysis){
			value=treatVar(value);
		}
		return value;
	}
	/**
	 * 根据key获取对应value，如果为空则返回默认的value;特别注意：该方法可以解析properties文件中的${}
	 * @param key
	 * @param isAnalysis 是否解析${}部分
	 * @return
	 */
	public static String getValue(String key,boolean isAnalysis){
		return getValue(key, "", isAnalysis);
	}
	/**
	 * 解析${}
	 * @param value
	 * @return
	 */
	private static String treatVar(String value) {
		int start = -1, end = -1;
		if (!StringUtil.isEmpty(value) && (start = value.indexOf("${")) != -1 && (end = value.substring(start, value.length()).indexOf("}")) != -1) {
			String rkey = value.substring(start + 2, end + start);
			String rvalue = getInstatance().getProp(rkey);
			if (!StringUtil.isEmpty(rvalue)) {
				value = value.replaceAll("\\$\\{" + rkey + "\\}", rvalue);
				value = treatVar(value);
			}
		}
		return value;
	}

}
