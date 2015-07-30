package org.kisst.crud4j;

import org.kisst.item4j.Schema;
import org.kisst.item4j.Type;

public class CrudObjectSchema<T> extends CrudSchema<T> {
	public CrudObjectSchema(Class<T> cls) { 
		super(cls); 
	}
	public final IdField _id = new IdField();
	
	public Schema<T>.Field getKeyField() { return _id;}

	public class IdField extends Field {
		public IdField() { super(Type.javaString, "_id"); }
		public IdField(String name) { super(Type.javaString, name); }
	}
}
