/**
 *
 */
package org.apromore.clustering.algorithm.dbscan;

import org.apromore.service.model.ClusterSettings;

/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class DBSCANClusterSettings extends ClusterSettings {

    public static final String DBSCAN_ALGORITHM_NAME = "DBSCAN";


    /**
     * Public Constructor.
     */
    public DBSCANClusterSettings() {
        super();
    }


    /* (non-Javadoc)
     * @see org.apromore.service.model.ClusterSettings#getAlgorithm()
     */
    @Override
    public String getAlgorithm() {
        return DBSCAN_ALGORITHM_NAME;
    }
}
