package org.kisst.crud4j;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.LinkedHashMap;

import org.kisst.item4j.Schema;
import org.kisst.item4j.json.JsonParser;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public class CrudSchema<T extends CrudObject> implements Schema<T> {
	public static CrudSchema<CrudObject> schema=new CrudSchema<CrudObject>(CrudObject.class);
	
	private final LinkedHashMap<String, Field<?> > fields=new LinkedHashMap<String, Field<?>>();
	public final Class<T> cls;
	public final IdField _id = new IdField("_id");
	private final Constructor<?> cons;
	
	public CrudSchema(Class<T> cls) { 
		this.cls=cls;
		this.cons=ReflectionUtil.getConstructor(cls, new Class<?>[]{ Struct.class} );
	}
	@Override public Class<T> getJavaClass() { return cls;}
	@Override public String getName() { return cls.getSimpleName(); }
	@Override public boolean isValidObject(Object obj) { return false; } // TODO
	@Override public T parseString(String str) { return createObject(new JsonParser().parse(str)); }
	@SuppressWarnings("unchecked")
	public T createObject(Struct doc) { return (T) ReflectionUtil.createObject(cons, new Object[]{ doc} );}

	
	
	
	protected void addAllFields() { addAllFields(this.getClass());	}
	@SuppressWarnings("unchecked")
	private void addAllFields(Class<?> cls) {
		if (cls==null || cls==Object.class)
			return;
		addAllFields(cls.getSuperclass());
		try {
			for (java.lang.reflect.Field f : cls.getDeclaredFields()) {
				if (Field.class.isAssignableFrom(f.getType())) {
					fields.put(f.getName(), (CrudSchema.Field<T>) f.get(this));
				}
			}
		}
		catch (IllegalArgumentException e) { throw new RuntimeException(e); }
		catch (IllegalAccessException e) { throw new RuntimeException(e); }
	}
	@Override public String toString() { return cls.getSimpleName(); }
	public final IdField getKeyField() { return _id; }
	@SuppressWarnings("unchecked")
	public<F> Field<F> getField(String name) {return (Field<F>) fields.get(name); }
	public Iterable<String> fieldNames() { return fields.keySet(); }

	public static abstract class Field<FT> extends Schema.Field<FT> {
		public class Builder extends Schema.Field.Builder {
			private final Class<?> cls;
			private FT defaultValue=null;
			protected Builder(Class<?> cls, String name) {
				super(name);
				this.cls=cls;
			}
			public Builder defaultValue(FT defaultValue) { this.defaultValue=defaultValue; return this; }
		}
		private final FT defaultValue;
		private java.lang.reflect.Field field;
		public Field(Class<?> cls, String name) { 
			super(name); 
			this.field=ReflectionUtil.getField(cls, name);
			this.defaultValue=null;
		}
		public Field(Builder builder) {
			super(builder);
			this.defaultValue=builder.defaultValue;
			this.field=ReflectionUtil.getField(builder.cls, builder.name);
		}
		public Field(String name) {
			super(name);
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
			if (isOptional())
				return (FT) s.getObject(getName(),defaultValue);
			else
				return (FT) s.getObject(getName());
		}
		
		public void setValue(HashStruct doc, FT value) { doc.put(getName(), value); } 
	}
	public class StringField extends Field<String> implements Schema.StringField{
		public StringField(Class<T> cls, String name) {	super(cls, name); }
		public StringField(Builder builder) { super(builder); }
		@Override public String getString(Struct s) { return getValue(s); }
	}
	public class IdField extends Field<String> {
		public IdField(String name) { super(name); }
		//@Override public Type.JavaString getType() { return Type.javaString; }
	}
	public class BooleanField extends Field<Boolean> implements Schema.BooleanField {
		public BooleanField(Class<T> cls, String name) { super(cls, name); }
		public BooleanField(Builder builder) { super(builder); }
		public boolean getBoolean(Struct s) { return getValue(s); }
	}
	public class IntField extends Field<Integer> implements Schema.IntegerField {
		public IntField(Class<T> cls, String name) { super(cls, name); }
		public IntField(Builder builder) { super(builder); }
		public int getInt(Struct s) { return getValue(s); }
	}
	public class LongField extends Field<Long> implements Schema.LongField{
		public LongField(Class<T> cls, String name) { super(cls, name); }
		public LongField(Builder builder) { super(builder); }
		public long getLong(Struct s) { return getValue(s); }
	}
	public class DateField extends Field<Date> implements Schema.DateField {
		public DateField(Class<T> cls, String name) { super(cls, name); }
		public DateField(Builder builder) { super(builder); }
		@Override public String getString(Struct s) { return getValue(s).toString(); }
		public Date getDate(Struct s) { return getValue(s); }
	}
	/*
	public class RefField<RT extends CrudObject> extends Field<RT> {
		public RefField(Class<T> cls, String name) { super(cls, name); }
		public RefField(Builder builder) { super(builder); }
		@Override public Type.JavaString getType() { return Type.javaString; }
		public CrudTable.Ref<RT> get(CrudTable<RT> table, Struct s) { 
			return table.getRef(s.getString(getName()));
		}
	}
	*/
}
