package org.kisst.http4j;

public interface HttpPage extends HttpHandler {
	default public String getPath() { return getClass().getSimpleName(); }
	//public void handle(HttpServletRequest request, HttpServletResponse response);
}
