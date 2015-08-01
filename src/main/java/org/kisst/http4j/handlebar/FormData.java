package org.kisst.http4j.handlebar;

import java.util.ArrayList;
import java.util.List;

import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public class FormData implements Struct {
	public final Struct record;

	public FormData(Struct record) { this.record=record; }
	@Override public Iterable<String> fieldNames() {
		ArrayList<String> result=new ArrayList<>();
		for (Field f : fields()) 
			result.add(f.name);
		return result;
	}
	@Override public Object getDirectFieldValue(String name) {
		java.lang.reflect.Field fld = ReflectionUtil.getFieldOrNull(this.getClass(), name);
		if (fld==null)
			return UNKNOWN_FIELD;
		return ((Field) ReflectionUtil.getFieldValue(this, fld)).value;
	}

	public List<Field> fields() { return ReflectionUtil.getAllDeclaredFieldValuesOfType(this, Field.class); }
	public boolean isValid() { 
		for (Field f: fields()) {
			if (f.message!=null)
				return false;
		}
		return true;
	}

	@Override public String toString() { return toString(1,null); }
	private static Object staticCalcValue(Struct rec, String name) {
		if (rec==null)
			return null;
		return rec.getObject(name, null);
	}
	
	@FunctionalInterface public interface Validator { public String validate(Field field); } 

	public class Field {
		public final String name;
		public final Object value;
		public final String message;
		public Field(String name, Validator ... validators) { this(name, (record==null) ? null : staticCalcValue(record, name), validators); }
		public Field(String name, Object value, Validator ... validators) {
			//System.out.println("setting field "+name+" to value "+value);
			this.name=name.trim();
			this.value=value;
			for (Validator v: validators) {
				String msg=v.validate(this);
				if (msg!=null) {
					this.message=msg;
					return;
				}
			}
			this.message=null;
		}
		protected Object calcValue() {
			if (record==null)
				return null;
			return record.getObject(name, null);
		}
	}

	public String validateEmail(Field field) {
		if (field.value==null)
			return null;
		String stringValue = Item.asString(field.value);
		if (stringValue==null)
			return null; // TODO: should it be validated???
		int atpos=stringValue.indexOf('@');
		if (atpos<0)
			return "email address should contain @";
		int dotpos=stringValue.lastIndexOf('.');
		if (dotpos<atpos)
			return "email address domain should have a \".\"";
		return null;
	}

	public String validateStrongPassword(Field field) {
		if (field.value==null)
			return null;
		String stringValue = Item.asString(field.value);
		if (stringValue.length()<2)
			return "password should be longer";
		return null;
	}
}
