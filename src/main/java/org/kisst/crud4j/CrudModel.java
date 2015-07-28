package org.kisst.crud4j;

import org.kisst.item4j.Item;
import org.kisst.item4j.Schema;

public abstract class CrudModel implements Item.Factory {
	private final StructStorage[] storage;
	public CrudModel(StructStorage ... storage) {
		Schema.globalFactory=this;
		this.storage=storage;
	}
	
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
