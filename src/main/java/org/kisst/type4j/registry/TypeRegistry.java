package org.kisst.type4j.registry;

import org.kisst.type4j.SmartType;

public interface TypeRegistry {
	default public <T> SmartType<T> getType(Class<T> cls) { 
		SmartType<T> result = this.getTypeOrNull(cls);
		if (result==null)
			throw new UnknownTypeException(cls);
		return result;
	}
	public <T> SmartType<T> getTypeOrNull(Class<T> cls);
	
	public class UnknownTypeException extends SmartType.Type4jException {
		private static final long serialVersionUID = 1L;
		public final  Class<?> javaClass;

		public UnknownTypeException(Class<?> cls) { super("Unknown type "+cls); this.javaClass=cls;}

	}
}
