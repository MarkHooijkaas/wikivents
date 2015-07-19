package org.kisst.crud4j.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.StructStorage;
import org.kisst.item4j.Schema;
import org.kisst.item4j.json.JsonOutputter;
import org.kisst.item4j.json.JsonParser;
import org.kisst.item4j.seq.ArraySequence;
import org.kisst.item4j.seq.TypedSequence;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.FileUtil;

public class FileStorage implements StructStorage {
	private final File dir;
	private final String name;
	private final JsonParser parser=new JsonParser();
	private final JsonOutputter outputter = new JsonOutputter(null);
	private final CrudSchema<?>.IdField keyField;
	private final ArrayList<Index> indices=new ArrayList<Index>();
	private final CrudSchema<?> schema;
	private final boolean useCache;

	
	public FileStorage(CrudSchema<?> schema, File maindir, boolean useCache) {
		this.schema=schema;
		this.keyField = schema.getKeyField();
		this.name=schema.cls.getSimpleName();
		this.useCache=useCache;				
		dir=new File(maindir,name);
		if (! dir.exists())
			dir.mkdirs();
		//loadAllRecords();
		
	}
	public FileStorage(CrudSchema<?> schema, Struct props) {
		this(schema,new File(props.getString("datadir", "data")),props.getBoolean("useCache",true)); 
	}
	@Override public boolean useCache() { return useCache;}

	@Override public Class<?> getRecordClass() { return schema.cls; }
	public Index addIndex(Index idx) { indices.add(idx); return idx; }
	
	@Override public String createInStorage(Struct value) {
		String key = keyField.getString(value);
		if (key==null)
			key=createUniqueKey();
		File f = getFile(key);
		if (f.exists())
			throw new RuntimeException("File "+f.getAbsolutePath()+" already exists");
		FileUtil.saveString(f, outputter.createString(value));
		return key;
	}
	private static AtomicInteger number=new AtomicInteger(new Random().nextInt(13));
	private String createUniqueKey() {
		int i=number.incrementAndGet();
		return Long.toHexString(System.currentTimeMillis())+Integer.toHexString(i);
	}
	@Override public Struct readFromStorage(String key) {
		return parser.parse(getFile(key));
	}
	@Override public void updateInStorage(Struct oldValue, Struct newValue) {
		// The newValue may contain an id, but that is ignored
		String oldId = keyField.getString(oldValue);
		File f = getFile(oldId);
		FileUtil.saveString(f, outputter.createString(newValue));
	}
	@Override public void deleteInStorage(Struct oldValue)  {
		checkForConcurrentModification(oldValue);
		getFile(oldValue).delete();
	}
	private void checkForConcurrentModification(Struct obj) {
		// TODO Auto-generated method stub
		
	}
	private File getFile(String key) { return new File(dir, key+".rec"); }
	private File getFile(Struct obj) { return getFile(keyField.getString(obj));}
	
	@Override public TypedSequence<Struct> findAll() {
		ArrayList<Struct> list=new ArrayList<Struct>();
		long start= System.currentTimeMillis();
		System.out.println("loading all records from "+name);
		int count=0;
		for (File f:dir.listFiles()) {
			String key=f.getName();
			if (! key.endsWith(".rec"))
				continue;
			count++;
			key=key.substring(0,key.length()-4);
			
			Struct doc=parser.parse(f);
			list.add(doc);
		}
		System.out.println("DONE loading "+count+" records from "+name+" in "+(System.currentTimeMillis()-start)+" milliseconds");
		return new ArraySequence<Struct>(Struct.class,list);
	}
	
	@Override public UniqueIndex useUniqueIndex(Schema<?>.Field... fields) { 
		MyUniqueIndex index = new MyUniqueIndex(fields); 
		addIndex(index);
		return index;
	}
	@Override public MultiIndex useMultiIndex(Schema<?>.Field... fields) { 
		MyMultiIndex index = new MyMultiIndex(fields); 
		addIndex(index);
		return index;
	}
	private class Index {
		@SuppressWarnings("unused")
		private final CrudSchema<?>.Field[] fields;
		protected Index(CrudSchema<?>.Field ... fields) {
			this.fields=fields;
		}
		public void notifyCreate(Struct record) {}
		public void notifyUpdate(Struct oldRecord, Struct newRecord) {}
		public void notifyDelete(Struct oldRecord) {}
	}
	private class MyUniqueIndex extends Index implements UniqueIndex {
		private MyUniqueIndex(CrudSchema<?>.Field ... fields) { super(fields); }
		@Override public Struct get(String... values) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	private class MyMultiIndex extends Index implements MultiIndex {
		private MyMultiIndex(CrudSchema<?>.Field ... fields) { super(fields); }
		@Override public TypedSequence<Struct> get(String... values) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public void create(Struct doc) {
		try {
			for(Index index : indices)
				index.notifyCreate(doc);
			this.createInStorage(doc);
		}
		catch (RuntimeException e) {
			// TODO: rollback
			throw e;
		}
	}
	public Struct read(String key) { 
		return this.readFromStorage(key);
	}
	public void update(Struct oldValue, Struct newValue) {
		//checkSameId(oldValue, newValue);
		try {
			for(Index index : indices)
				index.notifyUpdate(oldValue,newValue);
			this.updateInStorage(oldValue, newValue); 
		}
		catch (RuntimeException e) {
			// TODO: rollback
			throw e;
		}
	}
	
	public void delete(Struct oldValue) {
		try {
			for(Index index : indices)
				index.notifyDelete(oldValue);
			this.deleteInStorage(oldValue);
		}
		catch (RuntimeException e) {
			// TODO: rollback
			throw e;
		}
	}
}
