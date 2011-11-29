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
 * Stores the Process Ranking entity.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "process_ranking")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries( {
        //@NamedQuery(name = TempVersion.FIND_USER, query = "SELECT usr FROM User usr WHERE usr.username = :username"),
        //@NamedQuery(name = TempVersion.FIND_ALL_USERS, query = "SELECT usr FROM User usr")
})
@Configurable("processRanking")
public class ProcessRanking implements Serializable {

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -2353318364719281118L;

	private ProcessRankingId id;


    /**
     * Default Constructor.
     */
	public ProcessRanking() { }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "processId", column = @Column(name = "processId")),
			@AttributeOverride(name = "ranking", column = @Column(name = "ranking", precision = 22, scale = 0)) })
	public ProcessRankingId getId() {
		return this.id;
	}

    /**
     * Set the Primary Key for the Object.
     * @param newId The id to set.
     */
	public void setId(final ProcessRankingId newId) {
		this.id = newId;
	}

}
