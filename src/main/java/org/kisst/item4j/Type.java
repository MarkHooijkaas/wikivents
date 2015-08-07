package org.kisst.item4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface Type<T> {
	@SuppressWarnings("unchecked") public static <T> T cast(Object obj){ return (T) obj; } 

	public Class<? extends T> getJavaClass();
	//public T parseString(String str);
	public T convertFrom(Item.Factory factory, Object obj); 


	default public String getStringRepresentation(Object obj) { return ""+obj; }
	default public String getName() { return getJavaClass().getSimpleName(); }
	default public boolean isValidObject(Object obj) {
		if (obj==null)
			return false; // TODO: is this desirable behaviour?
		return getJavaClass().isAssignableFrom(obj.getClass());
	}

	public static final Java<Object> javaObject =new Java<Object>(Object.class, obj->obj);
	public static final Java<String> javaString=new Java<String>(String.class, str -> str);
	public static final Java<Boolean> javaBoolean=new Java<Boolean>(Boolean.class, Boolean::parseBoolean);
	public static final Java<Integer> javaInteger=new Java<Integer>(Integer.class, Integer::parseInt);
	public static final Java<Long> javaLong=new Java<Long>(Long.class, Long::parseLong);
	public static final Java<LocalDate> javaLocalDate=new Java<LocalDate>(LocalDate.class, LocalDate::parse);
	public static final Java<LocalTime> javaLocalTime =new Java<LocalTime>(LocalTime.class, LocalTime::parse);
	public static final Java<LocalDateTime> javaLocalDateTime =new Java<LocalDateTime>(LocalDateTime.class, LocalDateTime::parse);
	public static final Java<Instant> javaInstant =new Java<Instant>(Instant.class, Instant::parse);

	public interface Parser<T> {
		T parseString(String str);
	}
	
	public class Java<T> implements Type<T> {
		private final Class<T> cls;
		private final Parser<T> parser;
		public Java(Class<T> cls, Parser<T> parser) {	this.cls=cls; this.parser=parser;}
		@Override public Class<T> getJavaClass() { return this.cls; };  
		public T parseString(String str) { return parser.parseString(str); }
		@SuppressWarnings("unchecked")
		@Override
		public T convertFrom(Item.Factory factory, Object obj) {
			if (obj==null)
				return null;
			if (cls.isAssignableFrom(obj.getClass()))
				return (T) obj;
			return parseString(obj.toString());
		}
	}


	
	/*
	public class FactoryClass<T> implements Type<T> {
		private final Class<T> cls;
		private final Item.Factory factory;
		public FactoryClass(final Class<T> cls, final Item.Factory factory) {
			this.cls=cls;
			this.factory=factory;
		}
		@Override public Class<T> getJavaClass() { return this.cls; };  
		@Override public T parseString(String str) { return factory.construct(cls,str); }
	}
	*/
}
