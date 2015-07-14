package org.kisst.http4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class HttpBasicPage implements HttpPage {
	@Override public void handle(HttpServletRequest request, HttpServletResponse response) {
		String method = request.getMethod();
		if ("GET".equals(method))
			handleGet(request,response);
		else if ("GET".equals(method))
			handlePost(request,response);
	}

	public void handleGet(HttpServletRequest request, HttpServletResponse response) {}
	public void handlePost(HttpServletRequest request, HttpServletResponse response) {}
	
}
