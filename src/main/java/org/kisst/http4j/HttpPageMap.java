package org.kisst.http4j;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpPageMap implements HttpPage {
	private final LinkedHashMap<String, HttpHandler> equals=new LinkedHashMap<String, HttpHandler>();
	private final LinkedHashMap<String, HttpHandler> startsWith=new LinkedHashMap<String, HttpHandler>();

	private final HttpPage defaultPage;
	private final String path;
	public HttpPageMap(String path, HttpPage defaultPage) { 
		this.path=path;
		this.defaultPage=defaultPage;
	}
	
	//public HttpPageMap setDefaultPage(HttpPage defaultPage) { this.defaultPage=defaultPage;  return this; }
	public HttpPageMap addPage(HttpPage page) { return addHandler(page.getPath(), page); }
	public HttpPageMap addHandler(String path, HttpHandler servlet) {
		while (path.startsWith("/"))
			path=path.substring(1);
		if (path.endsWith("*")) {
			path=path.substring(0, path.length()-1);
			startsWith.put(path, servlet);
			//System.out.println("added path that startsWith {"+path+"}");
		}
		else {
			equals.put(path, servlet);
			//System.out.println("added path that equals {"+path+"}");
		}
		return this;
	}
	public String getPath() { return path; }

	@Override public void handle(String path, HttpServletRequest request,HttpServletResponse response) {
		while (path.startsWith("/"))
			path=path.substring(1);
		//System.out.println("Searching for {"+path+"}");
		HttpHandler page = equals.get(path);
		String subPath="";
		if (page==null) {
			for (String prefix : startsWith.keySet()) {
				if (path.startsWith(prefix)) {
					subPath=path.substring(prefix.length());
					page=startsWith.get(prefix);
					break;
				}
			}
			if (page==null)
				page=defaultPage;
		}
		//System.out.println("subPath {"+subPath+"}");
		page.handle(subPath, request, response);
	}
}