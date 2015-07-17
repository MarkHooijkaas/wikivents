package org.kisst.http4j;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class HttpBasicPage implements HttpPage {
	@Override public void handle(String path, HttpServletRequest request, HttpServletResponse response) {
		String method = request.getMethod();
		System.out.println("handling "+method+" "+request.getRequestURI());
		System.out.println("handling "+method+" "+path);
		if ("GET".equals(method))
			handleGet(path, request,response);
		else if ("POST".equals(method))
			handlePost(path, request,response);
	}

	public void handleGet(String path, HttpServletRequest request, HttpServletResponse response) {}
	public void handlePost(String path, HttpServletRequest request, HttpServletResponse response) {}
	
	public void redirect(HttpServletResponse response, String url) {
		try {
			response.sendRedirect(url);
		}
		catch (IOException e) { throw new RuntimeException(e);}
	}
	
}
