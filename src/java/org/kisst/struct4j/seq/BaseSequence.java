package org.kisst.struct4j.seq;

import org.kisst.struct4j.Struct;


public abstract class BaseSequence implements Sequence {	
	@Override public String getString(int index) { return getObject(index).toString(); }
	@Override public int getInt(int index) {
		Object obj=getObject(index);
		if (obj instanceof Integer)
			return (Integer) obj;
		return Integer.parseInt(obj.toString());
	}

	@Override public long getLong(int index) {
		Object obj=getObject(index);
		if (obj instanceof Long)
			return (Long) obj;
		if (obj instanceof Integer)
			return (Integer) obj;
		return Long.parseLong(obj.toString());
	}
	@Override public boolean getBoolean(int index) {
		Object obj=getObject(index);
		if (obj instanceof Boolean)
			return (Boolean) obj;
		return Boolean.parseBoolean(obj.toString());
	}
	public Struct getStruct(int index) { return (Struct) getObject(index); }
	public Sequence getSequence(int index) { return (Sequence) getObject(index); }

}
