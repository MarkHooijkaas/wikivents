package org.kisst.crud4j;

import org.kisst.struct4j.Struct;

public interface Storage<T extends CrudObject> {
	public CrudSchema<T> getSchema();
	public void createStorage(CrudObject value);
	public Struct readStorage(String key);
	public void updateStorage(CrudObject oldValue, CrudObject newValue);
	public void deleteStorage(CrudObject oldValue);
}
