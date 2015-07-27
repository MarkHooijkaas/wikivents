package org.kisst.http4j.handlebar;

import java.util.List;

import org.kisst.item4j.Schema;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public class FormData {
	public final Struct record;
	
	public FormData(Struct record) { this.record=record; }

	public class Field {
		public final String name;
		public final Object value;
		public final String message;
		public Field(String name) {
			this.name=name.trim();
			this.value=(record==null) ? null : calcValue();
			this.message=(value==null)  ? null : validate();
		}
		public String validate() {return null;}
		protected Object calcValue() {
			if (record==null)
				return null;
			return record.getObject(name, null);
		}
	}
	public List<Field> fields() { return ReflectionUtil.getAllDeclaredFieldValuesOfType(this, Field.class); }
	public boolean isValid() { 
		for (Field f: fields()) {
			if (f.message!=null)
				return false;
		}
		return true;
	}

	public class StringField extends Field {
		public final String stringValue;
		public StringField(String name) { super(name); this.stringValue=(String) value;}
		public StringField(Schema<?>.Field field) { this(field.getName()); }
		@Override protected Object calcValue() { 
			if (record==null) return null;
			return record.getString(name, null);
		}
	}
	public class EmailField extends StringField {	
		public EmailField(String name) { super(name); }
		public EmailField(Schema<?>.Field field) { super(field); }
		@Override public String validate() {
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
	}
	public class PasswordField extends StringField {	
		public PasswordField(String name) { super(name); }
		@Override public String validate() {
			if (stringValue.length()<2)
				return "password should be longer";
			return null;
		}
	}
	public class DateField extends Field {
		public DateField(String name) { super(name);}
		public DateField(Schema<?>.Field field) { this(field.getName()); }
	}
}
