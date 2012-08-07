package org.apromore.manager.client.helper;

import org.apromore.model.ParameterType;
import org.apromore.model.ParametersType;

/**
 * Search for Similar Processes Helper.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public final class SearchForSimilarProcessesHelper {

    private static final String GREEDY_ALGORITHM = "Greedy";


    /* Private Constructor */
    private SearchForSimilarProcessesHelper() { }


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
    public static ParametersType setParams(final String method, final double modelThreshold, final double labelThreshold,
            final double contextThreshold, final double skipnWeight, final double subnWeight, final double skipeWeight) {
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
    private static ParameterType addParam(final String name, final double value) {
        ParameterType p = new ParameterType();
        p.setName(name);
        p.setValue(value);
        return p;
    }


}
