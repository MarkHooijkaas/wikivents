package org.kisst.crud4j.index;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.item4j.Schema;

public abstract class UniqueIndex<T extends CrudObject> extends Index<T>  {
	protected UniqueIndex(CrudSchema<T> recordClass, Schema<T>.Field[] fields) { super(recordClass, fields); }
	abstract public T get(String... values);
}
