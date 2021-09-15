package org.apromore.service.impl;

import org.apromore.dao.MembershipRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.Membership;
import org.apromore.dao.model.User;
import org.apromore.security.util.SecurityUtil;
import org.apromore.service.SecurityService;
import org.apromore.service.impl.SecurityServiceImpl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test suite for {@link SecurityServiceImpl}.
 */
public class SecurityServiceImplUnitTest {

    private MembershipRepository membershipRepository;
    private SecurityServiceImpl securityService;
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository = createMock(UserRepository.class);
        membershipRepository = createMock(MembershipRepository.class);
        securityService = new SecurityServiceImpl(
            userRepository,
            null,  // GroupRepository
            null,  // RoleRepository
            null,  // PermissionRepository
            membershipRepository,
            null,  // WorkspaceService
            null); // MailSender
    }

    /**
     * Test the {@link SecurityService#changeUserPassword} method.
     *
     * @param enableSaltingPasswords  whether to generate new password hashes using salt
     * @param enableUnsaltedPasswords  whether to accept old passwords if they match the unsalted hash
     * @param saltLength  the number of alphanumeric characters in newly-generated salts
     * @param hashedPassword  the hash to test the password "password" against.
     *     MD5("password") is "5f4dcc3b5aa765d61d8327deb882cf99".
     *     MD5("passwordsalt") is "b305cadbb3bce54f3aa59c64fec00dea".
     * @param salt  the initial salt to test <var>hashedPassword</var> against; may be
           ignored if <var>enableUnsaltedPasswords</var> is set
     * @param expectChanged  do we expect the password change to be permitted?
     */
    @ParameterizedTest
    @CsvSource({"false, true,  10, 5f4dcc3b5aa765d61d8327deb882cf99, salt, true",
                "true,  true,  10, 5f4dcc3b5aa765d61d8327deb882cf99, salt, true",
                "true,  false, 10, 5f4dcc3b5aa765d61d8327deb882cf99, salt, false",
                "true,  false, 10, b305cadbb3bce54f3aa59c64fec00dea, salt, true"})
    public void testChangeUserPassword(boolean enableSaltingPasswords,
                                       boolean enableUnsaltedPasswords,
                                       int     saltLength,
                                       String  hashedPassword,
                                       String  salt,
                                       boolean expectChanged) {

        securityService.enableSaltingPasswords = enableSaltingPasswords;
        securityService.enableUnsaltedPasswords = enableUnsaltedPasswords;
        securityService.saltLength = saltLength;

        Membership membership = new Membership();
        membership.setEmail("user@example.com");
        membership.setPassword(hashedPassword);
        membership.setSalt(salt);

        User user = new User();
        user.setMembership(membership);

        expect(userRepository.findByUsername("user")).andReturn(user);
        replay(userRepository);

        if (expectChanged) {
            expect(membershipRepository.save(membership)).andReturn(membership);
        }
        replay(membershipRepository);

        // Perform the test
        boolean changed = securityService.changeUserPassword("user", "password", "changedPassword");

        // Validate the result
        verify(userRepository);
        verify(membershipRepository);

        assertEquals(expectChanged, changed);

        if (!changed) {
            // We expect that that neither the password nor the salt were changed
            assertEquals(hashedPassword, membership.getPassword());
            assertEquals(salt, membership.getSalt());

        } else if (enableSaltingPasswords) {
            // We expect that the password was changed and uses a new randomized salt
            assertEquals(SecurityUtil.hashPassword("changedPassword" + membership.getSalt()), membership.getPassword());
            assertEquals(saltLength, membership.getSalt().length());

        } else {
            // We expect that the password was changed, but the hash is unsalted
            assertEquals(SecurityUtil.hashPassword("changedPassword"), membership.getPassword());
            assertEquals(salt, membership.getSalt());
        }
    }
}
