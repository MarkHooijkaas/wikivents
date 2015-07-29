package org.kisst.crud4j.index;

import org.kisst.crud4j.StorageOption;
import org.kisst.item4j.Schema;
import org.kisst.item4j.struct.Struct;

public abstract class Index implements StorageOption {
	public final Schema<?>.Field[] fields;
	private Class<?> recordClass;
	protected Index(Class<?> recordClass, Schema<?>.Field[] fields) { this.recordClass=recordClass;	this.fields=fields;	}

	@Override public Class<?> getRecordClass() { return this.recordClass; }
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

	abstract public void notifyCreate(Struct record);
	abstract public void notifyUpdate(Struct oldRecord, Struct newRecord);
	abstract public void notifyDelete(Struct oldRecord);
}
