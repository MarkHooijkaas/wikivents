package org.kisst.crud4j;

import org.bson.types.ObjectId;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public abstract class CrudObject implements Struct {
	public final String _id;
	public CrudObject(Struct data) { 
		this._id=createUniqueKey(data); 
	}
	public String getKey() { return _id;} 
	protected String createUniqueKey(Struct data) {
		String key= data.getString("_id",null);
		if (key==null)
			return uniqueKey();
		return key;
	}
	protected String uniqueKey() { return new ObjectId().toHexString();}
	abstract public boolean mayBeChangedBy(String userId);

	@Override public String toString() { return toShortString(); }
	@Override public Iterable<String> fieldNames() { throw new RuntimeException("unsupported to test if used");}
	@Override public Object getDirectFieldValue(String name) { return ReflectionUtil.getFieldValueOrUnknownField(this, name); }
}
