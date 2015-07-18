package org.kisst.item4j;

import java.util.LinkedHashMap;

import org.kisst.item4j.struct.Struct;

public class Params implements Struct {
	public static interface Param {
		public String getName();
		public String getValue();
	}
	
	private final LinkedHashMap<String, Param> params= new LinkedHashMap<String,Param>();
	
	public Params(Param... params) { 
		for (Param p: params)
			this.params.put(p.getName(),p);
	}
	
	@Override public Iterable<String> fieldNames() { return params.keySet(); }
	@Override public Object getDirectFieldValue(String name) {
		if (params.containsKey(name))
			return params.get(name).getValue();
		return UNKNOWN_FIELD;
	}
}
