package org.apromore.service.impl;

import org.apromore.service.CanoniserService;
import org.apromore.service.ProcessService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertTrue;

/**
 * Unit test the UserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class CanoniserServiceImplIntgTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanoniserServiceImplIntgTest.class);

    @Autowired
    private CanoniserService cSrv;
    @Autowired
    private ProcessService pSrv;


    @Test
    @Rollback(true)
    @SuppressWarnings("unchecked")
    /* TODO: Must implement some stuff here */
    public void deserialize() throws Exception {
        assertTrue(true);
//        LOGGER.debug("Testing the Canoniser Service, Deserialize.");
//        JAXBContext jc = JAXBContext.newInstance(CanoniserServiceImpl.CPF_CONTEXT);
//        Unmarshaller u = jc.createUnmarshaller();
//        InputStream data = new ByteArrayInputStream(TestData.CPF.getBytes());
//        JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(data);
//        CanonicalProcessType canType = rootElement.getValue();
//
//        ProcessModelGraph graph = cSrv.deserializeCPF(canType);
//        assertThat(graph, notNullValue());
    }

}
