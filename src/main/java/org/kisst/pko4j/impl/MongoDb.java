package org.kisst.pko4j.impl;

import org.bson.codecs.configuration.CodecRegistry;
import org.kisst.item4j.struct.StructProps;
import org.kisst.pko4j.PkoTable.KeyRef;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

public class MongoDb {
	private final MongoClient mongoClient;
	private final DB db;
	public MongoDb(StructProps props, MongoClientOptions options) {
		this.mongoClient = new MongoClient(props.getString("mongohost","localhost"), options);
		this.db = new DB(mongoClient,props.getString("mongodb"));
	}
	public void closeMongoDB() {
		mongoClient.close();
	}
	public DBCollection getCollection(String name) {
		printEncoder();
		return db.getCollection(name);
	}
	// test routine to debug Codec, but this will find custom codecs while insert will not 
	public void printEncoder() {
		MongoClientOptions options = mongoClient.getMongoClientOptions();
		CodecRegistry reg = options.getCodecRegistry();
		System.out.println(reg.get(KeyRef.class));
	}
}
