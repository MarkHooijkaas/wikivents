package org.kisst.struct4j;

public abstract class BaseStruct implements Struct {
	protected static Object UNKNOWN_FIELD=new Object();

	@Override abstract public Iterable<String> fieldNames();	
	abstract public Object getDirectFieldValue(String name);

	
	@Override public boolean hasField(String path) { return getObject(path, UNKNOWN_FIELD)==UNKNOWN_FIELD; }

	
	@Override public Object getObject(String path, Object defaultValue) {
		String name=path;
		String remainder=null;
		int pos=path.indexOf('.');
		if (pos>0) {
			name=path.substring(0,pos);
			remainder=path.substring(pos+1);
		}
		Object value=getDirectFieldValue(name);
		if (value==null)
			return defaultValue;
		if (remainder==null)
			return value;
		if (value instanceof Struct)
			return ((Struct) value).getObject(remainder);
		return new ReflectStruct(value).getObject(remainder);
	}
	
	@Override public Object getObject(String path) {
		Object value=getObject(path,UNKNOWN_FIELD);
		if (value==UNKNOWN_FIELD)
			throw new UnknownFieldException(this,path);
		if (value==null)
			throw new FieldHasNullValueException(this,path);
		return value;
	}


	@Override public String getString(String path, String defaultValue) {
		Object obj=getObject(path,null);
		if (obj==null)
			return defaultValue;
		return obj.toString();
	}


	@Override public int getInt(String path, int defaultValue) {
		Object obj=getObject(path,null);
		if (obj==null)
			return defaultValue;
		if (obj instanceof Integer)
			return (Integer) obj;
		return Integer.parseInt(obj.toString());
	}

	@Override public long getLong(String path, long defaultValue) {
		Object obj=getObject(path,null);
		if (obj==null)
			return defaultValue;
		if (obj instanceof Long)
			return (Long) obj;
		return Long.parseLong(obj.toString());
	}

	@Override public boolean getBoolean(String path, boolean defaultValue) {
		Object obj=getObject(path,null);
		if (obj==null)
			return defaultValue;
		if (obj instanceof Boolean)
			return (Boolean) obj;
		return Boolean.parseBoolean(obj.toString());
	}


	@Override public String getString(String path) { return getObject(path).toString(); }
	@Override public int getInt(String path) {
		Object obj=getObject(path);
		if (obj instanceof Integer)
			return (Integer) obj;
		return Integer.parseInt(obj.toString());
	}

	@Override public long getLong(String path) {
		Object obj=getObject(path);
		if (obj instanceof Long)
			return (Long) obj;
		if (obj instanceof Integer)
			return (Integer) obj;
		return Long.parseLong(obj.toString());
	}
	@Override public boolean getBoolean(String path) {
		Object obj=getObject(path);
		if (obj instanceof Boolean)
			return (Boolean) obj;
		return Boolean.parseBoolean(obj.toString());
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
