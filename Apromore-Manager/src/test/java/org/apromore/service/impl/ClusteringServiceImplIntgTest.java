/**
 *
 */
package org.apromore.service.impl;

import org.apromore.dao.ClusterRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.Cluster;
import org.apromore.service.ClusterService;
import org.apromore.service.ProcessService;
import org.apromore.service.model.ClusterFilter;
import org.apromore.service.model.MemberFragment;
import org.apromore.service.model.ProcessAssociation;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.inject.Inject;

/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(value = DependencyInjectionTestExecutionListener.class)
public class ClusteringServiceImplIntgTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusteringServiceImplIntgTest.class);

    @Inject
    private ClusterService cSrv;
    @Inject
    private ProcessService psrv;
    @Inject
    private ProcessRepository pdao;
    @Inject
    private ClusterRepository cdoa;


    @Ignore
    @Test
    @Rollback(true)
    @SuppressWarnings("unchecked")
    public void TestStandardImportProcess() throws Exception {
        ClusterFilter filter = new ClusterFilter();
        filter.setMinClusterSize(2);
        filter.setMaxClusterSize(8);
        filter.setMinAverageFragmentSize(5);
        filter.setMaxAverageFragmentSize(15);
        filter.setMinBCR(0);
        filter.setMaxBCR(5);
        List<org.apromore.service.model.Cluster> cs = cSrv.getClusters(filter);
        for (org.apromore.service.model.Cluster c : cs) {
            List<MemberFragment> fs = c.getFragments();
            for (MemberFragment f : fs) {
//                System.out.println(f.getFragmentId());
                List<ProcessAssociation> pas = f.getProcessAssociations();
                for (ProcessAssociation pa : pas) {
//                    System.out.println("\t" + pa.getProcessName() + " - " + pa.getProcessBranchName() + " - " + pa.getProcessVersionNumber());
                }
            }
        }

        List<Cluster> cis = cSrv.getClusterSummaries(filter);
//        System.out.println("got the cinfos");

        try {
            cSrv.getClusteringSummary();
        } catch (Exception e) {
//            System.out.println("error in getting summary - " + e.getMessage());
        }
    }

}
