package org.kisst.crud4j;

public class CrudModel {
	private final StructStorage[] storage;
	public CrudModel(StructStorage ... storage) { this.storage=storage;}
	
	public StructStorage getStorage(Class<?> cls) { return storage[0]; }
}
