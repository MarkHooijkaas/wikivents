package org.kisst.crud4j;

import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.SingleItemStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public abstract class CrudModelObject implements Struct {
	public final CrudSchema<?> schema;
	public <T extends CrudModelObject> CrudModelObject(CrudSchema<T> schema) { this.schema=schema; }

	@Override public String toString() { return toShortString(); }
	@Override public Iterable<String> fieldNames() { return schema.fieldNames(); }
	@Override public Object getDirectFieldValue(String name) { return ReflectionUtil.getFieldValueOrUnknownField(this, name); }
	
	@SuppressWarnings("unchecked")
	public <T extends CrudModelObject> T modified(CrudModel model, Struct modifiedFields) {
		return (T) schema.createObject(model, new MultiStruct(modifiedFields, this));
	}
	
	public <T extends CrudModelObject> T modified(CrudModel model, CrudSchema<T>.Field field, Object value) {
		return modified(model, new SingleItemStruct(field.getName(), value));
	}

}
