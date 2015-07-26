package org.kisst.util;

public interface AssertUtil {
	public static void assertNotNull(Object obj) { assertNotNull(obj,"Object"); }
	public static void assertNotNull(Object obj, String msg) {
		if (obj==null)
			throw new NullPointerException(msg);
	}
}
