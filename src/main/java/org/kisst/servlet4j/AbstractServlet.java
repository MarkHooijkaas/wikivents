package org.kisst.servlet4j;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.util.Base64;



public abstract class AbstractServlet {
	
	abstract public void handle(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException;

	protected String getUser(HttpServletRequest req, HttpServletResponse res) {
		return getBasicUser(req,res);
	}

	private  String getBasicUser(HttpServletRequest req, HttpServletResponse res) {
		String authhead=req.getHeader("Authorization");

		if(authhead!=null)
		{
			String usernpass;
			try {
				usernpass = new String(Base64.decode(authhead.substring(6)));
			} catch (IOException e) { throw new RuntimeException(e);}
			String user=usernpass.substring(0,usernpass.indexOf(":"));
			String password=usernpass.substring(usernpass.indexOf(":")+1);
		}
		res.setHeader("WWW-Authenticate","Basic realm=\"Authorisation test servlet\"");
		try {
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "");
		} catch (IOException e) { throw new RuntimeException(e);}
		return null;
	}
}
