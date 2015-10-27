package org.kisst.util;

import java.text.Normalizer;

import org.kisst.item4j.struct.Struct;
import org.kisst.item4j.struct.StructHelper;

public class StringUtil {

	public static String capitalize(String name) {
		if (name==null || name.length()==0 || Character.isUpperCase(name.charAt(0)))
			return name;
		if (name.length()==1)
			return name.substring(0,1).toUpperCase();
		return name.substring(0,1).toUpperCase()+name.substring(1); 
	}

	public static String quotedName(String name) {
		if (name.indexOf(' ')>=0 || name.indexOf('.')>=0)
			return '"'+name+'"';
		else
			return name;
	}
	
	public static String urlify(String s) {
		//s=s.replaceAll("[\"']+", "");
		//s=s.replaceAll("[;:]+", ".");
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("[^\\p{ASCII}]", "-");
		s = s.replaceAll("[&]+", "+");
		s = s.replaceAll("[^a-zA-Z0-9$_+.]+", "-");
		s = s.replaceAll("[+-]*\\+[+-]*", "+");
		return s;
	}

	public static String substitute(String str, Struct vars) {
		StringBuilder result = new StringBuilder();
		int prevpos=0;
		int pos=str.indexOf("${");
		while (pos>=0) {
			int pos2=str.indexOf("}", pos);
			if (pos2<0)
				throw new RuntimeException("Unbounded ${ starting with "+str.substring(pos,pos+10));
			String key=str.substring(pos+2,pos2);
			result.append(str.substring(prevpos,pos));
			Object value=StructHelper.getObject(vars,key,null);
			if (value==null && key.equals("dollar"))
				value="$";
			if (value==null)
				throw new RuntimeException("Unknown variable ${"+key+"}");
			result.append(value.toString());
			prevpos=pos2+1;
			pos=str.indexOf("${",prevpos);
		}
		result.append(str.substring(prevpos));
		return result.toString();
	}
	
	public static String doubleQuotedString(String str) {
		StringBuilder result= new StringBuilder("\""); 
		for (int i=0; i<str.length(); i++) {
			char ch=str.charAt(i);
			if (ch=='\n') { result.append("\\n"); continue; }
			if (ch=='\r') { result.append("\\r"); continue; }
			if (ch=='\t') { result.append("\\t"); continue; }
			if (ch=='"' || ch=='\\' || ch=='$') result.append('\\');
			result.append(ch);
		}
		result.append('"');
		return result.toString();
	}

	public static String singleQuotedString(String str) {
		StringBuilder result= new StringBuilder("'"); 
		for (int i=0; i<str.length(); i++) {
			char ch=str.charAt(i);
			if (ch=='\n') { result.append("\\n"); continue; }
			if (ch=='\r') { result.append("\\r"); continue; }
			if (ch=='\t') { result.append("\\t"); continue; }
			if (ch=='\'' || ch=='\\' ) result.append('\\');
			result.append(ch);
		}
		result.append("'");
		return result.toString();
	}
}
