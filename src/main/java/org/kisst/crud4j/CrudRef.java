package org.kisst.crud4j;

public class CrudRef<T extends CrudObject> {
	public final CrudTable<T> table;
	public final String _id;
	public CrudRef(CrudTable<T> table, String _id) { 
		this.table=table; 
		this._id=_id; 
	}
	public T get() { return table.read(_id); }
}
