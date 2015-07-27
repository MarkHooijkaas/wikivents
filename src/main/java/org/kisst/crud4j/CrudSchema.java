package org.kisst.crud4j;

import org.kisst.item4j.Schema;
import org.kisst.item4j.Type;
import org.kisst.item4j.struct.Struct;

public class CrudSchema<T> extends Schema<T> {
	public CrudSchema(Class<T> cls) { 
		super(cls);
	}
	public IdField getKeyField() { return null; } // TODO

	public class IdField extends Field {
		public IdField() { super(Type.javaString, "_id"); }
		public IdField(String name) { super(Type.javaString, name); }
	}
	public class RefField<RT extends CrudObject> extends Field {
		public RefField(String name) { super(Type.javaString,name); }
		public CrudTable<RT>.Ref getRef(CrudTable<RT> table, Struct s) {
			String id = s.getString(getName(),null);
			if (id==null)
				return null;
			String clsName=table.getSchema().getName();
			//System.out.println("Getting ref "+getName()+" with value "+id+ " should be of format "+clsName+"(...)");
			if (!(id.startsWith(clsName+"(") || id.endsWith(")")))
				throw new RuntimeException("reference "+id+" should be of format "+clsName+"(...)");
			id=id.substring(clsName.length()+1, id.length()-1);
			return table.createRef(id);
		}
	}
}
