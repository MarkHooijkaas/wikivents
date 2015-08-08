package org.kisst.item4j.struct;

import java.util.Map;

/**
 * @author mark
 *
 */
public class MapStruct implements Struct {
	private final Map<String, Object> map;

	@SuppressWarnings("unchecked")
	public MapStruct(Map<String,?> map) { this.map=(Map<String, Object>) map; }
	
	@Override public String toString() { return StructHelper.toString(this, 2,null); }
	@Override public Iterable<String> fieldNames() { return map.keySet(); }
	@Override public Object getDirectFieldValue(String key) { return map.get(key); }
	
}
