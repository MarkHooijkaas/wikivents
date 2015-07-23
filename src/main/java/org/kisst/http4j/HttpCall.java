package org.kisst.http4j;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpCall {
	public final HttpServletRequest request;
	public final HttpServletResponse response;
	public final  String path;
	private PrintWriter out;

	public HttpCall(HttpCall call, String path) { this(path,call.request,call.response); }
	public HttpCall(String path, HttpServletRequest request,HttpServletResponse response) {
		this.request=request;
		this.response=response;
		this.path=path;
	}
	
	public void handle(String subPath) {
		String method = request.getMethod();
		if ("GET".equals(method))
			handleGet(subPath);
		else if ("POST".equals(method))
			handlePost(subPath);
		else
			throw new RuntimeException("Unknown method type "+method);
	}

	public void handleGet(String subPath) {}
	public void handlePost(String subPath) {}
	
	
	public void output(String text) {
		try { 
			if (out==null)
				out = response.getWriter();
		    out.append(text);
		} 
		catch (IOException e) { throw new RuntimeException(e);}
	}
	public void close() {
		if (out!=null)
			out.close();
		out=null;
	}

	
	public void redirect(String url) {
		try {
			response.sendRedirect(url);
		}
		catch (IOException e) { throw new RuntimeException(e);}
	}

}
