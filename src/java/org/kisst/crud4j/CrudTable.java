package org.kisst.crud4j;

import java.util.concurrent.ConcurrentHashMap;

public abstract class CrudTable<T> {
	abstract public void save(String key, T value);
	abstract public T load(String key);
	
	public class Ref {
		public final String key;
		private T value;
		private Ref(String key) { this.key=key; }
		public T get() { 
			if (value==null)
				value = load(key);
			return value; }
		public String getKey() { return key; }
	}
	private final ConcurrentHashMap<String, Ref> map=new ConcurrentHashMap<String,Ref>();
	public  final CrudSchema<T> schema;
	public CrudTable(CrudSchema<T> schema) { 
		this.schema=schema;;
	}
	
	public T get(String key) { return getRef(key).get(); }
	public Ref getRef(String key) {
		CrudTable<T>.Ref result = map.get(key);
		if (result!=null)
			return result;
		synchronized(map) {
			result = map.get(key);
			if (result!=null)
				return result;
			result=new Ref(key);
			map.put(key, result);
			return result;
		}
	}
}
