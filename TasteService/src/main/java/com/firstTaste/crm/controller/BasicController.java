package com.firstTaste.crm.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * controller层的基本父类，里面封装了一些共用的方法.
 * 
 * @author yinwenjie
 */
public class BasicController {
	/**
	 * 向response中写入字符串,一般返回错误信息的时候，会使用该打印方法
	 * @param response HttpServletResponse
	 * @param msg 写入的字符串
	 */
	protected void writeResponseMsg(HttpServletResponse response, Object jsonEntity) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (Exception e) {
			
		}
		
		String json = JSONArray.toJSONString(jsonEntity , SerializerFeature.DisableCircularReferenceDetect);
		out.print(json);
	}
}