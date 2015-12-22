package org.kisst.http4j.handlebar;

public interface AccessChecker<T> {
	public boolean mayBeViewedBy(T user);
	public boolean mayBeChangedBy(T user);
	default public boolean fieldMayBeChangedBy(String field, T user) { return mayBeChangedBy(user); }
}
