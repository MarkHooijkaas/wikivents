package org.kisst.item4j;

import org.kisst.item4j.struct.SmartStruct;
import org.kisst.item4j.struct.Struct;

public interface Schema {
	
	//public Field getField(String name);
	public Iterable<String> fieldNames();
	public Class<?> getJavaClass();
	
	public interface Field<FT> extends HasName, SmartStruct.Member<FT> {
		@Override public String getName();
		public Type<FT> getType();
		public Object getObject(Struct data); 
	}
	
	public class BasicField<FT> implements Schema.Field<FT> {
		public final Type<FT> type;
		public final String name;
		public BasicField(Type<FT> type, String name) { this.type=type; this.name=name; }
		
		@Override public Type<FT> getType() { return this.type; }
		@Override public String getName() { return this.name; }
		@Override public Object getObject(Struct data) { return data.getDirectFieldValue(name); }
		//@Override public Class<FT> getJavaClass() { return type.getJavaClass(); }
	}
}