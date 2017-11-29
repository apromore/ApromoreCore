package ee.ut.eventstr.comparison.test;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class PossibleExtensionsTest {
	@Test
	public void testPES1() {
		PrimeEventStructure<Integer> pes1 = getPES1();
		System.out.println("===================================");
		pes1.printBRelMatrix(System.out);
		System.out.println("===================================");

		PESSemantics<Integer> pess = new PESSemantics<>(pes1);
		
				
		assertThat(pess.getPossibleExtensions(getBitSet(Arrays.asList(0))), equalTo(getBitSet(Arrays.asList(1,2,5))));
		assertThat(pess.getPossibleExtensions(getBitSet(Arrays.asList(0,1))), equalTo(getBitSet(Arrays.asList(5))));
		assertThat(pess.getPossibleExtensions(getBitSet(Arrays.asList(0,2))), equalTo(getBitSet(Arrays.asList(4,5))));		
	}

	@Test
	public void testPES2() {
		PrimeEventStructure<Integer> pes1 = getPES2();
		System.out.println("===================================");
		pes1.printBRelMatrix(System.out);
		System.out.println("===================================");

		PESSemantics<Integer> pess = new PESSemantics<>(pes1);
				
		assertThat(pess.getPossibleExtensions(getBitSet(Arrays.asList(0))), equalTo(getBitSet(Arrays.asList(1,2,5))));
		assertThat(pess.getPossibleExtensions(getBitSet(Arrays.asList(0,1))), equalTo(getBitSet(Arrays.asList(5))));
		assertThat(pess.getPossibleExtensions(getBitSet(Arrays.asList(0,2))), equalTo(getBitSet(Arrays.asList(4,5))));		
		assertThat(pess.getPossibleExtensions(getBitSet(Arrays.asList(0,2,5))), equalTo(getBitSet(Arrays.asList(4,6))));		
	}

	public BitSet getBitSet(List<Integer> list) {
		BitSet set = new BitSet();
		list.forEach(i -> set.set(i));
		return set;
	}
	
	public PrimeEventStructure<Integer> getPES1() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(0, 2);
		adj.put(1, 3);
		adj.put(2, 4);
		adj.put(5, 3);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(0, 5); conc.put(5, 0);
		conc.put(1, 5); conc.put(5, 1);
		conc.put(2, 5); conc.put(5, 2);
		conc.put(4, 5); conc.put(5, 4);

		List<String> labels = Arrays.asList("a", "b", "c", "d", "e", "f");
		return PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0,5), Arrays.asList(2,3), labels, "PES1");
	}
	
	public PrimeEventStructure<Integer> getPES2() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(0, 2);
		adj.put(1, 3);
		adj.put(2, 4);
		adj.put(5, 6);
		adj.put(6, 3);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(0, 5); conc.put(5, 0);
		conc.put(1, 5); conc.put(5, 1);
		conc.put(2, 5); conc.put(5, 2);
		conc.put(4, 5); conc.put(5, 4);
		conc.put(0, 6); conc.put(6, 0);
		conc.put(1, 6); conc.put(6, 1);
		conc.put(2, 6); conc.put(6, 2);
		conc.put(4, 6); conc.put(6, 4);

		List<String> labels = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
		return PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0,5), Arrays.asList(2,3), labels, "PES2");
	}

}
