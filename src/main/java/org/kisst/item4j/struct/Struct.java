package org.kisst.item4j.struct;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.kisst.item4j.Immutable;
import org.kisst.item4j.Item;
import org.kisst.item4j.seq.ItemSequence;
import org.kisst.util.ReflectionUtil;

public interface Struct {
	public Iterable<String> fieldNames();
	abstract public Object getDirectFieldValue(String name);

	
	default public Object getObject(String path, Object defaultValue) {
		String name=path;
		String remainder=null;
		int pos=path.indexOf('.');
		if (pos>0) {
			name=path.substring(0,pos);
			remainder=path.substring(pos+1);
		}
		Object value=getDirectFieldValue(name);
		if (value==null || value==UNKNOWN_FIELD)
			return defaultValue;
		if (remainder==null)
			return value;
		if (value instanceof Struct)
			return ((Struct) value).getObject(remainder);
		return new ReflectStruct(value).getObject(remainder);
	}
	

	default public boolean hasField(String path) { return getObject(path, UNKNOWN_FIELD)==UNKNOWN_FIELD; }

	default public String getString(String path, String defaultValue) {
		Object obj= getObject(path,null); 
		if (obj==null) return defaultValue;
		return Item.asString(obj);
	}
	default public int getInteger(String path, int defaultValue) {	
		Object obj=getObject(path,null);
		if (obj==null) return defaultValue;
		return Item.asInteger(obj);
	}
	default public long getLong(String path, long defaultValue) {
		Object obj= getObject(path,null); 
		if (obj==null) return defaultValue;
		return Item.asLong(obj);
	}
	default public boolean getBoolean(String path, boolean defaultValue) {
		Object obj= getObject(path,null); 
		if (obj==null) return defaultValue;
		return Item.asBoolean(obj);
	}
	default public Struct getStruct(String path, Struct defaultValue) {
		Object obj= getObject(path,null); 
		if (obj==null) return defaultValue;
		return Item.asStruct(obj);
	}
	default public ItemSequence getSequence(String path, ItemSequence defaultValue) {
		Object obj= getObject(path,null); 
		if (obj==null) return defaultValue;
		return Item.asItemSequence(obj);
	}
	default public<T> Immutable.Sequence<T> getTypedSequence(Class<?> type, String path, Immutable.Sequence<T> defaultValue) { 
		Object obj= getObject(path,null); 
		if (obj==null) return defaultValue;
		return Item.asTypedSequence(type,getObject(path)); 
	} 


	default public String toShortString() { return toString(1,""); }
	default public String toString(int levels, String indent) {
		if (levels==0)
			return "{...}";
		StringBuilder result=new StringBuilder("{");
		for (String name: fieldNames()) {
			Object value=this.getDirectFieldValue(name);
			result.append(name+":");
			if (value instanceof Struct) {
				if (indent==null)
					result.append(((Struct)value).toString(levels-1, indent+"\t"));
				else
					result.append(((Struct)value).toString(levels-1, null));
			}
			else
				result.append(""+value);
			result.append(';');
		}
		result.append('}');
		return result.toString();
	}


	default public Object getObject(String path) {
		Object value=getObject(path,UNKNOWN_FIELD);
		if (value==UNKNOWN_FIELD)
			throw new UnknownFieldException(this,path);
		if (value==null)
			throw new FieldHasNullValueException(this,path);
		return value;
	}
	default public String getString(String path) { return Item.asString(getObject(path)); } 
	default public int getInt(String path) { return Item.asInteger(getObject(path)); } 
	default public long getLong(String path) { return Item.asLong(getObject(path)); } 
	default public boolean getBoolean(String path) { return Item.asBoolean(getObject(path)); } 
	default public LocalDate getLocalDate(String path) { return Item.asLocalDate(getObject(path)); }
	default public LocalTime getLocalTime(String path) { return Item.asLocalTime(getObject(path)); }
	default public LocalDateTime getLocalDateTime(String path) { return Item.asLocalDateTime(getObject(path)); }
	default public Instant getInstant(String path) { return Item.asInstant(getObject(path)); }
	default public Struct getStruct(String path) { return Item.asStruct(getObject(path)); } 
	default public Immutable.ItemSequence getItemSequence(String path) { return Item.asItemSequence(getObject(path)); } 
	default public<T> Immutable.Sequence<T> getTypedSequence(Class<?> type, String path) { return Item.asTypedSequence(type,getObject(path)); } 

	public static final Object UNKNOWN_FIELD=ReflectionUtil.UNKNOWN_FIELD;
	public class UnknownFieldException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public final Struct struct;
		public final String fieldName;
		protected UnknownFieldException(Struct struct, String fieldName) { this(struct,fieldName,null);}
		protected UnknownFieldException(Struct struct, String fieldName, Throwable e) {
			super(struct+" does not have field "+fieldName,e); 
			this.struct=struct;
			this.fieldName=fieldName;
		}
	}
	public class FieldHasNullValueException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public final Struct struct;
		public final String fieldName;
		protected FieldHasNullValueException(Struct struct, String fieldName) { this(struct,fieldName,null);}
		protected FieldHasNullValueException(Struct struct, String fieldName, Throwable e) {
			super(struct+" has null value for field "+fieldName,e); 
			this.struct=struct;
			this.fieldName=fieldName;
		}
	}
}
