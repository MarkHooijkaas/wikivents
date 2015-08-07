package org.kisst.item4j;

import java.util.Collection;

public interface Immutable {


	///////////////////////////////////////////////////////////////////////////////////////////////////

	//public class Item implements org.kisst.item4j.Item, Immutable {
	//	@Override public Object asObject() { return this;} // TODO
	//}


	///////////////////////////////////////////////////////////////////////////////////////////////////

	public static Immutable.ItemSequence items(Object ... obj) { return new Immutable.ItemSequence.ArraySequence(obj); }

	public abstract class ItemSequence implements org.kisst.item4j.seq.ItemSequence, Immutable {
		private ItemSequence() { } 


		public Immutable.ItemSequence subsequence(int start, int end) { return new SubSequence(this, start, end); }
		public Immutable.ItemSequence subsequence(int start) { return new SubSequence(this, start, size()); }

		public static  ItemSequence realCopy(org.kisst.item4j.seq.ItemSequence seq) {
			Object[] arr = new Object[seq.size()];
			int i=0; 
			for (Object obj: seq.objects())
				arr[i++]= obj; 
			return new ArraySequence(arr);
		}
		public static  ItemSequence realCopy(Collection<?> collection) {
			Object[] arr = new Object[collection.size()];
			int i=0; for (Object obj : collection) arr[i++]=obj;
			return new ArraySequence(arr);
		}

		public static  Immutable.ItemSequence smartCopy(org.kisst.item4j.seq.ItemSequence seq) {
			if (seq instanceof Immutable.ItemSequence) 
				return (Immutable.ItemSequence) seq;
			return realCopy(seq);
		}

		public static  Immutable.ItemSequence smartCopy(Collection<?> collection) {
			if (collection instanceof org.kisst.item4j.seq.ItemSequence) 
				return smartCopy((org.kisst.item4j.seq.ItemSequence) collection); 
			return realCopy(collection);
		}

		public ItemSequence removeFirst() { return subsequence(1); }
		public ItemSequence removeLast()  { return subsequence(0,size()-1); }
		public ItemSequence remove(int index) {
			if (index==0)      return removeFirst();
			if (index==size()) return removeLast();
			return subsequence(0,index).join(subsequence(index+1));
		}
		public ItemSequence remove(int begin, int end) {
			if (begin==0)      return subsequence(end+1);
			if (end==size()) return subsequence(0, begin);
			return subsequence(0,begin).join(this.subsequence(end));
		}

		public final ItemSequence join(ItemSequence ... sequences) {
			if (sequences.length==0) return this;
			ItemSequence[] result= new ItemSequence[sequences.length+1];
			result[0]=this;
			int i=1;
			for (ItemSequence seq  : sequences) result[i++]=seq;
			return new MultiSequence(result);
		}

		public ItemSequence join(Collection<?> ... collections) {
			Immutable.ItemSequence[] result= new Immutable.ItemSequence[collections.length+1];
			result[0]=this;
			int i=1;
			for (Collection<?> col : collections)
				result[i++]=smartCopy(col);
			return new MultiSequence(result);
		}

		private final static class ArraySequence extends Immutable.ItemSequence {
			private final Object[] array; 
			private ArraySequence(Object[] arr) {
				this.array=arr;
			}
			@Override public int size() { return array.length; }
			@Override public Object getObject(int index) { return array[index]; }
		}

		private final static class SubSequence extends Immutable.ItemSequence {
			private final ArraySequence seq;
			private final int start;
			private final int end;
			private  SubSequence(Immutable.ItemSequence seq, int start, int end) {
				if (seq instanceof ArraySequence) {
					this.seq=(ArraySequence) seq;
					this.start=start;
					this.end=end;
				}
				else if (seq instanceof SubSequence) {
					SubSequence sub = (SubSequence) seq;
					this.seq=sub.seq;
					this.start=sub.start+start;
					this.end=sub.start+end;
				}
				else
					throw new RuntimeException("Unsupported Immutable.ItemSequence type "+seq.getClass()); // should never happen
				seq.checkIndex(this.start);
				seq.checkIndex(this.end);
				if (this.start>this.end)
					throw new IndexOutOfBoundsException("subsequence start "+start+" should be less or equal to end "+end);
			}
			@Override public int size() { return end-start; }
			@Override public Object getObject(int index) { return seq.array[start+index]; }
		}

		private final static class MultiSequence extends ItemSequence {
			private final Immutable.ItemSequence[] sequences;
			private final int size; 
			private MultiSequence(Immutable.ItemSequence ... sequences) {
				this.sequences=sequences;
				int size=0;
				for (Immutable.ItemSequence seq : this.sequences)
					size+=seq.size();
				this.size=size;
			}
			@Override public int size() { return size; }
			@Override public Object getObject(int index) { 
				for (Immutable.ItemSequence seq : this.sequences) {
					if (index< seq.size())
						return seq.getObject(index);
					index -=seq.size();
				}
				throw new IndexOutOfBoundsException("index too large for size "+size());
			}
			@Override public Immutable.ItemSequence subsequence(int start, int end) {
				checkIndex(start);
				checkIndex(end);
				int offset=0;
				for (Immutable.ItemSequence seq : this.sequences) {
					if (offset+start< seq.size()) {
						if (offset+end<seq.size())
							return seq.subsequence(start-offset, end-offset);
						else
							break; // subsequence not in one segment
					}
					offset +=seq.size();
				}
				Object[] result=new Object[end-start];
				for(int i=start; i<end; i++)
					result[i-start]=getObject(i);
				return new ArraySequence(result); }
		}

	}



	///////////////////////////////////////////////////////////////////////////////////////////////////

}
