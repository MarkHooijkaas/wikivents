package org.kisst.struct4j.seq;

import org.kisst.struct4j.Struct;

public interface Sequence extends Iterable<Object>{
	public int size();
	
	public Object getObject(int index);
	public String getString(int index);
	public int getInt(int index);
	public long getLong(int index);
	public boolean getBoolean(int index);
	public Struct getStruct(int index);
	public Sequence getSequence(int index);
}
