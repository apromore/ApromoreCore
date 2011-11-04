package org.apromore.dao.jpa;

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

import java.util.List;

/**
 * Test the Process DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml"
       ,"classpath:META-INF/spring/applicationContext-services-TEST.xml"
		})
@TransactionConfiguration(transactionManager = "jpaTransactionManager", defaultRollback = true)
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class ProcessDaoJpaIntgTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDaoJpaIntgTest.class);

    @Autowired
    private ProcessDaoJpa dao;

    @Test
    @Rollback(true)
    public void getProcessSummariesNoSearchExpression() {
        List<Object[]> processes = dao.getAllProcesses();
        for (Object[] process : processes) {
           LOGGER.debug(process[0].toString() + " - " + process[1].toString());
        }
    }

//    private void createProcesses() {
//        Process p1 = new Process();
//        p1.setName("Bob");
//        p1.setDomain("airport");
//        p1.setUser(createUser(p1));
//        p1.setNativeType(createNativeType(p1));
//
//        Canonical c1 = new Canonical();
//        c1.setAuthor("bob");
//        c1.setContent("1212121212");
//        c1.setDocumentation("doco");
//        c1.setCreationDate("10/10/2011");
//        c1.setLastUpdate("10/10/2011");
//        c1.setProcess(p1);
//        c1.setRanking("1.0");
//
//        dao.save(p1);
//    }
//
//    private User createUser(Process p1) {
//        Set<Process> p = new HashSet<Process>();
//        p.add(p1);
//
//        User user = new User();
//        user.setEmail("email");
//        user.setFirstname("john");
//        user.setLastname("doe");
//        user.setPasswd("password");
//        user.setUsername("johnd");
//        user.setProcesses(p);
//
//        return user;
//    }
//
//    private NativeType createNativeType(Process p1) {
//        Set<Process> p = new HashSet<Process>();
//        p.add(p1);
//
//        NativeType nat = new NativeType();
//        nat.setExtension("xpdl");
//        nat.setNatType("test");
//        nat.setProcesses(p);
//
//        return nat;
//    }

}
