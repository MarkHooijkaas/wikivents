package org.kisst.type4j.seq;

import java.util.Iterator;

public class ImmutableStringSequence implements DeeplyImmutableSequence<String> {
	private final String[] array;
	
	public ImmutableStringSequence(String ... strings) { this.array=strings;} // Can be abused to pass in array that one keeps reference to
	
	@Override public Class<String> getElementClass() { return String.class; }

	@Override public int size() { return array.length; }
	@Override public String get(int index) { return array[index]; }
	@Override public String getObject(int index) { return array[index]; }
	@Override public Iterator<String> iterator() { return null; }

}
