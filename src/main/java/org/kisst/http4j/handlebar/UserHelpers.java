package org.kisst.http4j.handlebar;
import java.io.IOException;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class UserHelpers<T> {
	private final Class<T> cls;
	private final String path;
	public UserHelpers(Class<T> cls, String path) { this.cls=cls; this.path=path; }

//	private T getUser(Options options) {
//		T result=getUserOrNull(options);
//		if (result==null)
//			throw new RuntimeException("No Context object "+path);
//		return result;
//	}
	@SuppressWarnings("unchecked")
	private T getUserOrNull(Options options) {
		Object obj = getRootContext(options).get(path); //options.context.get(path);
		if (obj==null)
			return null;
		if (cls.isAssignableFrom(obj.getClass()))
			return (T) obj;
		throw new RuntimeException("Object "+obj+" is not of type "+cls);
	}

	private Context getRootContext(Options options) {
		Context ctx = options.context;
		for (int i=0; i<10; i++) { // avoid an endless loop
			//System.out.println(i+"\t"+ctx);
			Context parent = ctx.parent();
			if (parent==null)
				return ctx;
			ctx=parent;
		}
		return ctx;	
	}
	
	public class IfMayChangeHelper implements Helper<Object> {
		@SuppressWarnings({ "unchecked" })
		@Override public CharSequence apply(final Object obj, final Options options) throws IOException {
			boolean authorized=false;
			if (obj instanceof AccessChecker) {
				T user =getUserOrNull(options);
				authorized= ((AccessChecker<T>)obj).mayBeChangedBy(user);
			}
			if (authorized) 
				return options.fn();
			else
				return options.inverse();
		}
	}

	public class IfMayViewHelper implements Helper<Object> {
		@SuppressWarnings({ "unchecked" })
		@Override public CharSequence apply(final Object obj, final Options options) throws IOException {
			boolean authorized=false;
			if (obj instanceof AccessChecker) {
				T user =getUserOrNull(options);
				authorized= ((AccessChecker<T>)obj).mayBeViewedBy(user);
			}
			if (authorized) 
				return options.fn();
			else
				return options.inverse();
		}
	}

	public class SimpleHelpers {
		public CharSequence priv(Object obj, Options options) {
			//System.out.println("Context:"+options.context);
			//System.out.println("Parent:"+options.context.parent());
			//System.out.println("Object:"+obj);
			T user=getUserOrNull(options);
			if (user!=null)
				return ""+obj;
			return "***";
		}
		public CharSequence debug(Object obj) {
			if (obj==null)
				return "NULL";
			return (obj.getClass()+"("+obj+")");
		}
	}
}
