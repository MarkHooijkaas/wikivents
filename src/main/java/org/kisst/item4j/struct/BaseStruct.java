package org.kisst.item4j.struct;


public abstract class BaseStruct implements Struct {
	abstract public Object getDirectFieldValue(String name);

	@Override abstract public Iterable<String> fieldNames();	
	
	@Override public Object getObject(String path, Object defaultValue) {
		String name=path;
		String remainder=null;
		int pos=path.indexOf('.');
		if (pos>0) {
			name=path.substring(0,pos);
			remainder=path.substring(pos+1);
		}
		Object value=getDirectFieldValue(name);
		if (value==null || value==UNKNOWN_FIELD)
			return defaultValue;
		if (remainder==null)
			return value;
		if (value instanceof Struct)
			return ((Struct) value).getObject(remainder);
		return new ReflectStruct(value).getObject(remainder);
	}
	
	@Override public String toString() { return toString(1,null); }
	public String toString(int levels, String indent) {
		if (levels==0)
			return "{...}";
		StringBuilder result=new StringBuilder("{");
		for (String name: fieldNames()) {
			Object value=this.getDirectFieldValue(name);
			result.append(name+":");
			if (value instanceof BaseStruct) {
				if (indent==null)
					result.append(((BaseStruct)value).toString(levels-1, indent+"\t"));
				else
					result.append(((BaseStruct)value).toString(levels-1, null));
			}
			else
				result.append(""+value);
			result.append(';');
		}
		result.append('}');
		return result.toString();
	}

}
