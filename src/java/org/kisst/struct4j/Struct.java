package org.kisst.struct4j;

import org.kisst.struct4j.seq.Sequence;

public interface Struct {
	//public static Object UNKNOWN_FIELD= new Object();
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
	
	public boolean hasField(String path);
	public Iterable<String> fieldNames();
	
	public Object getObject(String path, Object defaultValue);
	public String getString(String path, String defaultValue);
	public int getInt(String path, int defaultValue);
	public long getLong(String path, long defaultValue);
	public boolean getBoolean(String path, boolean defaultValue);
	public Struct getStruct(String path, Struct defaultValue);
	public Sequence getSequence(String path, Sequence defaultValue);


	public Object getObject(String path);
	public String getString(String path);
	public int getInt(String path);
	public long getLong(String path);
	public boolean getBoolean(String path);
	public Struct getStruct(String path);
	public Sequence getSequence(String path);
}
