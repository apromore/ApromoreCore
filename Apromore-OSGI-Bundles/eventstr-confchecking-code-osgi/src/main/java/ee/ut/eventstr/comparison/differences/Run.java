package ee.ut.eventstr.comparison.differences;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;

import org.jbpt.pm.Activity;
import org.jbpt.pm.AlternativGateway;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.DataNode;
import org.jbpt.pm.Event;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.IDataNode;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.XorGateway;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;

import ee.ut.bpmn.BPMNReader;
import ee.ut.bpmn.replayer.Pomset;

/**
 * A run is the container of a given difference run. 
 * It contains all the necessary data for representing a
 * difference graphically (by run). The Javascript files in the 
 * front end of BPDiff are aligned with the format of this 
 * file.   
 */

public class Run {
	HashMap<String, Integer> repetitionsTask1;
	HashMap<String, Integer> repetitionsTask2;
	HashMap<String, String> colorsBPMN;
	BPMNReader loader;
	String sentence;
	Pomset pomset;
	
	public Run(HashMap<String, String> colorsBPMN, HashMap<String, Integer> repetitionsTask1,
			HashMap<String, Integer> repetitionsTask2, BPMNReader loader, String sentence, Pomset pomset) {
		this.colorsBPMN = colorsBPMN;
		this.repetitionsTask1 = repetitionsTask1;
		this.repetitionsTask2 = repetitionsTask2;
		this.loader = loader;
		this.sentence = sentence;
		this.pomset = pomset;
	}
	
	public String getPomset(){
		if(pomset != null)
			return pomset.toString();
		
		return "";
	}
	
	public HashMap<String, Integer> getRepetitionsTask1() {
		return repetitionsTask1;
	}

	public void setRepetitionsTask1(HashMap<String, Integer> repetitionsTask1) {
		this.repetitionsTask1 = repetitionsTask1;
	}

	public HashMap<String, Integer> getRepetitionsTask2() {
		return repetitionsTask2;
	}

	public void setRepetitionsTask2(HashMap<String, Integer> repetitionsTask2) {
		this.repetitionsTask2 = repetitionsTask2;
	}

	public HashMap<String, String> getColorsBPMN() {
		return colorsBPMN;
	}

	public void setColorsBPMN(HashMap<String, String> colorsBPMN) {
		this.colorsBPMN = colorsBPMN;
	}
	
	public String toString(){
		String r = "\"repetitionsTask1\":{";
		
		int i = repetitionsTask1.size();
		for(Entry<String, Integer> e : repetitionsTask1.entrySet()){
			i--;
			r += e.getKey() +":" + e.getValue() ;
			
			if(i > 0)
				r+=",";
		}
		r+="},\"repetitionsTask2\":{";
		i = repetitionsTask2.size();
		for(Entry<String, Integer> e : repetitionsTask2.entrySet()){
			i--;
			r += e.getKey() +":" + e.getValue() ;
			
			if(i > 0)
				r+=",";
		}
		r+="},\"colorsBPMN\":{";
		i = colorsBPMN.size();
		for(Entry<String, String> e : colorsBPMN.entrySet()){ 
			i--;
			r += e.getKey() +":" + e.getValue() ;
			
			if(i > 0)
				r+=",";
		}
		r+="},\"pomset1\":{";
		r+=pomset.toString();
		r+="}";
		
		return r;
	}
	
