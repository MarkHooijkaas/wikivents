package org.kisst.item4j;

import org.kisst.item4j.struct.Struct;

public interface Schema {
	
	//public Field getField(String name);
	public Iterable<String> fieldNames();
	public Class<?> getJavaClass();
	
	public interface Field<FT> extends HasName, Struct.Member<FT> {
		@Override public String getName();
		public Type<FT> getType();
		default public Object getObject(Struct data) {
			return data.getDirectFieldValue(getName());
		}
	}
	
	public class BasicField<FT> implements Schema.Field<FT> {
		public final Type<FT> type;
		public final String name;
		public BasicField(Type<FT> type, String name) { this.type=type; this.name=name; }
		
		@Override public Type<FT> getType() { return this.type; }
		@Override public String getName() { return this.name; }
		@Override public Object getObject(Struct data) { return data.getDirectFieldValue(name); }
		public Object getObject(Struct data, Object defaultValue) {
			Object result = data.getDirectFieldValue(name);
			if (result==null || result==Struct.UNKNOWN_FIELD)
				return defaultValue;
			return result;
		}
		//@Override public Class<FT> getJavaClass() { return type.getJavaClass(); }
	}
}