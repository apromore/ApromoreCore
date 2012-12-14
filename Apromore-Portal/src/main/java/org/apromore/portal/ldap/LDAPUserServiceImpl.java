package org.apromore.portal.ldap;

import org.apache.commons.codec.binary.Base64;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 19/06/12
 * Time: 8:59 PM
 * To change this template use File | Settings | File Templates.
 */
@Service(value = "LDAPUserService")
public class LDAPUserServiceImpl implements LDAPUserService {

    public static final String BASE_DN = "o=apromoread";

    private static class UserAttributesMapper implements AttributesMapper {

        public Object mapFromAttributes(Attributes attrs) throws NamingException {
            LDAPUser user = new LDAPUserImpl();
            if (attrs.get("uid") != null) {
                user.setUserName((String) attrs.get("uid").get());
            }
            if (attrs.get("cn") != null) {
                user.setFirstName((String) attrs.get("cn").get());
            }
            if (attrs.get("sn") != null) {
                user.setLastName((String) attrs.get("sn").get());
            }
            if (attrs.get("mail") != null) {
                user.setEmail((String) attrs.get("mail").get());
            }
            return user;
        }
    }

    private LdapTemplate ldapTemplate;

    public void setLdapTemplate(final LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public boolean authenticate(String userName, String password) {
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", "person")).and(new EqualsFilter("uid", userName));
        return ldapTemplate.authenticate(DistinguishedName.EMPTY_PATH, filter.toString(), password);
    }

    @SuppressWarnings("unchecked")
    public LDAPUser getUser(final String userName) {
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", "person")).and(new EqualsFilter("uid", userName));
        List<LDAPUser> users = ldapTemplate.search(DistinguishedName.EMPTY_PATH, filter.encode(), new UserAttributesMapper());
        if (!users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<LDAPUser> getUsers(final String pattern) {
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", "person"));
        if (pattern != null) {
            filter.and(new LikeFilter("uid", pattern));
        }
        return ldapTemplate.search(DistinguishedName.EMPTY_PATH, filter.encode(), new UserAttributesMapper());
    }

    public LDAPUser save(final LDAPUser user) {
        Name dn = buildDn(user);
        ldapTemplate.bind(dn, null, buildAttributes(user));

        // Update Groups
        for (String group : user.getGroups()) {
            try {
                DistinguishedName groupDn = new DistinguishedName();
                groupDn.add("ou", "Groups");
                groupDn.add("cn", group);
                DirContextOperations context = ldapTemplate.lookupContext(groupDn);
                context.addAttributeValue("memberUid", user.getUserName());
                ldapTemplate.modifyAttributes(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public LDAPUser update(final LDAPUser user) {
        Name dn = buildDn(user);
        ldapTemplate.rebind(dn, null, buildAttributes(user));
        return user;
    }

    public void delete(LDAPUser user) {
        Name dn = buildDn(user);
        ldapTemplate.unbind(dn);
    }

    private Name buildDn(final LDAPUser user) {
        DistinguishedName dn = new DistinguishedName();
        dn.add("ou", "People");
        if (user.getDepartment() != null) {
            dn.add("ou", user.getDepartment());
        }
        dn.add("uid", user.getUserName());
        return dn;
    }

    private Attributes buildAttributes(final LDAPUser user) {
        Attributes attrs = new BasicAttributes();
        BasicAttribute ocattr = new BasicAttribute("objectclass");
        ocattr.add("person");
        ocattr.add("inetOrgPerson");
        attrs.put(ocattr);
        attrs.put("cn", user.getFirstName());
        attrs.put("sn", user.getLastName());
        attrs.put("userPassword", "{SHA}" + this.encrypt(user.getPassword()));
        attrs.put("mail", user.getEmail());

        return attrs;
    }

    private String encrypt(final String plaintext) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            md.update(plaintext.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
        byte raw[] = md.digest();
        return (new Base64()).encodeAsString(raw);
    }
}

