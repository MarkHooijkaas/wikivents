package test;


import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.kisst.item4j.Immutable;

public class Tests {
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(Tests.class);
		System.out.println("Ran "+result.getRunCount()+" tests");
		System.out.println("==> "+result.getFailureCount()+" failures");
		for (Failure failure : result.getFailures()) 
			System.out.println(failure.toString()+failure.getTrace());
	}
	//Immutable.Sequence<Integer> seq = Immutable.sequence(Integer.class, 0,1,2,3,4,5,6,7,8,9);
	//Immutable.Sequence<Integer> sub=seq.subsequence(5);
	Immutable.ItemSequence seq = Immutable.items(0,1,2,3,4,5,6,7,8,9);
	Immutable.ItemSequence sub=seq.subsequence(5);

	@Test public void immutableSequences() {
		assertEquals("size", 10, seq.size());
		assertEquals("seq[0]", 0, (int) seq.get(0));
		assertEquals("seq[5]", 5, (int) seq.get(5));
		assertEquals("toFullString", "[0,1,2,3,4,5,6,7,8,9]", seq.toFullString());
	}
	
	@Test public void subsequence() {
		assertEquals("size", 5, sub.size());
		assertEquals("seq[0]", 5, (int) sub.get(0));
		assertEquals("seq[4]", 9, (int) sub.get(4));
		assertEquals("toFullString", "[5,6,7,8,9]", sub.toFullString());
	}

	@Test public void join() {
		Immutable.ItemSequence join=seq.join(sub,sub);
		assertEquals("size", 20, join.size());
		assertEquals("seq[12]", 7, (int) join.get(12));
		assertEquals("toFullString", "[0,1,2,3,4,5,6,7,8,9,5,6,7,8,9,5,6,7,8,9]", join.toFullString());
	}

	@Test public void remove() {
		assertEquals("removeFirst", "[1,2,3,4,5,6,7,8,9]", seq.removeFirst().toFullString());
		assertEquals("removeLast",  "[0,1,2,3,4,5,6,7,8]", seq.removeLast().toFullString());
		assertEquals("remove(3)",   "[0,1,2,4,5,6,7,8,9]", seq.remove(3).toFullString());
		assertEquals("remove(3,5)", "[0,1,2,5,6,7,8,9]",   seq.remove(3,5).toFullString());
	}

	
	@Test(expected = IndexOutOfBoundsException.class)  
	public void indexOutOfBounds() { seq.get(10);}
}
