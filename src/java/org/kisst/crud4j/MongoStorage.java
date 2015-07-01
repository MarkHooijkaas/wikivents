package org.kisst.crud4j;

import org.kisst.struct4j.BaseStruct;
import org.kisst.struct4j.ReflectStruct;
import org.kisst.struct4j.Struct;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoStorage<T extends CrudObject> implements Storage<T> {
	private final DBCollection collection;
	private final CrudSchema<T> schema;
	public MongoStorage(CrudSchema<T> schema, DB db) { 
		this.collection=db.getCollection(schema.cls.getSimpleName());
		this.schema=schema;
	}
	public CrudSchema<T> getSchema() { return this.schema; }
	
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
}
