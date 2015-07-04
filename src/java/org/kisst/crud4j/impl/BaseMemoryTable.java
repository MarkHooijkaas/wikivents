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

	private class BaseIndex {
		private final CrudSchema<T>.Field<?> keyField;
		private BaseIndex(CrudSchema<T>.Field<?> keyField) {this.keyField=keyField; }
		protected final String getKey(T record) { return keyField.getString(record); }
	}
	
	private class MyUniqueIndex extends BaseIndex implements UniqueIndex<T>{
		private final ConcurrentHashMap<String, T> map = new ConcurrentHashMap<String, T>();
		private MyUniqueIndex(CrudSchema<T>.Field<?> keyField) {super(keyField); }
		
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

		private void insertRecord(T record) {
			String key=getKey(record);
			T oldValue = map.get(key);
			if (oldValue!=null) {
				if (! oldValue._id.equals(record._id))
					throw new RuntimeException("Trying to insert record with non-unique key "+key);
			}
			map.put(key, record);
		}
	}

	private class MyMultiIndex extends BaseIndex implements MultiIndex<T>{
		private final ConcurrentHashMap<String, T> map = new ConcurrentHashMap<String, T>();
		public MyMultiIndex(CrudSchema<T>.Field<?> keyField) { super(keyField);}

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

	public UniqueIndex<T> useUniqueIndex(CrudSchema<T>.Field<?> field) {
		BaseMemoryTable<T>.MyUniqueIndex result = new MyUniqueIndex(field);
		addIndex(result);
		return result;
	} 
	public MultiIndex<T>  useMultiIndex(CrudSchema<T>.Field<?> field) { 
		BaseMemoryTable<T>.MyMultiIndex result = new MyMultiIndex(field);
		addIndex(result);
		return result;
	} 

	public OrderedIndex<T>  useOrderedIndex() { return null; } // TODO

}
