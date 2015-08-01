package org.kisst.item4j.struct;

import org.kisst.item4j.Schema.Field;

public class VarargStruct extends HashStruct{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	public VarargStruct(Object ... objs) {
		if (objs.length % 2 !=0)
			throw new IllegalArgumentException("Should have even number of parameters");
		for (int i=0; i<objs.length; i+=2) {
			if (objs[i] instanceof Field)
				put(((Field)objs[i]).getName(), objs[i+1]);
			else
				throw new IllegalArgumentException("every even parameter should be a field");
		}
	}
}
