/**
Copyright 2008, 2009 Mark Hooijkaas

This file is part of the Caas tool.

The Caas tool is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The Caas tool is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with the Caas tool.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.kisst.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {
	public static String smartClassName(Object obj) { return obj==null ? null : smartClassName(obj.getClass()); } 
	public static String smartClassName(Class<? >cls) { 
		String name=cls.getCanonicalName();
		int pos=name.lastIndexOf('.');
		int prevpos=0;
		while (pos>0) {
			if (! Character.isUpperCase(name.charAt(pos+1)))
				return name.substring(prevpos+1);
			prevpos=pos;
			pos=name.lastIndexOf('.', pos-1);
		}
		return name;
	}
	
	public static Constructor<?> getConstructor(String classname, Class<?>[] signature) {
		try {
			Class<?> c = Class.forName(classname);
			return getConstructor(c, signature);
		} catch (ClassNotFoundException e) { throw new ReflectionException(null, null, e); }
	}

	public static Field getDeclaredField(Class<?> cls, String name) {
		try {
			return cls.getDeclaredField(name);
		} 
		catch (SecurityException e) { throw new ReflectionException(cls, name, e); }
		catch (NoSuchFieldException e) { throw new ReflectionException(cls, name, e); }
	}
	public static Field getField(Class<?> cls, String name) {
		try {
			return cls.getField(name);
		} 
		catch (SecurityException e) { throw new ReflectionException(cls, name, e); }
		catch (NoSuchFieldException e) { throw new ReflectionException(cls, name, e); }
	}
	public static Field getFieldOrNull(Class<?> cls, String fieldname) {
		try {
			return cls.getField(fieldname);
		} 
		catch (SecurityException e) { throw new ReflectionException(cls, fieldname, e); }
		catch (NoSuchFieldException e) { return null;}
	}
	public static List<Field> getAllPublicFieldsOfType(Class<?> objectClass, Class<?> fieldClass) {
		ArrayList<Field> result=new ArrayList<Field>();
		try {
			for (Field f : objectClass.getFields()) {
				if (fieldClass.isAssignableFrom(f.getType())) {
					result.add(f);
					//System.out.println(smartClassName(objectClass)+"::"+f.getName());
				}					
			}
			return result;
		}
		catch (IllegalArgumentException e) { throw new ReflectionException(objectClass, null, e); }
	}
	public static List<Field> getAllDeclaredFieldsOfType(Class<?> objectClass, Class<?> fieldClass) {
		ArrayList<Field> result=new ArrayList<Field>();
		try {
			for (Field f : objectClass.getDeclaredFields()) {
				if (fieldClass.isAssignableFrom(f.getType())) {
					result.add(f);
					//System.out.println(smartClassName(objectClass)+"::"+f.getName());
				}					
			}
			return result;
		}
		catch (IllegalArgumentException e) { throw new ReflectionException(objectClass, null, e); }
	}

	public static<T> List<T> getAllDeclaredFieldValuesOfType(Object obj, Class<T> type) {
		ArrayList<T> result=new ArrayList<T>();
		try {
			for (Field f : obj.getClass().getDeclaredFields()) {
				if (type.isAssignableFrom(f.getType())) {
					@SuppressWarnings("unchecked")
					T value = (T)f.get(obj);
					result.add(value);
					//System.out.println(smartClassName(obj)+"::"+f.getName());
				}
			}
			return result;
		}
		catch (IllegalArgumentException e) { throw new ReflectionException(obj, type, e); }
		catch (IllegalAccessException e) { throw new ReflectionException(obj, type, e); }
	}

	public static final Object UNKNOWN_FIELD=new Object() { @Override public String toString() { return "UNKNOWN_FIELD";} }; // TODO: make class with fieldname???
	public static Object getFieldValueOrUnknownField(Object obj, String fieldname) {
		try {
			Field field = obj.getClass().getField(fieldname);
			return field.get(obj);
		}
		catch (IllegalArgumentException e) { throw new ReflectionException(obj, fieldname, e); }
		catch (SecurityException e) { throw new ReflectionException(obj, fieldname, e); }
		catch (NoSuchFieldException e) { return UNKNOWN_FIELD; } 
		catch (IllegalAccessException e) { throw new ReflectionException(obj, fieldname, e); }
	}
	public static Object getFieldValue(Object obj, Field field) {
		try {
			return field.get(obj);
		}
		catch (IllegalArgumentException e) { throw new ReflectionException(obj, field, e); }
		catch (IllegalAccessException e) { throw new ReflectionException(obj, field, e); }
		catch (SecurityException e) { throw new ReflectionException(obj, field, e); }
	}
	public static Object getFieldValue(Object obj, String name) {
		try {
			return getField(obj.getClass(),name).get(obj);
		}
		catch (IllegalArgumentException e) { throw new ReflectionException(obj, name, e); }
		catch (IllegalAccessException e) { throw new ReflectionException(obj, name, e); }
		catch (SecurityException e) { throw new ReflectionException(obj, name, e); }
	}

	
	public static Method getMethod(Class<?> cls, String name, Class<?>[] signature) {
		Method[] metharr = cls.getMethods();
		//System.out.println("Looking for "+name);
		//printSignature(signature);
		//System.out.println("----");
		for (Method meth :metharr) {
			if (name.equals(meth.getName())) {
				Class<?>[] paramtypes = meth.getParameterTypes();
				//printSignature(paramtypes);
				if (java.util.Arrays.equals(signature, paramtypes))
					return meth;
				//System.out.println("NOT");
			}
		}
		return null;
	}

	public static Constructor<?> getFirstCompatibleConstructor(Class<?> cls, Class<?>[] signature) {
		Constructor<?>[] consarr = cls.getDeclaredConstructors();
		for (int i=0; i<consarr.length; i++) {
			Class<?>[] paramtypes = consarr[i].getParameterTypes();
			if (paramtypes.length!=signature.length)
				continue;
			boolean compatible=true;
			for (int j=0; j<signature.length; j++) {
				if (!signature[j].isAssignableFrom(paramtypes[j]))
					compatible=false;
			}
			if (compatible)
				return consarr[i];
		}
		return null;
	}

	public static Constructor<?> getConstructor(Class<?> cls, Class<?>[] signature) {
		Constructor<?>[] consarr = cls.getDeclaredConstructors();
		for (int i=0; i<consarr.length; i++) {
			Class<?>[] paramtypes = consarr[i].getParameterTypes();
			if (java.util.Arrays.equals(signature, paramtypes))
				return consarr[i];
		}
		return null;
	}
	
	public static Object invoke(Object o, Method m, Object[] args) {
		try {
			m.setAccessible(true);
			return m.invoke(o, args);
		}
		catch (IllegalAccessException e) {  throw new ReflectionException(o, m, e); }
		catch (InvocationTargetException e) {e.getCause().printStackTrace();  throw new ReflectionException(o, m, e); }
	}
	public static Object invoke(Object o, String name, Object[] args) {
		return invoke(o.getClass(),o, name, args);
	}
	public static Object invoke(Class<?> c, Object o, String name, Object[] args) {
		try {
			return invoke(o, c.getMethod(name, getSignature(args)), args);
		}
		catch (NoSuchMethodException e) { throw new ReflectionException(c, name, e); }
	}

	public static void printSignature(Class<?>[] sig) {
		for (Class<?> c: sig)
			System.out.println(c);
	}
	
	private static Class<?>[] getSignature(Object[] args) {
		Class<?>[] signature=new Class<?>[args.length];
		for (int i=0; i<args.length; i++)
			signature[i]=args[i].getClass();
		return signature;
	}

	public static Class<?> findClass(String name) {
		try {	
			return Class.forName(name);
		}
		catch (IllegalArgumentException e) { throw new ReflectionException(name, null, e); }
		catch (ClassNotFoundException e) { throw new ReflectionException(name, null, e); }
	}

	public static Object createObject(String classname, Object[] args) {
		return createObject(findClass(classname), args);
	}
	public static Object createObject(Class<?> c, Object[] args) {
		Constructor<?> cons= getFirstCompatibleConstructor(c,getSignature(args));
		return createObject(cons,args);
	}
			
	public static Object createObject(Constructor<?> cons, Object[] args) {
		try {
			cons.setAccessible(true);
			return cons.newInstance(args);
		}
		catch (IllegalAccessException e) { throw new ReflectionException(cons, args, e); }
		catch (InstantiationException e) { throw new ReflectionException(cons, args, e); }
		catch (InvocationTargetException e) { throw new ReflectionException(cons, args, e); }
	}

	public static Object createObject(String classname) {
		return createObject(findClass(classname));
	}
	public static Object createObject(Class<?> c) {
		try {
			return c.newInstance();
		}
		catch (IllegalArgumentException e) { throw new ReflectionException(c, null, e); }
		catch (IllegalAccessException e) {  throw new ReflectionException(c, null, e); }
		catch (InstantiationException e) {  throw new ReflectionException(c, null, e); }
	}
	
	public static String toString(Object obj, String... fields) {
		StringBuilder result= new StringBuilder(obj.getClass().getSimpleName()+"(");
		String sep="";
		if (fields!=null) for (String field: fields) {
			if (field!=null) {
				result.append(sep);
				result.append(field);
				sep=",";
			}
		}
		result.append(")");
		return result.toString();
	}
	
	public static String toString(Object obj) { return toString(obj,0); }
	public static String toString(Object obj, int depth) {
		// TODO: special support for Hashmaps? Lists, etc. Better indentation, newline support etc?
		if (obj==null)
			return "null";
		if (obj instanceof String)
			return "\""+obj+"\""; // TODO: escape quotes?
		if (obj instanceof Number)
			return obj.toString();
		if (obj instanceof Boolean)
			return obj.toString();
		if (depth<0)
			return obj.getClass().getSimpleName()+"()";
		depth--;
		StringBuilder result= new StringBuilder();
		result.append(obj.getClass().getSimpleName()+"(");
		String sep="";
		for (Field field : obj.getClass().getFields()) {
			try {
				if (! Modifier.isStatic(field.getModifiers())) {
					result.append(sep+field.getName()+"="+toString(field.get(obj), depth));
					sep=", ";
				}
			} 
			catch (IllegalArgumentException e) { throw new ReflectionException(obj, result, e);} 
			catch (IllegalAccessException e) { throw new ReflectionException(obj, result, e);}
		}
		result.append(")");
		return result.toString();
	}
	
	public static class ReflectionException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public final Object target;
		public final Object member;
		public ReflectionException(Object target, Object member, Throwable e) {
			super(msg(target,member)+": "+e.getMessage(),e);
			this.target=target;
			this.member=member;
		}
		private static String msg(Object target, Object member) {
			String t=null;
			String m=null;
			if (target instanceof Class)
				t=((Class<?>) target).getName();
			else
				t=""+t; // TODO: infinite recursion if this is in toString???
			if (member instanceof Member)
				m=((Member) member).getName();
			else
				m=""+member;
			return "Reflection error when accessing "+m+" of class "+t;
		}
	}
}