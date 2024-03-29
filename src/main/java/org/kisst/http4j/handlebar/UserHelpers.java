package org.kisst.http4j.handlebar;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.TimeZone;

import org.kisst.http4j.HttpCall;
import org.kisst.util.ReflectionUtil;

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
	protected T getUserOrNull(Options options) {
		Object obj = getRootContext(options).get(path); //options.context.get(path);
		if (obj==null)
			return null;
		if (cls.isAssignableFrom(obj.getClass()))
			return (T) obj;
		throw new RuntimeException("Object "+obj+" is not of type "+cls);
	}
	protected HttpCall getCallOrNull(Options options) {
		Object obj = getRootContext(options).get("call");
		if (obj==null)
			return null;
		if (obj instanceof HttpCall)
			return (HttpCall) obj;
		throw new RuntimeException("Object "+obj+" is not of type HttpCall");
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

	/*
	public class IfLoggedInHelper implements Helper<Object> {
		@Override public CharSequence apply(Object obj, final Options options) throws IOException {
			T user =getUserOrNull(options);
			if (user!=null) 
				return options.fn();
			else
				return options.inverse();
		}
	}
	 */

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
				//System.out.println(user+(authorized?"":" NOT")+" authorized for "+((Wikivent) obj).title);
			}
			if (authorized) 
				return options.fn();
			else
				return options.inverse();
		}
	}

	public CharSequence priv(Object obj, Options options) {
		T user=getUserOrNull(options);
		if (user!=null)
			return obj==null?null:""+obj;
		return "***";
	}
	public CharSequence maySee(AccessChecker<T> obj1, Object value, Options options) {
		T user=getUserOrNull(options);
		if (user!=null && obj1.mayBeViewedBy(user))
			return value==null?null:value.toString();
		return "***";
	}

	public CharSequence ifUser(String method,  Object obj, Options options)  throws IOException {
		T user=getUserOrNull(options);
		if (user==null || obj==null)
			return options.inverse();
		Boolean result= (Boolean) ReflectionUtil.invokeFirstCompatibleMethod(user, method, new Object[] {obj});
		if (result)
			return options.fn();
		return options.inverse();
	}

	private Locale localeNl=new Locale("nl");
	private ZoneId zoneId=TimeZone.getDefault().toZoneId();
	public CharSequence dateFormat(Object obj, String format) {
		if (obj==null)
			return "";
		if (obj instanceof Instant) {
			Instant inst=(Instant) obj;
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, localeNl);
			return formatter.format(LocalDateTime.ofInstant(inst,zoneId));
		}
		else if (obj instanceof TemporalAccessor) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, localeNl);
			return formatter.format((TemporalAccessor) obj);
		}
		return obj.toString();
	}
	public CharSequence dateShortFormat(Object obj) {
		String format = "eee d MMM yyyy";
		if (obj==null)
			return "";
		if (obj instanceof Instant) {
			Instant inst=(Instant) obj;
			if (inst.plusSeconds(3600*24*180).isAfter(Instant.now()))
				format="eee d MMM";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, localeNl);
			return formatter.format(LocalDateTime.ofInstant(inst,zoneId));
		}
		else if (obj instanceof TemporalAccessor) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, localeNl);
			return formatter.format((TemporalAccessor) obj);
		}
		return obj.toString();

	}
	public CharSequence debug(Object obj) {
		if (obj==null)
			return "NULL";
		return (obj.getClass()+"("+obj+")");
	}
	public CharSequence join(Object obj, String join, Options options) {
		if (obj==null)
			return null;
		if (obj instanceof Iterable) {
			String result="";
			String sep="";
			for (Object it: (Iterable<?>) obj) {
				if (it instanceof Htmlable)
					result += sep+((Htmlable)it).getHtmlString();
				else
					result += sep+it;
				sep=join;
			}
			return result;
		}
		throw new IllegalArgumentException("Can not iterate over Object "+obj+" for joining");
	}

	public CharSequence ifLoggedIn(Object obj, final Options options) throws IOException {
		T user =getUserOrNull(options);
		if (user!=null) 
			return options.fn();
		return options.inverse();
	}
	public CharSequence ifParam(String name, String value, final Options options) throws IOException {
		HttpCall call = getCallOrNull(options);
		if (call!=null && value.equals(call.request.getParameter(name))) 
			return options.fn();
		return options.inverse();
	}
	public CharSequence ifEquals(Object obj1, Object obj2, final Options options) throws IOException {
		if (obj1!=null && obj1.equals(obj2)) 
			return options.fn();
		return options.inverse();
	}
	public CharSequence ifUrlEndsWith(String str, final Options options) throws IOException {
		boolean matches=false;
		HttpCall call = getCallOrNull(options);
		if (call!=null) {
			String[] parts = str.split("\\|");
			String url=call.getLocalUrl();
			for (String part: parts)
				matches=matches || url.endsWith(part.trim());
		}
		if (matches) 
			return options.fn();
		return options.inverse();
	}
}
