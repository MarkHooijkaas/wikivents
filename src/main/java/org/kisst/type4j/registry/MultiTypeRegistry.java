package org.kisst.type4j.registry;

import org.kisst.type4j.SmartType;

public class MultiTypeRegistry implements TypeRegistry {
	private final TypeRegistry[] registries;
	public MultiTypeRegistry(TypeRegistry ... registries) { this.registries=registries; }
	
	@Override public <T> SmartType<T> getTypeOrNull(Class<T> cls) {
		for (TypeRegistry r:registries) {
			SmartType<T> t = r.getTypeOrNull(cls);
			if (t!=null)
				return t;
		}
		return null;
	}
}
