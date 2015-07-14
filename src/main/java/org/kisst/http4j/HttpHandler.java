package org.kisst.http4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public interface HttpHandler {
	default public void handle(String subPath, HttpServletRequest request, HttpServletResponse response) {}
}
