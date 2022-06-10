/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apromore.builder.UserManagementBuilder;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.SubprocessProcessRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.User;
import org.apromore.exception.CircularReferenceException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.ProcessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SubprocessLinkUnitTest extends BaseTest {
    String subprocessId = "Test";

    UserManagementBuilder builder;

    @Autowired
    SubprocessProcessRepository subprocessProcessRepository;

    @Autowired
    ProcessRepository processRepository;

    @Autowired
    ProcessService processService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @BeforeEach
    void oneTimeSetup() {
        builder = new UserManagementBuilder();
    }

    @Test
    void testReLinkSameSubprocess() throws UserNotFoundException, CircularReferenceException {
        String username = "subProcessLinkUnitTestReLinkSameSubprocess";
        Group group = groupRepository.saveAndFlush(builder.withGroup("testGroup1", "USER").buildGroup());
        User user = builder.withGroup(group).withMembership("subProcessLinkUnitTestReLinkSameSubprocess@t.com").withUser(username, "first",
            "last", "org").buildUser();
        userRepository.saveAndFlush(user);

        Process process1 = new Process();
        process1.setUser(user);
        processRepository.saveAndFlush(process1);

        Process process2 = new Process();
        process2.setUser(user);
        processRepository.saveAndFlush(process2);

        Process process3 = new Process();
        process3.setUser(user);
        processRepository.saveAndFlush(process3);

        processService.linkSubprocess(process1.getId(), subprocessId, process2.getId(), username);
        assertEquals(process2.getId(),
            subprocessProcessRepository.getLinkedProcess(process1.getId(), subprocessId).getId());

        processService.linkSubprocess(process1.getId(), subprocessId, process3.getId(), username);
        assertEquals(process3.getId(),
            subprocessProcessRepository.getLinkedProcess(process1.getId(), subprocessId).getId());
    }

    @Test
    void testUnLinkSubprocess() throws UserNotFoundException, CircularReferenceException {
        String username = "subProcessLinkUnitTestUnLinkSubprocess";
        Group group = groupRepository.saveAndFlush(builder.withGroup("testGroup1", "USER").buildGroup());
        User user = builder.withGroup(group).withMembership("subProcessLinkUnitTestUnLinkSubprocess@t.com").withUser(username, "first",
            "last", "org").buildUser();
        userRepository.saveAndFlush(user);

        Process process1 = new Process();
        process1.setUser(user);
        processRepository.saveAndFlush(process1);

        Process process2 = new Process();
        process2.setUser(user);
        processRepository.saveAndFlush(process2);

        processService.linkSubprocess(process1.getId(), subprocessId, process2.getId(), username);
        assertEquals(process2.getId(),
            subprocessProcessRepository.getLinkedProcess(process1.getId(), subprocessId).getId());

        processService.unlinkSubprocess(process1.getId(), subprocessId);
        assertNull(subprocessProcessRepository.getLinkedProcess(process1.getId(), subprocessId));
    }
}
