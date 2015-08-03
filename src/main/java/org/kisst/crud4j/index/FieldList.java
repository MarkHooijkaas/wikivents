package org.kisst.crud4j.index;

import java.util.Arrays;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.item4j.ObjectSchema;
import org.kisst.item4j.struct.Struct;

public class FieldList<T extends CrudObject> { 
	private final CrudSchema<T>.Field[] fields;
	@SafeVarargs
	protected FieldList (CrudSchema<T>.Field ... fields) { this.fields=fields;	}

	public ObjectSchema<T>.Field[] fields() { return Arrays.copyOf(fields,fields.length); }
	
	@Override public String toString() { return this.getClass().getSimpleName()+"("+fieldnames()+")"; }
	public String fieldnames() {
		String result = fields[0].getName();
		for (int i=1; i<fields.length; i++)
			result+=", "+fields[i].getName();
		return result;
	}
	public String getKey(Struct obj) {
		String result="";
		for (ObjectSchema<?>.Field field : fields)
			result+="|"+field.getString(obj); // should be unique when fields don't have | in it
		return result;
	}
	public String getKey(String[] values) {
		String result="";
		for (String str: values)
			result+="|"+str;
		return result;
	}
	
}
