package org.kisst.item4j.struct;

import java.util.HashSet;
import java.util.LinkedHashMap;

import org.kisst.item4j.HasName;

public class HashStruct extends LinkedHashMap<String,Object> implements Struct {
	private static final long serialVersionUID = 1L;
	//private final LinkedHashMap<String, Object> map=new LinkedHashMap<String, Object>();

	public HashStruct() {}
	public HashStruct(Struct rec) {
		for (String key:rec.fieldNames())
			put(key, rec.getDirectFieldValue(key));
	}
	@Override public String toString() { return StructHelper.toShortString(this); }
	//public void put(String key, Object value) { map.put(key, value); }
	
	public HashStruct add(String name, Object value) { put(name,value); return this; }
	public HashStruct add(HasName field, Object value) { put(field.getName(),value); return this; }
	
	@Override public Iterable<String> fieldNames() {
		HashSet<String> result= new HashSet<String>();
		for (String key: keySet())
			result.add(key);
		return result;
	}
	
	@Override public Object getDirectFieldValue(String name) {
		Object result=get(name);
		if (result==null)
			return UNKNOWN_FIELD;
		return result;
	}
}
