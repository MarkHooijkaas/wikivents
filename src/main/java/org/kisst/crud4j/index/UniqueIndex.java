package org.kisst.crud4j.index;

import org.kisst.item4j.Schema;
import org.kisst.item4j.struct.Struct;

public abstract class UniqueIndex extends Index  {
	protected UniqueIndex(Class<?> recordClass, Schema<?>.Field[] fields) { super(recordClass, fields); }
	abstract public Struct get(String... values);
}
