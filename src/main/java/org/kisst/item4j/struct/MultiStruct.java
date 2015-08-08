package org.kisst.item4j.struct;

import java.util.HashSet;

public class MultiStruct implements Struct {
	private final Struct[] structs;
	
	public MultiStruct(Struct... structs) { 
		this.structs=structs;
		for (Struct struct: structs) {
			if (struct==null)
				throw new NullPointerException(toString(MultiStruct.class, structs)+" has null elements");
		}
	}

	@Override public String toString() { return toString(this.getClass(), structs); }
	private static String toString(Class<?> cls, Struct[] structs) {
		StringBuilder result=new StringBuilder(cls.getSimpleName());
		result.append('(');
		String sep="";
		for (Struct struct: structs) {
			result.append(sep);
			result.append(struct+"");
			sep=",";
		}
		result.append(')');
		return result.toString();
	}

	/*
	@Override public boolean hasField(String path) {
		for (Struct struct: structs) {
			if (struct.hasField(path))
				return true;
		}
		return false;
	}
	*/

	@Override public Iterable<String> fieldNames() {
		HashSet<String> result= new HashSet<String>();
		for (Struct struct: structs) {
			for (String key: struct.fieldNames())
				result.add(key);
		}
		return result;
	}
	
	@Override public Object getDirectFieldValue(String name) {
		for (Struct struct: structs) {
			Object result=struct.getDirectFieldValue(name);
			if (result!=UNKNOWN_FIELD && result!=null)
				return result;
		}
		return UNKNOWN_FIELD;
	}
	
	/*
	@Override public Object getObject(String path) {
		boolean fieldFound = false;
		for (Struct struct: structs) {
			if (struct.hasField(path)) {
				fieldFound=true;
				Object result=struct.getObject(path, DUMMY);
				if (result!=DUMMY)
					return result;
			}
		}
		if (fieldFound)
			throw new FieldHasNullValueException(this, path);
		throw new UnknownFieldException(this,path);
	}
	*/
}
