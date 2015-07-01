package org.kisst.crud4j;

import org.kisst.struct4j.Struct;

public abstract class CrudTable<T extends CrudObject> {
	public  final CrudSchema<T> schema;
	private final  Storage<T> storage;
	
	public CrudTable(Storage<T> storage) { 
		this.schema=storage.getSchema();
		this.storage=storage;
	}
	abstract protected T createObject(Struct  props);
	public void create(T doc) {
		storage.createInStorage(doc);
	}
	public T read(String key) { 
		return createObject(storage.readFromStorage(key));
	}
	public void update(T oldValue, T newValue) { 
		storage.updateInStorage(oldValue, newValue); 
	}
	public void delete(T oldValue) {
		storage.deleteInStorage(oldValue);
	}

	public class Ref {
		public final String _id;
		private Ref(String id) {this._id=id;}
		public T get() {return read(_id); }
	}
	public Ref getRef(String key) { return new Ref(key); }

}
