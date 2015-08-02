package org.kisst.item4j;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.kisst.util.ReflectionUtil;

public class ReflectSchema implements Schema {

	public Field getField(String name) { return new ReflectField(name); }
	public Iterable<String> fieldNames() { 
		ArrayList<String> result= new ArrayList<String>();
		for (java.lang.reflect.Field field: this.getClass().getFields()) {
			if (! Modifier.isStatic(field.getModifiers()))
				result.add(field.getName());
		}
		return result;

	}
	
	public class ReflectField implements Field {
		private java.lang.reflect.Field javafield;
		public ReflectField(String name) { this.javafield=ReflectionUtil.getField(this.getClass(), name);}
		@Override public String getName() { return javafield.getName(); }
		@Override public Class<?> getJavaClass() { return javafield.getType(); }
	}
}