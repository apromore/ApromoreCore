package org.apromore.dao.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * The Id of the Temp Version entity.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "temp_version")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("tempVersion")
public class TempVersion implements Serializable {

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -235331446190485548L;

	private TempVersionId id;
	private Date recordTime;
	private String preVersion;
	private String natType;
	private String creationDate;
	private String lastUpdate;
	private String ranking;
	private String documentation;
	private String name;
	private String cpf;
	private String apf;
	private String npf;


    /**
     * Default Constructor.
     */
	public TempVersion() { }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "code", column = @Column(name = "code", nullable = false)),
			@AttributeOverride(name = "processId", column = @Column(name = "processId", nullable = false)),
			@AttributeOverride(name = "newVersion", column = @Column(name = "new_version", nullable = false, length = 40))
    })
	public TempVersionId getId() {
		return this.id;
	}


    /**
     * Set the Primary Key for the Object.
     * @param newId The id to set.
     */
    public void setId(TempVersionId newId) {
		this.id = newId;
	}

    /**
     * Get the recordTime for the Object.
     * @return Returns the recordTime.
     */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "recordTime", length = 19)
	public Date getRecordTime() {
		return this.recordTime;
	}

    /**
     * Set the recordTime for the Object.
     * @param newRecordTime The recordTime to set.
     */
    public void setRecordTime(final Date newRecordTime) {
		this.recordTime = newRecordTime;
	}

    /**
     * Get the preVersion for the Object.
     * @return Returns the preVersion.
     */
	@Column(name = "pre_version", length = 40)
	public String getPreVersion() {
		return this.preVersion;
	}

    /**
     * Set the preVersion for the Object.
     * @param newPreVersion The preVersion to set.
     */
    public void setPreVersion(final String newPreVersion) {
		this.preVersion = newPreVersion;
	}

    /**
     * Get the natType for the Object.
     * @return Returns the natType.
     */
	@Column(name = "nat_type", length = 20)
	public String getNatType() {
		return this.natType;
	}

    /**
     * Set the natType for the Object.
     * @param newNatType The natType to set.
     */
    public void setNatType(final String newNatType) {
		this.natType = newNatType;
	}

    /**
     * Get the creationDate for the Object.
     * @return Returns the creationDate.
     */
	@Column(name = "creation_date", length = 35)
	public String getCreationDate() {
		return this.creationDate;
	}

    /**
     * Set the creationDate for the Object.
     * @param newCreationDate The creationDate to set.
     */
    public void setCreationDate(final String newCreationDate) {
		this.creationDate = newCreationDate;
	}

    /**
     * Get the lastUpdate for the Object.
     * @return Returns the lastUpdate.
     */
	@Column(name = "last_update", length = 35)
	public String getLastUpdate() {
		return this.lastUpdate;
	}

    /**
     * Set the lastUpdate for the Object.
     * @param newLastUpdate The lastUpdate to set.
     */
    public void setLastUpdate(final String newLastUpdate) {
		this.lastUpdate = newLastUpdate;
	}

    /**
     * Get the ranking for the Object.
     * @return Returns the ranking.
     */
	@Column(name = "ranking", length = 10)
	public String getRanking() {
		return this.ranking;
	}

    /**
     * Set the ranking for the Object.
     * @param newRanking The ranking to set.
     */
    public void setRanking(final String newRanking) {
		this.ranking = newRanking;
	}

    /**
     * Get the documentation for the Object.
     * @return Returns the documentation.
     */
    @Column(name = "documentation", length = 65535)
	public String getDocumentation() {
		return this.documentation;
	}

    /**
     * Set the documentation for the Object.
     * @param newDocumentation The documentation to set.
     */
    public void setDocumentation(final String newDocumentation) {
		this.documentation = newDocumentation;
	}

    /**
     * Get the name for the Object.
     * @return Returns the name.
     */
    @Column(name = "name", length = 40)
	public String getName() {
		return this.name;
	}

    /**
     * Set the name for the Object.
     * @param newName The name to set.
     */
    public void setName(final String newName) {
		this.name = newName;
	}

    /**
     * Get the cpf for the Object.
     * @return Returns the cpf.
     */
    @Column(name = "cpf")
	public String getCpf() {
		return this.cpf;
	}

    /**
     * Set the cpf for the Object.
     * @param newCpf The cpf to set.
     */
    public void setCpf(final String newCpf) {
		this.cpf = newCpf;
	}

    /**
     * Get the apf for the Object.
     * @return Returns the apf.
     */
    @Column(name = "apf")
	public String getApf() {
		return this.apf;
	}

    /**
     * Set the apf for the Object.
     * @param newApf The apf to set.
     */
    public void setApf(final String newApf) {
		this.apf = newApf;
	}

    /**
     * Get the npf for the Object.
     * @return Returns the npf.
     */
    @Column(name = "npf")
	public String getNpf() {
		return this.npf;
	}

    /**
     * Set the npf for the Object.
     * @param newNpf The npf to set.
     */
    public void setNpf(final String newNpf) {
		this.npf = newNpf;
	}

}
