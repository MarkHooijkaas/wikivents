package org.kisst.item4j;

import org.kisst.item4j.Immutable.Sequence;

@SuppressWarnings("rawtypes")
public class SequenceType<T> implements Type<Sequence> {
	private final Type<T> itemtype;
	public SequenceType(Type<T >type) {this.itemtype=type;} 
	
	@Override public  Class<Sequence> getJavaClass() { return Sequence.class; }
	//public  Class<? extends T> getItemClass() { return itemtype.getJavaClass(); }
	public  Type<T> getItemType() { return itemtype; }
	
	@Override public Sequence<?> parseString(String str) {
		return null; // TOD
	}

}
