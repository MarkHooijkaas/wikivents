package org.kisst.crud4j.impl;

import org.kisst.item4j.struct.BaseStruct;
import org.kisst.item4j.struct.Struct;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoStruct extends BaseStruct {
	public final DBObject data;
	public MongoStruct(DBObject data) { this.data=data; }
	public MongoStruct(Struct strc) { 
		this.data=new BasicDBObject(); 
		for(String key : strc.fieldNames())
			data.put(key, strc.getObject(key));
	}
	@Override public Iterable<String> fieldNames() { return data.keySet(); }
	@Override public Object getDirectFieldValue(String name) { return data.get(name);}
} 
