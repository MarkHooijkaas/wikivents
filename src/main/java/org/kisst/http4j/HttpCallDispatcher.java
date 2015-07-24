package org.kisst.http4j;

import java.util.LinkedHashMap;

public class HttpCallDispatcher<T extends HttpCall> implements HttpCallHandler<T> {
	private final LinkedHashMap<String, HttpCallHandler<T>> map=new LinkedHashMap<String, HttpCallHandler<T>>();

	private final HttpCallHandler<T> defaultHandler;
	public HttpCallDispatcher(HttpCallHandler<T> defaultHandler) { 
		this.defaultHandler=defaultHandler;
	}
	
	public HttpCallDispatcher<T> addHandler(String path, HttpCallHandler<T> handler) {
		while (path.startsWith("/"))
			path=path.substring(1);
		map.put(path, handler);
		return this;
	}

	@Override public void handle(T call, String subPath) {
		while (subPath.startsWith("/"))
			subPath=subPath.substring(1);
		int pos=subPath.indexOf("/");
		String name=subPath;
		if (pos>0) {
			name=subPath.substring(0, pos);
			subPath=subPath.substring(pos+1);
		}
		else
			subPath="";
		while (subPath.startsWith("/"))
			subPath=subPath.substring(1);
		HttpCallHandler<T> handler = map.get(name);
		if (handler==null)
			handler=defaultHandler;
		//System.out.println("Handling {"+name+"} with subPath {"+subPath+"} with handler "+handler.getClass().getSimpleName());
		if (handler==null)
			call.invalidPage();
		else
			handler.handle(call,subPath); // TODO: call the subType method????
	}
}