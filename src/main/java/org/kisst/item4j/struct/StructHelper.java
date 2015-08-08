package org.kisst.item4j.struct;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.kisst.item4j.Immutable;
import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.Item;
import org.kisst.item4j.seq.ItemSequence;
import org.kisst.item4j.struct.Struct.FieldHasNullValueException;
import org.kisst.item4j.struct.Struct.UnknownFieldException;

public class StructHelper {
	
	public static boolean hasField(Struct data, String path) { return getObject(data,path, Struct.UNKNOWN_FIELD)==Struct.UNKNOWN_FIELD; }

	public static Object getObject(Struct data, String path) {
		Object value=getObject(data,path,Struct.UNKNOWN_FIELD);
		if (value==Struct.UNKNOWN_FIELD)
			throw new UnknownFieldException(data,path);
		if (value==null)
			throw new FieldHasNullValueException(data,path);
		return value;
	}
	
	
	public static Object getObject(Struct data, String path, Object defaultValue) {
		String name=path;
		String remainder=null;
		int pos=path.indexOf('.');
		if (pos>0) {
			name=path.substring(0,pos);
			remainder=path.substring(pos+1);
		}
		Object value=data.getDirectFieldValue(name);
		if (value==null || value==Struct.UNKNOWN_FIELD)
			return defaultValue;
		if (remainder==null)
			return value;
		if (value instanceof Struct)
			return getObject((Struct) value,remainder);
		return getObject(new ReflectStruct(value),remainder);
	}

	

	public static String getString(Struct data, String path, String defaultValue) {
		Object obj= getObject(data,path,null); 
		if (obj==null) return defaultValue;
		return Item.asString(obj);
	}
	public static int getInteger(Struct data, String path, int defaultValue) {	
		Object obj=getObject(data,path,null);
		if (obj==null) return defaultValue;
		return Item.asInteger(obj);
	}
	public static long getLong(Struct data, String path, long defaultValue) {
		Object obj= getObject(data,path,null); 
		if (obj==null) return defaultValue;
		return Item.asLong(obj);
	}
	public static boolean getBoolean(Struct data, String path, boolean defaultValue) {
		Object obj= getObject(data,path,null); 
		if (obj==null) return defaultValue;
		return Item.asBoolean(obj);
	}
	public static LocalDate getLocalDate(Struct data, String path, LocalDate defaultValue) {
		Object obj= getObject(data,path,null); 
		if (obj==null) return defaultValue;
		return Item.asLocalDate(obj);
	}
	public static LocalTime getLocalTime(Struct data, String path, LocalTime defaultValue) {
		Object obj= getObject(data,path,null); 
		if (obj==null) return defaultValue;
		return Item.asLocalTime(obj);
	}
	public static LocalDateTime getLocalDateTime(Struct data, String path, LocalDateTime defaultValue) {
		Object obj= getObject(data,path,null); 
		if (obj==null) return defaultValue;
		return Item.asLocalDateTime(obj);
	}
	public static Instant getInstant(Struct data, String path, Instant defaultValue) {
		Object obj= getObject(data,path,null); 
		if (obj==null) return defaultValue;
		return Item.asInstant(obj);
	}
	public static<T> T getType(Struct data, Item.Factory factory, Class<?> cls, String path, T defaultValue) {
		Object obj= getObject(data,path,null); 
		if (obj==null) return defaultValue;
		return Item.asType(factory, cls, obj);
	}
	public static Struct getStruct(Struct data, String path, Struct defaultValue) {
		Object obj= getObject(data,path,null); 
		if (obj==null) return defaultValue;
		return Item.asStruct(obj);
	}
	public static ItemSequence getSequence(Struct data, String path, ItemSequence defaultValue) {
		Object obj= getObject(data,path,null); 
		if (obj==null) return defaultValue;
		return Item.asItemSequence(obj);
	}
	public static<T> ImmutableSequence<T> getTypedSequence(Struct data, Item.Factory factory, Class<?> type, String path, ImmutableSequence<T> defaultValue) { 
		Object obj= getObject(data,path,null); 
		if (obj==null) return defaultValue;
		return Item.asTypedSequence(factory, type,getObject(data,path));  
	} 
	public static<T> ImmutableSequence<T> getTypedSequenceOrEmpty(Struct data, Item.Factory factory, Class<T> type, String path) {
		return Item.cast(getTypedSequence(data,factory, type, path, ImmutableSequence.EMPTY));
	}


	public static String toShortString(Struct data) { return toString(data, 1,""); }
	public static String toString(Struct data, int levels, String indent) {
		if (levels==0)
			return "{...}";
		StringBuilder result=new StringBuilder("{");
		for (String name: data.fieldNames()) {
			Object value=data.getDirectFieldValue(name);
			result.append(name+":");
			if (value instanceof Struct) {
				if (indent==null)
					result.append(toString((Struct) value,levels-1, indent+"\t"));
				else
					result.append(toString((Struct) value,levels-1, null));
			}
			else
				result.append(""+value);
			result.append(';');
		}
		result.append('}');
		return result.toString();
	}



	public static String getString(Struct data, String path) { return Item.asString(getObject(data,path)); } 
	public static int getInteger(Struct data, String path) { return Item.asInteger(getObject(data,path)); } 
	public static long getLong(Struct data, String path) { return Item.asLong(getObject(data,path)); } 
	public static boolean getBoolean(Struct data, String path) { return Item.asBoolean(getObject(data,path)); } 
	public static LocalDate getLocalDate(Struct data, String path) { return Item.asLocalDate(getObject(data,path)); }
	public static LocalTime getLocalTime(Struct data, String path) { return Item.asLocalTime(getObject(data,path)); }
	public static LocalDateTime getLocalDateTime(Struct data, String path) { return Item.asLocalDateTime(getObject(data,path)); }
	public static Instant getInstant(Struct data, String path) { return Item.asInstant(getObject(data,path)); }
	public static<T> T getType(Struct data, Item.Factory factory, Class<?> cls, String path) { return Item.asType(factory, cls,getObject(data,path)); }
	public static Struct getStruct(Struct data, String path) { return Item.asStruct(getObject(data,path)); } 
	public static Immutable.ItemSequence getItemSequence(Struct data, String path) { return Item.asItemSequence(getObject(data,path)); } 
	public static<T> ImmutableSequence<T> getTypedSequence(Struct data, Item.Factory factory, Class<?> type, String path) { return Item.asTypedSequence(factory, type,getObject(data,path)); } 

}
