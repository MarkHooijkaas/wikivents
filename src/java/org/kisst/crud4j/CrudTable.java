package org.kisst.crud4j;

import java.util.concurrent.ConcurrentHashMap;

import org.kisst.struct4j.Struct;

public abstract class CrudTable<T extends CrudObject> {
	private final ConcurrentHashMap<String, Ref> map=new ConcurrentHashMap<String,Ref>();
	public  final CrudSchema<T> schema;
	private final  Storage<T> storage;
	public CrudTable(Storage<T> storage) { 
		this.schema=storage.getSchema();
		this.storage=storage;
	}
	abstract protected T createObject(Struct  props);
	public Ref create(T doc) {
		storage.createStorage(doc);
		Ref ref=new Ref(doc);
		map.put(doc._id, ref);
		return ref;
	}
	public Ref read(String key) { return getRef(key); }
	public void update(Ref r, T newValue) { storage.updateStorage(r.value, newValue); r.value=newValue; }
	public void delete(Ref r) {  /* TODO*/}
	
	public class Ref {
		public final String key;
		private T value;
		private Ref(String key) { this.key=key; }
		private Ref(T value) { this.key=value._id; this.value=value;}
		public T get() { 
			if (value==null)
				value = createObject(storage.readStorage(key));
			return value; }
		public String getKey() { return key; }
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
