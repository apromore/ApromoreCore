package ee.ut.eventstr.comparison.test;

import java.util.Arrays;

import org.jbpt.utils.IOUtils;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.comparison.PartialSynchronizedProduct;
import ee.ut.mining.log.poruns.pes.PORuns2PES;

public class TaskSkippingTest {
	@Test
	public void testCode() {
		PrimeEventStructure<Integer> pes1 = getPES1(), pes2 = getPES2();
		System.out.println("===================================");
		pes1.printBRelMatrix(System.out);
		System.out.println("===================================");
		pes2.printBRelMatrix(System.out);
		System.out.println("===================================");

		PartialSynchronizedProduct<Integer> psp = 
				new PartialSynchronizedProduct<>(new PESSemantics<>(pes1), new PESSemantics<>(pes2));
		
		for (String diff: psp.perform().prune().getDiff()) {
			System.out.println("DIFF: " + diff);
		}

		IOUtils.toFile("target/task_skipping.dot", psp.toDot());
	}

	public PrimeEventStructure<Integer> getPES1() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(0, 2);
		adj.put(1, 3);
		Multimap<Integer, Integer> conc = HashMultimap.create();

		return PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(2,3), Arrays.asList("a", "b", "c", "c"), "PES1");
	}
	public PrimeEventStructure<Integer> getPES2() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		Multimap<Integer, Integer> conc = HashMultimap.create();

		return PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(2), Arrays.asList("a", "b", "c"), "PES2");
	}
}
