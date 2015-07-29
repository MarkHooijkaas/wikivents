package org.kisst.type4j.item;

import org.kisst.type4j.SmartType;

public interface SmartItemWithType extends SmartItem {
	public SmartType<?> getType();
}