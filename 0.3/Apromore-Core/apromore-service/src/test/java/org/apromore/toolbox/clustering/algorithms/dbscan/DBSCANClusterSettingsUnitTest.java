package org.apromore.toolbox.clustering.algorithms.dbscan;

import org.apromore.service.model.CanonisedProcess;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the CanonisedProcess POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class DBSCANClusterSettingsUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(DBSCANClusterSettings.class);
    }

}
