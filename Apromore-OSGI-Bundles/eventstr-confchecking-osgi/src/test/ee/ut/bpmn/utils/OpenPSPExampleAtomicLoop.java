package ee.ut.bpmn.utils;

//import static org.hamcrest.MatcherAssert.assertThat;

//--
//OpenPSP test using base BPMN vs atomic changes in the log
//all changes take place in a loop
//--

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.UnfoldingPESSemantics;
import ee.ut.eventstr.comparison.OpenPartialSynchronizedProduct;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.Unfolder_PetriNet;

public class OpenPSPExampleAtomicLoop {

	@Test
	public void test() throws Exception {
		PESSemantics<Integer> pes1 = getLogPES_R1b(); // rename this void with _I2, _I3 etc.
		UnfoldingPESSemantics<Integer> pes2 = getUnfoldingPESExample();
		
		OpenPartialSynchronizedProduct<Integer> psp = new OpenPartialSynchronizedProduct<Integer>(pes1, pes2);
		psp.perform()
			.prune()
		;
		
		IOUtils.toFile("psp.dot", psp.toDot());
	}
	
	public UnfoldingPESSemantics<Integer> getUnfoldingPESExample() throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File("models/AtomicLoopTest/baseLoop.bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		Set<String> labels = new HashSet<>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ONEUNFOLDING);
		unfolder.computeUnfolding();
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		UnfoldingPESSemantics<Integer> pessem = new UnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		IOUtils.toFile("bpmnpes.dot", pessem.toDot());
		return pessem;
	}

