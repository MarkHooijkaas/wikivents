package org.kisst.type4j;

public interface ImmutableType<T> extends SmartType<T> {
	@Override default boolean isAllwaysImmutable() { return true;}

}
