package org.kisst.crud4j;

import org.kisst.item4j.seq.TypedSequence;
import org.kisst.item4j.struct.Struct;

public interface StructStorage {
	public Class<?> getRecordClass();
	public String createInStorage(Struct value);
	public Struct readFromStorage(String key);
	public void updateInStorage(Struct oldValue, Struct newValue);
	public void deleteInStorage(Struct oldValue);
	public TypedSequence<Struct> findAll();
	
	public UniqueIndex useUniqueIndex(CrudSchema<?>.Field ... fields);
	public MultiIndex  useMultiIndex(CrudSchema<?>.Field ... fields);
	
	public interface UniqueIndex { public Struct get(String ... values);}
	public interface MultiIndex { public TypedSequence<Struct> get(String...  values); }
	//public interface OrderedIndex{ public Sequence<Struct> get(String field); }

}
