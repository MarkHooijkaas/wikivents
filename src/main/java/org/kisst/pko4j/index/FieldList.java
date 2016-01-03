package org.kisst.pko4j.index;

import java.util.Arrays;

import org.kisst.item4j.Schema;
import org.kisst.item4j.struct.Struct;

public class FieldList { 
	private final Schema.Field<?>[] fields;
	@SafeVarargs
	protected FieldList (Schema.Field<?> ... fields) { this.fields=fields;	}

	public Schema.Field<?>[] fields() { return Arrays.copyOf(fields,fields.length); }
	
	@Override public String toString() { return this.getClass().getSimpleName()+"("+fieldnames()+")"; }
	public String fieldnames() {
		String result = fields[0].getName();
		for (int i=1; i<fields.length; i++)
			result+=", "+fields[i].getName();
		return result;
	}
	public String getKey(Struct obj) {
		String result="";
		for (Schema.Field<?> field : fields)
			result+=field.getObject(obj)+"|"; 
		return result;
	}
	public String getKey(String[] values) {
		String result="";
		for (String str: values)
			result+=str+"|";
		return result;
	}
	
}
