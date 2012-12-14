package org.apromore.portal.ldap;

public interface LDAPUser {

    String getUserName();
    void setUserName(String userName);

    String getFirstName();
    void setFirstName(String firstName);

    String getLastName();
    void setLastName(String lastName);

    String getEmail();
    void setEmail(String email);

    String getPassword();
    void setPassword(String password);

    String getDepartment();
    void setDepartment(String departement);

    String[] getGroups();
    void setGroups(String[] groups);
}
