package org.apromore.mapper;

import org.apromore.dao.model.SearchHistory;
import org.apromore.dao.model.User;
import org.apromore.model.SearchHistoriesType;
import org.apromore.model.UserType;
import org.apromore.model.UsernamesType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * User Mapper Unit test.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class UserMapperUnitTest {

    UserMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new UserMapper();
    }

    @Test
    public void testMapUserNames() throws Exception {
        List<User> usrs = new ArrayList<>();
        User usr1 = new User();
        usr1.setUsername("Bob1");
        User usr2 = new User();
        usr2.setUsername("Bob2");

        usrs.add(usr1);
        usrs.add(usr2);

        UsernamesType result = mapper.convertUsernameTypes(usrs);
        assertThat(result.getUsername().size(), equalTo(usrs.size()));
        assertThat(result.getUsername().get(0), equalTo(usrs.get(0).getUsername()));
    }


    @Test
    public void testMapUserType() throws Exception {
        SearchHistory searchHist = new SearchHistory();
        searchHist.setId(1);
        searchHist.setSearch("cheque processing");
        Set<SearchHistory> searches = new HashSet<>();
        searches.add(searchHist);

        User user = new User();

        user.setUsername("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setDateCreated(Calendar.getInstance().getTime());
        user.setLastActivityDate(Calendar.getInstance().getTime());

        UserType userType = mapper.convertUserTypes(user);

        assertThat(userType.getUsername(), equalTo(user.getUsername()));
        assertThat(userType.getFirstName(), equalTo(user.getFirstName()));
        assertThat(userType.getLastName(), equalTo(user.getLastName()));
        //assertThat(userType.getEmail(), equalTo(user.getEmail()));
        //assertThat(userType.getPasswd(), equalTo(user.getPasswd()));
        //assertThat(userType.getSearchHistories().size(), equalTo(user.getSearchHistories().size()));
    }

    @Test
    public void testMapFromUserType() {
        SearchHistoriesType searchHistType = new SearchHistoriesType();
        searchHistType.setNum(1);
        searchHistType.setSearch("playdo");

        List<SearchHistoriesType> searchTypes = new ArrayList<SearchHistoriesType>();
        searchTypes.add(searchHistType);

//        UserType userType = new UserType();
//        userType.setUsername("billyb");
//        userType.setFirstname("Billy");
//        userType.setLastname("Bob");
//        userType.setEmail("billyb@gmail.com");
//        userType.setPasswd("password");
//        userType.getSearchHistories().addAll(searchTypes);
//
//        User user = mapper.convertFromUserType(userType);
//
//        assertThat(user.getUsername(), equalTo(userType.getUsername()));
//        assertThat(user.getFirstName()ame(), equalTo(userType.getFirstName()));
//        assertThat(user.getLastname(), equalTo(userType.getLastname()));
//        assertThat(user.getEmail(), equalTo(userType.getEmail()));
//        assertThat(user.getPasswd(), equalTo(userType.getPasswd()));
//        assertThat(user.getSearchHistories().size(), equalTo(userType.getSearchHistories().size()));
    }
}
