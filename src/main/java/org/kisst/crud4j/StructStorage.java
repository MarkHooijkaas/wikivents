package org.kisst.crud4j;

import org.kisst.item4j.Schema;
import org.kisst.item4j.seq.Sequence;
import org.kisst.item4j.struct.Struct;

public interface StructStorage {
	public String createInStorage(Struct value);
	public Struct readFromStorage(String key);
	public void updateInStorage(Struct oldValue, Struct newValue);
	public void deleteInStorage(Struct oldValue);
	public Sequence<Struct> findAll();
	
	public UniqueIndex useUniqueIndex(Schema.Field ... fields);
	public MultiIndex  useMultiIndex(Schema.Field ... fields);
	
	public interface UniqueIndex { public Struct get(String ... values);}
	public interface MultiIndex { public Sequence<Struct> get(String...  values); }
	//public interface OrderedIndex{ public Sequence<Struct> get(String field); }

}