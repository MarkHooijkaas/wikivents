package org.kisst.crud4j;

import java.util.LinkedHashMap;

import org.kisst.struct4j.Struct;

public class CrudSchema<T extends CrudObject> {
	private final LinkedHashMap<String, Field > fields=new LinkedHashMap<String, Field>();
	public final Class<?> cls;
	public final IdField _id = new IdField();

	public CrudSchema(Class<?> cls) { this.cls=cls; }
	public Field getField(String name) {return fields.get(name); }

	public class Field {
		public final String name;
		public final boolean optional;
		private final Object defaultValue;
		public Field(String name, boolean optional, Object defaultValue) {
			this.name=name;
			this.optional=optional;
			this.defaultValue=defaultValue;
		}
		public Object getValue(Struct s) { 
			if (optional)
				return s.getObject(name,defaultValue);
			else
				return s.getObject(name);
		}
	}
	public class StringField extends Field {
		public StringField(String name, boolean optional, String defaultValue) {
			super(name, optional, defaultValue);
		}
		public String getString(Struct s) { return (String) getValue(s); }
	}
	public class IdField extends Field {
		public IdField() { this("_id"); }
		public IdField(String name) { super(name, false, null); }
		public String getId(Struct s) { 
			String id = s.getString(name,null);
			if (id==null)
				return uniqueKey();
			return id;
		}
	}
	private static int keycount=0;
	private static String uniqueKey() {
		String tmp;
		synchronized (CrudSchema.class) {
			keycount=(keycount+1) % 1000;
			tmp=""+keycount;
		}
		return ""+System.currentTimeMillis()+tmp;
	}
	
	public class IntField extends Field {
		public IntField(String name, boolean optional, int defaultValue) {
			super(name, optional, defaultValue);
		}
		public int getInt(Struct s) { return (Integer) getValue(s); }
	}
	public class LongField extends Field {
		public LongField(String name, boolean optional, long defaultValue) {
			super(name, optional, defaultValue);
		}
		public long getLong(Struct s) { return (Long) getValue(s); }
	}
	public class RefField<RT extends CrudObject> extends Field {
		public RefField(String name, boolean optional) {
			super(name, optional, null);
		}
		public CrudTable<RT>.Ref getRef(CrudTable<RT> table, Struct s) { 
			return table.getRef(s.getString(name));
		}
	}
}
