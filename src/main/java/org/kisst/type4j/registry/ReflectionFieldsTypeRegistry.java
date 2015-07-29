package org.kisst.type4j.registry;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;

import org.kisst.type4j.DeeplyImmutable;
import org.kisst.type4j.SmartType;
import org.kisst.util.ReflectionUtil;

public class ReflectionFieldsTypeRegistry implements TypeRegistry, DeeplyImmutable {
	private final LinkedHashMap<Class<?>, Field> map = new LinkedHashMap<Class<?>, Field>();
	private final Object object;

	protected ReflectionFieldsTypeRegistry() {
		this.object=this;
		fillMap();
	}
	public ReflectionFieldsTypeRegistry(Object object) {
		this.object=object;
		fillMap();
	}

	private void fillMap() {
		for (Field field : ReflectionUtil.getAllPublicFieldsOfType(this.getClass(), SmartType.class)) {
			int modifiers = field.getModifiers();
			if (Modifier.isFinal(modifiers)) { // TO_THINK: check other modifiers???
				JavaClass anno = field.getAnnotation(JavaClass.class);
				if (anno!=null)
					map.put(anno.value(), field);
			}
		}

	}
	@SuppressWarnings("unchecked")
	@Override public <T> SmartType<T> getTypeOrNull(Class<T> cls) { 
		Field field = map.get(cls);
		if (field==null)
			return null;
		return (SmartType<T>) ReflectionUtil.getFieldValue(object, field);  
	}
	
	@Retention( RetentionPolicy.RUNTIME )
	public @interface JavaClass {	public Class<?> value(); }
}
