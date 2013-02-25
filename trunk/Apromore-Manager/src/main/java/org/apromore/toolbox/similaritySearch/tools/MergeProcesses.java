package org.apromore.toolbox.similaritySearch.tools;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.toolbox.similaritySearch.algorithms.MergeModels;
import org.apromore.toolbox.similaritySearch.common.CPFModelParser;
import org.apromore.toolbox.similaritySearch.common.IdGeneratorHelper;
import org.apromore.toolbox.similaritySearch.graph.Graph;

import java.util.ArrayList;

public class MergeProcesses {

    /**
     * Finds the Processes Similarity.
     * @param models    the Canonical Process Type
     * @param removeEnt The Canonical Process Type
     * @param algorithm the search Algorithm
     * @param threshold the search merge threshold
     * @param param     the search parameters
     * @return the similarity between processes
     */
    public static CanonicalProcessType mergeProcesses(ArrayList<CanonicalProcessType> models, boolean removeEnt, String algorithm,
            double threshold, double... param) {
        IdGeneratorHelper idGenerator = new IdGeneratorHelper();
        Graph m1 = CPFModelParser.readModel(models.get(0));
        m1.setIdGenerator(idGenerator);
        m1.removeEmptyNodes();
        m1.reorganizeIDs();

        Graph m2 = CPFModelParser.readModel(models.get(1));
        m2.setIdGenerator(idGenerator);
        m2.removeEmptyNodes();
        m2.reorganizeIDs();


        m1.addLabelsToUnNamedEdges();
        m2.addLabelsToUnNamedEdges();

        Graph merged = MergeModels.mergeModels(m1, m2, idGenerator, removeEnt, algorithm, param);

        if (models.size() > 2) {
            for (int i = 2; i < models.size(); i++) {
                Graph m3 = CPFModelParser.readModel(models.get(i));
                m3.setIdGenerator(idGenerator);
                m3.removeEmptyNodes();
                m3.reorganizeIDs();
                m3.addLabelsToUnNamedEdges();

                merged = MergeModels.mergeModels(merged, m3, idGenerator, removeEnt, algorithm, param);
            }

        }

        return CPFModelParser.writeModel(merged, idGenerator);
    }
}
