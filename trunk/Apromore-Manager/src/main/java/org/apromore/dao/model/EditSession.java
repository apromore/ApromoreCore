package org.apromore.dao.model;

import org.springframework.beans.factory.annotation.Configurable;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Stores all the edits to the session mappings in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "edit_session")
@Configurable("editSession")
public class EditSession implements Serializable {

    private Integer id;
    private Date recordTime;
    private String versionName;
    private String natType;
    private String annotation;
    private Boolean removeFakeEvents;
    private String creationDate;
    private String lastUpdate;

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


    @ManyToOne(fetch = FetchType.LAZY)
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

    @Column(name = "version_name", length = 40)
    public String getVersionName() {
        return this.versionName;
    }

    public void setVersionName(final String newVersionName) {
        this.versionName = newVersionName;
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

    @Column(name = "creation_date", length = 35)
    public String getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(final String newCreationDate) {
        this.creationDate = newCreationDate;
    }

    @Column(name = "last_update", length = 35)
    public String getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(final String newLastUpdate) {
        this.lastUpdate = newLastUpdate;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processId")
    public Process getProcess() {
        return this.process;
    }

    public void setProcess(final Process newProcess) {
        this.process = newProcess;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="processModelVersionId", nullable=false)
    public ProcessModelVersion getProcessModelVersion() {
        return this.processModelVersion;
    }

    public void setProcessModelVersion(ProcessModelVersion processModelVersion) {
        this.processModelVersion = processModelVersion;
    }
}