	public PESSemantics<Integer> getLogPES_base() throws Exception {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		int loopend = 10;
		int repetitions = 3;
		
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		
		for (int i = 0; i < repetitions * loopend; i+=loopend) {
			adj.put(i + 3, i + 4);
			adj.put(i + 3, i + 6);
			adj.put(i + 4, i + 5);
			adj.put(i + 5, i + 7);
			adj.put(i + 6, i + 7);
			adj.put(i + 7, i + 8);
			adj.put(i + 8, i + 9);

			adj.put(i + loopend - 1, i + loopend);
			adj.put(i + loopend, i + loopend + 1);
			adj.put(i + loopend + 1, i + loopend + 2);
			if (i < (repetitions - 1) * loopend) adj.put(i + loopend - 1, i + loopend + 3); // if not last repetition, then create arc to new iteration
		}
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		for (int i = 0; i < repetitions * loopend; i+=loopend) {
			conc.put(i + 4, i + 6);
			conc.put(i + 5, i + 6);
		}
		
		List<Integer> sinks = new ArrayList<Integer>();
		
		for (int i = 0; i < repetitions; i++)
			sinks.add((i + 1) * loopend + 2);
		
		System.out.println(adj);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), sinks, 
					Arrays.asList("_0_", "start", "init", "A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_",
							"A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_",
							"A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_"
							), "BASE");

		IOUtils.toFile("basepes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_I1() throws Exception {
		// remove F
		Multimap<Integer, Integer> adj = HashMultimap.create();
		int loopend = 9;
		int repetitions = 3;
		
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		
		for (int i = 0; i < repetitions * loopend; i+=loopend) {
			adj.put(i + 3, i + 4);
			adj.put(i + 3, i + 6);
			adj.put(i + 4, i + 5);
			adj.put(i + 5, i + 7);
			adj.put(i + 6, i + 7);
			adj.put(i + 7, i + 8);
			//adj.put(i + 8, i + 9);

			adj.put(i + loopend - 1, i + loopend);
			adj.put(i + loopend, i + loopend + 1);
			adj.put(i + loopend + 1, i + loopend + 2);
			if (i < (repetitions - 1) * loopend) adj.put(i + loopend - 1, i + loopend + 3); // if not last repetition, then create arc to new iteration
		}
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		for (int i = 0; i < repetitions * loopend; i+=loopend) {
			conc.put(i + 4, i + 6);
			conc.put(i + 5, i + 6);
		}
		
		List<Integer> sinks = new ArrayList<Integer>();
		
		for (int i = 0; i < repetitions; i++)
			sinks.add((i + 1) * loopend + 2);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), sinks, 
					Arrays.asList("_0_", "start", "init", "A", "B", "C", "D", "E", "G", "H", "end", "_1_",
							"A", "B", "C", "D", "E", "G", "H", "end", "_1_",
							"A", "B", "C", "D", "E", "G", "H", "end", "_1_"
							), "I1");

		IOUtils.toFile("i1pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_I2() throws Exception {
		// duplicate A after E
		Multimap<Integer, Integer> adj = HashMultimap.create();
		int loopend = 11;
		int repetitions = 3;
		
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		
		for (int i = 0; i < repetitions * loopend; i+=loopend) {
			adj.put(i + 3, i + 4);
			adj.put(i + 3, i + 6);
			adj.put(i + 4, i + 5);
			adj.put(i + 5, i + 7);
			adj.put(i + 6, i + 7);
			adj.put(i + 7, i + 8);
			adj.put(i + 8, i + 9);
			adj.put(i + 9, i + 10);

			adj.put(i + loopend - 1, i + loopend);
			adj.put(i + loopend, i + loopend + 1);
			adj.put(i + loopend + 1, i + loopend + 2);
			if (i < (repetitions - 1) * loopend) adj.put(i + loopend - 1, i + loopend + 3); // if not last repetition, then create arc to new iteration
		}
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		for (int i = 0; i < repetitions * loopend; i+=loopend) {
			conc.put(i + 4, i + 6);
			conc.put(i + 5, i + 6);
		}
		
		List<Integer> sinks = new ArrayList<Integer>();
		
		for (int i = 0; i < repetitions; i++)
			sinks.add((i + 1) * loopend + 2);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), sinks, 
					Arrays.asList("_0_", "start", "init", "A", "B", "C", "D", "E", "A", "F", "G", "H", "end", "_1_",
							"A", "B", "C", "D", "E", "A", "F", "G", "H", "end", "_1_",
							"A", "B", "C", "D", "E", "A", "F", "G", "H", "end", "_1_"
							), "I2");

		IOUtils.toFile("i2pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_I3() throws Exception {
		// substitute F with X
		Multimap<Integer, Integer> adj = HashMultimap.create();
		int loopend = 10;
		int repetitions = 3;
		
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		
		for (int i = 0; i < repetitions * loopend; i+=loopend) {
			adj.put(i + 3, i + 4);
			adj.put(i + 3, i + 6);
			adj.put(i + 4, i + 5);
			adj.put(i + 5, i + 7);
			adj.put(i + 6, i + 7);
			adj.put(i + 7, i + 8);
			adj.put(i + 8, i + 9);

			adj.put(i + loopend - 1, i + loopend);
			adj.put(i + loopend, i + loopend + 1);
			adj.put(i + loopend + 1, i + loopend + 2);
			if (i < (repetitions - 1) * loopend) adj.put(i + loopend - 1, i + loopend + 3); // if not last repetition, then create arc to new iteration
		}
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		for (int i = 0; i < repetitions * loopend; i+=loopend) {
			conc.put(i + 4, i + 6);
			conc.put(i + 5, i + 6);
		}
		
		List<Integer> sinks = new ArrayList<Integer>();
		
		for (int i = 0; i < repetitions; i++)
			sinks.add((i + 1) * loopend + 2);
		
		System.out.println(adj);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), sinks, 
					Arrays.asList("_0_", "start", "init", "A", "B", "C", "D", "E", "X", "G", "H", "end", "_1_",
							"A", "B", "C", "D", "E", "X", "G", "H", "end", "_1_",
							"A", "B", "C", "D", "E", "X", "G", "H", "end", "_1_"
							), "I3");

		IOUtils.toFile("i3pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_R1() throws Exception {
		// loop F and G
		Multimap<Integer, Integer> adj = HashMultimap.create();
		
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		
		adj.put(3, 4);
		adj.put(3, 6);
		adj.put(4, 5);
		adj.put(5, 7);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(8, 9);

		adj.put(9, 10);
		adj.put(10, 11);
		adj.put(11, 12);
		
		adj.put(9, 13);
		adj.put(13, 14);
		adj.put(14, 15);
		adj.put(15, 17);
		adj.put(13, 16);
		adj.put(16, 17);
		adj.put(17, 18);
		adj.put(18, 19);
		
		adj.put(19, 20);
		adj.put(20, 21);
		adj.put(21, 22);
		
		adj.put(19, 23);
		adj.put(23, 24);
		adj.put(24, 25);
		adj.put(25, 26);
		adj.put(26, 27);
		
		adj.put(9, 28);
		adj.put(28, 29);
		adj.put(29, 30);
		adj.put(30, 31);
		adj.put(31, 32);
		
		adj.put(29, 33);
		adj.put(33, 34);
		adj.put(34, 35);
		adj.put(35, 37);
		adj.put(33, 36);
		adj.put(36, 37);
		adj.put(37, 38);
		adj.put(38, 39);
		
		adj.put(39, 40);
		adj.put(40, 41);
		adj.put(41, 42);
		
		adj.put(39, 43);
		adj.put(43, 44);
		adj.put(44, 45);
		adj.put(45, 46);
		adj.put(46, 47);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(4, 6);
		conc.put(5, 6);
		conc.put(14, 16);
		conc.put(15, 16);
		conc.put(34, 36);
		conc.put(35, 36);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(12, 22, 27, 32, 47), 
					Arrays.asList("_0_", "start", "init", "A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_"
							, "A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_"
								, "F", "G", "H", "end", "_1_"
							, "F", "G", "H", "end", "_1_"
							, "A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_"
								, "F", "G", "H", "end", "_1_"
							), "R1");

		IOUtils.toFile("r1pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_R1b() throws Exception {
		// loop F and G
		Multimap<Integer, Integer> adj = HashMultimap.create();
		
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(4, 5);
		adj.put(3, 6);
		adj.put(5, 7);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(8, 9);
		adj.put(9, 10);
		adj.put(9, 90);
		adj.put(9, 260);
		
		adj.put(10, 11);
		adj.put(11, 12);
		adj.put(10, 13);
		adj.put(12, 14);
		adj.put(13, 14);
		adj.put(14, 15);
		adj.put(15, 16);
		adj.put(16, 17);
		adj.put(16, 37);
		adj.put(16, 87);
		
		adj.put(17, 18);
		adj.put(18, 19);
		adj.put(17, 20);
		adj.put(19, 21);
		adj.put(20, 21);
		adj.put(21, 22);
		adj.put(22, 23);
		adj.put(23, 24);
		adj.put(23, 34);
		
		adj.put(24, 25);
		adj.put(25, 26);
		adj.put(25, 31);
		
		adj.put(26, 27);
		adj.put(27, 28);
		adj.put(28, 29);
		adj.put(29, 30);
		
		adj.put(31, 32);
		adj.put(32, 33);
		
		adj.put(34, 35);
		adj.put(35, 36);
		
		adj.put(37, 38);
		adj.put(38, 39);
		adj.put(38, 59);
		adj.put(38, 84);
		
		adj.put(39, 40);
		adj.put(40, 41);
		adj.put(39, 42);
		adj.put(41, 43);
		adj.put(42, 43);
		adj.put(43, 44);
		adj.put(44, 45);
		adj.put(45, 46);
		adj.put(45, 56);
		
		adj.put(46, 47);
		adj.put(47, 48);
		adj.put(47, 53);
		
		adj.put(48, 49);
		adj.put(49, 50);
		adj.put(50, 51);
		adj.put(51, 52);
		
		adj.put(53, 54);
		adj.put(54, 55);
		
		adj.put(56, 57);
		adj.put(57, 58);
		
		adj.put(59, 60);
		adj.put(60, 61);
		adj.put(60, 81);
		
		adj.put(61, 62);
		adj.put(62, 63);
		adj.put(61, 64);
		adj.put(63, 65);
		adj.put(64, 65);
		adj.put(65, 66);
		adj.put(66, 67);
		adj.put(67, 68);
		adj.put(67, 78);
		
		adj.put(68, 69);
		adj.put(69, 70);
		adj.put(69, 75);
		
		adj.put(70, 71);
		adj.put(71, 72);
		adj.put(72, 73);
		adj.put(73, 74);
		
		adj.put(75, 76);
		adj.put(76, 77);
		
		adj.put(78, 79);
		adj.put(79, 80);
		
		adj.put(81, 82);
		adj.put(82, 83);
		
		adj.put(84, 85);
		adj.put(85, 86);
		
		adj.put(87, 88);
		adj.put(88, 89);
		
		adj.put(90, 91);
		adj.put(91, 92);
		adj.put(91, 172);
		adj.put(91, 257);
				
		adj.put(92, 93);
		adj.put(93, 94);
		adj.put(92, 95);
		adj.put(94, 96); 
		adj.put(95, 96);
		adj.put(96, 97);
		adj.put(97, 98);
		adj.put(98, 99);
		adj.put(98, 119);
		adj.put(98, 169);
		
		adj.put(99, 100);
		adj.put(100, 101);
		adj.put(99, 102);
		adj.put(101, 103);
		adj.put(102, 103);
		adj.put(103, 104);
		adj.put(104, 105);
		adj.put(105, 106);
		adj.put(105, 116);
		
		adj.put(105, 106);
		adj.put(106, 107);
		adj.put(107, 108);
		adj.put(107, 113);
		
		adj.put(108, 109);
		adj.put(109, 110);
		adj.put(110, 111);
		adj.put(111, 112);
		
		adj.put(113, 114);
		adj.put(114, 115);

		adj.put(116, 117);
		adj.put(117, 118);
		
		adj.put(119, 120);
		adj.put(120, 121);
		adj.put(120, 141);
		adj.put(120, 166);
		
		adj.put(121, 122);
		adj.put(122, 123);
		adj.put(121, 124);
		adj.put(123, 125);
		adj.put(124, 125);
		adj.put(125, 126);
		adj.put(126, 127);
		adj.put(127, 128);
		adj.put(127, 138);
		
		adj.put(128, 129);
		adj.put(129, 130);
		adj.put(129, 135);
		
		adj.put(130, 131);		
		adj.put(131, 132);
		adj.put(132, 133);
		adj.put(133, 134);
		
		adj.put(135, 136);
		adj.put(136, 137);
		
		adj.put(138, 139);
		adj.put(139, 140);
		
		adj.put(141, 142);
		adj.put(142, 143);
		adj.put(142, 163);
		
		adj.put(143, 144);
		adj.put(144, 145);
		adj.put(143, 146);
		adj.put(145, 147);
		adj.put(146, 147);
		adj.put(147, 148);
		adj.put(148, 149);
		adj.put(149, 150);
		adj.put(149, 160);
		
		adj.put(150, 151);
		adj.put(151, 152);
		adj.put(151, 157);
		
		adj.put(152, 153);
		adj.put(153, 154);
		adj.put(154, 155);
		adj.put(155, 156);

		adj.put(157, 158);
		adj.put(158, 159);
		
		adj.put(160, 161);
		adj.put(161, 162);
		
		adj.put(163, 164);
		adj.put(164, 165);
		
		adj.put(166, 167);
		adj.put(167, 168);
		
		adj.put(169, 170);
		adj.put(170, 171);
		
		adj.put(172, 173);
		adj.put(173, 174);
		adj.put(173, 254);
		
		adj.put(174, 175);
		adj.put(175, 176);
		adj.put(174, 177);
		adj.put(176, 178);
		adj.put(177, 178);
		adj.put(178, 179);
		adj.put(179, 180);
		adj.put(180, 181);
		adj.put(180, 201);
		adj.put(180, 251);
		
		adj.put(181, 182);
		adj.put(182, 183);
		adj.put(181, 184);
		adj.put(183, 185);
		adj.put(184, 185);
		adj.put(185, 186);
		adj.put(186, 187);
		adj.put(187, 188);
		adj.put(187, 198);

		adj.put(188, 189);
		adj.put(189, 190);
		adj.put(189, 195);
		
		adj.put(190, 191);
		adj.put(191, 192);
		adj.put(192, 193);
		adj.put(193, 194);
		
		adj.put(195, 196);
		adj.put(196, 197);

		adj.put(197, 199);
		adj.put(198, 200);
		
		adj.put(201, 202);
		adj.put(202, 203);
		adj.put(202, 223);
		adj.put(202, 248);
		
		adj.put(203, 204);
		adj.put(204, 205);
		adj.put(203, 206);
		adj.put(205, 207);
		adj.put(206, 207);
		
		adj.put(207, 208);
		adj.put(208, 209);
		adj.put(209, 210);
		adj.put(209, 220);
		
		adj.put(210, 211);
		adj.put(211, 212);
		adj.put(212, 217);
		
		adj.put(212, 213);
		adj.put(213, 214);
		adj.put(214, 215);
		adj.put(215, 216);
		
		adj.put(217, 218);
		adj.put(218, 219);
		
		adj.put(220, 221);
		adj.put(221, 222);
		
		adj.put(223, 224);
		adj.put(224, 225);
		adj.put(224, 245);
		
		adj.put(225, 226);
		adj.put(226, 227);
		adj.put(225, 228);
		adj.put(227, 229);
		adj.put(228, 229);
		adj.put(229, 230);
		adj.put(230, 231);
		adj.put(231, 232);
		adj.put(231, 242);
		
		adj.put(232, 233);
		adj.put(233, 234);
		adj.put(233, 239);
		
		adj.put(234, 235);
		adj.put(235, 236);
		adj.put(236, 237);
		adj.put(237, 238);
		
		adj.put(239, 240);
		adj.put(240, 241);
		
		adj.put(242, 243);
		adj.put(243, 244);
		
		adj.put(245, 246);
		adj.put(246, 247);
		
		adj.put(248, 249);
		adj.put(249, 250);
		
		adj.put(251, 252);
		adj.put(252, 253);
		
		adj.put(254, 255);
		adj.put(255, 256);
		
		adj.put(257, 258);
		adj.put(258, 259);
		
		adj.put(260, 261);
		adj.put(261, 262);
				
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(4, 6);
		conc.put(5, 6);
		conc.put(11, 13);
		conc.put(12, 13);
		conc.put(18, 20);
		conc.put(19, 20);
		
		conc.put(40, 42);
		conc.put(41, 42);
		conc.put(62, 64);
		conc.put(63, 64);
		conc.put(93, 96);
		conc.put(94, 96);
		conc.put(100, 102);
		conc.put(101, 102);
		conc.put(122, 124);
		conc.put(123, 124);
		conc.put(144, 146);
		conc.put(145, 146);
		conc.put(175, 177);
		conc.put(176, 177);
		conc.put(182, 184);
		conc.put(183, 184);
		conc.put(204, 206);
		conc.put(205, 206);
		conc.put(226, 228);
		conc.put(227, 228);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(30, 31, 36, 52, 55, 58, 74, 77, 80, 86,
															89, 112, 115, 118, 134, 137, 140, 156, 
															159, 162, 165, 168, 171, 194, 197, 200,
															216, 219, 222, 238, 241, 244, 247, 250,
															253, 256, 259, 262), 
					Arrays.asList("_0_", "start", "init", "A", "B", "C", "D", "E", "F", "G"
							, "A", "B", "C", "D", "E", "F", "G"
								, "A", "B", "C", "D", "E", "F", "G"
									, "F", "G"
										, "F", "G"
											, "H", "end", "_1_"
										, "H", "end", "_1_"
									, "H", "end", "_1_"
								, "F", "G"
									, "A", "B", "C", "D", "E", "F", "G"
										, "F", "G"
											, "F", "G"
												, "H", "end", "_1_"
											, "H", "end", "_1_"
										, "H", "end", "_1_"
									, "F", "G"
										, "A", "B", "C", "D", "E", "F", "G"
											, "F", "G"
												, "F", "G"
													, "H", "end", "_1_"
												, "H", "end", "_1_"
											, "H", "end", "_1_"
										, "H", "end", "_1_"
									, "H", "end", "_1_"
								, "H", "end", "_1_"
							, "F", "G"
								, "A", "B", "C", "D", "E", "F", "G"
									, "A", "B", "C", "D", "E", "F", "G"
										, "F", "G"
											, "F", "G"
												, "H", "end", "_1_"
											, "H", "end", "_1_"
										, "H", "end", "_1_"
									, "F", "G"
										, "A", "B", "C", "D", "E", "F", "G"
											, "F", "G"
												, "F", "G"
													, "H", "end", "_1_"
												, "H", "end", "_1_"
											, "H", "end", "_1_"
										, "F", "G"
											, "A", "B", "C", "D", "E", "F", "G"
												, "F", "G"
													, "F", "G"
														, "H", "end", "_1_"
													, "H", "end", "_1_"
												, "H", "end", "_1_"
											, "H", "end", "_1_"
										, "H", "end", "_1_"
									, "H", "end", "_1_"
								, "F", "G"
									, "A", "B", "C", "D", "E", "F", "G"
										, "A", "B", "C", "D", "E", "F", "G"
											, "F", "G"
												, "F", "G"
													, "H", "end", "_1_"
												, "H", "end", "_1_"
											, "H", "end", "_1_"
										, "F", "G"
											, "A", "B", "C", "D", "E", "F", "G"
												, "F", "G"
													, "F", "G"
														, "H", "end", "_1_"
													, "H", "end", "_1_"
												, "H", "end", "_1_"
											, "F", "G"
												, "A", "B", "C", "D", "E", "F", "G"
													, "F", "G"
														, "F", "G"
															, "H", "end", "_1_"
														, "H", "end", "_1_"
													, "H", "end", "_1_"
												, "H", "end", "_1_"
											, "H", "end", "_1_"
										, "H", "end", "_1_"
									, "H", "end", "_1_"
								, "H", "end", "_1_"
							, "H", "end", "_1_"
							
							), "R1b");

		IOUtils.toFile("r1bpes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_R2() throws Exception {
		// skip F and G
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		
		adj.put(3, 4);
		adj.put(4, 5);
		adj.put(5, 7);
		adj.put(3, 6);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(8, 9);
		
		adj.put(9, 10);
		adj.put(10, 11);
		adj.put(11, 12);

		adj.put(9, 13);
		adj.put(13, 14);
		adj.put(14, 15);
		adj.put(15, 17);
		adj.put(13, 16);
		adj.put(16, 17);
		adj.put(17, 18);
		adj.put(18, 19);
		
		adj.put(19, 20);
		adj.put(20, 21);
		adj.put(21, 22);

		adj.put(17, 23);
		adj.put(23, 24);
		adj.put(24, 25);

		adj.put(7, 26);
		adj.put(26, 27);
		adj.put(27, 28);

		adj.put(7, 29);
		adj.put(29, 30);
		adj.put(30, 31);
		adj.put(30, 31);
		adj.put(31, 33);
		adj.put(29, 32);
		adj.put(32, 33);
		
		adj.put(33, 34);
		adj.put(34, 35);
		adj.put(35, 36);
		adj.put(36, 37);
		adj.put(37, 38);
		
		adj.put(33, 39);
		adj.put(39, 40);
		adj.put(40, 41);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(4, 6);
		conc.put(5, 6);
		conc.put(14, 16);
		conc.put(15, 16);
		conc.put(30, 32);
		conc.put(31, 32);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(12, 22, 25, 28, 38, 41), 
					Arrays.asList("_0_", "start", "init", "A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_"
							, "A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_"
								, "H", "end", "_1_"
								, "H", "end", "_1_"
							, "A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_"
								, "H", "end", "_1_"
					), "R2");

		IOUtils.toFile("r2pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_O1() throws Exception {
		// sequentialize B, C and D
		Multimap<Integer, Integer> adj = HashMultimap.create();
		int loopend = 10;
		int repetitions = 3;
		
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		
		for (int i = 0; i < repetitions * loopend; i+=loopend) {
			adj.put(i + 3, i + 4);
			adj.put(i + 4, i + 5);
			adj.put(i + 5, i + 6);
			adj.put(i + 6, i + 7);
			adj.put(i + 7, i + 8);
			adj.put(i + 8, i + 9);

			adj.put(i + loopend - 1, i + loopend);
			adj.put(i + loopend, i + loopend + 1);
			adj.put(i + loopend + 1, i + loopend + 2);
			if (i < (repetitions - 1) * loopend) adj.put(i + loopend - 1, i + loopend + 3); // if not last repetition, then create arc to new iteration
		}
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		
		List<Integer> sinks = new ArrayList<Integer>();
		
		for (int i = 0; i < repetitions; i++)
			sinks.add((i + 1) * loopend + 2);
		
		System.out.println(adj);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), sinks, 
					Arrays.asList("_0_", "start", "init", "A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_",
							"A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_",
							"A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_"
							), "O1");

		IOUtils.toFile("o1pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_O2() throws Exception {
		// F and G conditional
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		
		adj.put(3, 4);
		adj.put(4, 5);
		adj.put(5, 7);
		adj.put(3, 6);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(8, 9);
		adj.put(9, 10);
		adj.put(10, 11);
		
		adj.put(8, 12);
		adj.put(12, 13);
		adj.put(13, 14);
		adj.put(14, 16);
		adj.put(12, 15);
		adj.put(15, 16);
		
		adj.put(16, 17);
		adj.put(17, 18);
		adj.put(18, 19);
		adj.put(19, 20);
		
		adj.put(16, 21);
		adj.put(21, 22);
		adj.put(22, 23);
		adj.put(23, 24);
		
		adj.put(7, 25);
		adj.put(25, 26);
		adj.put(26, 27);
		adj.put(27, 28);
		
		adj.put(25, 29);
		adj.put(29, 30);
		adj.put(30, 31);
		adj.put(31, 33);
		adj.put(29, 32);
		adj.put(32, 33);

		adj.put(33, 34);
		adj.put(34, 35);
		adj.put(35, 36);
		adj.put(36, 37);
		
		adj.put(33, 38);
		adj.put(38, 39);
		adj.put(39, 40);
		adj.put(40, 41);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(4, 6);
		conc.put(5, 6);
		conc.put(13, 15);
		conc.put(14, 15);
		conc.put(30, 32);
		conc.put(31, 32);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(11, 20, 24, 28, 37, 41), 
					Arrays.asList("_0_", "start", "init", "A", "B", "C", "D", "E", "F", "H", "end", "_1_"
							, "A", "B", "C", "D", "E", "F", "H", "end", "_1_"
								, "G", "H", "end", "_1_"
							, "G", "H", "end", "_1_"
							, "A", "B", "C", "D", "E", "F", "H", "end", "_1_"
								, "G", "H", "end", "_1_"
					), "O2");

		IOUtils.toFile("O2pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_O3() throws Exception {
		// sync B with D
		Multimap<Integer, Integer> adj = HashMultimap.create();
		int loopend = 10;
		int repetitions = 3;
		
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		
		for (int i = 0; i < repetitions * loopend; i+=loopend) {
			adj.put(i + 3, i + 4);
			adj.put(i + 3, i + 6);
			adj.put(i + 4, i + 5);
			adj.put(i + 5, i + 7);
			adj.put(i + 6, i + 5);
			adj.put(i + 7, i + 8);
			adj.put(i + 8, i + 9);

			adj.put(i + loopend - 1, i + loopend);
			adj.put(i + loopend, i + loopend + 1);
			adj.put(i + loopend + 1, i + loopend + 2);
			if (i < (repetitions - 1) * loopend) adj.put(i + loopend - 1, i + loopend + 3); // if not last repetition, then create arc to new iteration
		}
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		for (int i = 0; i < repetitions * loopend; i+=loopend) {
			conc.put(i + 4, i + 6);
		}
		
		List<Integer> sinks = new ArrayList<Integer>();
		
		for (int i = 0; i < repetitions; i++)
			sinks.add((i + 1) * loopend + 2);
		
		System.out.println(adj);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), sinks, 
					Arrays.asList("_0_", "start", "init", "A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_",
							"A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_",
							"A", "B", "C", "D", "E", "F", "G", "H", "end", "_1_"
							), "O3");

		IOUtils.toFile("o3pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
}
