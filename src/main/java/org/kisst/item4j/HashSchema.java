package org.kisst.item4j;

import java.util.LinkedHashMap;

public class HashSchema implements Schema {
	private final LinkedHashMap<String, Field> fields=new LinkedHashMap<String, Field>(); 

	public HashSchema add(String name, Field field) { fields.put(name,field); return this; }
	public Field getField(String name) { return fields.get(name); }
	public Iterable<String> fieldNames() { return fields.keySet(); }
}