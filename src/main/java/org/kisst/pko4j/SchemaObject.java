package org.kisst.pko4j;

import java.lang.reflect.Constructor;

import org.kisst.item4j.Schema;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.SingleItemStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.item4j.struct.StructHelper;
import org.kisst.util.ReflectionUtil;

// TODO: this class may be moved to hooi4j
public abstract class SchemaObject implements Struct, PkoModel.MyObject {
	public final Schema schema;
	public <T extends SchemaObject> SchemaObject(Schema schema) { this.schema=schema; }

	@Override public String toString() { return StructHelper.toShortString(this); }
	@Override public Iterable<String> fieldNames() { return schema.fieldNames(); }
	@Override public Object getDirectFieldValue(String name) { return ReflectionUtil.getFieldValueOrUnknownField(this, name); }
	
	@SuppressWarnings("unchecked")
	public <T extends SchemaObject> T modified(PkoModel model, Struct modifiedFields) {
		Constructor<?> cons=ReflectionUtil.getConstructor(this.getClass(), new Class<?>[]{ model.getClass(), Struct.class} );
		return (T) ReflectionUtil.createObject(cons, new Object[] {model, new MultiStruct(modifiedFields, this)});
		//return (T) schema.createObject(model, new MultiStruct(newObject, this));
	}
	
	public <T extends SchemaObject> T modified(PkoModel model, Schema.Field<?> field, Object value) {
		return modified(model, new SingleItemStruct(field.getName(), value));
	}
}
