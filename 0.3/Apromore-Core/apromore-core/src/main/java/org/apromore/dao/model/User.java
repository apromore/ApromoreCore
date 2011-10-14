package org.apromore.dao.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * Stores the process in apromore.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "users",
       uniqueConstraints = {@UniqueConstraint(columnNames = { "username" }) }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries( {
        @NamedQuery(name = User.FIND_USER, query = "SELECT usr FROM User usr WHERE usr.username = :username"),
        @NamedQuery(name = User.FIND_ALL_USERS, query = "SELECT usr FROM User usr")
})
@Configurable("users")
public class User implements Serializable {

    public static final String FIND_USER = "usr.findUser";
    public static final String FIND_ALL_USERS = "usr.findAllUsers";

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -2353314404638485548L;

    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String password;

    /**
     * Default Constructor.
     */
    public User() { }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
    @Id @Column(name = "username", unique = true, nullable = false, length = 10)
    public String getUsername() {
        return username;
    }

    /**
     * Set the Primary Key for the Object.
     * @param newUsername The id to set.
     */
    public void setUsername(final String newUsername) {
        this.username = newUsername;
    }


    /**
     * Get the firstname for the Object.
     * @return Returns the firstname.
     */
    @Column(name = "firstname", unique = true, nullable = true, length = 40)
    public String getFirstname() {
        return firstname;
    }

    /**
     * Set the firstname for the Object.
     * @param newFirstname The firstname to set.
     */
    public void setFirstname(final String newFirstname) {
        this.firstname = newFirstname;
    }

    /**
     * Get the lastname for the Object.
     * @return Returns the lastname.
     */
    @Column(name = "lastname", unique = true, nullable = true, length = 40)
    public String getLastname() {
        return lastname;
    }

    /**
     * Set the lastname for the Object.
     * @param newLastname The lastname to set.
     */
    public void setLastname(final String newLastname) {
        this.lastname = newLastname;
    }

    /**
     * Get the email for the Object.
     * @return Returns the email.
     */
    @Column(name = "email", unique = true, nullable = true, length = 80)
    public String getEmail() {
        return email;
    }

    /**
     * Set the Email for the Object.
     * @param newEmail The Email to set.
     */
    public void setEmail(final String newEmail) {
        this.email = newEmail;
    }

    /**
     * Get the password for the Object.
     * @return Returns the password.
     */
    @Column(name = "passwd", unique = true, nullable = false, length = 80)
    public String getPassword() {
        return password;
    }

    /**
     * Set the password for the Object.
     * @param newPassword The password to set.
     */
    public void setPassword(final String newPassword) {
        this.password = newPassword;
    }
}
