package org.kisst.struct4j;

public interface Item {
	default public Object asObject() { return this; }
	default public Struct asStruct() { return asStruct(asObject()); }
	default public String asString() { return asString(asObject()); }
	default public int asInteger() { return asInteger(asObject()); }
	default public long asLong() { return asLong(asObject()); }
	default public short asShort() { return asShort(asObject()); }
	default public byte asByte() { return asByte(asObject()); }
	default public float asFloat() { return asFloat(asObject()); }
	default public double asDouble() { return asDouble(asObject()); }
	default public boolean asBoolean() { return asBoolean(asObject()); }
	
	
	public class Wrapper implements Item {
		private final Object obj;
		public Wrapper(Object obj) {
			if (obj==null)
				throw new NullPointerException("Can not wrap null pointer");
			this.obj=obj;
		}
		public Object asObject() { return obj; }
	}

	public static String asString(Object obj) { 
		if (obj==null) return null; 
		return obj.toString();
	}
	public static Item asItem(Object obj) { 
		if (obj==null) return null; 
		if (obj instanceof Item)
			return (Item) obj;
		return new Wrapper(obj);
	}
	public static Struct asStruct(Object obj) { 
		if (obj==null) return null; 
		if (obj instanceof Struct)
			return (Struct) obj;
		return new ReflectStruct(obj);
	}		
	public static Integer asInteger(Object obj) { 
		if (obj==null) return null; 
		if (obj instanceof Integer)
			return (Integer) obj;
		if (obj instanceof Number)
			return ((Number)obj).intValue();
		return Integer.parseInt(asString(obj));
	}		
	public static Long asLong(Object obj) { 
		if (obj==null) return null; 
		if (obj instanceof Long)
			return (Long) obj;
		if (obj instanceof Number)
			return ((Number)obj).longValue();
		return Long.parseLong(asString(obj));
	}		
	public static Byte asByte(Object obj) { 
		if (obj==null) return null; 
		if (obj instanceof Byte)
			return (Byte) obj;
		if (obj instanceof Number)
			return ((Number)obj).byteValue();
		return Byte.parseByte(asString(obj));
	}		
	public static Short asShort(Object obj) { 
		if (obj==null) return null; 
		if (obj instanceof Short)
			return (Short) obj;
		if (obj instanceof Number)
			return ((Number)obj).shortValue();
		return Short.parseShort(asString(obj));
	}		
	public static Float asFloat(Object obj) { 
		if (obj==null) return null; 
		if (obj instanceof Float)
			return (Float) obj;
		if (obj instanceof Number)
			return ((Number)obj).floatValue();
		return Float.parseFloat(asString(obj));
	}		
	public static Double asDouble(Object obj) { 
		if (obj==null) return null; 
		if (obj instanceof Double)
			return (Double) obj;
		if (obj instanceof Number)
			return ((Number)obj).doubleValue();
		return Double.parseDouble(asString(obj));
	}				
	public static Boolean asBoolean(Object obj) { 
		if (obj==null) return null; 
		if (obj instanceof Boolean)
			return (Boolean) obj;
		return Boolean.parseBoolean(asString(obj));
	}				
}
