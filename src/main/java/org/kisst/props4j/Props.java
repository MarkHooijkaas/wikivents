/**
Copyright 2008, 2009 Mark Hooijkaas

This file is part of the RelayConnector framework.

The RelayConnector framework is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The RelayConnector framework is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with the RelayConnector framework.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.kisst.props4j;

import org.kisst.item4j.Immutable;
import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.Item;
import org.kisst.item4j.seq.ItemSequence;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.item4j.struct.StructProps;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;



public interface Props extends Struct {
	public final static StructProps EMPTY_PROPS=new StructProps(Struct.EMPTY);

	public Props getParent();
	public String getLocalName();
	default public String getFullName() {
		if (getParent()==null)
			return getLocalName();
		else {
			String prefix=getParent().getFullName();
			if (prefix==null)
				return getLocalName();
			else
				return prefix+"."+getLocalName();
		}
	}


	default public boolean hasField(String path) { return getObject(path, UNKNOWN_FIELD)==UNKNOWN_FIELD; }

	default public Object getObject(String path) {
		Object value=getObject(path,UNKNOWN_FIELD);
		if (value==UNKNOWN_FIELD)
			throw new UnknownFieldException(this,path);
		if (value==null)
			throw new FieldHasNullValueException(this,path);
		return value;
	}


	default public Object getObject(String path, Object defaultValue) {
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
		if (value instanceof StructProps)
			return ((StructProps) value).getObject(remainder);
		if (value instanceof Struct)
			return new StructProps((Struct) value, this, path).getObject(remainder); // TODO: path is not name
		return new StructProps(new ReflectStruct(value), this, path).getObject(remainder); // TODO: path is not name
	}



	default public String getString(String path, String defaultValue) {
		Object obj= getObject(path,null);
		if (obj==null) return defaultValue;
		return Item.asString(obj);
	}
	default public String[] getStringArray(String path) {
		return getStringArray(path, null);
	}
	default public String[] getStringArray(String path, String[] defaultValue) {
		String value = getString(path, null);
		return value == null ? defaultValue : value.split(",");
	}
	default public int getInteger(String path, int defaultValue) {
		Object obj=getObject(path,null);
		if (obj==null) return defaultValue;
		return Item.asInteger(obj);
	}
	default public long getLong(String path, long defaultValue) {
		Object obj= getObject(path,null);
		if (obj==null) return defaultValue;
		return Item.asLong(obj);
	}
	default public boolean getBoolean(String path, boolean defaultValue) {
		Object obj= getObject(path,null);
		if (obj==null) return defaultValue;
		return Item.asBoolean(obj);
	}
	default public LocalDate getLocalDate(String path, LocalDate defaultValue) {
		Object obj= getObject(path,null);
		if (obj==null) return defaultValue;
		return Item.asLocalDate(obj);
	}
	default public LocalTime getLocalTime(String path, LocalTime defaultValue) {
		Object obj= getObject(path,null);
		if (obj==null) return defaultValue;
		return Item.asLocalTime(obj);
	}
	default public LocalDateTime getLocalDateTime(String path, LocalDateTime defaultValue) {
		Object obj= getObject(path,null);
		if (obj==null) return defaultValue;
		return Item.asLocalDateTime(obj);
	}
	default public Instant getInstant(String path, Instant defaultValue) {
		Object obj= getObject(path,null);
		if (obj==null) return defaultValue;
		return Item.asInstant(obj);
	}
	default public<T> T getType(Item.Factory factory, Class<?> cls, String path, T defaultValue) {
		Object obj= getObject(path,null);
		if (obj==null) return defaultValue;
		return Item.asType(factory, cls, obj);
	}
	default public Struct getStruct(String path, Struct defaultValue) {
		Object obj= getObject(path,null);
		if (obj==null) return defaultValue;
		return Item.asStruct(obj);
	}
	default public ItemSequence getSequence(String path, ItemSequence defaultValue) {
		Object obj= getObject(path,null);
		if (obj==null) return defaultValue;
		return Item.asItemSequence(obj);
	}
	default public<T> ImmutableSequence<T> getTypedSequence(Item.Factory factory, Class<?> type, String path, ImmutableSequence<T> defaultValue) {
		Object obj= getObject(path,null);
		if (obj==null) return defaultValue;
		return Item.asTypedSequence(factory, type, getObject(path));
	}
	default public<T> ImmutableSequence<T> getTypedSequenceOrEmpty(Item.Factory factory, Class<T> type, String path) {
		return Item.cast(getTypedSequence(factory, type, path, ImmutableSequence.EMPTY));
	}


	default public String toShortString() { return toString(1, ""); }
	default public String toString(int levels, String indent) {
		if (levels==0)
			return "{...}";
		StringBuilder result=new StringBuilder("{");
		for (String name: fieldNames()) {
			Object value=this.getDirectFieldValue(name);
			result.append(name+":");
			if (value instanceof StructProps) {
				if (indent==null)
					result.append(((StructProps)value).toString(levels-1, indent+"\t"));
				else
					result.append(((StructProps)value).toString(levels-1, null));
			}
			else
				result.append(""+value);
			result.append(';');
		}
		result.append('}');
		return result.toString();
	}

	default public Props getPropsOrEmpty(String path) { return getProps(path, EMPTY_PROPS); }


	default public String getString(String path) { return Item.asString(getObject(path)); }
	default public int getInteger(String path) { return Item.asInteger(getObject(path)); }
	default public long getLong(String path) { return Item.asLong(getObject(path)); }
	default public boolean getBoolean(String path) { return Item.asBoolean(getObject(path)); }
	default public LocalDate getLocalDate(String path) { return Item.asLocalDate(getObject(path)); }
	default public LocalTime getLocalTime(String path) { return Item.asLocalTime(getObject(path)); }
	default public LocalDateTime getLocalDateTime(String path) { return Item.asLocalDateTime(getObject(path)); }
	default public Instant getInstant(String path) { return Item.asInstant(getObject(path)); }
	default public<T> T getType(Item.Factory factory, Class<?> cls, String path) { return Item.asType(factory, cls, getObject(path)); }
	default public Struct getStruct(String path) { return Item.asStruct(getObject(path)); }
	default public Immutable.ItemSequence getItemSequence(String path) { return Item.asItemSequence(getObject(path)); }
	default public<T> ImmutableSequence<T> getTypedSequence(Item.Factory factory, Class<?> type, String path) { return Item.asTypedSequence(factory, type, getObject(path)); }

	default public Props getProps(String path) {
		Object obj= getObject(path);
		if (obj instanceof Props)
			return (Props) obj;
		return new StructProps(Item.asStruct(obj), this, path); // TODO: path is not name;
	}
	default public Props getProps(String path, Props defaultValue) {
		Object obj= getObject(path,null);
		if (obj==null) return defaultValue;
		if (obj instanceof Props)
			return (Props) obj;
		return new StructProps(Item.asStruct(obj), this, path); // TODO: path is not name;
	}
}
