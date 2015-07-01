package org.kisst.crud4j;

import org.kisst.struct4j.Struct;

public interface Storage<T extends CrudObject> {
	public CrudSchema<T> getSchema();
	public void createInStorage(T value);
	public Struct readFromStorage(String key);
	public void updateInStorage(T oldValue, T newValue);
	public void deleteInStorage(T oldValue);
}
