package org.kisst.crud4j.impl;

import java.util.ArrayList;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.Struct;

public abstract class BaseTable<T extends CrudObject> implements CrudTable<T>{
	protected final CrudSchema<T> schema;
	private final String name;
	private final ArrayList<Index<T>> indices=new ArrayList<Index<T>>();
	
	private boolean alwaysCheckId=true;
	public BaseTable(CrudSchema<T> schema) { 
		this.schema=schema;
		this.name=schema.cls.getSimpleName();
	}
	@Override public CrudSchema<T> getSchema() { return schema; }
	@Override public String getName() { return name; }
	@Override public String getKey(T obj) { return getSchema().getKeyField().getValue(obj); }

	public Index<T> addIndex(Index<T> idx) { indices.add(idx); return idx; }
	
	@Override public T createObject(Struct props) { return null; }
	public void create(T doc) {
		try {
			for(Index<T> index : indices)
				index.notifyCreate(doc);
			createInStorage(doc);
		}
		catch (RuntimeException e) {
			// TODO: rollback
			throw e;
		}
	}
	public T read(String key) { 
		return createObject(readFromStorage(key));
	}
	public void update(T oldValue, T newValue) {
		checkSameId(oldValue, newValue);
		try {
			for(Index<T> index : indices)
				index.notifyUpdate(oldValue,newValue);
			updateInStorage(oldValue, newValue); 
		}
		catch (RuntimeException e) {
			// TODO: rollback
			throw e;
		}
	}
	public void updateFields(T oldValue, Struct newFields) { 
		update(oldValue, createObject(new MultiStruct(newFields, oldValue))); 
	}
	public void delete(T oldValue) {
		try {
			for(Index<T> index : indices)
				index.notifyDelete(oldValue);
			deleteInStorage(oldValue);
		}
		catch (RuntimeException e) {
			// TODO: rollback
			throw e;
		}
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
	
	public abstract void createInStorage(T value);
	public abstract Struct readFromStorage(String key);
	public abstract void updateInStorage(T oldValue, T newValue);
	public abstract void deleteInStorage(T oldValue);
}
