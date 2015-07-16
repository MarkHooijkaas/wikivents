package org.kisst.item4j.struct;

import org.kisst.item4j.Item;
import org.kisst.item4j.seq.ItemSequence;

public interface Struct {
	public Iterable<String> fieldNames();
	public Object getObject(String path, Object defaultValue);

	

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
	default public ItemSequence<Item> getSequence(String path, ItemSequence<Item> defaultValue) {
		Object obj= getObject(path,null); 
		if (obj==null) return defaultValue;
		return Item.asItemSequence(obj);
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
	default public Struct getStruct(String path) { return Item.asStruct(getObject(path)); } 
	default public ItemSequence<Item> getItemSequence(String path) { return Item.asItemSequence(getObject(path)); } 

	public static final Object UNKNOWN_FIELD=new Object();
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
