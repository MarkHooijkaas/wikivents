package org.kisst.crud4j.index;

import java.util.concurrent.ConcurrentHashMap;

import org.kisst.crud4j.CrudModel.UniqueIndex;
import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.item4j.Immutable;
import org.kisst.item4j.Immutable.Sequence;
import org.kisst.item4j.ObjectSchema;

public class MemoryUniqueIndex<T extends CrudObject> extends AbstractKeyedIndex<T>  implements UniqueIndex<T> {
	private final FieldList<T> fields;
	private final ConcurrentHashMap<String, T> map = new ConcurrentHashMap<String, T>();
	
	@SafeVarargs
	public MemoryUniqueIndex(CrudSchema<T> schema, ObjectSchema<T>.Field ... fields) { 
		super(schema);
		this.fields=new FieldList<T>(fields);
	}
	@Override protected void add(String key, T record) { map.put(key, record); }
	@Override protected void remove(String key) { map.remove(key); }
	@Override public String calcUniqueKey(T record) { return fields.getKey(record); }
	@Override public boolean keyExists(String key) { return map.containsKey(key); }

	public Sequence<T> getAll() { 
		return Immutable.Sequence.smartCopy(null/*schema.model*/, schema.getJavaClass(), map.values());
	}


	@Override public T get(String ... values) { return map.get(fields.getKey(values)); }

	@Override public ObjectSchema<T>.Field[] fields() { return fields.fields(); }

}
