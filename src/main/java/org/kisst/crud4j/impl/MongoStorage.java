package org.kisst.crud4j.impl;

import java.util.ArrayList;

import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.StructStorage;
import org.kisst.item4j.Schema;
import org.kisst.item4j.seq.ArraySequence;
import org.kisst.item4j.seq.TypedSequence;
import org.kisst.item4j.struct.Struct;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoClient;

public class MongoStorage implements StructStorage {

	private final DBCollection collection;
	private final CrudSchema<?>.IdField keyField;
	private final CrudSchema<?> schema;
	private final boolean useCache;
	
	public MongoStorage(CrudSchema<?> schema, Struct props) {
		this.schema=schema;
		this.collection=getMongoDB(props).getCollection(schema.cls.getSimpleName());
		this.keyField=schema.getKeyField();
		this.useCache=props.getBoolean("useCache",false);
	}
	@Override public boolean useCache() { return this.useCache; }
	@Override public void close() { closeMongoDB(); }
	@Override public Class<?> getRecordClass() { return schema.cls; }
	@Override public String createInStorage(Struct value) {
		MongoStruct doc = new MongoStruct(value);
        collection.insert(doc.data);
        return keyField.getString(doc);
	}
	@Override public Struct readFromStorage(String key) {
		BasicDBObject query = new BasicDBObject(keyField.getName(), key);
		DBCursor cursor = collection.find(query);
		try {
			return new MongoStruct(cursor.next());
		} finally { cursor.close(); }		
	}
	@Override public void updateInStorage(Struct oldValue, Struct newValue) {
		MongoStruct oldDoc= new MongoStruct(oldValue);
		MongoStruct newDoc= new MongoStruct(newValue);
        collection.update(oldDoc.data, newDoc.data);
	}
	@Override public void deleteInStorage(Struct oldValue){ collection.remove(new MongoStruct(oldValue).data); } 

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

	@Override public UniqueIndex useUniqueIndex(Schema<?>.Field ... fields) { return new MyUniqueIndex(fields);} 
	@Override public MultiIndex  useMultiIndex(Schema<?>.Field ... fields) { return new MyMultiIndex(fields); } 
	//public OrderedIndex  useOrderedIndex(SchkeyField) { return null; } // TODO

	
	private class MyUniqueIndex extends Index implements UniqueIndex {
		private MyUniqueIndex(CrudSchema<?>.Field ... fields) {
			super(fields);
		}
		@Override public Struct get(String ... values) {
			DBCursor cursor = query(values);
			try {
				return new MongoStruct(cursor.one());
			}
			finally { cursor.close(); }
		}
	}

	private class MyMultiIndex extends Index implements MultiIndex {
		private MyMultiIndex(CrudSchema<?>.Field ... fields) {
			super(fields);
		}
		@Override public TypedSequence<Struct> get(String ... values) {
			ArrayList<Struct> list = new ArrayList<Struct>();
			DBCursor cursor = query(values);
			try {
				list.add(new MongoStruct(cursor.one()));
			}
			finally { cursor.close(); }
			return new ArraySequence<Struct>(Struct.class,list);
		}
	}

	private class Index {
		private final CrudSchema<?>.Field[] fields;
		protected Index(CrudSchema<?>.Field ... fields) {
			this.fields=fields;
			BasicDBObject keys= new BasicDBObject();
			for (CrudSchema<?>.Field f:fields)
				keys.append(f.getName(), 1);
			DBObject options = new BasicDBObject("unique", true);
			try {
				collection.createIndex(keys, options);
			}
			catch (DuplicateKeyException e) { /* ignore */ } // TODO better way to ensure index
		}
		protected DBCursor query(String ... values) {
			if (fields.length!=values.length)
				throw new IllegalArgumentException("query with wrong number of arguments "); // TODO add info
			BasicDBObject keys= new BasicDBObject();
			int i=0;
			for (CrudSchema<?>.Field f:fields)
				keys.append(f.getName(), values[i++]);
			return collection.find(keys);
		} 
	}

	private static MongoClient mongoClient;
	private static DB db;
	private static DB getMongoDB(Struct props) {
		if (db!=null)
			return db;
		mongoClient = new MongoClient(props.getString("mongohost", "localhost"));
		db = new DB(mongoClient,props.getString("mongodb", "wikivents"));
		return db;
	}
	private static void closeMongoDB() {
		if (mongoClient==null)
			return;
		mongoClient.close();
		mongoClient=null;
		db=null;
	}

}
