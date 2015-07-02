package org.kisst.crud4j;

import org.kisst.struct4j.MultiStruct;
import org.kisst.struct4j.Struct;

public abstract class CrudTable<T extends CrudObject> {
	public  final CrudSchema<T> schema;
	private final  Storage<T> storage;
	public  final String name;

	private boolean alwaysCheckId=true;
	public CrudTable(Storage<T> storage) { 
		this.schema=storage.getSchema();
		this.storage=storage;
		this.name=storage.getClass().getName();
	}
	abstract protected T createObject(Struct  props);
	public void create(T doc) {
		storage.createInStorage(doc);
	}
	public T read(String key) { 
		return createObject(storage.readFromStorage(key));
	}
	public void update(T oldValue, T newValue) { 
		checkSameId(oldValue, newValue);
		storage.updateInStorage(oldValue, newValue); 
	}
	public void updateFields(T oldValue, Struct newFields) { 
		update(oldValue, createObject(new MultiStruct(newFields, oldValue))); 
	}
	public void delete(T oldValue) {
		storage.deleteInStorage(oldValue);
	}

	public class Ref {
		public final String _id;
		private Ref(String id) {this._id=id;}
		public T get() {return read(_id); }
		public CrudTable<T> getTable() { return CrudTable.this; }
		@Override public boolean equals(Object obj) {
			if (obj==this)
				return true;
			if (obj==null)
				return false;
			if (! (obj instanceof CrudTable.Ref))
				return false;
			CrudTable<?>.Ref ref = (CrudTable<?>.Ref)obj;
			if (this.getTable()!=ref.getTable())
				return false;
			return _id.equals((ref)._id);
		}
		@Override public int hashCode() { return _id.hashCode()+getTable().hashCode(); }
		@Override public String toString() { return getTable().name+"("+_id+")"; }
	}
	public Ref getRef(String key) { return new Ref(key); }

	private void checkSameId(T oldValue, T newValue) {
		if (! alwaysCheckId)
			return;
		String newId = schema.getKeyField().getObjectValue(newValue);
		if (newId!=null) {
			String oldId = schema.getKeyField().getObjectValue(oldValue);
			if (!newId.equals(oldId))
				throw new IllegalArgumentException("Trying to update object with id "+oldId+" with object with id "+newId);
		}
	}

}
