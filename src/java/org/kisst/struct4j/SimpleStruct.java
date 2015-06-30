package org.kisst.struct4j;

import java.util.HashSet;
import java.util.LinkedHashMap;

public class SimpleStruct extends BaseStruct {
	private final LinkedHashMap<String, Object> map=new LinkedHashMap<String, Object>();

	//@Override public String toString() { return toString(this.getClass(),); }

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
