package org.kisst.crud4j.impl;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;

public class BaseIndex<T extends CrudObject> implements CrudTable.Index<T> {
	private final CrudSchema<T>.Field<?> keyField;
	public BaseIndex(CrudSchema<T>.Field<?> keyField) {this.keyField=keyField; }
	protected final String getKey(T record) { return keyField.getString(record); }

	@Override public void notifyCreate(T record) {}
	@Override public void notifyUpdate(T oldRecord, T newRecord) {}
	@Override public void notifyDelete(T oldRecord) {}

}
