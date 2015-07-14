package org.kisst.http4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public interface HttpPage {
	public void handle(HttpServletRequest request, HttpServletResponse response);
}
