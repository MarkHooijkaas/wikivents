package org.kisst.crud4j;

import java.lang.reflect.Constructor;

import org.kisst.item4j.ObjectSchema;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.SingleItemStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public abstract class SchemaObject implements Struct, CrudModelObject {
	public final ObjectSchema<?> schema;
	public <T extends SchemaObject> SchemaObject(ObjectSchema<T> schema) { this.schema=schema; }

	@Override public String toString() { return toShortString(); }
	@Override public Iterable<String> fieldNames() { return schema.fieldNames(); }
	@Override public Object getDirectFieldValue(String name) { return ReflectionUtil.getFieldValueOrUnknownField(this, name); }
	
	@SuppressWarnings("unchecked")
	public <T extends SchemaObject> T modified(CrudModel model, Struct modifiedFields) {
		Constructor<?> cons=ReflectionUtil.getConstructor(this.getClass(), new Class<?>[]{ model.getClass(), Struct.class} );
		return (T) ReflectionUtil.createObject(cons, new Object[] {model, new MultiStruct(modifiedFields, this)});
		//return (T) schema.createObject(model, new MultiStruct(newObject, this));
	}
	
	public <T extends SchemaObject> T modified(CrudModel model, CrudSchema<T>.Field field, Object value) {
		return modified(model, new SingleItemStruct(field.getName(), value));
	}
}
