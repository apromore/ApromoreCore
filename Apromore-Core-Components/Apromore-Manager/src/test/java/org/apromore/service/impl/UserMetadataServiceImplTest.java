/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.service.impl;

import org.apromore.common.ConfigBean;
import org.apromore.dao.*;
import org.apromore.dao.model.*;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.UserMetadataService;
import org.apromore.service.UserService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.powermock.api.easymock.PowerMock.*;

public class UserMetadataServiceImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMetadataServiceImplTest.class);
    // inject EntityManager for simple test
    private static EntityManagerFactory emf;
    private EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("TESTApromore");
        }
        return emf;
    }

    private UserService userSrv;

    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private String now = dateFormat.format(new Date());

    @Before
    public final void setUp() {
        GroupUsermetadataRepository groupUsermetadataRepository = createMock(GroupUsermetadataRepository.class);
        LogRepository logRepo = createMock(LogRepository.class);
        GroupLogRepository groupLogRepo = createMock(GroupLogRepository.class);
        userSrv = createMock(UserService.class);
        UsermetadataRepository userMetadataRepo = createMock(UsermetadataRepository.class);
        UsermetadataTypeRepository usermetadataTypeRepo = createMock(UsermetadataTypeRepository.class);
        UsermetadataLogRepository usermetadataLogRepo = createMock(UsermetadataLogRepository.class);

        ConfigBean config = new ConfigBean();

        UserMetadataService userMetadataService = new UserMetadataServiceImpl(logRepo, groupLogRepo, userSrv,
                groupUsermetadataRepository,
                userMetadataRepo, usermetadataTypeRepo, usermetadataLogRepo);
    }

    @Test
    public void getUserMetadata() throws UserNotFoundException {

//        String username = "user1";
//        User user1 = new User();
//        user1.setUsername(username);
//        user1.setGroup(new Group(1));
//
//        Integer userId = 1;
//        Integer logId = 1;
//        String reallyLongString = "a really long String";
//        expect(userSrv.findUserByLogin(username)).andReturn(user1);
//
//
//
//        replay(dashboardLayoutRepository);
//
//        String result = eventLogService.getLayoutByLogId(userId, logId);
//        verify(dashboardLayoutRepository);
//        assertThat(result, equalTo(reallyLongString));

    }

    @Test
    public void canUserEditMetadata() {
    }

    @Test
    @Rollback
    @Ignore("For pressure testing only")
    public void insertUsermetadataTest() {

        EntityManager em = getEntityManagerFactory().createEntityManager();
        assert em != null;

        UsermetadataType usermetadataType = new UsermetadataType();
        usermetadataType.setIsValid(true);
        usermetadataType.setType("Test Type");
        usermetadataType.setVersion(1);

        Usermetadata um = new Usermetadata();
        um.setContent("test content");
        um.setCreatedBy("user_UUID");
        um.setCreatedTime(now);
        um.setIsValid(true);
        um.setRevision(1);
        um.setUsermetadataType(usermetadataType);
        um.setUpdatedBy("user_UUID");
        um.setUpdatedTime(now);

        em.getTransaction().begin();

        em.persist(um);

        Query query = em.createQuery("SELECT s FROM Usermetadata s WHERE s.isValid=:param1")
                .setParameter("param1", true);
        List<Usermetadata> stats = query.getResultList();

        for (Usermetadata stat : stats) {
            LOGGER.info("RESULT: " + stat.getCreatedTime());
        }

//        em.flush();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @Rollback
    @Ignore("For pressure testing only")
    public void insertUsermetadataLogTest() {

        EntityManager em = getEntityManagerFactory().createEntityManager();
        assert em != null;

        UsermetadataType usermetadataType = new UsermetadataType();
        usermetadataType.setIsValid(true);
        usermetadataType.setType("Test Type");
        usermetadataType.setVersion(1);

        Usermetadata um = new Usermetadata();
        um.setContent("test content");
        um.setCreatedBy("user_UUID");
        um.setCreatedTime(now);
        um.setIsValid(true);
        um.setRevision(1);
        um.setUsermetadataType(usermetadataType);
        um.setUpdatedBy("user_UUID");
        um.setUpdatedTime(now);

        Query query = em.createQuery("SELECT s FROM Log s WHERE s.id=:param1")
                .setParameter("param1", 144);
        List<Log> logs = query.getResultList();

        Log log = logs.get(0);
        log.setCreateDate(now);

        UsermetadataLog ul = new UsermetadataLog();
        ul.setLog(log);
        ul.setUsermetadata(um);

        em.getTransaction().begin();

        em.persist(ul);

        em.getTransaction().commit();
        em.close();
    }

    @Test
    @Rollback
    @Ignore("For pressure testing only")
    public void insertGroupUsermetadataTest() {

        EntityManager em = getEntityManagerFactory().createEntityManager();
        assert em != null;

        Query query1 = em.createQuery("SELECT s FROM Usermetadata s WHERE s.id=:param1")
                .setParameter("param1", 10);
        List<Usermetadata> userMetadata = query1.getResultList();

        Usermetadata um = userMetadata.get(0);

        Query query = em.createQuery("SELECT s FROM Group s WHERE s.id=:param1")
                .setParameter("param1", 8);
        List<Group> groups = query.getResultList();

        Group group = groups.get(0);

        GroupUsermetadata gu = new GroupUsermetadata();
        gu.setGroup(group);
        gu.setUsermetadata(um);
        gu.setHasOwnership(true);
        gu.setHasRead(true);
        gu.setHasWrite(true);

        em.getTransaction().begin();

        em.persist(gu);

        em.getTransaction().commit();
        em.close();
    }


}
