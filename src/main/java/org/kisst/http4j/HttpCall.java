package org.kisst.http4j;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpCall {
	public final HttpServletRequest request;
	public final HttpServletResponse response;
	private PrintWriter out;

	protected HttpCall(HttpCall call) { this(call.request,call.response); }
	public HttpCall(HttpServletRequest request,HttpServletResponse response) {
		this.request=request;
		this.response=response;
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

	public void handleGet(String subPath) { invalidPage(); }
	public void handlePost(String subPath) { invalidPage(); }
	
	
	public PrintWriter getWriter() {
		try { 
			if (out==null)
				out = response.getWriter();
			return out;
		}
		catch (IOException e) { throw new RuntimeException(e);}
	}
	public void output(String text) { getWriter().append(text); }
	public void close() {
		if (out!=null)
			out.close();
		out=null;
	}

	public void invalidPage() { throw new RuntimeException("Invalid page "+request.getRequestURI()); }

	public void redirect(String url) {
		try {
			response.sendRedirect(url);
		}
		catch (IOException e) { throw new RuntimeException(e);}
	}
	
	public String calcSubPath(String path) {
		while (path.startsWith("/"))
			path=path.substring(1);
		int pos=path.indexOf("/");
		if (pos<=0)
			return "";
		path=path.substring(pos+1);
		while (path.startsWith("/"))
			path=path.substring(1);
		return path;
	}

}
