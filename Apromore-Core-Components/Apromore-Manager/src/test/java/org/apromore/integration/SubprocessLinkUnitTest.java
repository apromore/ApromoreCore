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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import org.apromore.builder.UserManagementBuilder;
import org.apromore.dao.GroupProcessRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.SubprocessProcessRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.AccessRights;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupProcess;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.User;
import org.apromore.exception.CircularReferenceException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.ProcessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SubprocessLinkUnitTest extends BaseTest {
    private static final AccessRights OWNER_ACCESS = new AccessRights(true, true, true);
    private static final AccessRights EDITOR_ACCESS = new AccessRights(true, true, false);

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

    @Autowired
    GroupProcessRepository groupProcessRepository;

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

    @Test
    void testLinkCircularSubprocess() throws UserNotFoundException {
        String username1 = "subProcessLinkUnitTestLinkCircularSubprocess1";
        User user1 = createUser(username1);

        String username2 = "subProcessLinkUnitTestLinkCircularSubprocess2";
        User user2 = createUser(username2);

        Process process1 = new Process();
        Process process2 = new Process();
        Process process3 = new Process();
        Process process4 = new Process();
        List<Process> processes = List.of(process1, process2, process3, process4);
        processRepository.saveAllAndFlush(processes);

        //Set user process access
        grantAccess(user1, List.of(process1, process2, process3, process4), OWNER_ACCESS);
        grantAccess(user2, List.of(process1, process3), EDITOR_ACCESS);

        //Process cannot be linked to itself
        assertThrows(CircularReferenceException.class, () ->
            processService.linkSubprocess(process1.getId(), subprocessId, process1.getId(), username1));

        //Process can be linked to other processes
        try {
            processService.linkSubprocess(process1.getId(), subprocessId, process2.getId(), username1);
            processService.linkSubprocess(process2.getId(), subprocessId, process3.getId(), username1);
        } catch (CircularReferenceException e) {
            fail();
        }
        assertEquals(process2.getId(),
            subprocessProcessRepository.getLinkedProcess(process1.getId(), subprocessId).getId());
        assertEquals(process3.getId(),
            subprocessProcessRepository.getLinkedProcess(process2.getId(), subprocessId).getId());

        assertEquals(process2.getId(),
            processService.getLinkedProcesses(process1.getId(), username1).get(subprocessId));
        assertEquals(process3.getId(),
            processService.getLinkedProcesses(process2.getId(), username1).get(subprocessId));
        assertTrue(processService.getLinkedProcesses(process1.getId(), username2).isEmpty());

        //If processes 1 -> 2 -> 3 are linked, 3 -> 1 cannot be linked by a user with access to all three processes.
        assertThrows(CircularReferenceException.class, () ->
            processService.linkSubprocess(process3.getId(), subprocessId, process1.getId(), username1));

        //Link process 3 -> 1 with a user that doesn't have access to process 2.
        //(i.e. 1 -> 2 -> 3 link is broken for this user.)
        try {
            processService.linkSubprocess(process3.getId(), subprocessId, process1.getId(), username2);
        } catch (CircularReferenceException e) {
            fail();
        }
        assertEquals(process1.getId(),
            subprocessProcessRepository.getLinkedProcess(process3.getId(), subprocessId).getId());

        //A fourth process can be linked to a process already linked in a loop
        try {
            processService.linkSubprocess(process4.getId(), subprocessId, process1.getId(), username1);
        } catch (CircularReferenceException e) {
            fail();
        }
        assertEquals(process1.getId(),
            subprocessProcessRepository.getLinkedProcess(process4.getId(), subprocessId).getId());
    }

    private User createUser(String username) {
        Group group1 = groupRepository.saveAndFlush(builder.withGroup(username, "USER").buildGroup());
        User user1 = builder.withGroup(group1)
            .withMembership(username + "@t.com")
            .withUser(username, "first","last", "org").buildUser();
        return userRepository.saveAndFlush(user1);
    }

    private void grantAccess(User user, List<Process> processes, AccessRights accessRights) {
        List<GroupProcess> groupProcesses = new ArrayList<>();
        for (Process process : processes) {
            groupProcesses.add(new GroupProcess(process, user.getGroup(), accessRights));
        }
        groupProcessRepository.saveAllAndFlush(groupProcesses);
    }
}
