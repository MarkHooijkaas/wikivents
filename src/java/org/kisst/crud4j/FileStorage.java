package org.kisst.crud4j;

import java.io.File;

import org.kisst.props4j.SimpleProps;
import org.kisst.struct4j.ReflectStruct;
import org.kisst.struct4j.Struct;
import org.kisst.util.FileUtil;

public class FileStorage<T extends CrudObject> implements Storage<T> {
	private final File dir;
	private final CrudSchema<T> schema;
	public FileStorage(CrudSchema<T> schema, File maindir) {
		this.schema=schema;
		dir=new File(maindir,schema.cls.getSimpleName());
		if (! dir.exists())
			dir.mkdirs();
	}
	@Override public CrudSchema<T> getSchema() { return this.schema;}
	
	@Override public void createStorage(CrudObject value) {
		File f = new File(dir, value._id);
		FileUtil.saveString(f, new ReflectStruct(value).toString(1, ""));
	}
	@Override public Struct readStorage(String key) {
		return new SimpleProps(new File(dir, key));
	}
	@Override public void updateStorage(CrudObject oldValue, CrudObject newValue) {
		File f = new File(dir, newValue._id);
		FileUtil.saveString(f, new ReflectStruct(newValue).toString());
	}
	@Override public void deleteStorage(CrudObject oldValue)  {} // TODO
}
