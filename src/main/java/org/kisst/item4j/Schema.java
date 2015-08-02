package org.kisst.item4j;

public interface Schema {
	
	public Field getField(String name);
	public Iterable<String> fieldNames();
	public interface Field extends HasName {
		@Override public String getName();
		public Class<?> getJavaClass();
	}
}