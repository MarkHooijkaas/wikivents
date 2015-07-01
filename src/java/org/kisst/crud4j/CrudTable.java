package org.kisst.crud4j;

import java.util.concurrent.ConcurrentHashMap;

import org.kisst.struct4j.Struct;

public abstract class CrudTable<T extends CrudObject> {
	private final ConcurrentHashMap<String, T> map=new ConcurrentHashMap<String,T>();
	public  final CrudSchema<T> schema;
	private final  Storage<T> storage;
	public CrudTable(Storage<T> storage) { 
		this.schema=storage.getSchema();
		this.storage=storage;
	}
	abstract protected T createObject(Struct  props);
	public void create(T doc) {
		storage.createStorage(doc);
		map.put(doc._id, doc);
	}
	public T read(String key) { return get(key); }
	public void update(T oldValue, T newValue) { 
		storage.updateStorage(oldValue, newValue); 
		map.put(newValue._id, newValue);
	}
	public void delete(T value) {  /* TODO*/}
	

	public T get(String key) { 
		T result = map.get(key);
		if (result!=null)
			return result;
		synchronized(map) {
			result = map.get(key);
			if (result!=null)
				return result;
			map.put(key, result);
			return result;
		}
	}
}
