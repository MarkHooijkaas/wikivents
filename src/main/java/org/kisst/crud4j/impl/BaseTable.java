package org.kisst.crud4j.impl;

import java.util.ArrayList;
import java.util.Iterator;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.StructStorage;
import org.kisst.item4j.seq.ArraySequence;
import org.kisst.item4j.seq.Sequence;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.Struct;

public class BaseTable<T extends CrudObject> implements CrudTable<T>{
	protected final CrudSchema<T> schema;
	private final String name;
	private final StructStorage storage;
	
	private boolean alwaysCheckId=true;
	public BaseTable(CrudSchema<T> schema, StructStorage storage) { 
		this.schema=schema;
		this.storage=storage;
		this.name=schema.cls.getSimpleName();
	}
	@Override public CrudSchema<T> getSchema() { return schema; }
	@Override public String getName() { return name; }
	@Override public String getKey(T obj) { return getSchema().getKeyField().getValue(obj); }

	@Override public T createObject(Struct doc) { return schema.createObject(doc); }
	
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

	public class MyRef implements Ref<T> {
		public final String _id;
		private MyRef(String id) {this._id=id;}
		public T get() {return read(_id); }
		public BaseTable<T> getTable() { return BaseTable.this; }
		@Override public boolean equals(Object obj) {
			if (obj==this)
				return true;
			if (obj==null)
				return false;
			if (! (obj instanceof BaseTable.MyRef))
				return false;
			BaseTable<?>.MyRef ref = (BaseTable<?>.MyRef)obj;
			if (this.getTable()!=ref.getTable())
				return false;
			return _id.equals((ref)._id);
		}
		@Override public int hashCode() { return _id.hashCode()+getTable().hashCode(); }
		@Override public String toString() { return getTable().name+"("+_id+")"; }
	}
	public Ref<T> getRef(String key) { return new MyRef(key); }

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
	private Sequence<T> all=null;
	@Override public Sequence<T> findAll() {
		if (all!=null)
			return all;
		Sequence<Struct> seq = storage.findAll();
		ArrayList<T> list=new ArrayList<T>(seq.size());
		for (Struct rec:seq)
			list.add(createObject(rec));
		all=new ArraySequence<T>(schema.cls, list);
		return all;
	}
	
	
	@Override public int size() { return findAll().size();}
	@Override public T get(int index) { return findAll().get(index); }
	@Override public Iterator<T> iterator() { return findAll().iterator(); }
}
