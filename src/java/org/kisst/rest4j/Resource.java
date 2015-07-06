package org.kisst.rest4j;

import org.kisst.struct4j.Struct;
import org.kisst.struct4j.seq.TypedSequence;

public interface Resource {
	public void createResource(Struct doc);
	public TypedSequence<Struct> getResources(String[] filters);
	public Struct getSingleResource(String key);
	public void updateResource(String key, Struct newData);
	public void deleteResource(String key);
}
