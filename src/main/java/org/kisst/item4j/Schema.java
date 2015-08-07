package org.kisst.item4j;

public interface Schema {
	
	//public Field getField(String name);
	public Iterable<String> fieldNames();
	public interface Field<FT> extends HasName {
		@Override public String getName();
		public Class<FT> getJavaClass();
	}
}