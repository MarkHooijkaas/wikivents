package org.kisst.crud4j;

import java.io.File;

import org.kisst.props4j.SimpleProps;
import org.kisst.struct4j.ReflectStruct;
import org.kisst.struct4j.Struct;
import org.kisst.util.FileUtil;

public abstract class CrudFileTable<T> extends CrudTable<T> {
	private final File dir;
	public CrudFileTable(CrudSchema<T> schema) { 
		super(schema);
		dir=new File("data/"+schema.cls.getSimpleName());
		if (! dir.exists())
			dir.mkdirs();
	}

	@Override public T load(String key) {
		SimpleProps props=new SimpleProps(new File(dir, key));
		return create(props);
	}

	@Override public void save(String key, T value) {
		File f = new File(dir, key);
		FileUtil.saveString(f, new ReflectStruct(value).toString());
	}

	abstract protected T create(Struct  props);

}
