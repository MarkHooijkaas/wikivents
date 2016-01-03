package org.kisst.pko4j.impl;

import java.util.ArrayList;

import org.kisst.item4j.seq.ArraySequence;
import org.kisst.item4j.seq.TypedSequence;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.PkoSchema;
import org.kisst.pko4j.StructStorage;
import org.kisst.props4j.Props;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoStorage implements StructStorage {

	private final DBCollection collection;
	private final PkoSchema<?> schema;
	private final boolean useCache;
	private final MongoDb db;
	
	public MongoStorage(PkoSchema<?> schema, Props props, MongoDb db) {
		this.schema=schema;
		this.db=db;
		this.collection=db.getCollection(schema.getJavaClass().getSimpleName());
		this.useCache=props.getBoolean("useCache",false);
	}
	@Override public boolean useCache() { return this.useCache; }

	@Override public void close() { db.closeMongoDB(); }
	@Override public Class<?> getRecordClass() { return schema.getJavaClass(); }
	@Override public String create(Struct value) {
		//db.printEncoder();
		MongoStruct doc = new MongoStruct(value);
        collection.insert(doc.data);
        return (String) doc.getDirectFieldValue("_id");
	}
	@Override public Struct read(String key) {
		BasicDBObject query = new BasicDBObject("_id", key);
		DBCursor cursor = collection.find(query);
		try {
			return new MongoStruct(cursor.next());
		} finally { cursor.close(); }		
	}
	@Override public void update(Struct oldValue, Struct newValue) {
		MongoStruct oldDoc= new MongoStruct(oldValue);
		MongoStruct newDoc= new MongoStruct(newValue);
        collection.update(oldDoc.data, newDoc.data);
	}
	@Override public void delete(Struct oldValue){ collection.remove(new MongoStruct(oldValue).data); } 

	@Override public TypedSequence<Struct> findAll() {
		ArrayList<Struct> list=new ArrayList<Struct>();
		DBCursor cursor = collection.find();
		try {
			for (DBObject obj: cursor)
				list.add(new MongoStruct(obj));
			return new ArraySequence<Struct>(Struct.class,list);
		}
		finally { cursor.close(); }
	}
	@Override public String readBlob(String key, String path) { return null; }		// TODO Auto-generated method stub
	@Override public void writeBlob(String key, String path, String blob) {} 		// TODO Auto-generated method stub
	@Override public void appendBlob(String key, String path, String blob) {} 		// TODO Auto-generated method stub
}
