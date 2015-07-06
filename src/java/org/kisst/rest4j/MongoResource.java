package org.kisst.rest4j;

import org.kisst.crud4j.impl.MongoStruct;
import org.kisst.struct4j.Struct;
import org.kisst.struct4j.seq.TypedSequence;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class MongoResource implements Resource {
	private final DBCollection collection;
	public MongoResource(DBCollection coll) { this.collection=coll; }
	
	@Override public void createResource(Struct struct) {
		MongoStruct newObject = new MongoStruct(struct);
		collection.insert(newObject.data);
	}
	@Override public TypedSequence<Struct> getResources(String[] filters) {
		return null; // TODO
	}
	@Override public Struct getSingleResource(String key) {
		BasicDBObject query = new BasicDBObject("_id", key);
		DBCursor cursor = collection.find(query);
		try {
			return new MongoStruct(cursor.next());
		} finally { cursor.close(); }		
	}
	
	@Override public void updateResource(String key, Struct newData) {
		BasicDBObject query = new BasicDBObject("_id", key);
		MongoStruct newObject = new MongoStruct(newData);
		collection.update(query, newObject.data);
	}
	
	@Override public void deleteResource(String key) {
		BasicDBObject query = new BasicDBObject("_id", key);
		collection.remove(query);
	}
}
