package org.kisst.item4j;

import org.kisst.item4j.struct.Struct;

public interface Schema extends Type {
	public Field getField(String name);
	public Iterable<String> fieldNames();

	public interface Field {
		public String getName();
		public Type getType();
		default public boolean isOptional() { return false; }
		default public boolean allowsNull() { return false; }
		default public String getStringValue(Struct data, String defaultValue) { return data.getString(getName(),defaultValue); }
	}
	public interface StringField extends Field {
		@Override default public Type.JavaString getType() { return Type.javaString; }
	}
	public interface BooleanField extends Field {
		@Override default public Type.JavaBoolean getType() { return Type.javaBoolean; }
	}
	public interface IntegerField extends Field {
		@Override default public Type.JavaInteger getType() { return Type.javaInteger; }
	}
	public interface LongField extends Field {
		@Override default public Type.JavaLong getType() { return Type.javaLong; }
	}
	public interface DateField extends Field {
		@Override default public Type.JavaDate getType() { return Type.javaDate; }
	}

	public abstract class BaseField implements Field {
		private final String name;
		private final boolean optional;
		private final boolean allowsNull;
		public static Builder builder(String name) { return new Builder(name); }
		public BaseField(String name) { 
			this.name=name; 
			this.optional=false; 
			this.allowsNull=false;
		}
		public BaseField(Builder builder) { 
			this.name=builder.name; 
			this.optional=builder.optional; 
			this.allowsNull=builder.allowsNull;
		}
		@Override public String getName() { return this.name; }
		@Override public boolean isOptional() { return optional; }
		@Override public boolean allowsNull() { return allowsNull; }
		protected static class Builder {
			public final String name;
			private boolean optional=false;
			private boolean allowsNull=false;
			protected Builder(String name) { this.name=name; }
			public Builder optional() { return optional(true); }
			public Builder optional(boolean value) { this.optional=value; return this; }
			public Builder allowsNull() { return allowsNull(true); }
			public Builder allowsNull(boolean value) { this.allowsNull=value; return this; }
		}
	}
	public class BaseStringField extends BaseField implements StringField {
		public BaseStringField(String name) { super(name); }
		public BaseStringField(Builder builder) { super(builder); }
	}
	public class BaseBooleanField extends BaseField implements BooleanField {
		public BaseBooleanField(String name) { super(name); }
		public BaseBooleanField(Builder builder) { super(builder); }
	}
	public class BaseIntegerField extends BaseField implements IntegerField {
		public BaseIntegerField(String name) { super(name); }
		public BaseIntegerField(Builder builder) { super(builder); }
	}
	public class BaseLongField extends BaseField implements LongField {
		public BaseLongField(String name) { super(name); }
		public BaseLongField(Builder builder) { super(builder); }
	}
	public class BaseDateField extends BaseField implements DateField {
		public BaseDateField(String name) { super(name); }
		public BaseDateField(Builder builder) { super(builder); }
	}
}