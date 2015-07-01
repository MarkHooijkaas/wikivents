package org.kisst.crud4j;

import org.kisst.struct4j.Struct;

public interface Storage<T extends CrudObject> {
	public CrudSchema<T> getSchema();
	public void createStorage(T value);
	public Struct readStorage(String key);
	public void updateStorage(T oldValue, T newValue);
	public void deleteStorage(T oldValue);
}
