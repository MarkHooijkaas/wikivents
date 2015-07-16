package org.kisst.rest4j;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudTable;
import org.kisst.item4j.seq.TypedSequence;
import org.kisst.item4j.struct.Struct;

public class CrudResource<T extends CrudObject> implements Resource {
	private final CrudTable<T> table;
	public CrudResource(CrudTable<T> table) { this.table=table; }
	
	@Override public String createResource(Struct doc) {
		T obj = table.createObject(doc);
		table.create(obj);
		return obj._id;
	}
	@Override public TypedSequence<Struct> getResources(String[] filters) {
		return null; // TODO
	}
	@Override public Struct getSingleResource(String key) {
		return table.read(key);
	}
	@Override public void updateResource(String key, Struct newData) {
		T newObject=table.createObject(newData);
		T oldObject=table.read(key);
		table.update(oldObject,newObject);
	}
	@Override public void deleteResource(String key) {
		T oldObject=table.read(key);
		table.delete(oldObject);
	}
}
