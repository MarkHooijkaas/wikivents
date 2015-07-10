package org.kisst.crud4j;

import org.kisst.item4j.seq.Sequence;
import org.kisst.item4j.struct.Struct;

public interface CrudTable<T extends CrudObject> {
	public  CrudSchema<T> getSchema();
	public  String  getName();

	public T createObject(Struct props);

	public void create(T doc);
	public T read(String key);
	public void update(T oldValue, T newValue);
	public void updateFields(T oldValue, Struct newFields);
	public void delete(T oldValue);
	public Sequence<T> findAll(); 
	
	public Ref<T> getRef(String key);
	public String getKey(T obj);

	public interface Ref<T extends CrudObject> {
		public T get();
		public CrudTable<T> getTable();
	}

	public interface UniqueIndex<T extends CrudObject> { public T get(String field); }
	public interface MultiIndex<T extends CrudObject> { public Sequence<T> get(String field); }
	public interface OrderedIndex<T extends CrudObject> { public Sequence<T> get(String field);}

	
}
