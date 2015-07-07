package org.kisst.crud4j.impl;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;

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
	

	private class MyUniqueIndex extends BaseIndex<T> implements UniqueIndex<T>{
		private MyUniqueIndex(CrudSchema<T>.Field<?> keyField) {
			super(keyField); 
			DBObject keys= new BasicDBObject(keyField.getName(),1);
			DBObject options = new BasicDBObject("unique", true);
			try {
				collection.createIndex(keys, options);
			}
			catch (DuplicateKeyException e) { /* ignore */ } // TODO better way to ensure index
		}
		@Override public T get(String field) { return null; } // TODO
	}

	public UniqueIndex<T> useUniqueIndex(CrudSchema<T>.Field<?> keyField) { return new MyUniqueIndex(keyField);} 
	public MultiIndex<T>  useMultiIndex(CrudSchema<T>.Field<?> keyField) { return null; } // TODO
	public OrderedIndex<T>  useOrderedIndex(CrudSchema<T>.Field<?> keyField) { return null; } // TODO

}
