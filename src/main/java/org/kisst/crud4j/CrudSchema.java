package org.kisst.crud4j;

import java.lang.reflect.Constructor;

import org.kisst.item4j.ObjectSchema;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public class CrudSchema<T> extends ObjectSchema<T> {
	public CrudSchema(Class<T> cls) {  super(cls); }
	
	@SuppressWarnings("unchecked")
	public T createObject(CrudModel model, Struct doc) { 
		Constructor<?> cons=ReflectionUtil.getConstructor(cls, new Class<?>[]{ model.getClass(), Struct.class} );
		return (T) ReflectionUtil.createObject(cons, new Object[]{model, doc} );
	}
	
//	
//	public class RefField<RT extends CrudObject> extends Field {
//		public RefField(String name) { super(Type.javaString,name); }
//		@SuppressWarnings("unchecked")
//		public CrudRef<RT> getRef(CrudTable<RT> table, Struct s) {
//			Object obj=s.getObject(getName(), null);
//			//System.out.println("Resolving ref "+getName()+" of class "+ReflectionUtil.smartClassName(obj)+" with value "+obj);
//			if (obj==null)
//				return null;
//			if (obj instanceof CrudRef)
//				return (CrudRef<RT>) obj;
//			if (obj instanceof String)
//				return table.createRef((String) obj);
//			/*
//			if (cls.isAssignableFrom(obj.getClass())) { // TODO: is this other way round?
//				@SuppressWarnings("unchecked")
//				RT rec = (RT) obj;
//				return table.createRef(rec._id);
//			}
//			if (Ref.class.isAssignableFrom(obj.getClass())) { // TODO: is this other way round?
//				@SuppressWarnings("unchecked")
//				CrudTable<RT>.Ref result = (CrudTable<RT>.Ref) obj;
//				return result;
//			}
//			*/
//			String id = s.getString(getName(),null);
//			if (id==null)
//				return null;
//			//System.out.println("Getting ref "+getName()+" with value "+id+ " should be of format "+clsName+"(...)");
//			return table.createRef(id);
//		}
//	}
}
