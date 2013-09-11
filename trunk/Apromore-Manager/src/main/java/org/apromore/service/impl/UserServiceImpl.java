package org.apromore.service.impl;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import org.apromore.dao.SearchHistoryRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.SearchHistory;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the UserService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    private static final Integer MIN_SEARCH_SAVE = 0;
    private static final Integer MAX_SEARCH_SAVE = 10;

    private UserRepository userRepo;
    private SearchHistoryRepository searchHistoryRepo;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param userRepository User Repository.
     */
    @Inject
    public UserServiceImpl(final UserRepository userRepository, final SearchHistoryRepository searchHistoryRepository) {
        userRepo = userRepository;
        searchHistoryRepo = searchHistoryRepository;
    }



    /**
     * @see org.apromore.service.UserService#findAllUsers()
     * {@inheritDoc}
     * <p/>
     * NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    /**
     * @see org.apromore.service.UserService#findUserByLogin(String)
     * {@inheritDoc}
     */
    @Override
    public User findUserByLogin(String username) throws UserNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user != null) {
            return user;
        } else {
            throw new UserNotFoundException("User with username (" + username + ") could not be found.");
        }
    }

    /**
     * @see org.apromore.service.UserService#writeUser(org.apromore.dao.model.User)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void writeUser(User user) {
        User dbUser = userRepo.findOne(user.getId());
        dbUser.setSearchHistories(user.getSearchHistories());
        userRepo.save(dbUser);
    }

    /**
     * @see org.apromore.service.UserService#updateUserSearchHistory(org.apromore.dao.model.User, java.util.List)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void updateUserSearchHistory(User user, List<SearchHistory> searchHistories) {
        List<SearchHistory> history = new ArrayList<>();
        User dbUser = userRepo.findByUsername(user.getUsername());

        if (searchHistories != null) {
            searchHistories = updateSearchHistoriesWithUser(dbUser, searchHistories);
            if (searchHistories.size() > 10) {
               for (SearchHistory searchHistory : searchHistories) {
                   if (searchHistory.getIndex() > MIN_SEARCH_SAVE && searchHistory.getIndex() < MAX_SEARCH_SAVE) {
                       history.add(searchHistory);
                   }
               }
            } else {
                history = searchHistories;
            }
        }
        user.setSearchHistories(history);

        searchHistoryRepo.save(history);
        userRepo.save(dbUser);
    }

    /* Need to update the search history records with the user we are attaching to. */
    private List<SearchHistory> updateSearchHistoriesWithUser(User user, List<SearchHistory> searchHistories) {
        List<SearchHistory> updatedSearchHistories = new ArrayList<>();
        if (user != null && searchHistories != null) {
            for (SearchHistory searchHistory : searchHistories) {
                searchHistory.setUser(user);
                updatedSearchHistories.add(searchHistory);
            }
        }
        return updatedSearchHistories;
    }

}
