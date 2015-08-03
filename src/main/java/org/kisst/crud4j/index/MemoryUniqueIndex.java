package org.kisst.crud4j.index;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.kisst.crud4j.CrudModel.UniqueIndex;
import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;
import org.kisst.item4j.ObjectSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryUniqueIndex<T extends CrudObject> extends FieldIndex<T>  implements UniqueIndex<T> {
	public final static Logger logger = LoggerFactory.getLogger(MemoryOrderedIndex.class);

	private ConcurrentHashMap<String, T> map=new ConcurrentHashMap<String, T>();

	@SafeVarargs
	public MemoryUniqueIndex(CrudSchema<T> schema, ObjectSchema<T>.Field ... fields) { super(schema, fields); }

	@Override public T get(String ... values) { return map.get(getKey(values)); }

	public Collection<T> getAll() { return map.values(); } // needed for cache, but not for general cache use
	
	@Override public boolean allow(CrudTable<T>.Change change) {
		if (change.oldRecord==null) // create
			return ! map.containsKey(change.newRecord._id);
		else if (change.newRecord==null) // delete
			return true;
		else { // update
			String oldkey = getKey(change.oldRecord);
			String newkey = getKey(change.newRecord);
			if (oldkey.equals(newkey))
				return true;
			else
				return ! map.containsKey(newkey);
		}
	}
	@Override public void commit(CrudTable<T>.Change change) {
		logger.debug("committing {}",change);
		// TODO: should we check the prepare again??
		if (change.oldRecord!=null) {
			String oldkey = getKey(change.oldRecord);
			logger.info("removing unique key {} ",oldkey);
			map.remove(oldkey);
		}
		if (change.newRecord!=null) {
			String newkey = getKey(change.newRecord);
			logger.info("adding unique key {}",newkey);
			map.put(newkey, change.newRecord);
		}
	}

	@Override public void rollback(CrudTable<T>.Change change) {
		if (change.newRecord!=null) {
			String newkey = getKey(change.newRecord);
			logger.info("rollback of adding unique key {}",newkey);
			map.remove(newkey);
		}
		if (change.oldRecord!=null) {
			String oldkey = getKey(change.oldRecord);
			logger.info("rollback of removing unique key {}",oldkey);
			map.put(oldkey, change.oldRecord);
		}
	}
}
