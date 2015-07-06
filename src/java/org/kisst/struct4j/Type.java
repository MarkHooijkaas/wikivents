package org.kisst.struct4j;

import java.util.Date;

public interface Type {
	public String getName();
	public boolean isValidObject(Object obj);
	default public String getStringRepresentation(Object obj) { return ""+obj; }
	public Object parseString(String str);
	
	
	public interface Java<T> extends Type {
		public Class<?> getJavaClass();
		@Override default public String getName() { return getJavaClass().getSimpleName(); }
		@Override default public boolean isValidObject(Object obj) {
			if (obj==null)
				return false; // TODO: is this desirable behaviour?
			return getJavaClass().isAssignableFrom(obj.getClass());
		}
		@Override T parseString(String str);
	}
	public interface JavaString extends Java<String> {
		@Override default public Class<?> getJavaClass() { return String.class; }
		@Override default public String parseString(String str) { return str;}
	}
	public interface JavaBoolean extends Java<Boolean> {
		@Override default public Class<?> getJavaClass() { return String.class; }
		@Override default public Boolean parseString(String str) { return Boolean.parseBoolean(str);}
	}
	public interface JavaNumber extends Java<Number> {
		@Override default public Class<?> getJavaClass() { return Number.class; }
	}
	public interface JavaInteger extends JavaNumber {
		@Override default public Class<?> getJavaClass() { return Integer.class; }
		@Override default public Integer parseString(String str) { return Integer.parseInt(str);}
	}
	public interface JavaLong extends JavaNumber {
		@Override default public Class<?> getJavaClass() { return Long.class; }
		@Override default public Long parseString(String str) { return Long.parseLong(str);}
	}
	public interface JavaDate extends Java<Date>{
		@Override default public Class<?> getJavaClass() { return Date.class; }
		@SuppressWarnings("deprecation")
		@Override default public Date parseString(String str) { return new Date(Date.parse(str));}
	}

	public static final JavaString javaString=new JavaString(){};
	public static final JavaBoolean javaBoolean=new JavaBoolean(){};
	public static final JavaInteger javaInteger=new JavaInteger(){};
	public static final JavaLong javaLong=new JavaLong(){};
	public static final JavaDate javaDate=new JavaDate(){};
}
