package org.kisst.type4j.item;

import org.kisst.type4j.registry.TypeRegistry;
import org.kisst.type4j.seq.SmartSequence;

public interface SmartItemSequence extends SmartSequence<SmartItem> { 
	default public Class<SmartItem> getElementClass() { return SmartItem.class; };
	//public int size();
	
	@Override default public SmartItem get(int index) {
		Object result=getObject(index);
		if (result instanceof SmartItem)
			return (SmartItem) result;
		return new SmartItem.Wrapper(result);
	} 
	
	default public <T> T getAs(TypeRegistry reg, Class<T> cls, int index) { return reg.getType(cls).convertFrom(getObject(index)); }  
}
