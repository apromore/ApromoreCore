package org.apromore.dao.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Stores the native something in apromore.
 *
 * How to get this to work with Canonical table????
 *
 * @author Cameron James
 */
@Entity
@Table(name = "derived_versions")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries( {
//        @NamedQuery(name = User.FIND_USER, query = "SELECT usr FROM User usr WHERE usr.username = :username"),
//        @NamedQuery(name = User.FIND_ALL_USERS, query = "SELECT usr FROM User usr")
})
@Configurable("derivedVersion")
public class DerivedVersion implements Serializable {

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -235332908738485548L;


}
