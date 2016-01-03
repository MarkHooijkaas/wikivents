package org.kisst.pko4j.index;

import org.kisst.item4j.Schema;
import org.kisst.pko4j.CrudObject;
import org.kisst.pko4j.StorageOption;
import org.kisst.pko4j.CrudTable.ChangeHandler;

public abstract class Index<T extends CrudObject> implements StorageOption, ChangeHandler<T> {
	public final Schema schema;
	protected Index(Schema schema) { this.schema=schema;	}
	
	@SuppressWarnings("unchecked")
	@Override public Class<T> getRecordClass() { return (Class<T>) this.schema.getJavaClass(); }
}
