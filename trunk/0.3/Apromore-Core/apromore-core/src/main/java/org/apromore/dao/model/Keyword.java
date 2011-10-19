package org.apromore.dao.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * This is view for the keywords used in the system.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "keywords")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries( {
//        @NamedQuery(name = User.FIND_USER, query = "SELECT usr FROM User usr WHERE usr.username = :username"),
//        @NamedQuery(name = User.FIND_ALL_USERS, query = "SELECT usr FROM User usr")
})
@Configurable("keyword")
public class Keyword implements Serializable {

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -2353098704638485548L;

    private KeywordId id;


    /**
     * Default Constructor.
     */
    public Keyword() { }

   
    @EmbeddedId
    @AttributeOverrides( {
        @AttributeOverride(name = "processId", column = @Column( name = "processId" ) ),
        @AttributeOverride(name = "word", column = @Column( name = "word", length = 100) ) } )
    public KeywordId getId() {
        return this.id;
    }
    
    public void setId(final KeywordId newId) {
        this.id = newId;
    }

}


