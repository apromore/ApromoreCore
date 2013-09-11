package org.apromore.security.model;

import java.io.Serializable;

/**
 * Just a shell class so Devs doesn't confused between the DAO Models and the Security Models.
 *
 * @author Cameron James
 */
public class ApromoreRoleDetails implements Serializable {

    private String id;
    private String name;

    /**
     * Default Constructor to create the User Details object.
     * @param id the id of the role
     * @param name the name of the role
     */
    public ApromoreRoleDetails(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
