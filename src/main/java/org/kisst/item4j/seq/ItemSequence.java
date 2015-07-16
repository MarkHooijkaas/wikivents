package org.kisst.item4j.seq;

import java.util.Iterator;

import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;

public interface ItemSequence {
	public Class<?> getElementClass();
	public int size();

	public Object getObject(int index); // { return get(index); }
	default public Iterable<Item> items() { return new IterableItems(this); }
	default public Item getItem(int index) { return Item.asItem(getObject(index)); }
	default public Struct getStruct(int index) { return Item.asStruct(getObject(index)); }
	default public String getString(int index) { return Item.asString(getObject(index)); }
	default public int getInteger(int index) { return Item.asInteger(getObject(index)); }
	default public long getLong(int index) { return Item.asLong(getObject(index)); }
	default public short getShort(int index) { return Item.asShort(getObject(index)); }
	default public byte getByte(int index) { return Item.asByte(getObject(index)); }
	default public float getFloat(int index) { return Item.asFloat(getObject(index)); }
	default public double getDouble(int index) { return Item.asDouble(getObject(index)); }
	default public boolean getBoolean(int index) { return Item.asBoolean(getObject(index)); }
	//default public ItemSequence getSequence(int index);

	public final class IterableItems implements Iterable<Item> {
		private final ItemSequence seq;
		public IterableItems(ItemSequence seq) { this.seq=seq; }
		@Override public Iterator<Item> iterator() { return new MyIterator();}
		public final class MyIterator implements Iterator<Item>{
			private int index=0;
			@Override public boolean hasNext() { return index<seq.size();}
			@Override public Item next() { return seq.getItem(index++); }
			@Override public void remove() { throw new RuntimeException("remove is not allowed on this list"); }
		}
	}
}
