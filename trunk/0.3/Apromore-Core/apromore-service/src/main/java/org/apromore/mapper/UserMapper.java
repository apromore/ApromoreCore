package org.apromore.mapper;

import org.apromore.dao.model.SearchHistory;
import org.apromore.dao.model.User;
import org.apromore.model.SearchHistoriesType;
import org.apromore.model.UserType;
import org.apromore.model.UsernamesType;

import java.util.List;

/**
 * Mapper helper class to convert from the DAO Model to the Webservice Model.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class UserMapper {

    /**
     * Convert the List of Users to a UserNamesType Webservice object.
     * @param users the list of Users
     * @return the UsernameType object
     */
    public static UsernamesType convertUsernameTypes(List<User> users) {
        UsernamesType userNames = new UsernamesType();
        for (User usr : users) {
            userNames.getUsername().add(usr.getUsername());
        }
        return userNames;
    }

    /**
     * Convert a user object to a UserType Webservice object.
     * @param user the DB User Model
     * @return the Webservice UserType
     */
    public static UserType convertUserTypes(User user) {
        UserType userType = new UserType();
        userType.setLastname(user.getLastname());
        userType.setFirstname(user.getFirstname());
        userType.setUsername(user.getUsername());
        userType.setEmail(user.getEmail());
        userType.setPasswd(user.getPasswd());
        for (SearchHistory esm : user.getSearchHistories()) {
            SearchHistoriesType sht = new SearchHistoriesType();
            sht.setNum(esm.getNum());
            sht.setSearch(esm.getSearch());
            userType.getSearchHistories().add(sht);
        }
        return userType;
    }

    /**
     * Convert from the WS (UserType) to the DB model (User).
     * @param userType the userType from the WebService
     * @return the User dao model populated.
     */
    public static User convertFromUserType(UserType userType) {
        User user = new User();
        user.setLastname(userType.getLastname());
        user.setFirstname(userType.getFirstname());
        user.setUsername(userType.getUsername());
        user.setEmail(userType.getEmail());
        user.setPasswd(userType.getPasswd());
        user.setSearchHistories(SearchHistoryMapper.convertFromSearchHistoriesType(userType.getSearchHistories()));
        return user;
    }

}
