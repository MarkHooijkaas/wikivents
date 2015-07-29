package org.kisst.crud4j.index;

import java.util.concurrent.ConcurrentHashMap;

import org.kisst.item4j.Schema;
import org.kisst.item4j.struct.Struct;

public class MemoryUniqueIndex extends UniqueIndex {
	private ConcurrentHashMap<String, Struct> map=new ConcurrentHashMap<String, Struct>();

	public MemoryUniqueIndex(Class<?> recordClass, Schema<?>.StringField ... fields) { super(recordClass, fields); }

	@Override public Struct get(String ... values) { return map.get(getKey(values)); }

	@Override public void notifyCreate(Struct newRecord) {
		String newkey = getKey(newRecord);
		System.out.println("Indexing "+newkey);
		if (map.get(newkey)!=null)
			throw new RuntimeException("key "+newkey+" is not unique");
		map.put(newkey, newRecord);
	}


	@Override public void notifyUpdate(Struct oldRecord, Struct newRecord) {
		String oldkey = getKey(oldRecord);
		String newkey = getKey(newRecord);
		if (oldkey.equals(newkey))
			map.put(newkey, newRecord); // TODO: check if oldRecord exists???
		else {
			if (map.get(newkey)!=null)
				throw new RuntimeException("key "+newkey+" is not unique");
			map.put(newkey, newRecord);
			map.remove(oldkey);
		}
	}

	@Override public void notifyDelete(Struct oldRecord) { map.remove(getKey(oldRecord)); }
}
