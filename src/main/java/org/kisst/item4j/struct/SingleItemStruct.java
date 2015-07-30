package org.kisst.item4j.struct;

import java.util.Arrays;

import org.kisst.util.AssertUtil;

public class SingleItemStruct implements Struct {
	public final String fieldName;
	public final Object value;

	public SingleItemStruct(String fieldName, Object value) { 
		this.fieldName=fieldName; 
		this.value=value;
		AssertUtil.assertNotNull(value);
	}
	
	@Override public String toString() { 
		StringBuilder result=new StringBuilder(getClass().getSimpleName());
		result.append('(');
		result.append(value);
		result.append(')');
		return result.toString();
	}
	
	@Override public Iterable<String> fieldNames() { return Arrays.asList(new String[]{this.fieldName}); } 
	
	@Override public Object getDirectFieldValue(String name) {
		if (fieldName.equals(name))
			return value;
		return UNKNOWN_FIELD;
	}
}
