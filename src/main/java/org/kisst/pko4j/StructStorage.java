package org.kisst.pko4j;

import org.kisst.item4j.seq.TypedSequence;
import org.kisst.item4j.struct.Struct;

public interface StructStorage extends StorageOption {
	public Class<?> getRecordClass();
	public String create(Struct value);
	public Struct read(String key);
	public void update(Struct oldValue, Struct newValue);
	public void delete(Struct oldValue);
	public TypedSequence<Struct> findAll();
	default public void close() {}
	public boolean useCache();

	public String readBlob(String key, String path);
	public void writeBlob(String key, String path, String blob);
	public void appendBlob(String key, String path, String blob);
}
