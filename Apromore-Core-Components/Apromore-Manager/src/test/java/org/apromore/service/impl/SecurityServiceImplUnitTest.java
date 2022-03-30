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
package org.apromore.service.impl;

import java.security.NoSuchAlgorithmException;
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
class SecurityServiceImplUnitTest {

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
     * @param allowedPasswordHashingAlgorithms
     * @param passwordHashingAlgorithm
     * @param saltLength  the number of alphanumeric characters in newly-generated salts
     * @param upgradePasswords
     * @param hashedPassword  the hash of "password" to test the <var>password</var> against.
     *     Beware that this value must not be zero-extended.
     *     MD5("password") is "5f4dcc3b5aa765d61d8327deb882cf99".
     *     MD5("passwordNaCl") is "f25b019a9470318d44d60e1416631f34".
     *     SHA-1("passwordNaCl") is "40274892d2fe01a6ab1e0fbde5c22b8312d10780".
     *     SHA-256("passwordNaCl") is "028480971104b37691f41c430e59e07fd4c5ae0f53317b2aa2e06cf8ddbbfe10".
     * @param salt  the initial salt to test <var>hashedPassword</var> against;
     *     ignored if {@link Membership#MD5_UNSALTED} is in effect
     * @param password
     * @param expectChanged  do we expect the password change to be permitted?
     */
    @ParameterizedTest
    @CsvSource({ // change an unsalted password (salt ignored)
                "MD5-UNSALTED,     MD5-UNSALTED, 3, false, MD5-UNSALTED, 5f4dcc3b5aa765d61d8327deb882cf99, NaCl, password, true",
                "MD5-UNSALTED,     MD5-UNSALTED, 3, false, MD5-UNSALTED, 5f4dcc3b5aa765d61d8327deb882cf99, KCl,  password, true",

                // change a salted password
                "MD5,              MD5,          3, false, MD5,          f25b019a9470318d44d60e1416631f34, NaCl, password, true",
                "SHA-1,            SHA-1,        3, false, SHA-1,        40274892d2fe01a6ab1e0fbde5c22b8312d10780, NaCl, password, true",
                "SHA-256,          SHA-256,      3, false, SHA-256,       28480971104b37691f41c430e59e07fd4c5ae0f53317b2aa2e06cf8ddbbfe10, NaCl, password, true",

                // change a password and upgrade its algorithm
                "MD5-UNSALTED MD5, MD5,          3, true,  MD5-UNSALTED, 5f4dcc3b5aa765d61d8327deb882cf99, NaCl, password, true",
                "MD5 SHA-256,      SHA-256,      3, true,  MD5,          f25b019a9470318d44d60e1416631f34, NaCl, password, true",

                // can't change because current algorithm isn't allowed for authentication
                "MD5,              MD5,          3, false, MD5-UNSALTED, 5f4dcc3b5aa765d61d8327deb882cf99, NaCl, password, false",
                "MD5,              MD5,          3, true,  MD5-UNSALTED, 5f4dcc3b5aa765d61d8327deb882cf99, NaCl, password, false",

                // can't change because the password is wrong
                "MD5,              MD5,          3, false, MD5,          f25b019a9470318d44d60e1416631f34, NaCl, badpass, false",
                "MD5,              MD5,          3, true,  MD5,          f25b019a9470318d44d60e1416631f34, NaCl, badpass, false"})
    void testChangeUserPassword(String  allowedPasswordHashingAlgorithms,
                                String  passwordHashingAlgorithm,
                                int     saltLength,
                                boolean upgradePasswords,
                                String  hashingAlgorithm,
                                String  hashedPassword,
                                String  salt,
                                String  password,
                                boolean expectChanged) {

        securityService.allowedPasswordHashingAlgorithms = allowedPasswordHashingAlgorithms;
        securityService.passwordHashingAlgorithm = passwordHashingAlgorithm;
        securityService.saltLength = saltLength;
        securityService.upgradePasswords = upgradePasswords;

        Membership membership = new Membership();
        membership.setEmail("user@example.com");
        membership.setHashingAlgorithm(hashingAlgorithm);
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
        boolean changed = securityService.changeUserPassword("user", password, "changedPassword");

        // Validate the result
        verify(userRepository);
        verify(membershipRepository);

        assertEquals(expectChanged, changed);

        try {
            if (changed) {
                assertTrue(SecurityUtil.authenticate(membership, "changedPassword"));
                assertEquals(upgradePasswords ? passwordHashingAlgorithm : hashingAlgorithm, membership.getHashingAlgorithm());
                if (!Membership.MD5_UNSALTED.equals(membership.getHashingAlgorithm())) {
                    assertEquals(saltLength, membership.getSalt().length());  // if we're salting, the new salt is long enough
                }

            } else {
                assertTrue(SecurityUtil.authenticate(membership, "password"));
                assertEquals(hashedPassword, membership.getPassword());
                assertEquals(salt, membership.getSalt());
            }
        } catch (NoSuchAlgorithmException e) {
            fail("Unsupported hashing algorithm: " + membership.getHashingAlgorithm());
        }
    }
}
