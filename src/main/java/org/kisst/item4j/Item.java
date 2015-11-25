package org.kisst.item4j;

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kisst.item4j.seq.ItemSequence;
import org.kisst.item4j.seq.TypedSequence;
import org.kisst.item4j.struct.MapStruct;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public interface Item {
	public static <T> T cast(Object obj){ return Type.cast(obj); } 

	public Object asObject(); // { return this; }
	default public Struct asStruct() { return asStruct(asObject()); }
	default public String asString() { return asString(asObject()); }
	default public int asInteger() { return asInteger(asObject()); }
	default public long asLong() { return asLong(asObject()); }
	default public short asShort() { return asShort(asObject()); }
	default public byte asByte() { return asByte(asObject()); }
	default public float asFloat() { return asFloat(asObject()); }
	default public double asDouble() { return asDouble(asObject()); }
	default public boolean asBoolean() { return asBoolean(asObject()); }
	default public <T> T asType(Factory factory, Class<T> cls) { return asType(factory, cls,asObject()); }
	
	public static String asString(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		return obj.toString();
	}
	public static Item asItem(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Item) return (Item) obj;
		return new Wrapper(obj);
	}
	public static Struct asStruct(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Struct) return (Struct) obj;
		if (obj instanceof Map) return new MapStruct(cast(obj));
		return new ReflectStruct(obj);
	}		
	public static Integer asInteger(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Integer) return (Integer) obj;
		if (obj instanceof Number) return ((Number)obj).intValue();
		return Integer.parseInt(asString(obj).trim());
	}		
	public static Long asLong(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Long) return (Long) obj;
		if (obj instanceof Number) return ((Number)obj).longValue();
		return Long.parseLong(asString(obj).trim());
	}		
	public static Byte asByte(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Byte) return (Byte) obj;
		if (obj instanceof Number) return ((Number)obj).byteValue();
		return Byte.parseByte(asString(obj).trim());
	}		
	public static Short asShort(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Short) return (Short) obj;
		if (obj instanceof Number) return ((Number)obj).shortValue();
		return Short.parseShort(asString(obj).trim());
	}		
	public static Float asFloat(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Float) return (Float) obj;
		if (obj instanceof Number) return ((Number)obj).floatValue();
		return Float.parseFloat(asString(obj).trim());
	}		
	public static Double asDouble(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Double) return (Double) obj;
		if (obj instanceof Number) return ((Number)obj).doubleValue();
		return Double.parseDouble(asString(obj).trim());
	}				
	public static Boolean asBoolean(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Boolean) return (Boolean) obj;
		return Boolean.parseBoolean(asString(obj).trim());
	}
	public static LocalDate asLocalDate(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof LocalDate) return (LocalDate) obj;
		return LocalDate.parse(asString(obj).trim());
	}
	public static LocalTime asLocalTime(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof LocalTime) return (LocalTime) obj;
		String str = asString(obj).trim();
		if (str==null || str.trim().length()==0)
			return null;
		return LocalTime.parse(str);
	}
	public static LocalDateTime asLocalDateTime(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof LocalDateTime) return (LocalDateTime) obj;
		return LocalDateTime.parse(asString(obj).trim());
	}
	public static Instant asInstant(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Instant) return (Instant) obj;
		return Instant.parse(asString(obj).trim());
	}

	public static Immutable.ItemSequence asItemSequence(Object obj) {
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof ItemSequence) return Immutable.ItemSequence.smartCopy((ItemSequence) obj);
		if (obj instanceof Collection)   return Immutable.ItemSequence.smartCopy((Collection<?>) obj);
		throw new ClassCastException("Can not make a ItemSequence of type "+obj.getClass()+", "+obj);
	}
	@SuppressWarnings("unchecked")
	public static <T> ImmutableSequence<T> asTypedSequence(Item.Factory factory, Class<?> type, Object obj) {
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof TypedSequence) return ImmutableSequence.smartCopy(factory, type,(TypedSequence<T>) obj);
		if (obj instanceof Collection)   return ImmutableSequence.smartCopy(factory, type, (Collection<T>) obj);
		throw new ClassCastException("Can not make a ItemSequence of type "+obj.getClass()+", "+obj);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T asType(Factory factory, Class<?> cls, Object obj) {
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (cls.isAssignableFrom(obj.getClass()))
			return (T) obj;
		//System.out.println("Converting "+obj+" to "+cls);
		if (obj instanceof Struct) {
			Object result = factory.construct(cls,(Struct)obj);
			//System.out.println("result is "+result.getClass()+" "+result);
			return cast(result);
		}
		if (obj instanceof Map) {
			Struct struct=new MapStruct((Map<String,Object>)obj);
			Object result = factory.construct(cls,struct);
			//System.out.println("result is "+result.getClass()+" "+result);
			return cast(result);
		}
		if (obj instanceof String) {
			Object result = factory.construct(cls,(String) obj);
			//System.out.println("result is "+result.getClass()+" "+result);
			return cast(result);
		}
		//if (cls.isAssignableFrom(obj.getClass()))
		throw new IllegalArgumentException("Can not convert "+obj+" to "+cls);
	}
		
	public class Wrapper implements Item {
		private final Object obj;
		public Wrapper(Object obj) {
			if (obj==null)
				throw new NullPointerException("Can not wrap null pointer");
			this.obj=obj;
		}
		public Object asObject() { return obj; }
	}
	
	
	public interface Factory {
		public default <T> T cast(Object obj){ return Type.cast(obj); } 
		public <T> T construct(Class<?> cls, Struct data);
		public <T> T construct(Class<?> cls, String data);
		
		public final static BasicFactory basicFactory=new BasicFactory();
		
		public class BasicFactory implements Factory {
			public<T> T construct(Class<?> cls, Struct data){
				Constructor<?> c = ReflectionUtil.getFirstCompatibleConstructor(cls, new Class<?>[]{data.getClass()});
				if (c!=null)
					return cast(ReflectionUtil.createObject(c, new Object[] {data}));
				throw new IllegalArgumentException("Unknown Struct Constructor for class "+cls+" and data "+data);
			}
			public<T> T construct(Class<?> cls, String data){
				Constructor<?> c = ReflectionUtil.getFirstCompatibleConstructor(cls, new Class<?>[]{String.class});
				if (c!=null)
					return cast(ReflectionUtil.createObject(c, new Object[] {data}));
				throw new IllegalArgumentException("Unknown String Constructor "+cls+" and data "+data);
			}
		}

		public class MappedFactory extends BasicFactory {
			public interface ConStructor { public Object ConStruct(Struct data); }
			private final HashMap<Class<?>, ConStructor> constructors = new HashMap<Class<?>, ConStructor>(); 

			public MappedFactory addClass(Class<?> cls, ConStructor c) { constructors.put(cls, c); return this;}

			public <T> T construct(Class<?> cls, Struct data){
				ConStructor c = constructors.get(cls);
				if (c!=null)
					return cast(c.ConStruct(data));
				return super.construct(cls, data);
			}
		}
	}
	
//	public static final DummyFactory dummyFactory=new DummyFactory();
//	public static class DummyFactory implements Factory {
//		public <T> T construct(Class<?> cls, Struct data) { throw new RuntimeException("Factory should not be needed"); }
//		public <T> T construct(Class<?> cls, String data) { throw new RuntimeException("Factory should not be needed"); }
//	}
}
