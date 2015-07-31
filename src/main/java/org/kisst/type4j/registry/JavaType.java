package org.kisst.type4j.registry;

import java.util.function.Function;

import org.kisst.type4j.DeeplyImmutableType;

public interface JavaType {// extends ReflectionFieldsTypeRegistry  {

	public static class StringParseType<T> implements DeeplyImmutableType<T> {
		private final Class<T> cls;
		private final Function<String,T> convertor;
		public StringParseType(Class<T> cls, Function<String ,T> convertor) { this.cls=cls; this.convertor=convertor; }
			@Override public Class<T> getJavaClass() { return this.cls; }
		@SuppressWarnings("unchecked")
		@Override public T deepCopyOf(Object obj) {
			if (cls.isAssignableFrom(obj.getClass()))
				return (T) obj;
			return convertor.apply(obj.toString());
		}
	}
}
