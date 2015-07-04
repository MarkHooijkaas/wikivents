package org.kisst.crud4j.impl;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.struct4j.BaseStruct;
import org.kisst.struct4j.ReflectStruct;
import org.kisst.struct4j.Struct;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DuplicateKeyException;

public abstract class MongoTable<T extends CrudObject> extends BaseTable<T> {
	private final DBCollection collection;
	public MongoTable(CrudSchema<T> schema, DB db) { 
		super(schema);
		this.collection=db.getCollection(schema.cls.getSimpleName());
	}
	
	@Override public void createInStorage(T value) {
		MongoStruct doc = new MongoStruct(makeStruct(value));
        collection.insert(doc.data);
	}
	@Override public Struct readFromStorage(String key) {
		BasicDBObject query = new BasicDBObject("_id", key);
		DBCursor cursor = collection.find(query);
		try {
			return new MongoStruct(cursor.next());
		} finally { cursor.close(); }		
	}
	@Override public void updateInStorage(T oldValue, T newValue) {
		MongoStruct oldDoc= new MongoStruct(makeStruct(oldValue));
		MongoStruct newDoc= new MongoStruct(makeStruct(newValue));
        collection.update(oldDoc.data, newDoc.data);
	}
	@Override public void deleteInStorage(T oldValue){} // TODO

	
	
	private static Struct makeStruct(Object value) {
		if (value instanceof Struct)
			return (Struct) value;
		return new ReflectStruct(value);
	}
	
	private class MongoStruct extends BaseStruct {
		private final DBObject data;
		public MongoStruct(DBObject data) { this.data=data; }
		public MongoStruct(Struct strc) { 
			this.data=new BasicDBObject(); 
			for(String key : strc.fieldNames())
				data.put(key, strc.getObject(key));
		}
		@Override public Iterable<String> fieldNames() { return data.keySet(); }
		@Override public Object getDirectFieldValue(String name) { return data.get(name);}
	}

	
	private class MyUniqueIndex extends BaseIndex<T> implements UniqueIndex<T>{
		private MyUniqueIndex(CrudSchema<T>.Field<?> keyField) {
			super(keyField); 
			DBObject keys= new BasicDBObject(keyField.name,1);
			DBObject options = new BasicDBObject("unique", true);
			try {
				collection.createIndex(keys, options);
			}
			catch (DuplicateKeyException e) { /* ignore */ }
		}
		@Override public T get(String field) { return null; } // TODO
	}

	public UniqueIndex<T> useUniqueIndex(CrudSchema<T>.Field<?> keyField) { return new MyUniqueIndex(keyField);} 
	public MultiIndex<T>  useMultiIndex(CrudSchema<T>.Field<?> keyField) { return null; } // TODO
	public OrderedIndex<T>  useOrderedIndex(CrudSchema<T>.Field<?> keyField) { return null; } // TODO

}
