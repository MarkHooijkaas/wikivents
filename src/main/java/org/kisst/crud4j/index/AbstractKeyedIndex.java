package org.kisst.crud4j.index;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudTable;
import org.kisst.item4j.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractKeyedIndex<T extends CrudObject> extends Index<T> {
	public final static Logger logger = LoggerFactory.getLogger(AbstractKeyedIndex.class);

	public AbstractKeyedIndex(Schema schema) { 
		super(schema);
	}
	abstract protected String calcUniqueKey(T record);
	abstract  protected void add(String key, T record);
	abstract  protected void remove(String key);
	abstract boolean keyExists(String key);


	@Override public boolean allow(CrudTable<T>.Change change) {
		if (change.oldRecord==null) // create
			return ! keyExists(calcUniqueKey(change.newRecord));
		else if (change.newRecord==null) // delete
			return true;
		else { // update
			String oldkey = calcUniqueKey(change.oldRecord);
			String newkey = calcUniqueKey(change.newRecord);
			if (oldkey.equals(newkey))
				return true;
			else
				return ! keyExists(newkey);
		}
	}
	

	@Override public void commit(CrudTable<T>.Change change) {
		logger.debug("committing {}",change);
		// TODO: should we check the prepare again??
		if (change.oldRecord!=null) {
			String oldkey = calcUniqueKey(change.oldRecord);
			logger.info("removing unique key {} ",oldkey);
			remove(oldkey);
		}
		if (change.newRecord!=null) {
			String newkey = calcUniqueKey(change.newRecord);
			logger.info("adding unique key {}",newkey);
			add(newkey, change.newRecord);
		}
	}

	@Override public void rollback(CrudTable<T>.Change change) {
		if (change.newRecord!=null) {
			String newkey = calcUniqueKey(change.newRecord);
			logger.info("rollback of adding unique key {}",newkey);
			remove(newkey);
		}
		if (change.oldRecord!=null) {
			String oldkey = calcUniqueKey(change.oldRecord);
			logger.info("rollback of removing unique key {}",oldkey);
			add(oldkey, change.oldRecord);
		}
	}

}
