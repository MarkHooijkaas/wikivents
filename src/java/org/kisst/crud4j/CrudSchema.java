package org.kisst.crud4j;

import java.util.LinkedHashMap;

import org.kisst.struct4j.HashStruct;
import org.kisst.struct4j.Struct;

public class CrudSchema<T extends CrudObject> {
	private final LinkedHashMap<String, Field<?> > fields=new LinkedHashMap<String, Field<?>>();
	public final Class<?> cls;
	public final StringField _id = new StringField("_id",false, null);
	
	public CrudSchema(Class<?> cls) { this.cls=cls; }
	@Override public String toString() { return cls.getSimpleName(); }
	public final StringField getKeyField() { return _id; }
	public Field<?> getField(String name) {return fields.get(name); }

	public class Field<FT> {
		public final String name;
		public final boolean optional;
		private final FT defaultValue;
		public Field(String name, boolean optional, FT defaultValue) {
			this.name=name;
			this.optional=optional;
			this.defaultValue=defaultValue;
		}
		@SuppressWarnings("unchecked")
		public FT getValue(Struct s) { 
			if (optional)
				return (FT) s.getObject(name,defaultValue);
			else
				return (FT) s.getObject(name);
		}
		public void setValue(HashStruct doc, FT value) { doc.put(name, value); } 
	}
	public class StringField extends Field<String> {
		public StringField(String name, boolean optional, String defaultValue) {
			super(name, optional, defaultValue);
		}
		public String getString(Struct s) { return getValue(s); }
	}
	
	public class IntField extends Field<Integer> {
		public IntField(String name, boolean optional, int defaultValue) {
			super(name, optional, defaultValue);
		}
		public int getInt(Struct s) { return getValue(s); }
	}
	public class LongField extends Field<Long> {
		public LongField(String name, boolean optional, long defaultValue) {
			super(name, optional, defaultValue);
		}
		public long getLong(Struct s) { return getValue(s); }
	}
	public class RefField<RT extends CrudObject> extends Field<RT> {
		public RefField(String name, boolean optional) {
			super(name, optional, null);
		}
		public RT get(CrudTable<RT> table, Struct s) { 
			return table.read(s.getString(name));
		}
	}
}
