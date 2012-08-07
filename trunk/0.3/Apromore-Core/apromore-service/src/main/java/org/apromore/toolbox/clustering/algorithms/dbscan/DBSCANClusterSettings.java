/**
 *
 */
package org.apromore.toolbox.clustering.algorithms.dbscan;

import org.apromore.service.model.ClusterSettings;

/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class DBSCANClusterSettings extends ClusterSettings {

    public static final String DBSCAN_ALGORITHM_NAME = "DBSCAN";

    /* (non-Javadoc)
      * @see org.apromore.service.model.ClusterSettings#getAlgorithm()
      */
    @Override
    public String getAlgorithm() {
        return DBSCAN_ALGORITHM_NAME;
    }
}
