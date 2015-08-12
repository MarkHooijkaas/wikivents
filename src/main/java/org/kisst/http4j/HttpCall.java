package org.kisst.http4j;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.http4j.HttpServer.HttpException;
import org.kisst.util.Base64;

public class HttpCall {
	public final HttpServletRequest request;
	public final HttpServletResponse response;
	private PrintWriter out=null;

	private static final String COOKIE_NAME = "Hooi4jUser";
	private static final int LOGIN_DURATION= 7*24*60*60; // a week in seconds

	public final String userid;

	protected HttpCall(HttpCall call) { this(call.request,call.response); }
	public HttpCall(HttpServletRequest request,HttpServletResponse response) {
		this.request=request;
		this.response=response;
		this.userid=getUserId();
	}
	
	public boolean isGet() { return "GET".equals(request.getMethod()); }
	public boolean isPost() { return "POST".equals(request.getMethod()); }
	public boolean isAjax() { return "true".equals(request.getParameter("ajax")); }
	
	public void handle(String subPath) {
		String method = request.getMethod();
		if (isGet())
			handleGet(subPath);
		else if (isPost())
			handlePost(subPath);
		else
			throw new RuntimeException("Unknown method type "+method);
	}

	public void handleGet(String subPath) { invalidPage(); }
	public void handlePost(String subPath) { invalidPage(); }
	
	public void sendError(int code, String message) {
		try {
			response.sendError(code, message);
		} 
		catch (IOException e) { throw new RuntimeException(e);}
	}

	
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

	public UnauthorizedException throwUnauthorized(String message) { throw new UnauthorizedException(message); }
	
	public boolean isAuthenticated() { return userid!=null; }
	public void ensureUser() { if (! isAuthenticated()) throwUnauthorized("Not Authenticated user"); }
	public void ensureUserId(String userId) {	
		ensureUser();
		if (userid== null || ! userid.equals(userId))
			throwUnauthorized("Not Authorized expected "+userId+" but got "+userid);
	}
	
	public void clearCookie() {
		Cookie cookie = new Cookie(COOKIE_NAME, null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);		
	}

	public void setCookie(String userid) {
		Cookie cookie = new Cookie(COOKIE_NAME, createCookieString(userid, System.currentTimeMillis()));
		cookie.setMaxAge(LOGIN_DURATION);
		response.addCookie(cookie);		
	}

	private String getUserId() {
		Cookie[] cookies = request.getCookies();
		if (cookies==null)
			return null;
		for (Cookie c: cookies) {
			if (COOKIE_NAME.equals(c.getName()))
				return decodeCookie(c.getValue());
		}
		return null;
	}
	
	private String decodeCookie(String cookie) {
			//System.out.println("got cookie:"+cookie);
			String decode;
			try {
				decode = new String(Base64.decode(cookie),"UTF-8");
			}
			catch (IOException e) {  return null; } // ignore invalid cookie
			//System.out.println("decoded to:"+decode);
			String[] parts = decode.split("[:]");
			String userid=parts[0];
			//System.out.println("user id:"+userid);
			long loginTime=Long.parseLong(parts[1]);
			//System.out.println("login :"+(System.currentTimeMillis()-loginTime));
			if ((System.currentTimeMillis()-loginTime)>(LOGIN_DURATION*1000))
				return null; // Cookie too old
			String expectedCookie= createCookieString(userid,loginTime);
			//System.out.println("expected cookie:"+cookie);
			if (expectedCookie.equals(cookie))
				return userid;
			//System.out.println("expected cookie:"+cookie+" differs from "+cookie);
			return null; // invalid cookie
	}
	
	private static final String createCookieString(String userid, long time) {
		byte[] bytes = userid.getBytes();
		byte[] hash = Long.toHexString(time).getBytes();
		for (int i=0; i<bytes.length; i++)
			bytes[i] = (byte) (bytes[i] ^ hash[i%hash.length]);
		String signature=Base64.encodeBytes(bytes); // TODO: better signature
		return Base64.encodeBytes((userid+":"+time+":"+signature).getBytes());
	}
	public static class UnauthorizedException extends HttpException {
		private static final long serialVersionUID = 1L;
		public UnauthorizedException(String msg) {super(HttpServletResponse.SC_UNAUTHORIZED, msg); } 
	}

	
}
