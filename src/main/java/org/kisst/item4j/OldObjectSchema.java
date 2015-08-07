package org.kisst.item4j;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;

import org.kisst.item4j.json.JsonParser;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public class OldObjectSchema<T> implements Schema, Type<T> {
	//public static Factory globalFactory=Factory.basicFactory;

	private final LinkedHashMap<String, Field<?>> fields=new LinkedHashMap<String, Field<?>>(); // TODO: make Immutable
	public final Class<T> cls;
	private Constructor<?> cons;
	public OldObjectSchema(Class<T> cls) { 
		this.cls=cls;
		this.cons=ReflectionUtil.getConstructor(cls, new Class<?>[]{ Struct.class} );
	}
	public Field<?> getField(String name){ return fields.get(name);}
	public Iterable<String> fieldNames() { return fields.keySet(); }

	@Override public Class<T> getJavaClass() { return cls;}
	@Override public String getName() { return cls.getSimpleName(); }
	@Override public boolean isValidObject(Object obj) { return false; } // TODO
	@Override public T parseString(String str) { return createObject(new JsonParser().parse(str)); }
	@SuppressWarnings("unchecked")
	public T createObject(Struct doc) { return (T) ReflectionUtil.createObject(cons, new Object[]{doc} );}
	
	protected void addAllFields() { addAllFields(this.getClass());	}
	private void addAllFields(Class<?> cls) {
		if (cls==null || cls==Object.class)
			return;
		addAllFields(cls.getSuperclass());
		try {
			for (java.lang.reflect.Field f : cls.getDeclaredFields()) {
				if (Field.class.isAssignableFrom(f.getType())) {
					//System.out.println("adding "+f);
					fields.put(f.getName(), (Field<?>) f.get(this));
				}
			}
		}
		catch (IllegalArgumentException e) { throw new RuntimeException(e); }
		catch (IllegalAccessException e) { throw new RuntimeException(e); }
	}

}