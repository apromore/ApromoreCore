package nl.rug.ds.bpm.variability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecificationToXML {
	
	public static String[] getOutput(VariabilitySpecification vs, String silentprefix) {
		String xml = "";
		Map<String, String> labelidmap = getLabelIdMap(vs.getAllLabels(), silentprefix);

		String output[] = getSpecs(vs, labelidmap);

		xml += "<variabilitySpecification>\n";
		xml += getAPs(labelidmap);
		xml += output[0];
		xml += "</variabilitySpecification>";

		output[0] = xml;

		return output;
	}

	public static String getXML(VariabilitySpecification vs, String silentprefix) {
		String xml = "";
		Map<String, String> labelidmap = getLabelIdMap(vs.getAllLabels(), silentprefix);

		xml += "<variabilitySpecification>\n";
		xml += getAPs(labelidmap);
		xml += getSpecs(vs, labelidmap)[0];
		xml += "</variabilitySpecification>";

		return xml;
	}

	private static Map<String, String> getLabelIdMap(List<String> labels, String silentprefix) {
		Map<String, String> labelidmap = new HashMap<String, String>();
		int i = 0;

		for (String lbl: labels) {
			if ((!lbl.startsWith(silentprefix)) && (!lbl.equals("_0_")) && (!lbl.equals("_1_"))) {
				labelidmap.put(lbl, "ap" + i);
				i++;
			}
		}

		return labelidmap;
	}

	private static String getAPs(Map<String, String> labelidmap) {
		String aps = "";

		aps += "\t<atomicPropositions>\n";

		for (String lbl: labelidmap.keySet()) {
			aps += "\t\t<atomicProposition id=\"{" + labelidmap.get(lbl) + "}\" name=\"" + lbl + "\"/>\n";
		}

		aps += "\t</atomicPropositions>\n";

		return aps;
	}

	private static String[] getSpecs(VariabilitySpecification vs, Map<String, String> labelidmap) {
		String output[] = new String[2];
		String specs = "";
		String plaintext = "";

		specs += "\t<specifications>\n";

		for (String sp: vs.getViresp()) {
			specs += getSpecLine(sp, "always immediate response", labelidmap, true);
			plaintext += sp + "\n";
		}
		plaintext += "\n";

		for (String sp: vs.getViprec()) {
			specs += getSpecLine(sp, "always immediate precedence", labelidmap, false);
			plaintext += sp + "\n";
		}
		plaintext += "\n";

		for (String sp: vs.getVeiresp()) {
			specs += getSpecLine(sp, "exist immediate response", labelidmap, true);
			plaintext += sp + "\n";
		}
		plaintext += "\n";

		for (String sp: vs.getVerespReduced(false)) {
			specs += getSpecLine(sp, "exist response", labelidmap, true);
			plaintext += sp + "\n";
		}
		plaintext += "\n";

		for (String sp: vs.getVconf()) {
			specs += getSpecLine(sp, "always conflict", labelidmap, true);
			plaintext += sp + "\n";
		}
		plaintext += "\n";

		for (String sp: vs.getVpar()) {
			specs += getSpecLine(sp, "exist parallel", labelidmap, true);
			plaintext += sp + "\n";
		}

		specs += "\t</specifications>\n";

		output[0] = specs;
		output[1] = plaintext;

		return output;
	}

	private static String getSpecLine(String spec, String type, Map<String, String> labelidmap, Boolean sourceIsFirstElement) {
		String specline = "";
		String source;
		String reworkedspec = spec;

		for (String lbl: labelidmap.keySet()) {
			reworkedspec = reworkedspec.replace("{" + lbl + "}", "{" + labelidmap.get(lbl) + "}");
		}

		if (sourceIsFirstElement) {
			source = reworkedspec.substring(reworkedspec.indexOf("{"), reworkedspec.indexOf("}") + 1);
		}
		else {
			source = reworkedspec.substring(reworkedspec.lastIndexOf("{"), reworkedspec.lastIndexOf("}") + 1);
		}

		specline += "\t\t<specification id=\"Import\" language=\"CTL\" type=\"" + type + "\" source=\"" + source + "\">" + reworkedspec + "</specification>\n";

		return specline;
	}
}
