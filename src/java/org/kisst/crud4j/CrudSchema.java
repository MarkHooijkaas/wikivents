package org.kisst.crud4j;

import java.util.LinkedHashMap;

import org.kisst.struct4j.HashStruct;
import org.kisst.struct4j.Struct;
import org.kisst.util.ReflectionUtil;

public class CrudSchema<T extends CrudObject> {
	private final LinkedHashMap<String, Field<T> > fields=new LinkedHashMap<String, Field<T>>();
	public final Class<?> cls;
	public final IdField _id = new IdField("_id");
	
	public CrudSchema(Class<?> cls) { 
		this.cls=cls;
	}
	@SuppressWarnings("unchecked")
	protected void addAllFields() {
		try {
			for (java.lang.reflect.Field f : this.getClass().getFields()) {
				if (Field.class.isAssignableFrom(f.getType())) {
					fields.put(f.getName(), (CrudSchema<T>.Field<T>) f.get(this));
					System.out.println("Added"+cls.getSimpleName()+"::"+f.getName());
				}
			}
		}
		catch (IllegalArgumentException e) { throw new RuntimeException(e); }
		catch (IllegalAccessException e) { throw new RuntimeException(e); }
	}
	@Override public String toString() { return cls.getSimpleName(); }
	public final IdField getKeyField() { return _id; }
	public Field<T> getField(String name) {return fields.get(name); }
	public Iterable<String> fieldNames() { return fields.keySet(); }

	public class Field<FT> {
		public final String name;
		public final boolean optional;
		private final FT defaultValue;
		private java.lang.reflect.Field field;
		public Field(Class<T> cls, String name) { this(cls, name, false, null);}
		public Field(Class<?> cls, String name, boolean optional, FT defaultValue) {
			this.name=name;
			this.optional=optional;
			this.defaultValue=defaultValue;
			this.field=ReflectionUtil.getField(cls, name);
		}
		public Field(String name) {
			this.name=name;
			this.optional=false;
			this.defaultValue=null;
			this.field=ReflectionUtil.getField(CrudObject.class, name);
		}
		@SuppressWarnings("unchecked")
		public FT getObjectValue(CrudObject obj) { 
			try {
				return (FT) field.get(obj);
			}
			catch (IllegalArgumentException e) { throw new RuntimeException(e); }
			catch (IllegalAccessException e) { throw new RuntimeException(e); }
		}
		
		@SuppressWarnings("unchecked")
		public FT getValue(Struct s) { 
			if (optional)
				return (FT) s.getObject(name,defaultValue);
			else
				return (FT) s.getObject(name);
		}
		
		public void setValue(HashStruct doc, FT value) { doc.put(name, value); } 
	}
	public class StringField extends Field<String> {
		public StringField(Class<T> cls, String name, boolean optional, String defaultValue) {
			super(cls, name, optional, defaultValue);
		}
		public StringField(Class<T> cls, String name) { super(cls,name); }
		public String getString(Struct s) { return getValue(s); }
	}
	private class IdField extends Field<String> {
		public IdField(String name) { super(name); }
	}
	public class IntField extends Field<Integer> {
		public IntField(Class<T> cls, String name, boolean optional, int defaultValue) {
			super(cls, name, optional, defaultValue);
		}
		public int getInt(Struct s) { return getValue(s); }
	}
	public class LongField extends Field<Long> {
		public LongField(Class<T> cls, String name, boolean optional, long defaultValue) {
			super(cls, name, optional, defaultValue);
		}
		public long getLong(Struct s) { return getValue(s); }
	}
	public class RefField<RT extends CrudObject> extends Field<RT> {
		public RefField(Class<T> cls, String name, boolean optional) {
			super(cls, name, optional, null);
		}
		public CrudTable<RT>.Ref get(CrudTable<RT> table, Struct s) { 
			return table.getRef(s.getString(name));
		}
	}
}
