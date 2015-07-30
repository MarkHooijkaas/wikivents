package org.kisst.crud4j;

import org.bson.types.ObjectId;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.SingleItemStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public abstract class CrudObject extends CrudModelObject {//implements Struct {
	public final CrudTable<?> table;
	//public final CrudSchema<?> schema;
	public final String _id;
	public <T extends CrudObject> CrudObject(CrudTable<T> table, Struct data) {
		super(table.schema);
		this.table=table;
		//this.schema=table.schema;
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

	@SuppressWarnings("unchecked")
	public<T extends CrudObject> CrudRef<T> getRef() { return new CrudRef<T>((CrudTable<T>)table, _id); }
	
	/*
	@Override public String toString() { return toShortString(); }
	@Override public Iterable<String> fieldNames() { return table.schema.fieldNames(); }
	@Override public Object getDirectFieldValue(String name) { return ReflectionUtil.getFieldValueOrUnknownField(this, name); }
	
	public <T extends CrudObject> T modified(CrudModel model, Struct modifiedFields) {
		return (T) table.schema.createObject(model, new MultiStruct(modifiedFields, this));
	}
	
	public <T extends CrudObject> T modified(CrudModel model, CrudSchema<T>.Field field, Object value) {
		return modified(model, new SingleItemStruct(field.getName(), value));
	}
*/
}
