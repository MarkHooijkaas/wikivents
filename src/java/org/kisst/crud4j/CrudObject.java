package org.kisst.crud4j;

import org.bson.types.ObjectId;
import org.kisst.struct4j.Struct;

public class CrudObject {
	public final CrudSchema<?> schema;
	public final String _id;
	public CrudObject(CrudSchema<?> schema, Struct data) { 
		this.schema= schema;
		this._id=createUniqueKey(data); 
	}
	protected String createUniqueKey(Struct data) {
		String key= data.getString("_id",null);
		if (key==null)
			return uniqueKey();
		return key;
	}
	protected String uniqueKey() { return new ObjectId().toHexString();}
}
