package org.kisst.crud4j;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.kisst.item4j.Immutable;
import org.kisst.item4j.Item;
import org.kisst.item4j.seq.TypedSequence;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.Struct;

public abstract class CrudTable<T extends CrudObject> implements TypedSequence<T> {
	protected final CrudSchema<T> schema;
	protected final Item.Factory factory;
	private final String name;
	private final StructStorage storage;
	private final ConcurrentHashMap<String,T> cache;

	private boolean alwaysCheckId=true;
	public CrudTable(CrudSchema<T> schema, Item.Factory factory, StructStorage storage) { 
		this.schema=schema;
		this.factory=factory;
		this.storage=storage;
		this.name=schema.cls.getSimpleName();
		if (storage.useCache())
			cache=new ConcurrentHashMap<String,T>();
		else
			cache=null;
		if (cache!=null) {
			//System.out.println("Loading all "+name+" records to cache");
			TypedSequence<Struct> seq = storage.findAll();
			for (Struct rec:seq) {
				T obj=createObject(rec);
				//System.out.println("caching "+obj);
				cache.put(obj._id, obj);
			}
		}
	}
	public void close() { storage.close(); }
	public CrudSchema<T> getSchema() { return schema; }
	public String getName() { return name; }
	public String getKey(T obj) { return getSchema().getKeyField().getString(obj); }

	public T createObject(Struct doc) { return schema.createObject(doc); }

	public void create(T doc) {
		if (cache!=null)
			cache.put(doc._id, doc);
		storage.createInStorage(doc);
	}
	public T read(String key) { 
		T result;
		if (cache!=null)
			result=cache.get(key);
		else {	
			Struct rec = storage.readFromStorage(key);
			result = createObject(rec);
		}
		//System.out.println("struct "+rec.getClass()+"="+rec);
		//System.out.println("object "+obj.getClass()+"="+obj);
		if (result==null)
			throw new RuntimeException("Could not find "+name+" for key "+key);
		return result;
	}
	public void update(T oldValue, T newValue) {
		checkSameId(oldValue, newValue);
		if (cache!=null)
			cache.put(newValue._id, newValue);
		storage.updateInStorage(oldValue, newValue); 
	}
	public void updateFields(T oldValue, Struct newFields) { 
		update(oldValue, createObject(new MultiStruct(newFields, oldValue))); 
	}
	public void delete(T oldValue) {
		cache.remove(oldValue._id);
		storage.deleteInStorage(oldValue);
	}

	public class Ref {
		public final String _id;
		protected Ref(String id) {this._id=id;}
		public T get() {System.out.println("searching "+_id);return read(_id); }
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
		@Override public String toString() { return getTable().name+"("+_id+")"; }
	}
	abstract public Ref createRef(String key);

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
			return Immutable.Sequence.smartCopy(schema.getJavaClass(),cache.values());
		return Immutable.Sequence.realCopy(schema.getJavaClass(),storage.findAll());
	}


	@Override public int size() { return findAll().size();}
	@Override public Object getObject(int index) { return findAll().get(index); }
	@Override public Iterator<T> iterator() { return findAll().iterator(); }
	@Override public Class<?> getElementClass() { return schema.cls; }

	public interface UniqueIndex<T extends CrudObject> { public T get(String field); }
	public interface MultiIndex<T extends CrudObject> { public TypedSequence<T> get(String field); }
	public interface OrderedIndex<T extends CrudObject> { public TypedSequence<T> get(String field);}
}
