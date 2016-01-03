package org.kisst.pko4j.impl;

import org.kisst.item4j.struct.Struct;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoStruct implements Struct {
	public final DBObject data;
	public MongoStruct(DBObject data) { this.data=data; }
	public MongoStruct(Struct strc) { 
		this.data=new BasicDBObject(); 
		for(String key : strc.fieldNames()) {
			Object value = strc.getDirectFieldValue(key);
			// Mongo codecs do not seem to work, so most types should just be made toString
			if (value==null)  
				data.put(key, value);
			else if (value instanceof String)  
				data.put(key, value);
			else if (value instanceof Integer)  
				data.put(key, value);
			else if (value instanceof Long)  
				data.put(key, value);
			else if (value instanceof Boolean)  
				data.put(key, value);
			else
				data.put(key, value.toString());

		}
	}
	@Override public Iterable<String> fieldNames() { return data.keySet(); }
	@Override public Object getDirectFieldValue(String name) { return data.get(name);}
} 
