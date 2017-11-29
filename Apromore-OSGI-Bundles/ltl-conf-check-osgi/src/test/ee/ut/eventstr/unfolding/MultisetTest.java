package ee.ut.eventstr.unfolding;

import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class MultisetTest {
	@Test
	public void testMultisetIntersection() {
		Multiset<Integer> a = HashMultiset.create();
		
		a.add(1);
		a.add(1);
		a.add(1);
		a.add(2);
		a.add(2);
		a.add(3);

		Multiset<Integer> b = HashMultiset.create();
		
		b.add(1);
		b.add(3);
		
		a.retainAll(b);
		System.out.println(a);
	}
}
