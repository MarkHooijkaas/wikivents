package org.kisst.struct4j;


public interface Schema extends Type {
	public Field getField(String name);
	public Iterable<String> fieldNames();

	public interface Field {
		public String getName();
		public Type getType();
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
		public BaseField(String name) { this.name=name; }
		@Override public String getName() { return this.name; }
	}
	public class BaseStringField extends BaseField implements StringField {
		public BaseStringField(String name) { super(name); }
	}
	public class BaseBooleanField extends BaseField implements BooleanField {
		public BaseBooleanField(String name) { super(name); }
	}
	public class BaseIntegerField extends BaseField implements IntegerField {
		public BaseIntegerField(String name) { super(name); }
	}
	public class BaseLongField extends BaseField implements LongField {
		public BaseLongField(String name) { super(name); }
	}
	public class BaseDateField extends BaseField implements DateField {
		public BaseDateField(String name) { super(name); }
	}
}