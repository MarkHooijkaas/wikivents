package org.kisst.item4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.kisst.item4j.Immutable.Sequence;
import org.kisst.item4j.struct.Struct;

public abstract class SchemaBase implements Schema {
	
	public class StringField extends BasicField<String>{ //implements Schema.StringField{
		public StringField(String name) {	super(Type.javaString, name); }
		public String getString(Struct s) { return s.getString(getName(),null); }
	}
	public class BooleanField extends BasicField<Boolean> {//implements Schema.BooleanField {
		public BooleanField(String name) { super(Type.javaBoolean, name); }
		public boolean getBoolean(Struct s, boolean defaultValue) { return s.getBoolean(getName(),defaultValue); }
	}
	public class IntField extends BasicField<Integer> {//implements Schema.IntegerField {
		public IntField(String name) { super(Type.javaInteger, name); }
		public int getInt(Struct s) { return s.getInteger(getName(),0); }
	}
	public class LongField extends BasicField<Long> { //implements Schema.LongField{
		public LongField(String name) { super(Type.javaLong, name); }
		public long getLong(Struct s) { return s.getLong(getName(),0); }
	}
	public class LocalDateField extends BasicField<LocalDate> { // implements Schema.DateField {
		public LocalDateField(String name) { super(Type.javaLocalDate, name); }
		public LocalDate getLocalDate(Struct s) { return s.getLocalDate(getName(),null); }
	}
	public class LocalTimeField extends BasicField<LocalTime> { // implements Schema.DateField {
		public LocalTimeField(String name) { super(Type.javaLocalTime, name); }
		public LocalTime getLocalTime(Struct s) { return s.getLocalTime(getName(),null); }
	}
	public class LocalDateTimeField extends BasicField<LocalDateTime> { // implements Schema.DateField {
		public LocalDateTimeField(String name) { super(Type.javaLocalDateTime, name); }
		public LocalDateTime getLocalDateTime(Struct s) { return s.getLocalDateTime(getName(),null); }
	}
	public class InstantField extends BasicField<Instant> { // implements Schema.DateField {
		public InstantField(String name) { super(Type.javaInstant, name); }
		public Instant getInstant(Struct s) { return s.getInstant(getName(),null); }
		public Instant getInstantOrNow(Struct s) { return s.getInstant(getName(),Instant.now()); }
	}
	@SuppressWarnings("rawtypes")
	public class SequenceField<RT> extends BasicField<Sequence> {
		private final Type<RT> elementType;
		public SequenceField(Type<RT> type, String name) { 
			super(new SequenceType<RT>(type) , name); // TODO: parser is null
			this.elementType=type;
		} 
		public Sequence<RT> getSequence(Item.Factory factory, Struct data) {
			return data.getTypedSequenceOrEmpty(factory, elementType.getJavaClass(), name);
		}
	}
}