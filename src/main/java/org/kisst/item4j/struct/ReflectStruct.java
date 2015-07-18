package org.kisst.item4j.struct;

import java.lang.reflect.Field;
import java.util.HashSet;

import org.kisst.util.ReflectionUtil;

public class ReflectStruct extends BaseStruct {
	private final Object obj;

	public ReflectStruct(Object obj) { this.obj=obj; }

	@Override public String toString() { 
		StringBuilder result=new StringBuilder(getClass().getSimpleName());
		result.append('(');
		result.append(obj.getClass().getName());
		result.append(')');
		return result.toString();
	}
	
	@Override public Iterable<String> fieldNames() { 
		HashSet<String> result= new HashSet<String>();
		for (Field field: obj.getClass().getFields()) {
			// TODO: check if field is transient and accessible etc
			result.add(field.getName());
		}
		return result;
	}
	
	@Override public Object getDirectFieldValue(String name) { return ReflectionUtil.getFieldValue(this, name); }
}
