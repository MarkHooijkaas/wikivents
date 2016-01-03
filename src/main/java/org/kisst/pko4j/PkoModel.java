package org.kisst.pko4j;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;

import org.kisst.item4j.Item;
import org.kisst.item4j.Schema;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.PkoTable.ChangeHandler;
import org.kisst.util.ReflectionUtil;

public abstract class PkoModel implements Item.Factory {
	public interface MyObject {}

	
	private final StorageOption[] options;

	public PkoModel() { this(new StorageOption[0]); }
	public PkoModel(StorageOption[] options) {
		this.options=options;
	}

	public void initModel() {
		for (PkoTable<?> table: ReflectionUtil.getAllDeclaredFieldValuesOfType(this, PkoTable.class))
			table.initcache();
	}
	public void close() {
		for (PkoTable<?> table : ReflectionUtil.getAllDeclaredFieldValuesOfType(this, PkoTable.class))
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
	public<T extends PkoObject> ChangeHandler<T>[] getIndices(Class<?> cls) {
		ArrayList<ChangeHandler<T>> result=new ArrayList<ChangeHandler<T>>();
		for (StorageOption opt: options) {
			if (opt instanceof Index && opt.getRecordClass()==cls) {
				result.add((ChangeHandler<T>) opt);
				//System.out.println("Using index "+opt);
			}
		}
		ChangeHandler<T>[] arr=new ChangeHandler[result.size()];
		for (int i=0; i<result.size(); i++)
			arr[i]=result.get(i);
		return arr;
	}
	
	public <T extends PkoObject> UniqueIndex<T> getUniqueIndex(Class<?> cls, Schema.Field<?> ... fields) {
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

	public <T extends PkoObject> OrderedIndex<T> getOrderedIndex(Class<T> cls, Schema.Field<?> ... fields) {
		for (StorageOption opt: options) {
			if (opt instanceof OrderedIndex && opt.getRecordClass()==cls) {
				@SuppressWarnings("unchecked")
				OrderedIndex<T> idx=(OrderedIndex<T>) opt;
				if (Arrays.equals(fields, idx.fields()))
					return idx;
			}
		}
		throw new RuntimeException("Unknown OrderedIndex for type "+cls.getSimpleName());
	}

	@Override public <T> T construct(Class<?> cls, Struct data) {
		if (MyObject.class.isAssignableFrom(cls)) {
			//System.out.println("Trying to construct "+cls.getName());
			Constructor<?> cons=ReflectionUtil.getConstructor(cls, new Class<?>[]{ this.getClass(), Struct.class} );
			return cast(ReflectionUtil.createObject(cons, new Object[] {this, data}));
		}
		return basicFactory.construct(cls, data);
	}
	@Override public <T> T construct(Class<?> cls, String data) { 
		if (MyObject.class.isAssignableFrom(cls)) {
			//System.out.println("Trying to construct "+cls.getName());
			Constructor<?> cons=ReflectionUtil.getConstructor(cls, new Class<?>[]{ this.getClass(), String.class} );
			return cast(ReflectionUtil.createObject(cons, new Object[] {this, data}));
		}
		return basicFactory.construct(cls, data);
	}

	public interface Index<T extends PkoObject> {
		public Class<T> getRecordClass(); 
	}
	public interface UniqueIndex<T extends PkoObject> extends Index<T >{
		public Schema.Field<?>[] fields();
		public T get(String ... field); 
	}
	public interface OrderedIndex<T extends PkoObject> extends Index<T >, Iterable<T>{
		public Schema.Field<?>[] fields();
		public Iterable<T> tailList(String fromKey); 
		public Iterable<T> headList(String toKey);  
		public Iterable<T> subList(String fromKey,String toKey); 
	}
}
