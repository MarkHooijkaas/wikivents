package org.kisst.crud4j;

import java.util.concurrent.ConcurrentHashMap;

import org.kisst.struct4j.Struct;

public abstract class CrudCachedTable<T extends CrudObject> extends CrudTable<T> {
	private final ConcurrentHashMap<String, T> map=new ConcurrentHashMap<String,T>();
	public CrudCachedTable(Storage<T> storage) { super(storage);	}
	abstract protected T createObject(Struct  props);
	public void create(T doc) {
		super.create(doc);
		map.put(doc._id, doc);
	}
	public T read(String key) { 
		T result = map.get(key);
		if (result==null) {
			result=super.read(key);
			map.put(key, result);
		}
		return result;
	}
	public void update(T oldValue, T newValue) { 
		super.update(oldValue, newValue);
		map.put(newValue._id, newValue);
	}
	public void delete(T oldValue) {
		super.delete(oldValue);
		map.remove(oldValue._id);
	}
}
