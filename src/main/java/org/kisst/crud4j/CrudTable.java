package org.kisst.crud4j;

import java.util.Iterator;

import org.kisst.crud4j.index.MemoryUniqueIndex;
import org.kisst.item4j.Immutable;
import org.kisst.item4j.Immutable.Sequence;
import org.kisst.item4j.ObjectSchema;
import org.kisst.item4j.Schema.Field;
import org.kisst.item4j.seq.TypedSequence;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.SingleItemStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrudTable<T extends CrudObject> implements TypedSequence<T> {
	public final Logger logger = LoggerFactory.getLogger(CrudTable.class);
	
	protected final CrudObjectSchema<T> schema;
	protected final CrudModel model;
	private final String name;
	private final StructStorage storage;
	private final MemoryUniqueIndex<T> cache;
	private final ChangeHandler<T>[] indices;

	private boolean alwaysCheckId=true;
	@SuppressWarnings("unchecked")
	public CrudTable(CrudModel model, CrudObjectSchema<T> schema) { 
		this.model=model;
		this.schema=schema;
		this.name=schema.cls.getSimpleName();
		this.storage=model.getStorage(schema.cls);
		if (storage.useCache()) {
			cache=new MemoryUniqueIndex<T>(schema, schema.getKeyField());
			this.indices=(ChangeHandler<T>[]) ArrayUtil.join(cache,model.getIndices(schema.cls));
		}
		else {
			cache=null;
			this.indices=model.getIndices(schema.cls);

		}
	}
	// This can not be done in the constructor, because then the CrudObjects will hae a null table
	public void initcache() { 
		if (cache==null) 
			return;
		//System.out.println("Loading all "+name+" records to cache");
		TypedSequence<Struct> seq = storage.findAll();
		for (Struct rec:seq) {
			try {
				T obj=createObject(rec);
				executeChange(new Change(null,obj));
			}
			catch (RuntimeException e) { e.printStackTrace(); /*ignore*/ } // TODO: return dummy activity
		}
	}
	public void close() { storage.close(); }
	public CrudSchema<T> getSchema() { return schema; }
	public String getName() { return name; }
	public String getKey(T obj) { return schema.getKeyField().getString(obj); }

	public T createObject(Struct doc) { return schema.createObject(model, doc); }

	public synchronized void create(T newValue) {
		if (executeChange(new Change(null,newValue)))
			storage.create(newValue);
	}

	public T read(String key) {
		T result=readOrNull(key);
		if (result==null)
			throw new RuntimeException("Could not find "+name+" for key "+key);
		return result;
	}
	public T readOrNull(String key) {  
		T result;
		if (cache!=null)
			result=cache.get(key);
		else {	
			Struct rec = storage.read(key);
			result = createObject(rec);
		}
		//System.out.println("struct "+rec.getClass()+"="+rec);
		//System.out.println("object "+obj.getClass()+"="+obj);
		return result;
	}
	private synchronized void update(T oldValue, T newValue) {
		checkSameId(oldValue, newValue);
		if (executeChange(new Change(oldValue,newValue)))
			storage.update(oldValue, newValue); 
	}
	public synchronized void updateFields(T oldValue, Struct newFields) { 
		update(oldValue, createObject(new MultiStruct(newFields, oldValue))); 
	}
	public synchronized void updateField(T oldValue, Field field, Object newValue) { 
		updateFields(oldValue, new SingleItemStruct(field.getName(),newValue)); 
	}
	public synchronized <ST> void addSequenceItem(T oldValue, ObjectSchema<T>.SequenceField<ST> field, ST value) {
		Sequence<ST> oldSequence = field.getSequence(model, oldValue);
		Sequence<ST> newSequence = oldSequence.growTail(value);
		updateField(oldValue, field, newSequence);
	}
	public synchronized <ST> int removeSequenceItem(T oldValue, ObjectSchema<T>.SequenceField<ST> field, ST value) {
		Sequence<ST> oldSequence = field.getSequence(model, oldValue);
		int index=0;
		for (ST it: oldSequence) {
			if (it.equals(value)) { // TODO: will equals work?
				Sequence<ST> newSequence = oldSequence.remove(index);
				updateField(oldValue, field, newSequence);
				return 1;
			}
			index++;
		}
		return 0;
	}

	
	
	public synchronized void delete(T oldValue) {
		if (executeChange(new Change(oldValue,null)))
			storage.delete(oldValue);
	}

	
	public static class CrudRef<TT extends CrudObject> {
		public final CrudTable<TT> table;
		public final String _id;
		protected CrudRef(CrudTable<TT> table, String _id) { 
			this.table=table; 
			this._id=_id; 
		}
		public TT get() { return table.read(_id); }
		public TT get0() { return table.readOrNull(_id); }
		@Override public String toString() { return _id; } //return "Ref("+table.getName()+":"+_id+")";}
	}

	
	public CrudRef<T> createRef(String key) { return new CrudRef<T>(this,key); }

	private void checkSameId(T oldValue, T newValue) {
		if (! alwaysCheckId)
			return;
		String newId = schema.getKeyField().getString(newValue);
		if (newId!=null) {
			String oldId = schema.getKeyField().getString(oldValue);
			if (!newId.equals(oldId))
				throw new IllegalArgumentException("Trying to update object with id "+oldId+" with object with id "+newId+": "+oldValue+"->"+newValue);
		}
	}
	public TypedSequence<T> findAll() {
		if (cache!=null)  
			return Immutable.Sequence.smartCopy(model, schema.getJavaClass(),cache.getAll());
		return Immutable.Sequence.realCopy(model, schema.getJavaClass(),storage.findAll());
	}


	@Override public int size() { return findAll().size();}
	@Override public Object getObject(int index) { return findAll().get(index); }
	@Override public Iterator<T> iterator() { return findAll().iterator(); }
	@Override public Class<?> getElementClass() { return schema.cls; }
	
	public class Change {
		public final T oldRecord;
		public final T newRecord;
		public Change(T oldRecord, T newRecord) { this.oldRecord=oldRecord; this.newRecord=newRecord; }
		@Override public String toString() { return "Change("+oldRecord+","+newRecord+")"; } 
	}
	public interface ChangeHandler<TT extends CrudObject> {
		public boolean allow(CrudTable<TT>.Change change); 
		public void commit(CrudTable<TT>.Change change); 
		public void rollback(CrudTable<TT>.Change change); 
	}

	private final static Logger changeLogger = LoggerFactory.getLogger(CrudTable.Change.class);
	
	private boolean executeChange(Change change) {
		changeLogger.info("applying {}", change);
		if (allow(change))
			return commmit(change);
		return false;
	}

	private boolean allow(Change change) {
		changeLogger.debug("asking to allow {}",change);
		for (ChangeHandler<T> res : indices) {
			try { 
				changeLogger.debug("asking {}",res);
				if (! res.allow(change)) {
					changeLogger.warn("Resource {} refused {}",res,change);
					return false;
				}
			} 
			catch(RuntimeException e) { 
				logger.error("Error preparing {}", change, e);
				return false;
			}
		}
		return true;
	}

	
	private boolean commmit(Change change) {
		changeLogger.debug("commiting {}",change);
		for (int index=0; index<indices.length; index++) {
			ChangeHandler<T> res = indices[index];
			changeLogger.debug("commiting in {}",res);
			try { res.commit(change); } 
			catch(RuntimeException e) { 
				changeLogger.error("Error commiting {}", change, e);
				rollback(change,index-1);
				return false;
			}
		}
		return true;
	}

	private void rollback(Change change, int highestIndex) {
		for (int index=highestIndex; index>=0; index--) {
			try { indices[index].rollback(change); } 
			catch(RuntimeException e) { changeLogger.error("Error rolling back {}", change, e);}
		}
	}
}
