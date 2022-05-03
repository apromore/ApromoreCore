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
import org.apromore.dao.RoleRepository;
import org.apromore.dao.SubprocessProcessRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.exception.ResourceNotFoundException;
import org.apromore.service.ProcessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SubprocessLinkUnitTest extends BaseTest {
    String subprocessId = "Test";

    UserManagementBuilder builder;

    @Autowired
    SubprocessProcessRepository subprocessProcessRepository;

    @Autowired
    ProcessRepository processRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ProcessService processService;

    @BeforeEach
    final void setUp() {
        builder = new UserManagementBuilder();
    }

    @Test
    void testReLinkSameSubprocess() throws ResourceNotFoundException {
        Process process1 = new Process();
        processRepository.saveAndFlush(process1);

        Process process2 = new Process();
        processRepository.saveAndFlush(process2);

        Process process3 = new Process();
        processRepository.saveAndFlush(process3);

        Group group = groupRepository.saveAndFlush(builder.withGroup("testGroup1", "USER").buildGroup());
        Role role = roleRepository.saveAndFlush(builder.withRole("testRole").buildRole());
        User user = builder.withGroup(group).withRole(role).withMembership("testReLinkSameSubprocess@test.com")
            .withUser("testReLinkSameSubprocess", "first",
                "last", "org").buildUser();
        userRepository.saveAndFlush(user);

        processService.linkSubprocess(process1.getId(), subprocessId, process2.getId(), user.getUsername());
        assertEquals(process2.getId(),
            subprocessProcessRepository.getLinkedProcess(process1.getId(), subprocessId, user.getId()).getId());

        processService.linkSubprocess(process1.getId(), subprocessId, process3.getId(), user.getUsername());
        assertEquals(process3.getId(),
            subprocessProcessRepository.getLinkedProcess(process1.getId(), subprocessId, user.getId()).getId());
    }

    @Test
    void testUnLinkSubprocess() throws ResourceNotFoundException {
        Process process1 = new Process();
        processRepository.saveAndFlush(process1);

        Process process2 = new Process();
        processRepository.saveAndFlush(process2);

        Group group = groupRepository.saveAndFlush(builder.withGroup("testGroup1", "USER").buildGroup());
        Role role = roleRepository.saveAndFlush(builder.withRole("testRole").buildRole());
        User user = builder.withGroup(group).withRole(role).withMembership("testUnLinkSubprocess@test.com")
            .withUser("testUnLinkSubprocess", "first",
                "last", "org").buildUser();
        userRepository.saveAndFlush(user);

        processService.linkSubprocess(process1.getId(), subprocessId, process2.getId(), user.getUsername());
        assertEquals(process2.getId(),
            subprocessProcessRepository.getLinkedProcess(process1.getId(), subprocessId, user.getId()).getId());

        processService.unlinkSubprocess(process1.getId(), subprocessId, user.getUsername());
        assertNull(subprocessProcessRepository.getLinkedProcess(process1.getId(), subprocessId, user.getId()));
    }
}
