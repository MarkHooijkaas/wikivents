package org.kisst.crud4j.impl;

import java.io.File;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.item4j.json.JsonOutputter;
import org.kisst.item4j.json.JsonParser;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.FileUtil;

public abstract class FileTable<T extends CrudObject> extends BaseMemoryTable<T> {
	private final File dir;
	private final JsonParser parser=new JsonParser();
	private final JsonOutputter outputter = new JsonOutputter(null);
	
	public FileTable(CrudSchema<T> schema, File maindir) {
		super(schema);
		dir=new File(maindir,schema.cls.getSimpleName());
		if (! dir.exists())
			dir.mkdirs();
		loadAllRecords();
	}
	
	@Override public void createInStorage(T value) {
		File f = getFile(value._id);
		if (f.exists())
			throw new RuntimeException("File "+f.getAbsolutePath()+" already exists");
		FileUtil.saveString(f, outputter.createString(value));
	}
	@Override public Struct readFromStorage(String key) {
		return parser.parse(getFile(key));
	}
	@Override public void updateInStorage(T oldValue, T newValue) {
		// The newValue may contain an id, but that is ignored
		String oldId = getSchema().getKeyField().getObjectValue(oldValue);
		File f = getFile(oldId);
		FileUtil.saveString(f, outputter.createString(newValue));
	}
	@Override public void deleteInStorage(T oldValue)  {
		checkForConcurrentModification(oldValue);
		getFile(oldValue).delete();
	}
	private void checkForConcurrentModification(T obj) {
		// TODO Auto-generated method stub
		
	}
	private File getFile(String key) { return new File(dir, key+".rec"); }
	private File getFile(T obj) { return getFile(getKey(obj));}
	
	protected void loadAllRecords() {
		long start= System.currentTimeMillis();
		System.out.println("loading all records from "+getName());
		int count=0;
		for (File f:dir.listFiles()) {
			String key=f.getName();
			if (! key.endsWith(".rec"))
				continue;
			count++;
			key=key.substring(0,key.length()-4);
			
			Struct doc=parser.parse(f);
			System.out.println(doc);
			for (String fld: doc.fieldNames())
				System.out.println(fld+"="+doc.getString(fld,"JANTJE"));

			T obj = createObject(doc);
			for (String ff: obj.fieldNames())
				System.out.println(ff+"="+doc.getString(ff,"JANTJE"));
			cacheRecord(obj);
		}
		System.out.println("DONE loading "+count+" records from "+getName()+" in "+(System.currentTimeMillis()-start)+" milliseconds");
	}

}
