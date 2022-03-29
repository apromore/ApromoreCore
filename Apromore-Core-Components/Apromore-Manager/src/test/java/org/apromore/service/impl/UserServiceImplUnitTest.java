/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apromore.dao.SearchHistoryRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.SearchHistory;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test the UserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
class UserServiceImplUnitTest {

    private UserServiceImpl usrServiceImpl;
    private UserRepository usrRepo;
    private SearchHistoryRepository searchHistoryRepo;

    @BeforeEach
    final void setUp() throws Exception {
        usrRepo = createMock(UserRepository.class);
        searchHistoryRepo = createMock(SearchHistoryRepository.class);
        usrServiceImpl = new UserServiceImpl(usrRepo, searchHistoryRepo);
    }

    @Test
    void getAllUsers() {
        List<User> users = new ArrayList<>();

        expect(usrRepo.findAll()).andReturn(users);
        replay(usrRepo);

        List<User> serviceUsers = usrServiceImpl.findAllUsers();
        verify(usrRepo);
        assertThat(serviceUsers, equalTo(users));
    }

    @Test
    void getUser() throws Exception {
        String username = "jaybob";
        User usr = new User();

        expect(usrRepo.findByUsername(username)).andReturn(usr);
        replay(usrRepo);

        User serviceUsr = usrServiceImpl.findUserByLogin(username);
        verify(usrRepo);
        assertThat(serviceUsr.getId(), equalTo(usr.getId()));
    }

    @Test
    void getUserNotFound() throws Exception {
        String username = "jaybob";

        expect(usrRepo.findByUsername(username)).andReturn(null);
        replay(usrRepo);

        assertThrows(UserNotFoundException.class, ()->{
            usrServiceImpl.findUserByLogin(username);

            verify(usrRepo);
        });

    }


    @Test
    void writeUser() {
        Integer id = 1;
        String username = "username";
        User usr = createUser();

        expect(usrRepo.findById(id)).andReturn(Optional.of(usr));
        expect(usrRepo.save((User) anyObject())).andReturn(usr);
        replay(usrRepo);

        usrServiceImpl.writeUser(usr);
        verify(usrRepo);

        assertThat(username, equalTo(usr.getUsername()));
    }

    @Test
    void testUpdateUserSearchHistory() {
        String username = "username";
        User usr = createUser();
        List<SearchHistory> histories = new ArrayList<>();
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setSearch("");
        histories.add(searchHistory);

        expect(usrRepo.findByUsername(username)).andReturn(usr);
        expect(usrRepo.saveAndFlush((User) anyObject())).andReturn(usr);
        expect(usrRepo.saveAndFlush((User) anyObject())).andReturn(usr);
        replay(usrRepo);

        usrServiceImpl.updateUserSearchHistory(usr, histories);
        verify(usrRepo);

        assertThat(username, equalTo(usr.getUsername()));
    }

    private User createUser() {
        User user = new User();

        user.setId(1);
        user.setUsername("username");
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setDateCreated(new Date());
        user.setLastActivityDate(new Date());

        return user;
    }
}
