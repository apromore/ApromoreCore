package org.apromore.dao.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * Stores the Annotation in apromore.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "annotations",
       uniqueConstraints = {
               @UniqueConstraint(columnNames = { "canonical", "name" }),
               @UniqueConstraint(columnNames = { "native" })
       }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries( {
        @NamedQuery(name = Annotation.FIND_ALL, query = "SELECT usr FROM User usr WHERE usr.username = :username")
})
@Configurable("annotation")
public class Annotation implements Serializable {

    public static final String FIND_ALL = "ann.findAll";

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -2353376324638485548L;

    private int uri;
    private String canonical;
    private String name;
    private String contents;

    private Native natve;

    /**
     * Default Constructor.
     */
    public Annotation() { }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the uri.
     */
    @Id @Column(name = "uri", unique = true, nullable = false, precision = 11, scale = 0)
    public int getUri() {
        return uri;
    }

    /**
     * Set the Primary Key for the Object.
     * @param newUri The uri to set.
     */
    public void setUri(final int newUri) {
        this.uri = newUri;
    }


    /**
     * Get the canonical format for the Object.
     * @return Returns the canonical format.
     */
    @Column(name = "canonical", unique = false, nullable = true, length = 40)
    public String getCanonical() {
        return canonical;
    }

    /**
     * Set the Canonical Format for the Object.
     * @param newCanonical The canonical format to set.
     */
    public void setCanonical(final String newCanonical) {
        this.canonical = newCanonical;
    }


    /**
     * Get the name for the Object.
     * @return Returns the name.
     */
    @Column(name = "name", unique = false, nullable = true, length = 40)
    public String getName() {
        return name;
    }

    /**
     * Set the name for the Object.
     * @param newName The name to set.
     */
    public void setName(final String newName) {
        this.name = newName;
    }


    /**
     * Get the contents for the Object.
     * @return Returns the contents.
     */
    @Column(name = "contents", unique = false, nullable = true, length = 4000)
    public String getContents() {
        return contents;
    }

    /**
     * Set the contents for the Object.
     * @param newContents The contents to set.
     */
    public void setContents(final String newContents) {
        this.contents = newContents;
    }


    /**
     * Get the native format for the Object.
     * @return Returns the native format.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "native")
    public Native getNatve() {
        return this.natve;
    }

    /**
     * Set the native format for the Object.
     * @param newNative The native format to set.
     */
    public void setNatve(Native newNative) {
        this.natve = newNative;
    }

}
