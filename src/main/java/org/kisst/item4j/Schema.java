package org.kisst.item4j;

public interface Schema<T> extends Type<T> {
	public<F> Field<F> getField(String name);
	public Iterable<String> fieldNames();


	public interface StringField  {
		default public Type.JavaString getType() { return Type.javaString; }
	}
	public interface BooleanField  {
		default public Type.JavaBoolean getType() { return Type.javaBoolean; }
	}
	public interface IntegerField {
		default public Type.JavaInteger getType() { return Type.javaInteger; }
	}
	public interface LongField {
		default public Type.JavaLong getType() { return Type.javaLong; }
	}
	public interface DateField {
		default public Type.JavaDate getType() { return Type.javaDate; }
	}

	public abstract class Field<F>  {
		private final String name;
		private final boolean optional;
		private final boolean allowsNull;
		public static Builder builder(String name) { return new Builder(name); }
		public Field(String name) { 
			this.name=name; 
			this.optional=false; 
			this.allowsNull=false;
		}
		public Field(Builder builder) { 
			this.name=builder.name; 
			this.optional=builder.optional; 
			this.allowsNull=builder.allowsNull;
		}
		public String getName() { return this.name; }
		public boolean isOptional() { return optional; }
		public boolean allowsNull() { return allowsNull; }
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
}