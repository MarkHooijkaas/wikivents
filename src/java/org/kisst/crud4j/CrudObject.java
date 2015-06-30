package org.kisst.crud4j;

import org.bson.types.ObjectId;
import org.kisst.struct4j.Struct;

public class CrudObject {
	public final String _id;
	public CrudObject(Struct data) { this._id=uniqueKey(data); }
	public CrudObject(String key) { this._id=uniqueKey(key); }
	protected String uniqueKey(Struct data) {
		String key= data.getString("_id",null);
		if (key==null)
			return uniqueKey(data);
		return key;
	}
	protected String uniqueKey(String key) {
		if (key==null)
			return uniqueKey();
		return key;
	}
	protected String uniqueKey() { return new ObjectId().toHexString();}
}
