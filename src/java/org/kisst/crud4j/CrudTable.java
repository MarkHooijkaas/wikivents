package org.kisst.crud4j;

import org.kisst.struct4j.Struct;

public abstract class CrudTable<T extends CrudObject> {
	public  final CrudSchema<T> schema;
	private final  Storage<T> storage;
	public CrudTable(Storage<T> storage) { 
		this.schema=storage.getSchema();
		this.storage=storage;
	}
	abstract protected T createObject(Struct  props);
	public void create(T doc) {
		storage.createStorage(doc);
	}
	public T read(String key) { 
		return createObject(storage.readStorage(key));
	}
	public void update(T oldValue, T newValue) { 
		storage.updateStorage(oldValue, newValue); 
	}
	public void delete(T oldValue) {
		storage.deleteStorage(oldValue);
}
}
