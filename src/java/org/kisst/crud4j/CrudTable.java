package org.kisst.crud4j;

import org.kisst.struct4j.Struct;
import org.kisst.struct4j.seq.TypedSequence;

public interface CrudTable<T extends CrudObject> {
	public  CrudSchema<T> getSchema();
	public  String  getName();

	public T createObject(Struct props);

	public void create(T doc);
	public T read(String key);
	public void update(T oldValue, T newValue);
	public void updateFields(T oldValue, Struct newFields);
	public void delete(T oldValue);
	
	public Ref<T> getRef(String key);
	public String getKey(T obj);

	public interface Ref<T extends CrudObject> {
		public T get();
		public CrudTable<T> getTable();
	}

	public interface Index<T extends CrudObject> {
		public void notifyCreate(T record);
		public void notifyUpdate(T oldRrecord, T newRecord);
		public void notifyDelete(T record);
	}
	public interface UniqueIndex<T extends CrudObject> extends Index<T> {
		public T get(String field);
	}
	public interface MultiIndex<T extends CrudObject> extends Index<T> {
		public TypedSequence<T> get(String field);
	}
	public interface OrderedIndex<T extends CrudObject> extends Index<T> {
		public TypedSequence<T> get(String field);
	}
}
