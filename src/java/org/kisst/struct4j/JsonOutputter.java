package org.kisst.struct4j;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.kisst.util.StringUtil;

public class JsonOutputter {
	private final String indentString;
	public JsonOutputter(String indent) { this.indentString=indent; }

	public String createString(Struct struct) {
		StringWriter out=new StringWriter();
		write(new PrintWriter(out), struct);
		return out.toString();
	}

	public void write(PrintWriter out, Struct struct) { write(out,struct,""); }
	public void write(PrintWriter out, Struct struct, String currentIndent) {
		boolean firstElement=true;
		for (String name: struct.fieldNames()) {
			Object value=struct.getObject(name,null);
			if (value==null)
				continue;
			if (! firstElement) {
				if (indentString==null)
					out.write(',');
				else
					out.write(",\n");
			}
			firstElement=false;
			out.print('"'+name+"\" : ");
			if (value instanceof Struct) {
				if (indentString==null)
					write(out, (Struct) value, currentIndent);
				else {
					//out.write(" ");
					write(out, (Struct) value, currentIndent+indentString);

				}
			}
			else {
				if (value instanceof Number)
					out.write(value.toString());
				else
					out.write(StringUtil.doubleQuotedString(value.toString()));
			}
		}
		out.write('}');
	}

}
