package org.kisst.item4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.struct.Struct;

public abstract class SchemaBase implements Schema {
	
	public class StringField extends BasicField<String>{ 
		public StringField(String name) {	super(Type.javaString, name); }
		public String getString(Struct s) { return Item.asString(s.getDirectFieldValue(name)); }
		public String getString(Struct s, String defaultValue) { return Item.asString(getObject(s,defaultValue)); }
	}
	public class BooleanField extends BasicField<Boolean> {
		public BooleanField(String name) { super(Type.javaBoolean, name); }
		//public boolean getBoolean(Struct s, boolean defaultValue) { return Item.asBoolean(s.getDirectFieldValue(name)); }
		public boolean getBoolean(Struct s) { return Item.asBoolean(s.getDirectFieldValue(name)); }
		public boolean getBoolean(Struct s, boolean defaultValue) { return Item.asBoolean(getObject(s,defaultValue)); }
	}
	public class IntField extends BasicField<Integer> {
		public IntField(String name) { super(Type.javaInteger, name); }
		public int getInt(Struct s) { return  Item.asInteger(s.getDirectFieldValue(name)); }
		public int getInt(Struct s, int defaultValue) { return  Item.asInteger(getObject(s, defaultValue)); }
	}
	public class LongField extends BasicField<Long> { 
		public LongField(String name) { super(Type.javaLong, name); }
		public long getLong(Struct s) { return  Item.asLong(s.getDirectFieldValue(name)); }
		public long getLong(Struct s, long defaultValue) { return  Item.asLong(getObject(s, defaultValue)); }
	}
	public class LocalDateField extends BasicField<LocalDate> { 
		public LocalDateField(String name) { super(Type.javaLocalDate, name); }
		public LocalDate getLocalDate(Struct s) { return  Item.asLocalDate(s.getDirectFieldValue(name,null)); }
	}
	public class LocalTimeField extends BasicField<LocalTime> { 
		public LocalTimeField(String name) { super(Type.javaLocalTime, name); }
		public LocalTime getLocalTime(Struct s) { return  Item.asLocalTime(s.getDirectFieldValue(name,null)); }
	}
	public class LocalDateTimeField extends BasicField<LocalDateTime> { 
		public LocalDateTimeField(String name) { super(Type.javaLocalDateTime, name); }
		public LocalDateTime getLocalDateTime(Struct s) { return  Item.asLocalDateTime(s.getDirectFieldValue(name,null)); }
	}
	public class InstantField extends BasicField<Instant> { 
		public InstantField(String name) { super(Type.javaInstant, name); }
		public Instant getInstant(Struct s) { return  Item.asInstant(s.getDirectFieldValue(name,null)); }
		public Instant getInstantOrNow(Struct s) { 
			Object obj = s.getDirectFieldValue(name);
			if (obj==null || obj==Struct.UNKNOWN_FIELD)
				return Instant.now();
			return Item.asInstant(obj);
		}
		
	}
	@SuppressWarnings("rawtypes")
	public class SequenceField<RT> extends BasicField<ImmutableSequence> {
		private final Type<RT> elementType;
		public SequenceField(Type<RT> type, String name) { 
			super(new SequenceType<RT>(type) , name); // TODO: parser is null
			this.elementType=type;
		} 
		@SuppressWarnings("unchecked")
		public ImmutableSequence<RT> getSequence(Item.Factory factory, Struct data) {
			return (ImmutableSequence<RT>) Item.asTypedSequence(factory, elementType.getJavaClass(), data.getDirectFieldValue(name));
		}
		@SuppressWarnings("unchecked")
		public ImmutableSequence<RT> getSequenceOrEmpty(Item.Factory factory, Struct data) {
			ImmutableSequence<RT> result = (ImmutableSequence<RT>) Item.asTypedSequence(factory, elementType.getJavaClass(), data.getDirectFieldValue(name,null));
			if (result==null)
				return Item.cast(ImmutableSequence.EMPTY);
			return result;
		}
	}
}