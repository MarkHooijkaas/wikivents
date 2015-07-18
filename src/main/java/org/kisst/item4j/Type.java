package org.kisst.item4j;

import java.util.Date;

public interface Type<T> {
	public Class<T> getJavaClass();
	public T parseString(String str);

	default public String getStringRepresentation(Object obj) { return ""+obj; }
	default public String getName() { return getJavaClass().getSimpleName(); }
	default public boolean isValidObject(Object obj) {
		if (obj==null)
			return false; // TODO: is this desirable behaviour?
		return getJavaClass().isAssignableFrom(obj.getClass());
	}


	public interface JavaString extends Type<String> {
		@Override default public Class<String> getJavaClass() { return String.class; }
		@Override default public String parseString(String str) { return str;}
	}
	public interface JavaBoolean extends Type<Boolean> {
		@Override default public Class<Boolean> getJavaClass() { return Boolean.class; }
		@Override default public Boolean parseString(String str) { return Boolean.parseBoolean(str);}
	}
	public interface JavaInteger extends Type<Integer> {
		@Override default public Class<Integer> getJavaClass() { return Integer.class; }
		@Override default public Integer parseString(String str) { return Integer.parseInt(str);}
	}
	public interface JavaLong extends Type<Long> {
		@Override default public Class<Long> getJavaClass() { return Long.class; }
		@Override default public Long parseString(String str) { return Long.parseLong(str);}
	}
	public interface JavaDate extends Type<Date>{
		@Override default public Class<Date> getJavaClass() { return Date.class; }
		@SuppressWarnings("deprecation")
		@Override default public Date parseString(String str) { return new Date(Date.parse(str));}
	}

	public static final JavaString javaString=new JavaString(){};
	public static final JavaBoolean javaBoolean=new JavaBoolean(){};
	public static final JavaInteger javaInteger=new JavaInteger(){};
	public static final JavaLong javaLong=new JavaLong(){};
	public static final JavaDate javaDate=new JavaDate(){};

	public class Helper<T> implements Type<T> {
		private final Class<?> cls;
		private final Item.Factory factory;
		public Helper(Class<?> cls, Item.Factory factory) {
			this.cls=cls;
			this.factory=factory;
		}
		@SuppressWarnings("unchecked")
		public Class<T> getJavaClass() { return (Class<T>) this.cls; };  
		@Override public T parseString(String str) { return factory.construct(cls,str); }
		@Override public String getName() { return getJavaClass().getSimpleName(); }
		@Override public boolean isValidObject(Object obj) {
			if (obj==null)
				return false; // TODO: is this desirable behaviour?
			return getJavaClass().isAssignableFrom(obj.getClass());
		}
	}
}
