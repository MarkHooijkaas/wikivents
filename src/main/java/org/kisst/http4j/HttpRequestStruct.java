package org.kisst.http4j;

import javax.servlet.http.HttpServletRequest;

import org.kisst.item4j.struct.BaseStruct;

public class HttpRequestStruct extends BaseStruct{
	private final HttpServletRequest request;
	
	public HttpRequestStruct(HttpServletRequest request) {this.request=request;} 
	
	@Override public Object getDirectFieldValue(String name) { return request.getParameter(name);}
	@SuppressWarnings("unchecked")
	@Override public Iterable<String> fieldNames() { return request.getParameterMap().keySet(); }

}
