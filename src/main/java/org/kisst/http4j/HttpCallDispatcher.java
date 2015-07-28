package org.kisst.http4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedHashMap;

import org.kisst.util.ReflectionUtil;

public class HttpCallDispatcher implements HttpCallHandler {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Path { public String dispatchPath(); }
	
	
	private final LinkedHashMap<String, HttpCallHandler> map=new LinkedHashMap<String, HttpCallHandler>();
	private final HttpCallHandler home;

	public HttpCallDispatcher(Object pages) {
		HttpCallHandler tmpHome=null;
		//System.out.println("Loading fields from "+ReflectionUtil.smartClassName(pages.getClass()));
		for (java.lang.reflect.Field f : ReflectionUtil.getAllDeclaredFieldsOfType(pages.getClass(), HttpCallHandler.class)) {
			f.setAccessible(true);
			Object h =ReflectionUtil.getFieldValue(pages, f);
			String path=f.getName();
			if (h==null || h.getClass()==Object.class)
				continue;
			HttpCallHandler handler=(HttpCallHandler) h;
			// TODO: which wins if both annotation and instanceof are true????
			if (h instanceof Path)
				path=((Path) h).dispatchPath();
			Path anno = f.getAnnotation(Path.class);
			if (anno!=null)
				path=anno.dispatchPath();
			map.put(path, handler);
			//System.out.println("Adding url "+path+" with value "+handler);
			if ("home".equals(path))
				tmpHome=handler;
					
		}
		this.home=tmpHome;
	}

	@Override public void handle(HttpCall call, String subPath) {
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
		HttpCallHandler handler = name.length()==0 ? home : map.get(name);
		//System.out.println("Handling {"+name+"} with subPath {"+subPath+"} with handler "+handler);
		if (handler==null)
			call.invalidPage();
		else
			handler.handle(call,subPath); // TODO: call the subType method????
	}
}