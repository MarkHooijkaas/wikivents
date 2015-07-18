package org.kisst.item4j.struct;

import java.util.HashSet;
import java.util.LinkedHashMap;

public class HashStruct implements Struct {
	private final LinkedHashMap<String, Object> map=new LinkedHashMap<String, Object>();

	//@Override public String toString() { return toString(this.getClass(),); }
	public void put(String key, Object value) { map.put(key, value); }
	
	@Override public Iterable<String> fieldNames() {
		HashSet<String> result= new HashSet<String>();
		for (String key: map.keySet())
			result.add(key);
		return result;
	}
	
	@Override public Object getDirectFieldValue(String name) {
		Object result=map.get(name);
		if (result==null)
			return UNKNOWN_FIELD;
		return result;
	}
}
