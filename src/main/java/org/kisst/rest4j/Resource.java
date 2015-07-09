package org.kisst.rest4j;

import org.kisst.item4j.seq.Sequence;
import org.kisst.item4j.struct.Struct;

public interface Resource {
	public String createResource(Struct doc);
	public Sequence<Struct> getResources(String[] filters);
	public Struct getSingleResource(String key);
	public void updateResource(String key, Struct newData);
	public void deleteResource(String key);
}
