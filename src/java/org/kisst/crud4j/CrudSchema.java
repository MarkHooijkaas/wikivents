package org.kisst.crud4j;

import java.util.Date;
import java.util.LinkedHashMap;

import org.kisst.struct4j.HashStruct;
import org.kisst.struct4j.Schema;
import org.kisst.struct4j.Struct;
import org.kisst.struct4j.Type;
import org.kisst.util.ReflectionUtil;

public class CrudSchema<T extends CrudObject> implements Schema {
	public static CrudSchema<CrudObject> schema=new CrudSchema<CrudObject>(CrudObject.class);
	
	private final LinkedHashMap<String, Field<T> > fields=new LinkedHashMap<String, Field<T>>();
	public final Class<?> cls;
	public final IdField _id = new IdField("_id");
	
	public CrudSchema(Class<?> cls) { this.cls=cls;	}

	@Override public String getName() { return cls.getSimpleName(); }
	@Override public boolean isValidObject(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override public Object parseString(String str) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	protected void addAllFields() { addAllFields(this.getClass());	}
	@SuppressWarnings("unchecked")
	private void addAllFields(Class<?> cls) {
		if (cls==null || cls==Object.class)
			return;
		addAllFields(cls.getSuperclass());
		try {
			for (java.lang.reflect.Field f : cls.getDeclaredFields()) {
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

	public abstract class Field<FT> extends Schema.BaseField {
		public final boolean optional;
		private final FT defaultValue;
		private java.lang.reflect.Field field;
		public Field(Class<T> cls, String name) { this(cls, name, false, null);}
		public Field(Class<?> cls, String name, boolean optional, FT defaultValue) {
			super(name);
			this.optional=optional;
			this.defaultValue=defaultValue;
			this.field=ReflectionUtil.getField(cls, name);
		}
		public Field(String name) {
			super(name);
			this.optional=false;
			this.defaultValue=null;
			this.field=ReflectionUtil.getField(CrudObject.class, name);
		}
		public String getString(Struct s) { 
			FT result = getValue(s);
			if (result==null)
				return null;
			return result.toString();
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
				return (FT) s.getObject(getName(),defaultValue);
			else
				return (FT) s.getObject(getName());
		}
		
		public void setValue(HashStruct doc, FT value) { doc.put(getName(), value); } 
	}
	public class StringField extends Field<String> implements Schema.StringField{
		public StringField(Class<T> cls, String name, boolean optional, String defaultValue) {
			super(cls, name, optional, defaultValue);
		}
		public StringField(Class<T> cls, String name) { super(cls,name); }
		@Override public String getString(Struct s) { return getValue(s); }
	}
	public class IdField extends Field<String> {
		public IdField(String name) { super(name); }
		@Override public Type.JavaString getType() { return Type.javaString; }
	}
	public class BooleanField extends Field<Boolean> implements Schema.BooleanField {
		public BooleanField(Class<T> cls, String name, boolean optional, boolean defaultValue) {
			super(cls, name, optional, defaultValue);
		}
		public boolean getBoolean(Struct s) { return getValue(s); }
	}
	public class IntField extends Field<Integer> implements Schema.IntegerField {
		public IntField(Class<T> cls, String name, boolean optional, int defaultValue) {
			super(cls, name, optional, defaultValue);
		}
		public int getInt(Struct s) { return getValue(s); }
	}
	public class LongField extends Field<Long> implements Schema.LongField{
		public LongField(Class<T> cls, String name, boolean optional, long defaultValue) {
			super(cls, name, optional, defaultValue);
		}
		public long getLong(Struct s) { return getValue(s); }
	}
	public class DateField extends Field<Date> implements Schema.DateField {
		public DateField(Class<T> cls, String name, boolean optional) {
			super(cls, name, optional, null);
		}
		@Override public String getString(Struct s) { return getValue(s).toString(); }
		public Date getDate(Struct s) { return getValue(s); }
	}
	public class RefField<RT extends CrudObject> extends Field<RT> {
		public RefField(Class<T> cls, String name, boolean optional) {
			super(cls, name, optional, null);
		}
		@Override public Type.JavaString getType() { return Type.javaString; }
		public CrudTable.Ref<RT> get(CrudTable<RT> table, Struct s) { 
			return table.getRef(s.getString(getName()));
		}
	}
}
