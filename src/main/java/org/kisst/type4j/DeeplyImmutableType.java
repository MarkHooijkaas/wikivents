package org.kisst.type4j;

public interface DeeplyImmutableType<T> extends ImmutableType<T> {
	@Override default boolean isAllwaysDeeplyImmutable() { return true; }

}
