package org.kisst.http4j.handlebar;

import java.io.IOException;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public  class IfMayViewHelper<T> implements Helper<Object> {
	//public static final Helper<String> INSTANCE = new AssignHelper();
	public static final String NAME = "ifMaySee";

	private final String path;
	public IfMayViewHelper(String path) { this.path=path; }
	
	@SuppressWarnings({ "unchecked" })
	@Override public CharSequence apply(final Object obj, final Options options) throws IOException {
		boolean authorized=false;
		if (obj instanceof AccessChecker) {
			T user = (T) options.context.get(path);
			authorized= ((AccessChecker<T>)obj).mayBeViewedBy(user);
		}
		
		if (authorized) 
			return options.fn();
		else
			return options.inverse();
	}
}