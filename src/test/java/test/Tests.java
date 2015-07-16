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
	Immutable.Sequence<Integer> seq = Immutable.sequence(Integer.class, 9,8,7,6,5,4,3,2,1,0);
	Immutable.Sequence<Integer> sub=seq.subsequence(5);

	@Test public void immutableSequences() {
		assertEquals("size", 10, seq.size());
		assertEquals("seq[0]", 9, (int) seq.get(0));
		assertEquals("seq[5]", 4, (int) seq.get(5));
		assertEquals("toFullString", "[9,8,7,6,5,4,3,2,1,0]", seq.toFullString());
	}
	
	@Test public void subsequence() {
		assertEquals("size", 5, sub.size());
		assertEquals("seq[0]", 4, (int) sub.get(0));
		assertEquals("seq[4]", 0, (int) sub.get(4));
		assertEquals("toFullString", "[4,3,2,1,0]", sub.toFullString());
	}

	@Test public void join() {
		Immutable.Sequence<Integer> join=seq.join(sub);
		assertEquals("size", 15, join.size());
		assertEquals("seq[12]", 2, (int) join.get(12));
		assertEquals("toFullString", "[9,8,7,6,5,4,3,2,1,0,4,3,2,1,0]", join.toFullString());
	}

	
	@Test(expected = IndexOutOfBoundsException.class)  
	public void indexOutOfBounds() { seq.get(10);}
}
