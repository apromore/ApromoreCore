package org.apromore.portal.ldap;

import org.springframework.ldap.core.LdapTemplate;

import java.util.List;
/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 19/06/12
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */

public interface LDAPUserService {

    void setLdapTemplate(final LdapTemplate ldapTemplate);

    LDAPUser getUser(final String email);

    LDAPUser save(final LDAPUser user);

    boolean authenticate(final String userName, final String password);

    List<LDAPUser> getUsers(final String pattern);

    void delete(final LDAPUser user);
}

