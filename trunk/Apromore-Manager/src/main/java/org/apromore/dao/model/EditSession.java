package org.apromore.dao.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Stores all the edits to the session mappings in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "edit_session")
@Configurable("editSession")
@Cache(expiry = 180000, size = 1000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class EditSession implements Serializable {

    private Integer id;
    private Date recordTime;
    private String originalBranchName;
    private String newBranchName;
    private Double versionNumber;
    private Boolean createNewBranch;
    private String natType;
    private String annotation;
    private Boolean removeFakeEvents;
    private String createDate;
    private String lastUpdateDate;

    private User user;
    private Process process;
    private ProcessModelVersion processModelVersion;


    /**
     * Default Constructor.
     */
    public EditSession() { }


    /**
     * returns the Id of this Object.
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the Id of this Object
     * @param id the new Id.
     */
    public void setId(final Integer id) {
        this.id = id;
    }


    @ManyToOne
    @JoinColumn(name = "userId")
    public User getUser() {
        return this.user;
    }

    public void setUser(final User newUser) {
        this.user = newUser;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "recordTime", length = 19)
    public Date getRecordTime() {
        return this.recordTime;
    }

    public void setRecordTime(final Date newRecordTime) {
        this.recordTime = newRecordTime;
    }

    @Column(name = "original_branch_name", length = 200)
    public String getOriginalBranchName() {
        return this.originalBranchName;
    }

    public void setOriginalBranchName(final String newOriginalBranchName) {
        this.originalBranchName = newOriginalBranchName;
    }

    @Column(name = "new_branch_name", length = 200)
    public String getNewBranchName() {
        return this.originalBranchName;
    }

    public void setNewBranchName(final String newNewBranchName) {
        this.originalBranchName = newNewBranchName;
    }

    @Column(name = "version_number")
    public Double getVersionNumber() {
        return this.versionNumber;
    }

    public void setVersionNumber(final Double newVersionNumber) {
        this.versionNumber = newVersionNumber;
    }

    @Column(name = "create_new_branch")
    public Boolean getCreateNewBranch() {
        return this.createNewBranch;
    }

    public void setCreateNewBranch(final Boolean newCreateNewBranch) {
        this.createNewBranch = newCreateNewBranch;
    }

    @Column(name = "nat_type", length = 20)
    public String getNatType() {
        return this.natType;
    }

    public void setNatType(final String newNatType) {
        this.natType = newNatType;
    }

    @Column(name = "annotation", length = 40)
    public String getAnnotation() {
        return this.annotation;
    }

    public void setAnnotation(final String newAnnotation) {
        this.annotation = newAnnotation;
    }

    @Column(name = "remove_fake_events")
    public Boolean getRemoveFakeEvents() {
        return this.removeFakeEvents;
    }

    public void setRemoveFakeEvents(final Boolean newRemoveFakeEvents) {
        this.removeFakeEvents = newRemoveFakeEvents;
    }

    @Column(name = "createDate")
    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(final String newCreationDate) {
        this.createDate = newCreationDate;
    }

    @Column(name = "lastUpdateDate")
    public String getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(final String newLastUpdate) {
        this.lastUpdateDate = newLastUpdate;
    }


    @ManyToOne
    @JoinColumn(name = "processId")
    public Process getProcess() {
        return this.process;
    }

    public void setProcess(final Process newProcess) {
        this.process = newProcess;
    }

    @ManyToOne
    @JoinColumn(name = "processModelVersionId")
    public ProcessModelVersion getProcessModelVersion() {
        return this.processModelVersion;
    }

    public void setProcessModelVersion(ProcessModelVersion processModelVersion) {
        this.processModelVersion = processModelVersion;
    }
}


