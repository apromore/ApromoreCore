package org.apromore.dao.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * The Id of the Temp Version entity.
 *
 * @author Cameron James
 */
@Embeddable
public class TempVersionId implements Serializable {

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -2353314404638481118L;

    private int code;
    private int processId;
    private String newVersion;


    /**
     * Default Constructor.
     */
    public TempVersionId() { }

    /**
     * Constructor for the Id.
     * @param newCode the temp version code
     * @param newProcessId the temp version processId
     * @param newNewVersion the temp version new version
     */
    public TempVersionId(final int newCode, final int newProcessId, final String newNewVersion) {
        this.code = newCode;
        this.processId = newProcessId;
        this.newVersion = newNewVersion;
    }


    /**
     * Get the npf for the Object.
     * @return Returns the npf.
     */
    @Column(name = "code", nullable = false)
    public int getCode() {
        return this.code;
    }

    /**
     * Set the code for the Object.
     * @param newCode The code to set.
     */
    public void setCode(final int newCode) {
        this.code = newCode;
    }

    /**
     * Get the npf for the Object.
     * @return Returns the npf.
     */
    @Column(name = "processId", nullable = false)
    public int getProcessId() {
        return this.processId;
    }

    /**
     * Set the processId for the Object.
     * @param newProcessId The processId to set.
     */
    public void setProcessId(final int newProcessId) {
        this.processId = newProcessId;
    }

    /**
     * Get the npf for the Object.
     * @return Returns the npf.
     */
    @Column(name = "new_version", nullable = false, length = 40)
    public String getNewVersion() {
        return this.newVersion;
    }

    /**
     * Set the newVersion for the Object.
     * @param newNewVersion The newVersion to set.
     */
    public void setNewVersion(final String newNewVersion) {
        this.newVersion = newNewVersion;
    }


    /**
     * The equals standard method to test if the Processing Ranking entity is the same.
     * @param obj the other ID object
     * @return true if the same otherwise false
     */
    @Override
	public boolean equals(Object obj) {
        Boolean result = false;

        if (obj instanceof TempVersionId) {
            TempVersionId other = (TempVersionId) obj;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(getCode(), other.getCode());
            builder.append(getProcessId(), other.getProcessId());
            builder.append(getNewVersion(), other.getNewVersion());
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
        builder.append(getCode());
        builder.append(getProcessId());
        builder.append(getNewVersion());
        return builder.toHashCode();
	}


}
