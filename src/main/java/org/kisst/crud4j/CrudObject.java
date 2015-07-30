package org.kisst.crud4j;

import org.bson.types.ObjectId;
import org.kisst.crud4j.CrudTable.CrudRef;
import org.kisst.item4j.struct.Struct;

public abstract class CrudObject extends CrudModelObject {
	public final CrudTable<?> table;
	public final String _id;
	public <T extends CrudObject> CrudObject(CrudTable<T> table, Struct data) {
		super(table.schema);
		this.table=table;
		this._id=createUniqueKey(data); 
	}
	public String getKey() { return _id;} 
	protected String createUniqueKey(Struct data) {
		String key= data.getString("_id",null);
		if (key==null)
			return uniqueKey();
		return key;
	}
	protected String uniqueKey() { return new ObjectId().toHexString();}
	abstract public boolean mayBeChangedBy(String userId);

	@SuppressWarnings("unchecked")
	public<T extends CrudObject> CrudRef<T> getRef() { return (CrudRef<T>) table.createRef(_id);}
	
}
