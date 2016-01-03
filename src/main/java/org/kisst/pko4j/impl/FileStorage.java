package org.kisst.pko4j.impl;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jgit.api.Git;
import org.kisst.item4j.Item;
import org.kisst.item4j.json.JsonOutputter;
import org.kisst.item4j.json.JsonParser;
import org.kisst.item4j.seq.ArraySequence;
import org.kisst.item4j.seq.TypedSequence;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.SingleItemStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.PkoObject;
import org.kisst.pko4j.StructStorage;
import org.kisst.props4j.Props;
import org.kisst.util.CallInfo;
import org.kisst.util.FileUtil;

public class FileStorage implements StructStorage {
	private final File dir;
	private final String name;
	private final boolean useCache;
	private final JsonParser parser=new JsonParser();
	private final JsonOutputter outputter = new JsonOutputter(null);
	private final Class<?> cls;
	private final Git git;
	//private final Repository gitrepo;


	public FileStorage(Class<? extends PkoObject> cls, Git git, File maindir, boolean useCache) {
		this.cls=cls;
		this.useCache=useCache;
		this.name=cls.getSimpleName();
		dir=new File(maindir,name);
		if (! dir.exists())
			dir.mkdirs();
		//loadAllRecords();
		this.git=git;
	}
	public FileStorage(Class<? extends PkoObject> cls, Props props, Git git) {
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
		File dir = getDir(key);
		if (! dir.exists())
			dir.mkdirs();
		FileUtil.saveString(f, outputter.createString(value));
		gitCommit("create"+name,key);
		return key;
	}
	private static AtomicInteger number=new AtomicInteger(new Random().nextInt(13));
	private String createUniqueKey() {
		int i=number.incrementAndGet();
		return Long.toHexString(System.currentTimeMillis())+Integer.toHexString(i);
	}
	
	private Struct createStruct(File f) {
		Struct result = new MultiStruct(
				parser.parse(f),
				new SingleItemStruct("savedModificationDate", Instant.ofEpochMilli(f.lastModified()))
				);
		//System.out.println(result);
		return result;
	}
	
	@Override public Struct read(String key) {
		File f = getFile(key);
		return createStruct(f);
	}
	@Override public void update(Struct oldValue, Struct newValue) {
		// The newValue may contain an id, but that is ignored
		String oldId = getKey(oldValue);
		File f = getFile(oldId);
		FileUtil.saveString(f, outputter.createString(newValue));
		//git.add().addFilepattern(name+"/"+f.getName()).call();
		gitCommit("update"+name,oldId);
	}
	@Override public void delete(Struct oldValue)  {
		checkForConcurrentModification(oldValue);
		getFile(oldValue).delete();
		gitCommit("delete"+name,getKey(oldValue));
	}
	private void checkForConcurrentModification(Struct obj) {
		// TODO Auto-generated method stub

	}
	private File getDir(String key) { return new File(dir, key+".dir"); }
	private File getFile(String key, String path) { return new File(dir, key+".dir/"+path); }
	private File getFile(String key) { return getFile(key, "record.dat"); }
	private File getFile(Struct obj) { return getFile(getKey(obj));}

	@Override public TypedSequence<Struct> findAll() {
		ArrayList<Struct> list=new ArrayList<Struct>();
		long start= System.currentTimeMillis();
		//System.out.println("loading all records from "+name);
		int count=0;
		for (File f:dir.listFiles()) {
			try {
				String key=f.getName();
				if (! (key.endsWith(".dir") && f.isDirectory()))
					continue;
				f=new File(f,"record.dat");
				count++;
				key=key.substring(0,key.length()-4);

				Struct doc=createStruct(f);
				list.add(doc);
			}
			catch (Exception e) { e.printStackTrace();}// TODO: return dummy placeholder
		}
		System.out.println("DONE loading "+count+" records from "+name+" in "+(System.currentTimeMillis()-start)+" milliseconds");
		return new ArraySequence<Struct>(Struct.class,list);
	}


	private void gitCommit(String action, String data) {
		try {
			if (git==null)
				return;
			CallInfo callinfo = CallInfo.instance.get();
			if (callinfo.action!=null)
				action=callinfo.action;
			if (callinfo.data!=null)
				data=callinfo.data;
			String comment = action+" by "+callinfo.user+" on "+data;
			synchronized(git) {				
				git.add().addFilepattern(".").call();
				git.commit().setMessage(comment).call();
			}
		}
		catch (Exception e) { throw new RuntimeException(e); }
	}

	public String readBlob(String key, String path) {
		File f = getFile(key, path);
		return FileUtil.loadString(f);
	}
	public void writeBlob(String key, String path, String blob) {
		File f = getFile(key, path);
		FileUtil.saveString(f, blob);
	}
	public void appendBlob(String key, String path, String blob) {
		File f = getFile(key, path);
		FileUtil.appendString(f, blob);
	}
}
