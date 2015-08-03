package org.kisst.crud4j.index;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable.ChangeHandler;
import org.kisst.crud4j.StorageOption;

public abstract class Index<T extends CrudObject> implements StorageOption, ChangeHandler<T> {
	private final CrudSchema<T> schema;
	protected Index(CrudSchema<T> schema) { this.schema=schema;	}
	
	@Override public Class<T> getRecordClass() { return this.schema.getJavaClass(); }
}
