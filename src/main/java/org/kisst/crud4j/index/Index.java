package org.kisst.crud4j.index;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.StorageOption;
import org.kisst.item4j.Schema;
import org.kisst.item4j.struct.Struct;

public abstract class Index<T extends CrudObject> implements StorageOption {
	public final CrudSchema<T>.Field[] fields;
	private CrudSchema<T> schema;
	protected Index(CrudSchema<T> schema, CrudSchema<T>.Field[] fields) { this.schema=schema;	this.fields=fields;	}

	@Override public Class<T> getRecordClass() { return this.schema.getJavaClass(); }
	@Override public String toString() { return this.getClass().getSimpleName()+"("+getRecordClass().getSimpleName()+"::"+fieldnames()+")"; }
	public String fieldnames() {
		String result = fields[0].getName();
		for (int i=1; i<fields.length; i++)
			result+=", "+fields[i].getName();
		return result;
	}
	public String getKey(Struct obj) {
		String result="";
		for (Schema<?>.Field field : fields)
			result+="|"+field.getString(obj); // should be unique when fields don't have | in it
		return result;
	}
	public String getKey(String[] values) {
		String result="";
		for (String str: values)
			result+="|"+str;
		return result;
	}

	abstract public void notifyCreate(T record);
	abstract public void notifyUpdate(T oldRecord, T newRecord);
	abstract public void notifyDelete(T oldRecord);
}
