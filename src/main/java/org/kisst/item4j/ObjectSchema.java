package org.kisst.item4j;

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;

import org.kisst.crud4j.CrudModel;
import org.kisst.item4j.Immutable.Sequence;
import org.kisst.item4j.json.JsonParser;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.AssertUtil;
import org.kisst.util.ReflectionUtil;

public class ObjectSchema<T> implements Type<T> {
	//public static Factory globalFactory=Factory.basicFactory;

	private final LinkedHashMap<String, Field> fields=new LinkedHashMap<String, Field>(); // TODO: make Immutable
	public final Class<T> cls;
	private Constructor<?> cons;
	public ObjectSchema(Class<T> cls) { 
		this.cls=cls;
		this.cons=ReflectionUtil.getConstructor(cls, new Class<?>[]{ Struct.class} );
	}
	public Field getField(String name){ return fields.get(name);}
	public Iterable<String> fieldNames() { return fields.keySet(); }

	@Override public Class<T> getJavaClass() { return cls;}
	@Override public String getName() { return cls.getSimpleName(); }
	@Override public boolean isValidObject(Object obj) { return false; } // TODO
	@Override public T parseString(String str) { return createObject(new JsonParser().parse(str)); }
	@SuppressWarnings("unchecked")
	public T createObject(Struct doc) { return (T) ReflectionUtil.createObject(cons, new Object[]{doc} );}
	
	protected void addAllFields() { addAllFields(this.getClass());	}
	@SuppressWarnings("unchecked")
	private void addAllFields(Class<?> cls) {
		if (cls==null || cls==Object.class)
			return;
		addAllFields(cls.getSuperclass());
		try {
			for (java.lang.reflect.Field f : cls.getDeclaredFields()) {
				if (Field.class.isAssignableFrom(f.getType())) {
					//System.out.println("adding "+f);
					fields.put(f.getName(), (Field) f.get(this));
				}
			}
		}
		catch (IllegalArgumentException e) { throw new RuntimeException(e); }
		catch (IllegalAccessException e) { throw new RuntimeException(e); }
	}

	public class Field implements HasName {
		public final Type<?> type;
		public final String name;
		private final boolean optional;
		private final boolean allowsNull;
		private final Object defaultValue;
		private java.lang.reflect.Field field;

		public  Builder builder(Type<?> type, String name) { return new Builder(type, name); }
		public Field(Type<?> type, String name) {
			this.type=type;
			this.name=name; 
			this.optional=false; 
			this.allowsNull=false;
			this.field=ReflectionUtil.getField(getJavaClass(), name);
			this.defaultValue=null;
			AssertUtil.assertNotNull(field, "field "+getJavaClass()+"::"+name);
		}
		public Field(Builder builder) {
			this.type=builder.type;
			this.name=builder.name; 
			this.optional=builder.optional; 
			this.allowsNull=builder.allowsNull;
			this.defaultValue=builder.defaultValue;
			this.field=ReflectionUtil.getField(getJavaClass(), getName());
			AssertUtil.assertNotNull(field, "field "+getJavaClass()+"::"+name);
		}
		public Object getObject(Struct s) { 
			Object result = s.getObject(getName(),null);
			if (result==null)
				return defaultValue;
			return result;
		}
		public String getString(Struct s) { 
			Object result = getObject(s);
			if (result==null)
				return null;
			return result.toString();
		}
		public Object getObjectValue(Object obj) { 
			try {
				System.out.println(this+" "+this.getName()+this.field);
				return field.get(obj);
			}
			catch (IllegalArgumentException e) { throw new RuntimeException(e); }
			catch (IllegalAccessException e) { throw new RuntimeException(e); }
		}
		public void setValue(HashStruct doc, Object value) { doc.put(getName(), value); } 

		public Type<?> getType() { return this.type; }
		public String getName() { return this.name; }
		public boolean isOptional() { return optional; }
		public boolean allowsNull() { return allowsNull; }
		protected class Builder {
			public Object defaultValue;
			private final Type<?> type;
			private final String name;
			private boolean optional=false;
			private boolean allowsNull=false;
			protected Builder(Type<?> type, String name) { this.type=type; this.name=name; }
			public Builder optional() { return optional(true); }
			public Builder optional(boolean value) { this.optional=value; return this; }
			public Builder allowsNull() { return allowsNull(true); }
			public Builder allowsNull(boolean value) { this.allowsNull=value; return this; }
			public Builder defaultValue(Object defaultValue) { this.defaultValue=defaultValue; return this; }
		}
	}
	public class StringField extends Field{ //implements Schema.StringField{
		public StringField(String name) {	super(Type.javaString, name); }
		public StringField(Builder builder) { super(builder); }
		@Override public String getString(Struct s) { return s.getString(getName(),null); }
	}
	public class BooleanField extends Field {//implements Schema.BooleanField {
		public BooleanField(String name) { super(Type.javaBoolean, name); }
		public BooleanField(Builder builder) { super(builder); }
		public boolean getBoolean(Struct s, boolean defaultValue) { return s.getBoolean(getName(),defaultValue); }
	}
	public class IntField extends Field {//implements Schema.IntegerField {
		public IntField(String name) { super(Type.javaInteger, name); }
		public IntField(Builder builder) { super(builder); }
		public int getInt(Struct s) { return s.getInteger(getName(),0); }
	}
	public class LongField extends Field { //implements Schema.LongField{
		public LongField(String name) { super(Type.javaLong, name); }
		public LongField(Builder builder) { super(builder); }
		public long getLong(Struct s) { return s.getLong(getName(),0); }
	}
	public class LocalDateField extends Field { // implements Schema.DateField {
		public LocalDateField(String name) { super(Type.javaLocalDate, name); }
		public LocalDateField(Builder builder) { super(builder); }
		public LocalDate getLocalDate(Struct s) { return s.getLocalDate(getName(),null); }
	}
	public class LocalTimeField extends Field { // implements Schema.DateField {
		public LocalTimeField(String name) { super(Type.javaLocalTime, name); }
		public LocalTimeField(Builder builder) { super(builder); }
		public LocalTime getLocalTime(Struct s) { return s.getLocalTime(getName(),null); }
	}
	public class LocalDateTimeField extends Field { // implements Schema.DateField {
		public LocalDateTimeField(String name) { super(Type.javaLocalDateTime, name); }
		public LocalDateTimeField(Builder builder) { super(builder); }
		public LocalDateTime getLocalDateTime(Struct s) { return s.getLocalDateTime(getName(),null); }
	}
	public class InstantField extends Field { // implements Schema.DateField {
		public InstantField(String name) { super(Type.javaLocalDate, name); }
		public InstantField(Builder builder) { super(builder); }
		public Instant getInstant(Struct s) { return s.getInstant(getName(),null); }
		public Instant getInstantOrNow(Struct s) { return s.getInstant(getName(),Instant.now()); }
	}
	public class SequenceField<RT> extends Field {
		private final Class<?> elementClass;
		@SuppressWarnings("unchecked")
		public SequenceField(Class<?> cls, String name) { 
			super(new Type.Java<RT>((Class<RT>) cls,null), name); // TODO: parser is null
			this.elementClass=cls;
		} 
		public Sequence<RT> getSequence(CrudModel model, Struct data) {
			return data.getTypedSequenceOrEmpty(model, elementClass, name);
		}
	}
}