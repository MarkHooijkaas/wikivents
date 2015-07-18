package org.kisst.crud4j;

import org.kisst.item4j.Schema;
import org.kisst.item4j.Type;
import org.kisst.item4j.struct.Struct;

public class CrudSchema<T extends CrudObject> extends Schema<T> {
	public static CrudSchema<CrudObject> schema=new CrudSchema<CrudObject>(CrudObject.class);
	public final IdField _id = new IdField("_id");
	public IdField getKeyField() { return _id; }
	
	public CrudSchema(Class<T> cls) { super(cls);}

	public class IdField extends Field {
		public IdField(String name) { super(Type.javaString, name); }
	}
	public class RefField<RT extends CrudObject> extends Field {
		public RefField(String name) { super(Type.javaString,name); }
		public CrudTable<RT>.Ref getRef(CrudTable<RT> table, Struct s) { 
			return table.createRef(s.getString(getName()));
		}
	}
}
