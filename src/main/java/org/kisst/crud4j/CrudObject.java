package org.kisst.crud4j;

import org.bson.types.ObjectId;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public abstract class CrudObject implements Struct {
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
	abstract public boolean mayBeChangedBy(String userId);

	@Override public String toString() { return toShortString(); }
	@Override public Iterable<String> fieldNames() { return schema.fieldNames(); }
	@Override public Object getDirectFieldValue(String name) { return ReflectionUtil.getFieldValue(this, name); }
}
