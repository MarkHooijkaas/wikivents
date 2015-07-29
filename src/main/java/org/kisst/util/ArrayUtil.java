package org.kisst.util;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayUtil {
	public static <T> T[] join(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	public static <T> T[] join(T first, T[] rest) {
		@SuppressWarnings("unchecked")
	    T[] result = (T[]) Array.newInstance(rest.getClass().getComponentType(), 1+rest.length);
		result[0]=first;
		System.arraycopy(rest, 0, result, 1, rest.length);
		return result;
	}
}
