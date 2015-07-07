package org.kisst.rest4j;

import org.kisst.struct4j.Struct;
import org.kisst.struct4j.seq.Sequence;

public interface Resource {
	public void createResource(Struct doc);
	public Sequence<Struct> getResources(String[] filters);
	public Struct getSingleResource(String key);
	public void updateResource(String key, Struct newData);
	public void deleteResource(String key);
}
