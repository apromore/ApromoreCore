package org.apromore.dao.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * The Id of the Process Ranking entity.
 *
 * @author Cameron James
 */
@Embeddable
public class ProcessRankingId implements Serializable {

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -2353311738938485548L;

	private Integer processId;
	private Double ranking;


    /**
     * Default Constructor.
     */
	public ProcessRankingId() {}

    /**
     * The Constructor to setup the id object.
     * @param newProcessId the processId
     * @param newRanking it's ranking
     */
    public ProcessRankingId(final Integer newProcessId, final Double newRanking) {
        this.processId = newProcessId;
        this.ranking = newRanking;
    }


    /**
     * Get the processId for the Object.
     * @return Returns the processId.
     */
	@Column(name = "processId")
	public Integer getProcessId() {
		return this.processId;
	}

    /**
     * Set the processId for the Object.
     * @param newProcessId The processId to set.
     */
	public void setProcessId(final Integer newProcessId) {
		this.processId = newProcessId;
	}

    /**
     * Get the ranking for the Object.
     * @return Returns the ranking.
     */
    @Column(name = "ranking", precision = 22, scale = 0)
	public Double getRanking() {
		return this.ranking;
	}

    /**
     * Set the ranking for the Object.
     * @param newRanking The ranking to set.
     */
	public void setRanking(final Double newRanking) {
		this.ranking = newRanking;
	}


    /**
     * The equals standard method to test if the Processing Ranking entity is the same.
     * @param obj the other ID object
     * @return true if the same otherwise false
     */
    @Override
	public boolean equals(Object obj) {
        Boolean result = false;

        if (obj instanceof ProcessRankingId) {
            ProcessRankingId other = (ProcessRankingId) obj;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(getProcessId(), other.getProcessId());
            builder.append(getRanking(), other.getRanking());
            result = builder.isEquals();
        }

        return result;
	}

    /**
     * Determines the hashcode of the object.
     * @return the hashcode
     */
    @Override
	public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getProcessId());
        builder.append(getRanking());
        return builder.toHashCode();
	}

}
