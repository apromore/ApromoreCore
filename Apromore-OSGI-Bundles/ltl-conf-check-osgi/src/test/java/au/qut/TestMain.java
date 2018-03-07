package au.qut;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import au.ltl.utils.ModelAbstractions;
import au.qut.nets.unfolding.NetReplayer;
import au.qut.nets.unfolding.UnfoldingDecomposer;
import hub.top.petrinet.Arc;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.Pnml;

public class TestMain {

	@org.junit.Test
	public void testVariant12() throws Exception {
		String modelName1 = "models/model77.bpmn";

		ModelAbstractions model = new ModelAbstractions(getFileAsArray(modelName1));

		UnfoldingDecomposer decomposer = new UnfoldingDecomposer(model);
		HashSet<PetriNet> subNets = decomposer.getSubNets(); //questo diventa coppia <Petrinet, mappa>

        /*Iterator<Activity> it =model.getBpmnModel().getActivities().iterator();

        //quello che dovrò avere alla fine
        while(it.hasNext()){
            Activity a=it.next();
            System.out.println(a.getId() + "    "+ a.getLabel());
        }*/

        int i = 0;
		for(PetriNet net : subNets) {
			NetReplayer replayer = new NetReplayer(net);
            System.out.println(replayer.getTraces()); // potrebbe prendere in input
            //write2File(translateNet(net),"net"+i+".pnml");
            i++;
		}
		// Assert.assertTrue();
	}

    private Petrinet translateNet(PetriNet net) {
        HashMap<Object, Object> map = new HashMap<>();
        Petrinet newNet = PetrinetFactory.newPetrinet("name-of-net");

        for(Transition n : net.getTransitions())
            map.put(n, newNet.addTransition(n.getName()));

        for(Place n : net.getPlaces())
            map.put(n, newNet.addPlace(n.getName()));

        for(Arc arc : net.getArcs()){
            if(arc.getSource() instanceof Place)
                newNet.addArc((org.processmining.models.graphbased.directed.petrinet.elements.Place) map.get(arc.getSource()), (org.processmining.models.graphbased.directed.petrinet.elements.Transition) map.get(arc.getTarget()));
            else
                newNet.addArc((org.processmining.models.graphbased.directed.petrinet.elements.Transition) map.get(arc.getSource()), (org.processmining.models.graphbased.directed.petrinet.elements.Place) map.get(arc.getTarget()));
        }

        return newNet;
    }

    private Marking getInitialM(Petrinet net) {
        Marking m = new Marking();

        for (org.processmining.models.graphbased.directed.petrinet.elements.Place p : net.getPlaces())
            if (net.getInEdges(p).isEmpty())
                m.add(p, 1);

        return m;
    }

    private void write2File(Petrinet net, String file) throws Exception {
        Marking marking = getInitialM(net);

        GraphLayoutConnection layout = new GraphLayoutConnection(net);

        Map<PetrinetGraph, Marking> markedNets = new HashMap<>();
        markedNets.put(net, marking);
        Pnml pnml = new Pnml().convertFromNet(markedNets, layout);
        pnml.setType(Pnml.PnmlType.PNML);
        String text = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + pnml.exportElement(pnml);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        bw.write(text);
        bw.close();
    }

	public static byte[] getFileAsArray(String fileName) {
		FileInputStream fileInputStream = null;
		File file = new File(fileName);

		try {
			byte[] bFile = new byte[(int) file.length()];

			// convert file into array of bytes
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bFile);
			fileInputStream.close();

			return bFile;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
