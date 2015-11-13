package org.kisst.crud4j;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.kisst.crud4j.CrudTable.CrudRef;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;

public abstract class CrudObject extends SchemaObject {
	public final CrudTable<?> table;
	public final int _crudObjectVersion;
	public final String _id;
	public final Instant creationDate;
	public final Instant modificationDate;
	public <T extends CrudObject> CrudObject(CrudTable<T> table, Struct data) {
		super(table.schema);
		this.table=table;
		this._crudObjectVersion=getCrudObjectVersion();
		this._id=createUniqueKey(data);
		this.creationDate=new ObjectId(_id).getDate().toInstant();
		this.modificationDate=(Instant) data.getDirectFieldValue("savedModificationDate", Instant.now());
		//System.out.println(data);
	}
	public String getKey() { return _id;} 
	protected String createUniqueKey(Struct data) {
		String key= Item.asString(data.getDirectFieldValue("_id",null)); 
		if (key==null)
			return uniqueKey();
		return key;
	}
	protected String uniqueKey() { return new ObjectId().toHexString();}

	@SuppressWarnings("unchecked")
	public<T extends CrudObject> CrudRef<T> getRef() { return (CrudRef<T>) table.createRef(_id);}
	
	public int getCrudObjectVersion() { return 0;}
	public int getCrudObjectVersionOf(Struct data) { return Integer.parseInt(""+data.getDirectFieldValue("_crudObjectVersion", "0"));}
}
