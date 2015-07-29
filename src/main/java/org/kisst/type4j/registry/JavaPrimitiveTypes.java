package org.kisst.type4j.registry;

import org.kisst.type4j.registry.ReflectionFieldsTypeRegistry.JavaClass;

public interface JavaPrimitiveTypes extends JavaType  {
	
	@JavaClass(String.class)  
	public final static StringParseType<String>  string  = new StringParseType<String>(String.class, str -> str);
	
	@JavaClass(Integer.class)
	public final static StringParseType<Integer> integer = new StringParseType<Integer>(Integer.class, Integer::parseInt);

	@JavaClass(Long.class)
	public final static StringParseType<Long>    longType= new StringParseType<Long>(Long.class, Long::parseLong);
	
}
