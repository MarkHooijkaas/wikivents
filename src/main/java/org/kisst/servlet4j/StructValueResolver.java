package org.kisst.servlet4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.kisst.item4j.struct.Struct;

import com.github.jknack.handlebars.ValueResolver;

public class StructValueResolver implements ValueResolver {
	public static final StructValueResolver INSTANCE=new StructValueResolver();
	
	private class MyEntry<K,V> implements Entry<K,V> {
		private final K key;
		private final V value;
		private MyEntry(K key, V value) { this.key=key; this.value=value; }
		@Override public K getKey() { return this.key;}
		@Override public V getValue() { return this.value;}
		@Override public V setValue(V arg0) { throw new RuntimeException("not implemented");}		
	}

	@Override public Set<Entry<String, Object>> propertySet(final Object context) {
		if (context instanceof Struct) {
			Struct obj=(Struct) context;
			Set<Entry<String, Object>> result = new HashSet<Entry<String, Object>>();
			for (String key: obj.fieldNames())
				result.add(new MyEntry<String, Object>(key,obj.getObject(key,null)));
			return result;
		}
		return Collections.emptySet();
	}

	@Override public Object resolve(Object context) {
		if (context instanceof Struct) {
			return context;
		}
		return UNRESOLVED;
	}

	@Override public Object resolve(final Object context, final String name) {
		Object value = null;
		if (context instanceof Struct)
			value = ((Struct) context).getObject(name,null);
		return value == null ? UNRESOLVED : value;
	}
}
