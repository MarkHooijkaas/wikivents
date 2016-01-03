package org.kisst.pko4j;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.PkoTable.KeyRef;

public abstract class PkoObject extends SchemaObject {
	public final PkoTable<?> table;
	public final int _pkoVersion;
	public final String _id;
	public final Instant creationDate;
	public final Instant modificationDate;
	public <T extends PkoObject> PkoObject(PkoTable<T> table, Struct data) {
		super(table.schema);
		this.table=table;
		this._pkoVersion=getPkoVersionOf(data);
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
	public<T extends PkoObject> KeyRef<T> getRef() { return (KeyRef<T>) table.createRef(_id);}
	
	public int getPkoVersion() { return 0;}
	public int getPkoVersionOf(Struct data) { return Integer.parseInt(""+data.getDirectFieldValue("_crudObjectVersion", "0"));}

	public String readBlob(String path) { return table.storage.readBlob(_id, path); }
	public void writeBlob(String path, String blob) { table.storage.writeBlob(_id, path, blob); }
	public void appendBlob(String path, String blob) { table.storage.appendBlob(_id, path, blob); }
}
