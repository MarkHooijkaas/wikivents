package org.kisst.type4j;

public interface SmartType<T> extends DeeplyImmutable {
	public Class<T> getJavaClass();

	default public T convertFrom(Object obj) { return this.privateVersionOf(obj); } 
	default public T privateVersionOf(Object obj) { return this.deepCopyOf(obj); }
	public T deepCopyOf(Object obj);

	default public T convertFromOrNull(Object obj) { return obj==null ? null : this.convertFrom(obj); } 
	default public T copyFromOrNull(Object obj) { return obj==null ? null : this.privateVersionOf(obj); }
	default public T deepCopyFromOrNull(Object obj) { return obj==null ? null : this.deepCopyOf(obj); }

	
	default boolean isAllwaysImmutable() { return this.isAllwaysDeeplyImmutable() || Immutable.class.isAssignableFrom(this.getClass()); }
	default boolean isAllwaysDeeplyImmutable() { return DeeplyImmutable.class.isAssignableFrom(this.getClass()); }

	default boolean isImmutable(Object obj) { return this.isAllwaysImmutable(); }
	default boolean isDeeplyImmutable(Object obj) { return this.isAllwaysDeeplyImmutable(); }

	//default public void formatTo(Formatter formatter, Object obj) {}
	//default public void formatFrom(Formatter formatter, Object obj) {}

	public class Type4jException extends RuntimeException implements DeeplyImmutable {
		private static final long serialVersionUID = 1L;
		public Type4jException(String message) { super(message);}
	}

	public class CannotConvertTypeException extends Type4jException {
		private static final long serialVersionUID = 1L;
		final public Class<?> javaClass;
		final public Object object;
		public CannotConvertTypeException(Class<?> javaClass, Object obj) {
			super("Don't know how to convert object "+obj+" to class "+javaClass);
			this.javaClass=javaClass;
			this.object=obj;
		}
	}

}
