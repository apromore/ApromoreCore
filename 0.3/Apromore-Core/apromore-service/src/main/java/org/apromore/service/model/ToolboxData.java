package org.apromore.service.model;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.model.ProcessModelVersion;

import java.util.HashMap;
import java.util.Map;

/**
 * A Data Type that stores the information used by the Similarity Search.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ToolboxData {

    public static final String MODEL_THRESHOLD = "modelthreshold";
    public static final String LABEL_THRESHOLD = "labelthreshold";
    public static final String CONTEXT_THRESHOLD = "contextthreshold";
    public static final String SKIP_N_WEIGHT = "skipnweight";
    public static final String SUB_N_WEIGHT = "subnweight";
    public static final String SKIP_E_WEIGHT = "skipeweight";
    public static final String REMOVE_ENT = "removeent";

    private String algorithm = null;
    private boolean removeEntanglements = false;
    private double modelthreshold = 0.0;
    private double labelthreshold = 0.0;
    private double contextthreshold = 0.0;
    private double skipnweight = 0.0;
    private double subnweight = 0.0;
    private double skipeweight = 0.0;

    private CanonicalProcessType origin;
    private Map<ProcessModelVersion, CanonicalProcessType> model;


    public ToolboxData() {
        model = new HashMap<ProcessModelVersion, CanonicalProcessType>(0);
    }



    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(final String algorithm) {
        this.algorithm = algorithm;
    }

    public boolean isRemoveEntanglements() {
        return removeEntanglements;
    }

    public void setRemoveEntanglements(final boolean newRemoveEntanglements) {
        this.removeEntanglements = newRemoveEntanglements;
    }

    public double getModelthreshold() {
        return modelthreshold;
    }

    public void setModelthreshold(final double modelthreshold) {
        this.modelthreshold = modelthreshold;
    }

    public double getLabelthreshold() {
        return labelthreshold;
    }

    public void setLabelthreshold(final double labelthreshold) {
        this.labelthreshold = labelthreshold;
    }

    public double getContextthreshold() {
        return contextthreshold;
    }

    public void setContextthreshold(final double contextthreshold) {
        this.contextthreshold = contextthreshold;
    }

    public double getSkipnweight() {
        return skipnweight;
    }

    public void setSkipnweight(final double skipnweight) {
        this.skipnweight = skipnweight;
    }

    public double getSubnweight() {
        return subnweight;
    }

    public void setSubnweight(final double subnweight) {
        this.subnweight = subnweight;
    }

    public double getSkipeweight() {
        return skipeweight;
    }

    public void setSkipeweight(final double skipeweight) {
        this.skipeweight = skipeweight;
    }


    public CanonicalProcessType getOrigin() {
        return origin;
    }

    public void setOrigin(final CanonicalProcessType originData) {
        this.origin = originData;
    }

    public Map<ProcessModelVersion, CanonicalProcessType> getModel() {
        return model;
    }

    public void setModel(final Map<ProcessModelVersion, CanonicalProcessType> model) {
        this.model = model;
    }

    public void addModel(final ProcessModelVersion processData, final CanonicalProcessType searchData) {
        this.model.put(processData, searchData);
    }
}
