package org.kisst.pko4j;

import java.lang.reflect.Constructor;

import org.kisst.item4j.Item;
import org.kisst.item4j.ReflectSchema;
import org.kisst.item4j.Type;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public class PkoSchema<T extends PkoObject> extends ReflectSchema<T> {
	public PkoSchema(Class<T> cls) { 
		super(cls); 
	}
	
	@SuppressWarnings("unchecked")
	public T createObject(PkoModel model, Struct doc) { 
		Constructor<?> cons=ReflectionUtil.getConstructor(getJavaClass(), new Class<?>[]{ model.getClass(), Struct.class} );
		return (T) ReflectionUtil.createObject(cons, new Object[]{model, doc} );
	}

	//public final IdField _id = new IdField();
	
	//public IdField getKeyField() { return _id;}

	public static class IdField extends BasicField<String> {
		public IdField() { super(Type.javaString, "_id"); }
		public IdField(String name) { super(Type.javaString, name); }
		public String getString(Struct data) { return Item.asString(data.getDirectFieldValue(name)); }; 
	}
}
