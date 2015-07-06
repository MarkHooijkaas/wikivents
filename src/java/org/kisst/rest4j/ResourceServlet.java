package org.kisst.rest4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.struct4j.JsonOutputter;
import org.kisst.struct4j.Struct;

public class ResourceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final Resource resource;
	public ResourceServlet(Resource resource) {this.resource=resource; }

	@Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		resource.createResource(getPostedStruct(req));
	}
	@Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		// TODO: 
		// recognize query to get multiple
		// Handle parameters
		// - fields=name,street,city
		// - embed=user.name,event.title
		// - sort=+name,-date
		// - select?? user=Mark&city=Groningen&future
		// - pretty/compact (json, xml)
		boolean pretty = true;
		String[] fields=null;
		String[] embed=null;
		String selects="";
		Enumeration<?> it = req.getParameterNames();
		while (it.hasMoreElements()) {
			String param=(String) it.nextElement();
			if ("pretty".equals(param))
				pretty=Boolean.getBoolean(req.getParameter(param));
			else if ("fields".equals(param))
				fields=req.getParameter(param).split("[,]");
			else if ("embed".equals(param))
				embed=req.getParameter(param).split("[,]");
			else
				selects+=","+param;
		}
		
		Struct struct = resource.getSingleResource(getKey(req));
		resp.setContentType("applicatio/json;charset=UTF-8");
		PrintWriter out=null;
		try {
			out = resp.getWriter();
			if (pretty)
				new JsonOutputter("\t").write(out,struct);
			else
				new JsonOutputter(null).write(out,struct);
		} 
		catch (IOException e) { throw new RuntimeException(e); }
		finally {
			if (out!=null)
				out.close();  
		}
	}
	@Override protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		resource.updateResource(getKey(req), getPostedStruct(req));
	}

	@Override protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		resource.deleteResource(getKey(req));
	}

	private String getKey(HttpServletRequest req) {
		String path = req.getPathInfo();
		while (path.startsWith("/"))
			path=path.substring(1);
		if (path.length()==0)
			return null;
		if (path.startsWith(":"))
			path=path.substring(1);
		int pos=path.indexOf('/');
		if (pos>0)
			return path.substring(0,pos);
		return path;
	}

	private Struct getPostedStruct(HttpServletRequest request) {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			try {
				while ((line = reader.readLine()) != null)
					jb.append(line);
			} 
			finally { reader.close(); }
		}
		catch (IOException e) { throw new RuntimeException(e); }
		return null; //new SimpleProps(jb.toString());
	}
}