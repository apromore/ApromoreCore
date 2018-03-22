
package au.ltl.main;

import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.importing.PnmlImportUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Extractor {

//    public static PetrinetGraph extractPetriNet(String pnmlFilename) throws Exception {
//        PnmlImportUtils pnmlImportUtils = new PnmlImportUtils();
//        File pnFile = new File (pnmlFilename);
//
//        InputStream input = new FileInputStream(pnFile);
//        Pnml pnml = pnmlImportUtils.importPnmlFromStream(null,input, pnFile.getName(), pnFile.length());
//        String nameWithoutExtension = pnFile.getName().split("\\.")[0];
//        PetrinetGraph net = PetrinetFactory.newPetrinet(nameWithoutExtension);
//        Marking marking = new Marking();
//        pnml.convertToNet(net, marking, new GraphLayoutConnection(net));
//        return net;
//    }
}
