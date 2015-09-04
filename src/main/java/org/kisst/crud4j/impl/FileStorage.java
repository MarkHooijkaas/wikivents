package org.kisst.crud4j.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jgit.api.Git;
import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.StructStorage;
import org.kisst.item4j.Item;
import org.kisst.item4j.json.JsonOutputter;
import org.kisst.item4j.json.JsonParser;
import org.kisst.item4j.seq.ArraySequence;
import org.kisst.item4j.seq.TypedSequence;
import org.kisst.item4j.struct.Struct;
import org.kisst.props4j.Props;
import org.kisst.util.FileUtil;

public class FileStorage implements StructStorage {
	private final File dir;
	private final String name;
	private final boolean useCache;
	private final JsonParser parser=new JsonParser();
	private final JsonOutputter outputter = new JsonOutputter(null);
	//private final CrudSchema<?>.IdField keyField;
	private final Class<?> cls;
	private final Git git;
	//private final Repository gitrepo;


	public FileStorage(Class<? extends CrudObject> cls, Git git, File maindir, boolean useCache) {
		this.cls=cls;
		this.useCache=useCache;
		this.name=cls.getSimpleName();
		dir=new File(maindir,name);
		if (! dir.exists())
			dir.mkdirs();
		//loadAllRecords();
		this.git=git;
	}
	public FileStorage(Class<? extends CrudObject> cls, Props props, Git git) {
		this(cls, git, new File(props.getString("datadir", "data")),props.getBoolean("useCache",true)); 
	}
	@Override public Class<?> getRecordClass() { return cls; }
	private String getKey(Struct record) { return Item.asString(record.getDirectFieldValue("_id")); }

	@Override public boolean useCache() { return useCache;}

	@Override public String create(Struct value) {
		String key = getKey(value);
		if (key==null)
			key=createUniqueKey();
		File f = getFile(key);
		if (f.exists())
			throw new RuntimeException("File "+f.getAbsolutePath()+" already exists");
		FileUtil.saveString(f, outputter.createString(value));
		gitCommit("created "+name+" "+key);
		return key;
	}
	private static AtomicInteger number=new AtomicInteger(new Random().nextInt(13));
	private String createUniqueKey() {
		int i=number.incrementAndGet();
		return Long.toHexString(System.currentTimeMillis())+Integer.toHexString(i);
	}
	@Override public Struct read(String key) {
		System.out.println("Reading "+key);
		return parser.parse(getFile(key));
	}
	@Override public void update(Struct oldValue, Struct newValue) {
		// The newValue may contain an id, but that is ignored
		String oldId = getKey(oldValue);
		File f = getFile(oldId);
		FileUtil.saveString(f, outputter.createString(newValue));
		//git.add().addFilepattern(name+"/"+f.getName()).call();
		gitCommit("updated "+name+" "+oldId);
	}
	@Override public void delete(Struct oldValue)  {
		checkForConcurrentModification(oldValue);
		getFile(oldValue).delete();
		gitCommit("deleted "+name+" "+getKey(oldValue));
	}
	private void checkForConcurrentModification(Struct obj) {
		// TODO Auto-generated method stub

	}
	private File getFile(String key) { return new File(dir, key+".rec"); }
	private File getFile(Struct obj) { return getFile(getKey(obj));}

	@Override public TypedSequence<Struct> findAll() {
		ArrayList<Struct> list=new ArrayList<Struct>();
		long start= System.currentTimeMillis();
		//System.out.println("loading all records from "+name);
		int count=0;
		for (File f:dir.listFiles()) {
			try {
				String key=f.getName();
				if (! key.endsWith(".rec"))
					continue;
				count++;
				key=key.substring(0,key.length()-4);

				Struct doc=parser.parse(f);
				list.add(doc);
			}
			catch (Exception e) { e.printStackTrace();}// TODO: return dummy placeholder
		}
		System.out.println("DONE loading "+count+" records from "+name+" in "+(System.currentTimeMillis()-start)+" milliseconds");
		return new ArraySequence<Struct>(Struct.class,list);
	}


	private void gitCommit(String comment) {
		try {
			if (git==null)
				return;
			synchronized(git) {
				git.add().addFilepattern(".").call();
				git.commit().setMessage(comment).call();
			}
		}
		catch (Exception e) { throw new RuntimeException(e); }
	}

}
