package org.kisst.crud4j;

import org.kisst.item4j.seq.TypedSequence;
import org.kisst.item4j.struct.Struct;

public interface StructStorage extends StorageOption {
	public Class<?> getRecordClass();
	public String createInStorage(Struct value);
	public Struct readFromStorage(String key);
	public void updateInStorage(Struct oldValue, Struct newValue);
	public void deleteInStorage(Struct oldValue);
	public TypedSequence<Struct> findAll();
	public boolean useCache();
	default public void close() {}
}
