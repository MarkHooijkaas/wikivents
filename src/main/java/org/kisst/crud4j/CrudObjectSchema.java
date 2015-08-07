package org.kisst.crud4j;

import org.kisst.item4j.Type;
import org.kisst.item4j.struct.Struct;

public class CrudObjectSchema<T extends CrudObject> extends CrudSchema<T> {
	public CrudObjectSchema(Class<T> cls) { 
		super(cls); 
	}
	public final IdField _id = new IdField();
	
	public IdField getKeyField() { return _id;}

	public class IdField extends BasicField<String> {
		public IdField() { super(Type.javaString, "_id"); }
		public IdField(String name) { super(Type.javaString, name); }
		public String getString(Struct data) { return data.getString(name); }
	}
}
