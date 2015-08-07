package org.kisst.item4j.struct;

import org.kisst.item4j.HasName;
import org.kisst.item4j.Type;

public interface SmartStruct {
	public Iterable<Member<?>> getMembers();
	public<T> T get(Member<T> member);
	
	
	public interface Member<T> extends HasName {
		public String getName();
		public Type<T> getType();
	}

}
