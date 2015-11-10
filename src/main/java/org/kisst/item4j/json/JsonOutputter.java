package org.kisst.item4j.json;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.kisst.item4j.Item;
import org.kisst.item4j.seq.ItemSequence;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.StringUtil;

public class JsonOutputter {
	//private final String indentString;
	public JsonOutputter(String indent) { }//this.indentString=indent; }

	public String createString(Object obj) {
		StringWriter out=new StringWriter();
		write(new PrintWriter(out), obj);
		return out.toString();
	}

	public void write(PrintWriter out, Struct struct) { write(out,struct,""); }
	public void write(PrintWriter out, Struct struct, String currentIndent) {
		boolean firstElement=true;
		out.write('{');
		for (String name: struct.fieldNames()) {
			Object value=struct.getDirectFieldValue(name);
			if (value==null)
				continue;
			if (! firstElement)
				out.write(",\n");
			firstElement=false;
			out.print("\""+name+"\":");
			write(out,value);
		}
		out.write('}');
	}
	public void write(PrintWriter out, ItemSequence seq) {
		out.write('[');
		boolean firstElement=true;
		for (int i=0; i<seq.size(); i++) {
			Item value=seq.getItem(i);
			if (! firstElement)
				out.write(",\n");
			
			firstElement=false;
			write(out,value);
		}
		out.write(']');
	}
	public void write(PrintWriter out, Iterable<?> seq) {
		out.write('[');
		boolean firstElement=true;
		for (Object obj: seq) {
			if (! firstElement)
				out.write(",\n");
			firstElement=false;
			write(out,obj);
		}
		out.write(']');
	}

	
	public void write(PrintWriter out, Object value) {
		//System.out.println(value.getClass());
		if (value==null)
			return;
		if (value instanceof Item)
			value=((Item) value).asObject();
		if (value instanceof Struct)
			write(out,(Struct) value);
		else if (value instanceof ItemSequence) {
			write(out,(ItemSequence) value);
		}
		else if (value instanceof Iterable) {
			write(out,(Iterable<?>) value);
		}
		else {
			if (value instanceof Number)
				out.write(value.toString());
			else
				out.write(StringUtil.doubleQuotedString(value.toString()));
		}
	}
}
