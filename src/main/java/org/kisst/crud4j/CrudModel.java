package org.kisst.crud4j;

public class CrudModel {
	private final StructStorage[] storage;
	public CrudModel(StructStorage ... storage) { this.storage=storage;}
	
	public StructStorage getStorage(Class<?> cls) {
		for (StructStorage s: storage) {
			if (s.getRecordClass()==cls)
				return s;
		}
		String classes="";
		for (StructStorage s: storage) 
			classes+=s.getRecordClass().getSimpleName()+" ";
		throw new RuntimeException("Unknown Storage for type "+cls.getSimpleName()+" and not one of "+classes);
	}
}
