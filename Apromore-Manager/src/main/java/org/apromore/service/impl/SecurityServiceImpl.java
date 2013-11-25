package org.apromore.service.impl;

import org.apromore.dao.MembershipRepository;
import org.apromore.dao.PermissionRepository;
import org.apromore.dao.RoleRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.Permission;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.SecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of the SecurityService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class SecurityServiceImpl implements SecurityService {

    private static final String ROLE_USER = "ROLE_USER";

    private UserRepository userRepo;
    private RoleRepository roleRepo;
    private PermissionRepository permissionRepo;
    private MembershipRepository membershipRepo;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param userRepository User Repository.
     * @param roleRepository Role Repository.
     */
    @Inject
    public SecurityServiceImpl(final UserRepository userRepository, final RoleRepository roleRepository,
            final PermissionRepository permissionRepository, final MembershipRepository membershipRepository) {
        userRepo = userRepository;
        roleRepo = roleRepository;
        permissionRepo = permissionRepository;
        membershipRepo = membershipRepository;
    }


    /**
     * @see org.apromore.service.SecurityService#getAllUsers()
     * {@inheritDoc}
     *
     * NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * @see org.apromore.service.SecurityService#getUserByName(String)
     * {@inheritDoc}
     */
    @Override
    public User getUserByName(String username) {
        return userRepo.findByUsername(username);
    }

    /**
     * @see org.apromore.service.SecurityService#getUserByName(String)
     * {@inheritDoc}
     */
    @Override
    public List<User> searchUsers(String searchString) {
        return userRepo.findByUsernameLike(searchString);
    }

    /**
     * @see org.apromore.service.SecurityService#login(String, String)
     * {@inheritDoc}
     */
    @Override
    public User login(String username, String password){
        return userRepo.login(username, password);
    }

    /**
     * @see org.apromore.service.SecurityService#getUserById(String)
     * {@inheritDoc}
     */
    @Override
    public User getUserById(String guid) throws UserNotFoundException{
        User user = userRepo.findByRowGuid(guid);
        if (user != null) {
            return user;
        } else {
            throw new UserNotFoundException("User with id (" + guid + ") could not be found.");
        }
    }

    /**
     * @see org.apromore.service.SecurityService#getUserPermissions(String)
     * {@inheritDoc}
     */
    @Override
    public List<Permission> getUserPermissions(String userGuid){
        return permissionRepo.findByUser(userGuid);
    }

    /**
     * @see org.apromore.service.SecurityService#hasAccess(String, String)
     * {@inheritDoc}
     */
    @Override
    public boolean hasAccess(String userId, String permissionId){
        return userRepo.hasAccess(userId, permissionId);
    }

    /**
     * @see org.apromore.service.SecurityService#createUser(org.apromore.dao.model.User)
     * {@inheritDoc}
     */
    @Override
    public User createUser(User user) {
        user.setDateCreated(Calendar.getInstance().getTime());
        user.setLastActivityDate(Calendar.getInstance().getTime());
        user.setRowGuid(UUID.randomUUID().toString());

        Role existingRole = roleRepo.findByName(ROLE_USER);
        if (existingRole != null) {
            Set<Role> roles = user.getRoles();
            roles.add(existingRole);
            user.setRoles(roles);

            Set<User> rolesUsers = existingRole.getUsers();
            rolesUsers.add(user);
            existingRole.setUsers(rolesUsers);
        }

        userRepo.save(user);

        user.setMembership(user.getMembership());
        user.getMembership().setUser(user);
        membershipRepo.save(user.getMembership());

        return user;
    }

}
