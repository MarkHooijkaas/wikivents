package org.kisst.http4j;

import javax.servlet.http.HttpServletRequest;

import org.kisst.item4j.struct.Struct;
import org.kisst.item4j.struct.StructHelper;

public class HttpRequestStruct implements Struct {
	private final HttpServletRequest request;
	
	public HttpRequestStruct(HttpServletRequest request) {this.request=request;	} 
	public HttpRequestStruct(HttpCall call) { this(call.request);}

	@Override public String toString() { return StructHelper.toShortString(this); }
	@Override public Object getDirectFieldValue(String name) { return request.getParameter(name); }
	@Override public Iterable<String> fieldNames() { return request.getParameterMap().keySet(); }
}
