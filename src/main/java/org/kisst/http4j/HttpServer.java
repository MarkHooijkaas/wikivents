package org.kisst.http4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.kisst.item4j.struct.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServer extends AbstractHandler {
	private final static Logger logger=LoggerFactory.getLogger(HttpServer.class);
	private final HashMap<String, HttpPage> handlerMap=new HashMap<String, HttpPage>();

	private Server server=null;
	protected final Struct props;

	public HttpServer(Struct props)
	{
		this.props=props;
	}
	
	public void addPage(String url, HttpPage servlet) {
		handlerMap.put(url, servlet);
	}
	
	public void startListening() {
		int port=props.getInteger("admin.port",8080);
		logger.info("admin site running on port {}",port);
		server = new Server(port);
        server.setHandler(this);
        
        try {
			server.start();
		} catch (Exception e) { throw new RuntimeException(e);}
	}


	public void join() {
		try {
			server.join();
		} catch (Exception e) { throw new RuntimeException(e);}
		logger.info("web server stopped");
		server=null;
	}

	public void stopListening() {
		final Server server=this.server; // remember it, because it will set it self to null
		logger.info("Stopping web server");
		try {
			//server.setGracefulShutdown(1000);
			//Thread.sleep(1000);
			server.stop();
			//Thread.sleep(3000);
			for (Connector conn : server.getConnectors())
				conn.stop();
			server.destroy();
		} catch (Exception e) { throw new RuntimeException(e);}
	}

	
	public void handle(String target,Request baseRequest,HttpServletRequest request,HttpServletResponse response) 
	{
		String path=request.getRequestURI();
        baseRequest.setHandled(true);
        try {
        	for (String prefix : handlerMap.keySet()) {
        		if (path.startsWith(prefix)) {
        			handlerMap.get(prefix).handle(request, response);
        			return;
        		}
        	}
        	handlerMap.get("default").handle(request, response);
        }
        catch (Exception e) {
        	try {
				PrintWriter out = response.getWriter();
				out.println(e.getMessage());
				out.println("<pre>");
				e.printStackTrace(out);
				out.println("</pre>");
			} catch (IOException e1) {
				// ignore the new error, and now write to the logfile anyway
				logger.error("Error when handling "+path, e);
			}
        }
	}
}
