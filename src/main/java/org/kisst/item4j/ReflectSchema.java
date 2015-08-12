package org.kisst.item4j;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public class ReflectSchema<T> extends SchemaBase implements Type<T> {
	private final Class<? extends T> javaClass;
	public ReflectSchema(Class<? extends T> javaClass) { this.javaClass=javaClass; }
	
	public Field<T> getField(String name) { return new ReflectField<T>(this.getClass(), name); }
	public Iterable<String> fieldNames() { 
		ArrayList<String> result= new ArrayList<String>();
		for (java.lang.reflect.Field field: this.getClass().getFields()) {
			if (! Modifier.isStatic(field.getModifiers()))
				result.add(field.getName());
		}
		return result;

	}
	
	private static class ReflectField<T> implements Field<T> {
		private java.lang.reflect.Field javafield;
		public ReflectField(Class<?> cls, String name) { this.javafield=ReflectionUtil.getField(cls, name);}
		@Override public String getName() { return javafield.getName(); }
		//@SuppressWarnings("unchecked")
		//@Override public Class<T> getJavaClass() { return (Class<T>) javafield.getType(); }
		@Override public Type<T> getType() { return null; } // XXX TODO type registry
		@Override public Object getObject(Struct data) { return data.getDirectFieldValue(javafield.getName()); }
	}

	@Override public Class<? extends T> getJavaClass() { return this.javaClass;}

	@SuppressWarnings("unchecked")
	@Override public T convertFrom(Item.Factory factory, Object obj) {
		if (obj==null)
			return null;
		if (javaClass.isAssignableFrom(obj.getClass()))
			return (T) obj;
		throw new IllegalArgumentException("can not convert "+obj.getClass()+" to "+javaClass+" for "+obj);
	}
}