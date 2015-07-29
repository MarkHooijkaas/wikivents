package org.kisst.crud4j;

import java.util.ArrayList;
import java.util.Arrays;

import org.kisst.crud4j.CrudTable.UniqueIndex;
import org.kisst.crud4j.index.Index;
import org.kisst.item4j.Item;
import org.kisst.item4j.Schema;

public abstract class CrudModel implements Item.Factory {
	private final StorageOption[] options;

	public CrudModel() { this(new StorageOption[0]); }
	public CrudModel(StorageOption[] options) {
		Schema.globalFactory=this; // TODO: big hack
		this.options=options;
	}

	public StructStorage getStorage(Class<?> cls) {
		for (StorageOption opt: options) {
			if (opt instanceof StructStorage && opt.getRecordClass()==cls)
				return (StructStorage) opt;
		}
		/*// not needed since the fields aren't initalized yet when this (the superconstructor is called)
		for (StructStorage s: ReflectionUtil.getAllDeclaredFieldValuesOfType(this, StructStorage.class)) {
			if (s.getRecordClass()==cls)
				return s;
		}
		*/
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
		/*// not needed since the fields aren't initalized yet when this (the superconstructor is called)
		for (Index idx: ReflectionUtil.getAllDeclaredFieldValuesOfType(this, Index.class)) {
			if (idx.getRecordClass()==cls)
				result.add(idx);
		}
		*/
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
				if (Arrays.equals(fields, ((Index<?>)idx).fields))
					return idx;
			}
		}
		String fieldnames=fields[0].getName();
		for (int i=1; i<fields.length; i++)
			fieldnames+=", "+fields[i].getName();
		throw new RuntimeException("Unknown UniqueIndex for type "+cls.getSimpleName()+" and field(s) "+fieldnames);
	}

}
