package org.kisst.item4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
	public interface JavaLocalDate extends Type<LocalDate>{
		@Override default public Class<LocalDate> getJavaClass() { return LocalDate.class; }
		@Override default public LocalDate parseString(String str) { return LocalDate.parse(str);}
	}
	public interface JavaLocalTime extends Type<LocalTime>{
		@Override default public Class<LocalTime> getJavaClass() { return LocalTime.class; }
		@Override default public LocalTime parseString(String str) { return LocalTime.parse(str);}
	}
	public interface JavaLocalDateTime extends Type<LocalDateTime>{
		@Override default public Class<LocalDateTime> getJavaClass() { return LocalDateTime.class; }
		@Override default public LocalDateTime parseString(String str) { return LocalDateTime.parse(str);}
	}
	public interface JavaInstant extends Type<Instant>{
		@Override default public Class<Instant> getJavaClass() { return Instant.class; }
		@Override default public Instant parseString(String str) { return Instant.parse(str);}
	}

	public static final JavaString javaString=new JavaString(){};
	public static final JavaBoolean javaBoolean=new JavaBoolean(){};
	public static final JavaInteger javaInteger=new JavaInteger(){};
	public static final JavaLong javaLong=new JavaLong(){};
	public static final JavaLocalDate javaLocalDate=new JavaLocalDate(){};
	public static final JavaLocalTime javaLocalTime =new JavaLocalTime(){};
	public static final JavaLocalDateTime javaLocalDateTime =new JavaLocalDateTime(){};
	public static final JavaInstant javaInstant =new JavaInstant(){};

	public class OfClass<T> implements Type<T> {
		private final Class<T> cls;
		private final Item.Factory factory;
		public OfClass(Class<T> cls, Item.Factory factory) {
			this.cls=cls;
			this.factory=factory;
		}
		public Class<T> getJavaClass() { return this.cls; };  
		@Override public T parseString(String str) { return factory.construct(cls,str); }
		@Override public String getName() { return getJavaClass().getSimpleName(); }
		@Override public boolean isValidObject(Object obj) {
			if (obj==null)
				return false; // TODO: is this desirable behaviour?
			return getJavaClass().isAssignableFrom(obj.getClass());
		}
	}
}
