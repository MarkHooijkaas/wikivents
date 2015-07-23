package org.kisst.http4j;

public abstract class HttpBasicPage<T extends HttpCall> implements HttpCallHandler<T>{
	@Override public void handle(T call, String subPath) {
		String method = call.request.getMethod();
		//System.out.println("handling "+method+" "+request.getRequestURI());
		//System.out.println("handling "+method+" "+path);
		if ("GET".equals(method))
			handleGet(call, subPath);
		else if ("POST".equals(method))
			handlePost(call, subPath);
		else
			throw new RuntimeException("Unknown method type "+method);
	}

	public void handleGet(HttpCall call, String subPath) {}
	public void handlePost(HttpCall call, String subPath) {}
}
