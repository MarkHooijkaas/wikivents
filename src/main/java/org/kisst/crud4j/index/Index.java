package org.kisst.crud4j.index;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudTable.ChangeHandler;
import org.kisst.crud4j.StorageOption;
import org.kisst.item4j.Schema;

public abstract class Index<T extends CrudObject> implements StorageOption, ChangeHandler<T> {
	public final Schema schema;
	protected Index(Schema schema) { this.schema=schema;	}
	
	@SuppressWarnings("unchecked")
	@Override public Class<T> getRecordClass() { return (Class<T>) this.schema.getJavaClass(); }
}
