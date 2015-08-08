package org.kisst.item4j.struct;

import org.kisst.props4j.Props;

public class StructProps implements Struct, Props {
	private final Struct data;
	private final Props parent;
	private final String name;
	
	public StructProps(Struct data, Props parent, String name) { this.data=data; this.parent=parent; this.name=name; }
	public StructProps(Struct data) { this(data,null, null);}

	@Override public Iterable<String> fieldNames() { return data.fieldNames(); }
	@Override public Object getDirectFieldValue(String name) { return data.getDirectFieldValue(name); }


	@Override public Props getParent() { return parent; }
	@Override public String getLocalName() { return name; }
}
