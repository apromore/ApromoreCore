package org.apromore.service.impl;

import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.model.Statistic;
import org.apromore.service.UserService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.helper.UuidAdapter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class EventLogServiceImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventLogServiceImplTest.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private LogRepository logRepository;
    private GroupRepository groupRepository;
    private FolderRepository folderRepo;
    private UserService userSrv;
    private UserInterfaceHelper ui;

    private EventLogServiceImpl eventLogService;
//    private LogRepositoryCustomImpl logRepositoryCustom = new LogRepositoryCustomImpl();

    private static EntityManagerFactory emf = null;
    public EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("TESTApromore");
        }

        return emf;
    }

//    @Resource( name="logRepositoryCustom" )
//    LogRepositoryCustomImpl logRepositoryCustom;

//    private static EventLogService eventLogService;
//    @BeforeClass
//    public static void init() {
//        ApplicationContext
//                context = new ClassPathXmlApplicationContext("classpath:META-INF/spring/applicationContext-services-TEST.xml");
//        eventLogService = (EventLogService)context.getBean("accountService");
//    }

//    @Inject
//    private EventLogService eventLogService;

//    @Autowired
//    private LogRepositoryCustomImpl logRepositoryCustom;
//
//    @Autowired
//    private StatisticRepository statisticRepository;

    private byte[] getUUID() {
        UUID uuid = UUID.randomUUID();
        byte[] uuidBytes = new byte[16];
        ByteBuffer.wrap(uuidBytes)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());
        return uuidBytes;
    }

    @Test
    @Rollback(true)
    public void getStatistic() {


        EntityManager em = getEntityManagerFactory().createEntityManager();
        assert em != null;
        Statistic fe = new Statistic();
        fe.setId(UuidAdapter.getBytesFromUUID(UUID.randomUUID()));
        fe.setStat_key("key");
        fe.setLogid(88);
        fe.setPid(UuidAdapter.getBytesFromUUID(UUID.randomUUID()));
        fe.setStat_value("value");
        em.getTransaction().begin();


        em.persist(fe);
//        Query query = em.createQuery("SELECT s FROM Statistic s WHERE s.logid =:param").setParameter("param", fe.getLogid());
//        List<Statistic> stats = query.getResultList();
//
//        for (Statistic stat : stats) {
//            LOGGER.info(stat.getStat_value());
//        }

        em.flush();
        em.getTransaction().commit();
        em.close();

//        logRepositoryCustom.saveStat(stats.get(0));
//        eventLogService.insertStatistic(stat);
    }
}