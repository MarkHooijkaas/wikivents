package org.kisst.pko4j.index;

import java.util.concurrent.ConcurrentHashMap;

import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.Schema;
import org.kisst.pko4j.PkoObject;
import org.kisst.pko4j.PkoModel.UniqueIndex;

public class MemoryUniqueIndex<T extends PkoObject> extends AbstractKeyedIndex<T>  implements UniqueIndex<T> {
	private final FieldList fields;
	private final boolean ignoreCase;
	private final ConcurrentHashMap<String, T> map = new ConcurrentHashMap<String, T>();
	
	@SafeVarargs
	public MemoryUniqueIndex(Schema schema, boolean ignoreCase, Schema.Field<?> ... fields) { 
		super(schema);
		this.ignoreCase=ignoreCase;
		this.fields=new FieldList(fields);
	}
	
	private String changeCase(String key) { return ignoreCase ? key.toLowerCase() :  key; }
	@Override public String calcUniqueKey(T record) { return changeCase(fields.getKey(record)); }
	
	@Override protected void add(String key, T record) { map.put(changeCase(key), record); }
	@Override protected void remove(String key) { map.remove(changeCase(key)); }
	@Override public boolean keyExists(String key) { return map.containsKey(changeCase(key)); }

	public ImmutableSequence<T> getAll() { 
		return ImmutableSequence.smartCopy(null/*schema.model*/, schema.getJavaClass(), map.values());
	}


	@Override public T get(String ... values) { return map.get(changeCase(fields.getKey(values))); }

	@Override public Schema.Field<?>[] fields() { return fields.fields(); }

}
