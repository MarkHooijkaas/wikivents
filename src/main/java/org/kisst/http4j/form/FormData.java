package org.kisst.http4j.form;

import java.util.ArrayList;
import java.util.List;

import org.kisst.item4j.HasName;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public class FormData  {
	public final Struct record;

	public FormData(Struct record) { this.record=record; }
	public Iterable<String> fieldNames() {
		ArrayList<String> result=new ArrayList<>();
		for (InputField f : fields()) 
			result.add(f.name);
		return result;
	}
	public Object getDirectFieldValue(String name) {
		java.lang.reflect.Field fld = ReflectionUtil.getFieldOrNull(this.getClass(), name);
		if (fld==null)
			return Struct.UNKNOWN_FIELD;
		return ((InputField) ReflectionUtil.getFieldValue(this, fld)).value;
	}

	public List<InputField> fields() { return ReflectionUtil.getAllDeclaredFieldValuesOfType(this, InputField.class); }
	public boolean isValid() { 
		for (InputField f: fields()) {
			if (f.message!=null)
				return false;
		}
		return true;
	}

	//@Override public String toString() { return toString(1,null); }
	private static String staticCalcValue(Struct rec, String name) {
		if (rec==null)
			return null;
		return Item.asString(rec.getDirectFieldValue(name));
	}
	
	@FunctionalInterface public interface Validator { public String validate(InputField field); } 

	public class InputField {
		public final String name;
		public final String value;
		public final String message;
		public InputField(HasName name, Validator ... validators) { this(name.getName(), validators); }
		public InputField(String name, Validator ... validators) { this(name, (record==null) ? null : staticCalcValue(record, name), validators); }
		public InputField(String name, String value, Validator ... validators) {
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
			return record.getDirectFieldValue(name);
		}
	}

	public String notEmpty(InputField field) {
		if (field.value==null || field.value.trim().length()==0)
			return "should not be empty";
		return null;
	}

	public String validateEmail(InputField field) {
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

	public String validateStrongPassword(InputField field) {
		if (field.value==null)
			return null;
		String stringValue = Item.asString(field.value);
		if (stringValue.length()<2)
			return "password should be longer";
		return null;
	}
}
