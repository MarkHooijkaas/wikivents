package org.kisst.crud4j;

import java.util.Iterator;

import org.kisst.crud4j.index.MemoryUniqueIndex;
import org.kisst.item4j.Immutable;
import org.kisst.item4j.seq.TypedSequence;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ArrayUtil;

public class CrudTable<T extends CrudObject> implements TypedSequence<T> { //, StructStorage<T> {
	protected final CrudObjectSchema<T> schema;
	protected final CrudModel model;
	private final String name;
	private final StructStorage storage;
	private final MemoryUniqueIndex<T> cache;
	private final Index<T>[] indices;

	private boolean alwaysCheckId=true;
	public CrudTable(CrudModel model, CrudObjectSchema<T> schema) { 
		this.model=model;
		this.schema=schema;
		this.name=schema.cls.getSimpleName();
		this.storage=model.getStorage(schema.cls);
		if (storage.useCache()) {
			cache=new MemoryUniqueIndex<T>(schema, schema.getKeyField());
			this.indices=ArrayUtil.join(cache,model.getIndices(schema.cls));
		}
		else {
			cache=null;
			this.indices=model.getIndices(schema.cls);

		}
		if (cache!=null) {
			//System.out.println("Loading all "+name+" records to cache");
			TypedSequence<Struct> seq = storage.findAll();
			for (Struct rec:seq) {
				try {
					T obj=createObject(rec);
					for (Index<T> index: indices) 
						index.notifyCreate(obj);
				}
				catch (Exception e) { e.printStackTrace(); /*ignore*/ } // TODO: return dummy activity
			}
		}
	}
	public void close() { storage.close(); }
	public CrudSchema<T> getSchema() { return schema; }
	public String getName() { return name; }
	public String getKey(T obj) { return schema.getKeyField().getString(obj); }

	public T createObject(Struct doc) { return schema.createObject(model, doc); }

	public synchronized void create(T doc) {
		for(Index<T> index : indices) index.notifyCreate(doc);
		storage.create(doc);
		// TODO : rollback indices in case of Exception?
	}
	public T read(String key) { 
		T result;
		if (cache!=null)
			result=cache.get(key);
		else {	
			Struct rec = storage.read(key);
			result = createObject(rec);
		}
		//System.out.println("struct "+rec.getClass()+"="+rec);
		//System.out.println("object "+obj.getClass()+"="+obj);
		if (result==null)
			throw new RuntimeException("Could not find "+name+" for key "+key);
		return result;
	}
	public synchronized void update(T oldValue, T newValue) {
		checkSameId(oldValue, newValue);
		for(Index<T> index : indices) index.notifyUpdate(oldValue, newValue);
		storage.update(oldValue, newValue); 
		// TODO : rollback indices in case of Exception?
	}
	public void updateFields(T oldValue, Struct newFields) { 
		update(oldValue, createObject(new MultiStruct(newFields, oldValue))); 
	}
	public synchronized void delete(T oldValue) {
		storage.delete(oldValue);
		for(Index<T> index : indices) index.notifyDelete(oldValue);
		// TODO : rollback indices in case of Exception?
	}

	public class Ref {
		public final String _id;
		protected Ref(String id) {this._id=id;}
		public T get() { return read(_id); }
		public CrudTable<T> getTable() { return CrudTable.this; }
		@Override public boolean equals(Object obj) {
			if (obj==this)
				return true;
			if (obj==null)
				return false;
			if (! (obj instanceof CrudTable.Ref))
				return false;
			CrudTable<?>.Ref ref = (CrudTable<?>.Ref)obj;
			if (this.getTable()!=ref.getTable())
				return false;
			return _id.equals((ref)._id);
		}
		@Override public int hashCode() { return _id.hashCode()+getTable().hashCode(); }
		@Override public String toString() { return _id; } //return getTable().name+"("+_id+")"; }
	}
	public CrudRef<T> createRef(String key) { return new CrudRef<T>(this,key); }

	private void checkSameId(T oldValue, T newValue) {
		if (! alwaysCheckId)
			return;
		String newId = schema.getKeyField().getString(newValue);
		if (newId!=null) {
			String oldId = schema.getKeyField().getString(oldValue);
			if (!newId.equals(oldId))
				throw new IllegalArgumentException("Trying to update object with id "+oldId+" with object with id "+newId);
		}
	}
	public TypedSequence<T> findAll() {
		if (cache!=null)  
			return Immutable.Sequence.smartCopy(schema.getJavaClass(),cache.getAll());
		return Immutable.Sequence.realCopy(schema.getJavaClass(),storage.findAll());
	}


	@Override public int size() { return findAll().size();}
	@Override public Object getObject(int index) { return findAll().get(index); }
	@Override public Iterator<T> iterator() { return findAll().iterator(); }
	@Override public Class<?> getElementClass() { return schema.cls; }

	public interface Index<T extends CrudObject> {
		public Class<T> getRecordClass(); 
		public void notifyCreate(T record);
		public void notifyUpdate(T oldRecord, T newRecord);
		public void notifyDelete(T oldRecord);
	}
	public interface UniqueIndex<T extends CrudObject> extends Index<T >{
		public CrudSchema<T>.Field[] fields();
		public T get(String ... field); 
	}
	public interface OrderedIndex<T extends CrudObject> extends Index<T >{
		public Iterable<T> all(); 
	}
	/*
	public interface MultiIndex<T extends CrudObject> { public TypedSequence<T> get(String field); }
	public interface OrderedIndex<T extends CrudObject> { public TypedSequence<T> get(String field);}
	*/
}
