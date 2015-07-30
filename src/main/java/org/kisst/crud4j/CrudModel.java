package org.kisst.crud4j;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;

import org.kisst.item4j.Item;
import org.kisst.item4j.Schema;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public abstract class CrudModel implements Item.Factory {
	private final StorageOption[] options;

	public CrudModel() { this(new StorageOption[0]); }
	public CrudModel(StorageOption[] options) {
		this.options=options;
	}

	public void initModel() {
		for (CrudTable<?> table: ReflectionUtil.getAllDeclaredFieldValuesOfType(this, CrudTable.class))
			table.initcache();
	}
	public void close() {
		for (CrudTable<?> table : ReflectionUtil.getAllDeclaredFieldValuesOfType(this, CrudTable.class))
			table.close();
	}

	public StructStorage getStorage(Class<?> cls) {
		for (StorageOption opt: options) {
			if (opt instanceof StructStorage && opt.getRecordClass()==cls)
				return (StructStorage) opt;
		}
		throw new RuntimeException("Unknown Storage for type "+cls.getSimpleName());
	}
	@SuppressWarnings("unchecked")
	public<T extends CrudObject> Index<T>[] getIndices(Class<?> cls) {
		ArrayList<Index<T>> result=new ArrayList<Index<T>>();
		for (StorageOption opt: options) {
			if (opt instanceof Index && opt.getRecordClass()==cls) {
				result.add((Index<T>) opt);
				System.out.println("Using index "+opt);
			}
		}
		Index<T>[] arr=new Index[result.size()];
		for (int i=0; i<result.size(); i++)
			arr[i]=result.get(i);
		return arr;
	}
	
	public <T extends CrudObject> UniqueIndex<T> getUniqueIndex(Class<?> cls, Schema<?>.Field ... fields) {
		for (StorageOption opt: options) {
			if (opt instanceof UniqueIndex && opt.getRecordClass()==cls) {
				@SuppressWarnings("unchecked")
				UniqueIndex<T> idx=(UniqueIndex<T>) opt;
				if (Arrays.equals(fields, idx.fields()))
					return idx;
			}
		}
		String fieldnames=fields[0].getName();
		for (int i=1; i<fields.length; i++)
			fieldnames+=", "+fields[i].getName();
		throw new RuntimeException("Unknown UniqueIndex for type "+cls.getSimpleName()+" and field(s) "+fieldnames);
	}

	@SuppressWarnings("unchecked")
	public <T extends CrudObject> OrderedIndex<T> getOrderedIndex(Class<T> cls) {
		for (StorageOption opt: options) {
			if (opt instanceof OrderedIndex && opt.getRecordClass()==cls) {
				System.out.println("Using OrderedIndex "+opt);
				return (OrderedIndex<T>) opt;
			}
		}
		throw new RuntimeException("Unknown OrderedIndex for type "+cls.getSimpleName());
	}

	@Override public <T> T construct(Class<?> cls, Struct data) {
		System.out.println("Looking to construct "+cls.getName());
		if (CrudModelObject.class.isAssignableFrom(cls)) {
			System.out.println("Trying to construct "+cls.getName());
			Constructor<?> cons = CrudSchema.findModelBasedConstructor(cls);
			return cast(ReflectionUtil.createObject(cons, new Object[] {this, data}));
		}
		//for (CrudTable<?> table: ReflectionUtil.getAllDeclaredFieldValuesOfType(this, CrudTable.class)) {
		//	if (table.schema.getJavaClass()==cls)
		//		return (T) table.schema.createObject(this, data);
		//}
		return basicFactory.construct(cls, data);
	}
	@Override public <T> T construct(Class<?> cls, String data) { return basicFactory.construct(cls, data);}

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
