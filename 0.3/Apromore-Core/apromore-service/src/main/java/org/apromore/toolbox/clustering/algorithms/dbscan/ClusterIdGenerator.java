/**
 *
 */
package org.apromore.toolbox.clustering.algorithms.dbscan;

/**
 * @author Chathura C. Ekanayake
 */
public class ClusterIdGenerator {

    private static long initialId = 1;
    private static long nextAvailableId = 1;

    public synchronized static long getId() {
        long id = nextAvailableId;
        nextAvailableId++;
        return id;
    }

    public synchronized static String getStringId() {
        return Long.toString(getId());
    }

    public synchronized static void setInitialId(long iId) {
        initialId = iId;
        nextAvailableId = initialId;
    }

    public synchronized static void reset() {
        nextAvailableId = initialId;
    }
}
