package org.kisst.item4j.struct;

import java.util.Iterator;

import org.kisst.item4j.HasName;
import org.kisst.item4j.Type;
import org.kisst.util.ReflectionUtil;

public interface Struct {
	
	public static final Object UNKNOWN_FIELD=ReflectionUtil.UNKNOWN_FIELD;

	public Iterable<String> fieldNames();

/*
 * should return UNKNOWN_FIELD if the field does not exist
 * otherwise returns the value of the field, which may be null 	
 */
	public Object getDirectFieldValue(String name);
	default public Object getDirectFieldValue(String name, Object defaultValue) {
		Object result = getDirectFieldValue(name);
		if (result==UNKNOWN_FIELD)
			return defaultValue;
		return result;
	}
	//public Object getObjectOrUnknownField(String name);
	//public boolean hasField(String name);
	
	public interface Member<T> extends HasName {
		public String getName();
		public Type<T> getType();
	}


	
	
	public final static Struct EMPTY=new Struct() {
		public final Iterable<String> fieldNames() { return new Iterable<String>() {
			public Iterator<String> iterator() { return new Iterator<String>(){
				@Override public final boolean hasNext() { return false; }
				@Override public final String next() { return null;}
		};}};};
		public Object getDirectFieldValue(String name) { return UNKNOWN_FIELD;}
	};
	

	public class UnknownFieldException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public final Struct struct;
		public final String fieldName;
		public UnknownFieldException(Struct struct, String fieldName) { this(struct,fieldName,null);}
		public UnknownFieldException(Struct struct, String fieldName, Throwable e) {
			super(struct+" does not have field "+fieldName,e); 
			this.struct=struct;
			this.fieldName=fieldName;
		}
	}
	public class FieldHasNullValueException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public final Struct struct;
		public final String fieldName;
		public FieldHasNullValueException(Struct struct, String fieldName) { this(struct,fieldName,null);}
		public FieldHasNullValueException(Struct struct, String fieldName, Throwable e) {
			super(struct+" has null value for field "+fieldName,e); 
			this.struct=struct;
			this.fieldName=fieldName;
		}
	}

}
