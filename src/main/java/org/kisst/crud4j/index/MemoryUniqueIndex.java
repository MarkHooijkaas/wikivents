package org.kisst.crud4j.index;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.item4j.Schema;

public class MemoryUniqueIndex<T extends CrudObject> extends UniqueIndex<T> {
	private ConcurrentHashMap<String, T> map=new ConcurrentHashMap<String, T>();

	@SafeVarargs
	public MemoryUniqueIndex(CrudSchema<T> schema, Schema<T>.Field ... fields) { super(schema, fields); }

	@Override public T get(String ... values) { return map.get(getKey(values)); }

	@Override public void notifyCreate(T newRecord) {
		String newkey = getKey(newRecord);
		System.out.println("Indexing "+newkey);
		if (map.get(newkey)!=null)
			throw new RuntimeException("key "+newkey+" is not unique");
		map.put(newkey, newRecord);
	}


	@Override public void notifyUpdate(T oldRecord, T newRecord) {
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

	@Override public void notifyDelete(T oldRecord) { map.remove(getKey(oldRecord)); }

	public Collection<T> getAll() { return map.values(); } // needed for cache, but not for general cache use
}
