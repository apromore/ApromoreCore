package au.edu.qut.processmining.miner;

import au.edu.qut.helper.DiagramHandler;
import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

/**
 * Created by Adriano on 14/06/2016.
 */

public class FakeMiner {

    private DiagramHandler diagramHandler;

    public FakeMiner() {
        diagramHandler = new DiagramHandler();
    }


    public BPMNDiagram optimize(BPMNDiagram diagram, XLog log) {
        BPMNDiagram optimizedDiagram = diagramHandler.copyDiagram(diagram);
        diagramHandler.touch(optimizedDiagram);

        return optimizedDiagram;
    }

}