	public void printModel(String folder) {
		Random r = new Random();
		int rand = r.nextInt();

		try {
			File dir = new File("target/tex/"+folder);
			
			if(!dir.exists())
				dir.mkdir();
			
			PrintStream out = new PrintStream("target/tex/"+folder+"/difference-" + rand + "BPMN.dot");
			@SuppressWarnings("unchecked")
			String modelColor = printBPMN2DOT(colorsBPMN, (Bpmn<BpmnControlFlow<FlowNode>, FlowNode>) loader.getModel(), loader, repetitionsTask1, repetitionsTask2);
			out.print(modelColor);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String printBPMN2DOT(HashMap<String, String> colorsUnf, Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model,
			BPMNReader loader, HashMap<String, Integer> repetitions1, HashMap<String, Integer> repetitions2) {
		String result = "";
		
		if (repetitions2 == null)
			repetitions2 = new HashMap<String, Integer>();

		result += "digraph G {\n";
		result += "rankdir=LR \n"; 

		for (Event e : model.getEvents()) {
			if (colorsUnf.containsKey(e.getId())) {
				result += String
						.format("  n%s[shape=ellipse,label=\"%s(x %s)(x%s)\", color=\"%s\"];\n",
								e.getId().replace("-", ""), e.getName(),
								getLabel(repetitions1, e),
								getLabel(repetitions2, e),
								colorsUnf.get(e.getId()));
			} else
				result += String.format("  n%s[shape=ellipse,label=\"%s\"];\n",
						e.getId().replace("-", ""), e.getName());
		}
		result += "\n";

		for (Activity a : model.getActivities()) {
			if (colorsUnf.containsKey(a.getId()))
				result += String
						.format("  n%s[shape=box,label=\"%s(x%s)(x%s)\",color=\"%s\"];\n",
								a.getId().replace("-", ""), a.getName(),
								getLabel(repetitions1, a),
								getLabel(repetitions2, a),
								colorsUnf.get(a.getId()));
			else
				result += String.format("  n%s[shape=box,label=\"%s\"];\n", a
						.getId().replace("-", ""), a.getName());
		}
		result += "\n";

		for (Gateway g : model.getGateways(AndGateway.class)) {
			if (colorsUnf.containsKey(g.getId()))
				result += String
						.format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
								g.getId().replace("-", ""), "AND",
								getLabel(repetitions1, g),
								getLabel(repetitions2, g),
								colorsUnf.get(g.getId()));
			else
				result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
						g.getId().replace("-", ""), "AND");
		}
		for (Gateway g : model.getGateways(XorGateway.class)) {
			if (colorsUnf.containsKey(g.getId()))
				result += String
						.format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
								g.getId().replace("-", ""), "XOR",
								getLabel(repetitions1, g),
								getLabel(repetitions2, g),
								colorsUnf.get(g.getId()));
			else
				result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
						g.getId().replace("-", ""), "XOR");
		}
		for (Gateway g : model.getGateways(OrGateway.class)) {
			if (colorsUnf.containsKey(g.getId()))
				result += String
						.format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
								g.getId().replace("-", ""), "OR",
								getLabel(repetitions1, g),
								getLabel(repetitions2, g),
								colorsUnf.get(g.getId()));
			else
				result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
						g.getId().replace("-", ""), "OR");
		}
		for (Gateway g : model.getGateways(AlternativGateway.class))
			result += String.format("  n%s[shape=diamond,label=\"%s\"];\n", g
					.getId().replace("-", ""), "?");
		result += "\n";

		for (DataNode d : model.getDataNodes()) {
			result += String.format("  n%s[shape=note,label=\"%s\"];\n", d
					.getId().replace("-", ""),
					d.getName().concat(" [" + d.getState() + "]"));
		}
		result += "\n";
		
		result += "node [shape=plaintext, style=solid, width=3.5] "
				+ "k1 [label=\"" + sentence + "\r\"]\n";

		for (ControlFlow<FlowNode> cf : model.getControlFlow()) {
			if (cf.getLabel() != null && cf.getLabel() != "")
				result += String.format("  n%s->n%s[label=\"%s\"];\n", cf
						.getSource().getId().replace("-", ""), cf.getTarget()
						.getId().replace("-", ""), cf.getLabel());
			else
				result += String.format("  n%s->n%s;\n", cf.getSource().getId()
						.replace("-", ""),
						cf.getTarget().getId().replace("-", ""));
		}
		result += "\n";

		for (Activity a : model.getActivities()) {
			for (IDataNode d : a.getReadDocuments()) {
				result += String.format("  n%s->n%s;\n",
						d.getId().replace("-", ""), a.getId().replace("-", ""));
			}
			for (IDataNode d : a.getWriteDocuments()) {
				result += String.format("  n%s->n%s;\n",
						a.getId().replace("-", ""), d.getId().replace("-", ""));
			}
		}
		result += "}";

		return result;
	}

	private String getLabel(HashMap<String, Integer> repetitions, FlowNode e) {
		if (!repetitions.containsKey(e.getId()))
			return "0";

		return repetitions.get(e.getId()) + "";
	}
}
