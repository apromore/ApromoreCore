package org.apromore.dao.jpa.process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import org.apromore.config.BaseTestClass;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.RoleRepository;
import org.apromore.dao.SubprocessProcessRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.jpa.usermanagement.UserManagementBuilder;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.SubprocessProcess;
import org.apromore.dao.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SubprocessProcessUnitTest extends BaseTestClass {
    Process process1;
    Process process2;
    User user;
    String subprocessId = "Test";

    @Autowired
    SubprocessProcessRepository subprocessProcessRepository;

    @Autowired
    ProcessRepository processRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    UserManagementBuilder builder = new UserManagementBuilder();

    @BeforeEach
    void setup() {
        process1 = new Process();
        processRepository.saveAndFlush(process1);

        process2 = new Process();
        processRepository.saveAndFlush(process2);

//        user = userBuilder.withUser("TestUser", "first", "last", "org").buildUser();
        Group group = groupRepository.saveAndFlush(builder.withGroup("testGroup1", "USER").buildGroup());
        Role role = roleRepository.saveAndFlush(builder.withRole("testRole").buildRole());
        user = builder.withGroup(group).withRole(role).withMembership("subprocessProcessUnitTest@test.com")
            .withUser("SubprocessProcessUnitTestUser", "first",
            "last", "org").buildUser();
        userRepository.saveAndFlush(user);

        SubprocessProcess subprocessProcess = new SubprocessProcess();
        subprocessProcess.setId(1);
        subprocessProcess.setSubprocessId(subprocessId);
        subprocessProcess.setSubprocessParent(process1);
        subprocessProcess.setLinkedProcess(process2);
        subprocessProcess.setUser(user);

        subprocessProcessRepository.saveAndFlush(subprocessProcess);
    }

    @Test
    public void testGetLinkedProcess() {
        assertEquals(process2, subprocessProcessRepository.getLinkedProcess(process1.getId(), subprocessId, user.getId()));
        assertNull(subprocessProcessRepository.getLinkedProcess(-1, subprocessId, -1));
    }

}
