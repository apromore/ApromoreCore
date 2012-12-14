package org.apromore.portal.ldap;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 19/06/12
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceUtils {

    public static LDAPUserService getUserService(){
        return (LDAPUserService)AppUtils.getBean("userService");
    }
}

