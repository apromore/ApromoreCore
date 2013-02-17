package org.apromore.toolbox.similaritySearch.tools;

import java.util.ArrayList;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.toolbox.similaritySearch.algorithms.MergeModels;
import org.apromore.toolbox.similaritySearch.common.CPFModelParser;
import org.apromore.toolbox.similaritySearch.common.IdGeneratorHelper;
import org.apromore.toolbox.similaritySearch.graph.Graph;

public class MergeProcesses {

    private MergeModels merge = new MergeModels();

    /**
     * Performs the merge of 2 or more models.
     * @param models the list of models
     * @param removeEnt removeEnt....?
     * @param algortithm the algorithm we are using
     * @param threshold thresholds for the model merge
     * @param param extra params
     * @return the new canonicalProcessType for the newly created model
     */
    public CanonicalProcessType mergeProcesses(ArrayList<CanonicalProcessType> models, boolean removeEnt, String algortithm,
            double threshold, double... param) {

//        IdGeneratorHelper idGenerator = new IdGeneratorHelper();
//        Canonical m1 = CPFModelParser.readModel(models.get(0));
//        m1.setIdGenerator(idGenerator);
//        m1.removeEmptyNodes();
//        m1.reorganizeIDs();
//
//        Graph m2 = CPFModelParser.readModel(models.get(1));
//        m2.setIdGenerator(idGenerator);
//        m2.removeEmptyNodes();
//        m2.reorganizeIDs();
//
//        m1.addLabelsToUnNamedEdges();
//        m2.addLabelsToUnNamedEdges();
//
//        Graph merged = merge.mergeModels(m1, m2, idGenerator, removeEnt, algortithm, param);
//
//        if (models.size() > 2) {
//            for (int i = 2; i < models.size(); i++) {
//                Graph m3 = CPFModelParser.readModel(models.get(i));
//                m3.setIdGenerator(idGenerator);
//                m3.removeEmptyNodes();
//                m3.reorganizeIDs();
//                m3.addLabelsToUnNamedEdges();
//
//                merged = merge.mergeModels(merged, m3, idGenerator, removeEnt, algortithm, param);
//            }
//
//        }
//
//        return CPFModelParser.writeModel(merged, idGenerator);
        return null;
    }
}
