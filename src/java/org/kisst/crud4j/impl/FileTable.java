package org.kisst.crud4j.impl;

import java.io.File;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.props4j.SimpleProps;
import org.kisst.struct4j.Struct;
import org.kisst.util.FileUtil;

public class FileTable<T extends CrudObject> extends BaseMemoryTable<T> {
	private final File dir;
	public FileTable(CrudSchema<T> schema, File maindir) {
		super(schema);
		dir=new File(maindir,schema.cls.getSimpleName());
		if (! dir.exists())
			dir.mkdirs();
	}
	
	@Override public void createInStorage(T value) {
		File f = new File(dir, value._id);
		if (f.exists())
			throw new RuntimeException("File "+f.getAbsolutePath()+" already exists");
		FileUtil.saveString(f, value.toString(1, ""));
	}
	@Override public Struct readFromStorage(String key) {
		return new SimpleProps(new File(dir, key));
	}
	@Override public void updateInStorage(T oldValue, T newValue) {
		// The newValue may contain an id, but that is ignored
		String oldId = getSchema().getKeyField().getObjectValue(oldValue);
		File f = new File(dir, oldId);
		FileUtil.saveString(f, newValue.toString(1,""));
	}
	@Override public void deleteInStorage(T oldValue)  {
		checkForConcurrentModification(oldValue);
		getFile(oldValue).delete();
	}
	private void checkForConcurrentModification(T obj) {
		// TODO Auto-generated method stub
		
	}
	private File getFile(T obj) { return new File(getKey(obj));}
}
