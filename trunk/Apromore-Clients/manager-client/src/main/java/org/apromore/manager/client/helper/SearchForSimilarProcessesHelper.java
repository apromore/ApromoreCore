package org.apromore.manager.client.helper;

import org.apromore.model.ParameterType;
import org.apromore.model.ParametersType;

/**
 * Created by IntelliJ IDEA.
 * User: lappie
 * Date: 27/11/11
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class SearchForSimilarProcessesHelper {

    public static final String GREEDY_ALGORITHM = "Greedy";

    /**
     * Creates the Parameter Type needed by the client.
     *
     * @param method the algorithm used by the service.
     * @param modelThreshold the Model Threshold
     * @param labelThreshold the Label Threshold
     * @param contextThreshold the Context Threshold
     * @param skipnWeight the Skip weight
     * @param subnWeight the Sub N weight
     * @param skipeWeight the Skip E weight
     * @return the ParameterType used in the WebService object
     */
    public static ParametersType setParams(String method, double modelThreshold, double labelThreshold, double contextThreshold,
            double skipnWeight, double subnWeight, double skipeWeight) {
        ParametersType params = new ParametersType();

        params.getParameter().add(addParam("modelthreshold", modelThreshold));
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
