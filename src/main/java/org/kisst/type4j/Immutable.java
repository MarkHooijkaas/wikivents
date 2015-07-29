package org.kisst.type4j;

import org.kisst.type4j.item.SmartItem;

public interface Immutable extends SmartItem {
	@Override default public boolean isImmutable() { return true; }
}
