package org.kisst.item4j;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public class ReflectSchema<T> extends SchemaBase implements Type<T> {
	private final Class<? extends T> javaClass;
	public ReflectSchema(Class<? extends T> javaClass) { this.javaClass=javaClass; }
	
	public Field<T> getField(String name) { return new ReflectField<T>(name); }
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
		public ReflectField(String name) { this.javafield=ReflectionUtil.getField(this.getClass(), name);}
		@Override public String getName() { return javafield.getName(); }
		//@SuppressWarnings("unchecked")
		//@Override public Class<T> getJavaClass() { return (Class<T>) javafield.getType(); }
		@Override public Type<T> getType() { return null; } // XXX TODO type registry
		@Override public Object getObject(Struct data) { return data.getDirectFieldValue(javafield.getName()); }
	}

	@Override public Class<? extends T> getJavaClass() { return this.javaClass;}
	@Override public T parseString(String str) { return null; } // TODO:
}