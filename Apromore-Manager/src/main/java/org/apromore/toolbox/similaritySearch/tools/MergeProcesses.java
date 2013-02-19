package org.apromore.toolbox.similaritySearch.tools;

import java.util.ArrayList;
import javax.inject.Inject;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.CanonicalConverter;
import org.apromore.toolbox.similaritySearch.algorithms.MergeModels;
import org.springframework.stereotype.Service;

/**
 * Used to merge 2 or more models and returns the generated merged process.
 *
 * @author unknown at this point.
 */
@Service
public class MergeProcesses {

    private CanonicalConverter converter;
    private MergeModels merge;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     * @param canonicalConverter the canonical converter
     * @param mergeModels        the merge model function.
     */
    @Inject
    public MergeProcesses(final CanonicalConverter canonicalConverter, final MergeModels mergeModels) {
        this.converter = canonicalConverter;
        this.merge = mergeModels;
    }


    /**
     * Performs the merge of 2 or more models.
     *
     * @param models     the list of models
     * @param removeEnt  removeEnt....?
     * @param algortithm the algorithm we are using
     * @param threshold  thresholds for the model merge
     * @param param      extra params
     * @return the new canonicalProcessType for the newly created model
     */
    public CanonicalProcessType mergeProcesses(ArrayList<CanonicalProcessType> models, boolean removeEnt, String algortithm,
                                               double threshold, double... param) {
        Canonical m1 = converter.convert(models.get(0));
        Canonical m2 = converter.convert(models.get(1));

        Canonical merged = merge.mergeModels(m1, m2, removeEnt, algortithm, param);

        if (models.size() > 2) {
            for (int i = 2; i < models.size(); i++) {
                Canonical m3 = converter.convert(models.get(i));
                merged = merge.mergeModels(merged, m3, removeEnt, algortithm, param);
            }
        }

        return converter.convert(merged);
    }
}
