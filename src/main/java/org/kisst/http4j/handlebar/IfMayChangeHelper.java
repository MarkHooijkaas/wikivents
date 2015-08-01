package org.kisst.http4j.handlebar;

import java.io.IOException;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public  class IfMayChangeHelper<T> implements Helper<Object> {
	private final String path;
	public IfMayChangeHelper(String path) { this.path=path; }
	
	@SuppressWarnings({ "unchecked" })
	@Override public CharSequence apply(final Object obj, final Options options) throws IOException {
		boolean authorized=false;
		if (obj instanceof AccessChecker) {
			T user = (T) options.context.get(path);
			authorized= ((AccessChecker<T>)obj).mayBeChangedBy(user);
		}
		
		if (authorized) 
			return options.fn();
		else
			return options.inverse();
	}
}