package org.apromore.security.model;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * Just a shell class so Devs doen't confused between the User Model and the Security User Model.
 *
 * @author Cameron James
 */
public class ApromoreUserDetails extends User {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Collection<? extends ApromoreRoleDetails> roles = new ArrayList<>();
    private Collection<? extends ApromorePermissionDetails> permissions = new ArrayList<>();


    /**
     * Default Constructor to create the User Details object.
     * @param username the username presented to the <code>DaoAuthenticationProvider</code>
     * @param password the password that should be presented to the <code>DaoAuthenticationProvider</code>
     * @param enabled set to <code>true</code> if the user is enabled
     * @param accountNonExpired set to <code>true</code> if the account has not expired
     * @param credentialsNonExpired set to <code>true</code> if the credentials have not expired
     * @param accountNonLocked set to <code>true</code> if the account is not locked
     * @param authorities the authorities that should be granted to the caller if they presented the correct username and password and the user is enabled. Not null.
     * @throws IllegalArgumentException if a <code>null</code> value was passed either as a parameter or as an element in the <code>GrantedAuthority</code> collection
     */
    public ApromoreUserDetails(String id, String firstName, String lastName, String username, String password, boolean enabled,
            boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, String email,
            Collection<? extends GrantedAuthority> authorities, Collection<? extends ApromoreRoleDetails> roles,
            Collection<? extends ApromorePermissionDetails> permissions) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Collection<? extends ApromorePermissionDetails> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<? extends ApromorePermissionDetails> permissions) {
        this.permissions = permissions;
    }

    public Collection<? extends ApromoreRoleDetails> getRoles() {
        return roles;
    }

    public void setRoles(Collection<? extends ApromoreRoleDetails> roles) {
        this.roles = roles;
    }

}
