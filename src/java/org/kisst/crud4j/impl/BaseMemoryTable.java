package org.kisst.crud4j.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.struct4j.seq.TypedSequence;

public abstract class BaseMemoryTable<T extends CrudObject> extends BaseTable<T> {
	private final ConcurrentHashMap<String, T> cache=new ConcurrentHashMap<String,T>();
	public BaseMemoryTable(CrudSchema<T> schema) { super(schema);	}

	@Override public void create(T doc) {
		super.create(doc);
		cache.put(doc._id, doc);
	}
	@Override public T read(String key) { 
		T result = cache.get(key);
		if (result==null) {
			result=super.read(key);
			cache.put(key, result);
		}
		return result;
	}
	@Override public void update(T oldValue, T newValue) { 
		super.update(oldValue, newValue);
		cache.put(newValue._id, newValue);
	}
	@Override public void delete(T oldValue) {
		super.delete(oldValue);
		cache.remove(oldValue._id);
	}


	private class MyUniqueIndex implements UniqueIndex<T>{
		private final ConcurrentHashMap<String, T> map = new ConcurrentHashMap<String, T>();
		
		@Override public T get(String field) { return map.get(field); }

		@Override public void notifyCreate(T record) {
			insertRecord(record);
		}

		@Override public void notifyUpdate(T oldRecord, T newRecord) {
			String newKey= getKey(oldRecord);
			String oldKey = getKey(oldRecord);
			if (newKey.equals(oldKey))
				return;
			insertRecord(newRecord);
			map.remove(oldKey);
		}

		@Override public void notifyDelete(T oldRecord) {
			String oldKey = getKey(oldRecord);
			map.remove(oldKey);
		}

		private String getKey(T record) {
			// TODO Auto-generated method stub
			return null;
		}

		private void insertRecord(T record) {
			String key=getKey(record);
			T oldValue = map.get(key);
			if (oldValue!=null) {
				String oldKey = getKey(oldValue);
				if (! key.equals(oldKey))
					throw new RuntimeException("Trying to insert record with non-unique key "+key);
			}
			map.put(key, record);
		}
	}

	private class MyMultiIndex implements MultiIndex<T>{
		private final ConcurrentHashMap<String, T> map = new ConcurrentHashMap<String, T>();
		
		@Override public TypedSequence<T> get(String field) { return null; }

		@Override public void notifyCreate(T record) {
			insertRecord(record);
		}

		@Override public void notifyUpdate(T oldRecord, T newRecord) {
			String newKey= getKey(oldRecord);
			String oldKey = getKey(oldRecord);
			if (newKey.equals(oldKey))
				return;
			insertRecord(newRecord);
			map.remove(oldKey);
		}

		@Override public void notifyDelete(T oldRecord) {
			String oldKey = getKey(oldRecord);
			map.remove(oldKey);
		}

		private String getKey(T record) {
			// TODO Auto-generated method stub
			return null;
		}

		private void insertRecord(T record) {
			String key=getKey(record);
			T oldValue = map.get(key);
			if (oldValue!=null) {
				String oldKey = getKey(oldValue);
				if (! key.equals(oldKey))
					throw new RuntimeException("Trying to insert record with non-unique key "+key);
			}
			map.put(key, record);
		}
	}

	public UniqueIndex<T> useUniqueIndex() { return new MyUniqueIndex(); } // TODO
	public MultiIndex<T>  useMultiIndex() { return new MyMultiIndex(); } // TODO
	public OrderedIndex<T>  useOrderedIndex() { return null; } // TODO

}
