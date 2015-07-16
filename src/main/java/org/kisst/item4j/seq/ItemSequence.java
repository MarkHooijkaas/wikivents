package org.kisst.item4j.seq;

import java.util.Collection;
import java.util.Iterator;

import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;

public interface ItemSequence {
	default public Class<?> getElementClass() { return Item.class; }
	public int size();

	public Object getObject(int index); // { return get(index); }
	default public Object get(int index) { return getObject(index); }
	default public Iterable<Item> items() { return new IterableItems(this); }
	default public Iterable<Object> objects() { return new IterableObjects(this); }
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

	default public String toFullString() {
		StringBuilder result=new StringBuilder("[");
		String sep="";
		for (Object obj:this.objects()) { result.append(sep+obj); sep=","; }
		return result.toString()+"]";
	}
	
	default void checkIndex(int index) {
		if (index<0)
			throw new IndexOutOfBoundsException("index "+index+" should be >=0");
		if (index>size())
			throw new IndexOutOfBoundsException("index "+index+" should be less or equal to size "+size());
	}

	
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
	public final class IterableObjects implements Iterable<Object> {
		private final ItemSequence seq;
		public IterableObjects(ItemSequence seq) { this.seq=seq; }
		@Override public Iterator<Object> iterator() { return new MyIterator();}
		public final class MyIterator implements Iterator<Object>{
			private int index=0;
			@Override public boolean hasNext() { return index<seq.size();}
			@Override public Object next() { return seq.getObject(index++); }
			@Override public void remove() { throw new RuntimeException("remove is not allowed on this list"); }
		}
	}
	public final class IterableItemsByIterator implements Iterable<Item> {
		private final Collection<?> collection;
		public IterableItemsByIterator(Collection<?> collection) { this.collection=collection; } 
		@Override public Iterator<Item> iterator() { return new MyIterator();}
		public final class MyIterator implements Iterator<Item>{
			private final Iterator<?> it=collection.iterator();
			@Override public boolean hasNext() { return it.hasNext();}
			@Override public Item next() { return  Item.asItem(it.next()); }
			@Override public void remove() { throw new RuntimeException("remove is not allowed on this list"); }
		}
	}
	public final class IterableObjectByIterator implements Iterable<Object> {
		private final Collection<?> collection;
		public IterableObjectByIterator(Collection<?> collection) { this.collection=collection; } 
		@Override public Iterator<Object> iterator() { return new MyIterator();}
		public final class MyIterator implements Iterator<Object>{
			private final Iterator<?> it=collection.iterator();
			@Override public boolean hasNext() { return it.hasNext();}
			@Override public Object next() { return  it.next(); }
			@Override public void remove() { throw new RuntimeException("remove is not allowed on this list"); }
		}
	}
}
