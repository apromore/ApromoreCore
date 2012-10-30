package org.apromore.service.helper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;

import org.apromore.TestData;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanoniserService;
import org.apromore.service.ProcessService;
import org.apromore.service.model.CanonisedProcess;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

/**
 * Unit test the CanoniserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class UIHelperImplIntgTest {

    @Autowired
    private UIHelper uiSrv;
    @Autowired
    private CanoniserService cSrv;
    @Autowired
    private ProcessService pSrv;

    private HashSet<RequestParameterType<?>> emptyCanoniserRequest;

    @Before
    public void setUp() {
        emptyCanoniserRequest = new HashSet<RequestParameterType<?>>();
    }


    @Test
    @Rollback(true)
    public void TestUIHelper() throws Exception {
        createProcessModel("testUI", "1.0");

        ProcessSummariesType processSummaries = uiSrv.buildProcessSummaryList("", null);

        assertThat(processSummaries, notNullValue());
        assertThat(processSummaries.getProcessSummary().size(), greaterThan(0));

        boolean found = false;
        for (ProcessSummaryType proSum : processSummaries.getProcessSummary()) {
            if (proSum.getName().equals("testUI")) {
                found = true;
                break;
            }
        }
        if (!found) {
            fail("Process with name testUI not found in processSummary.");
        }
    }



    private void createProcessModel(String name, String version) throws Exception {
        String nativeType = "EPML 2.0";

        InputStream data = new ByteArrayInputStream(TestData.EPML3.getBytes());
        CanonisedProcess cp = cSrv.canonise(nativeType, data, emptyCanoniserRequest);

        assertThat(cp, notNullValue());
        assertThat(cp.getCpt(), notNullValue());

        String username = "james";
        String cpfURI = "12325335343353";
        String domain = "TEST";
        String created = "01/01/2011";
        String lastUpdate = "01/01/2011";

        ProcessSummaryType pst = pSrv.importProcess(username, name, cpfURI, version, nativeType, cp, data, domain, "", created, lastUpdate);

        assertThat(pst, notNullValue());
        assertThat(pst.getDomain(), equalTo(domain));
        assertThat(pst.getOriginalNativeType(), equalTo(nativeType));
        assertThat(pst.getOwner(), equalTo(username));
    }

}
