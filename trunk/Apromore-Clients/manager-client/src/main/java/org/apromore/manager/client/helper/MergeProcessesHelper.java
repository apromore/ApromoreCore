package org.apromore.manager.client.helper;

import org.apromore.model.ParameterType;
import org.apromore.model.ParametersType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionIdType;
import org.apromore.model.ProcessVersionIdsType;
import org.apromore.model.VersionSummaryType;

import java.util.List;
import java.util.Map;

/**
 * Helper class to help construct the client's message to the WebService.
 */
public class MergeProcessesHelper {

    public static final String GREEDY_ALGORITHM = "Greedy";

    /**
     * Sets up the process models that are to be merged.
     *
     * @param selectedProcessVersions the list of models
     * @return the object to be sent to the Service
     */
    public static ProcessVersionIdsType setProcessModels(Map<ProcessSummaryType,List<VersionSummaryType>> selectedProcessVersions) {
        ProcessVersionIdType id;
        ProcessVersionIdsType modelIdList = new ProcessVersionIdsType();

        for (Map.Entry<ProcessSummaryType, List<VersionSummaryType>> i : selectedProcessVersions.entrySet()) {
            for (VersionSummaryType v : i.getKey().getVersionSummaries()) {
                id = new ProcessVersionIdType();
                id.setProcessId(i.getKey().getId());
                id.setBranchName(v.getName());
                id.setVersionNumber(v.getVersionNumber());
                modelIdList.getProcessVersionId().add(id);
            }
        }

        return modelIdList;
    }

    /**
     * Creates the Parameter Type needed by the client.
     *
     * @param method the algorithm used by the service.
     * @param removeEntanglements remove the entanglements
     * @param mergeThreshold the Merge Threshold
     * @param labelThreshold the Label Threshold
     * @param contextThreshold the Context Threshold
     * @param skipnWeight the Skip weight
     * @param subnWeight the Sub N weight
     * @param skipeWeight the Skip E weight
     * @return the ParameterType used in the WebService object
     */
    public static ParametersType setParams(String method, boolean removeEntanglements, double mergeThreshold, double labelThreshold,
            double contextThreshold, double skipnWeight, double subnWeight, double skipeWeight) {
        ParametersType params = new ParametersType();

        params.getParameter().add(addParam("removeent", removeEntanglements ? 1 : 0));
        params.getParameter().add(addParam("modelthreshold", mergeThreshold));
        params.getParameter().add(addParam("labelthreshold", labelThreshold));
        params.getParameter().add(addParam("contextthreshold", contextThreshold));

        if (GREEDY_ALGORITHM.equals(method)) {
            params.getParameter().add(addParam("skipnweight", skipnWeight));
            params.getParameter().add(addParam("subnweight", subnWeight));
            params.getParameter().add(addParam("skipeweight", skipeWeight));
        }

        return params;
    }


    /* Used to create a parameter object. */
    private static ParameterType addParam(String name, double value) {
        ParameterType p = new ParameterType();
        p.setName(name);
        p.setValue(value);
        return p;
    }

}
