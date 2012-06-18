package org.apromore.dao.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Configurable;

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
import java.io.Serializable;
import java.util.Date;

/**
 * Stores all the edits to the session mappings in apromore.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "edit_session_mapping")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("editSessionMapping")
public class EditSessionMapping implements Serializable {

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -7892398704638485548L;

    private Integer code;
    private Date recordTime;
    private String versionName;
    private String natType;
    private String annotation;
    private Boolean removeFakeEvents;
    private String creationDate;
    private String lastUpdate;

    private User user;
    private Process process;


    /**
     * Default Constructor.
     */
    public EditSessionMapping() { }


    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "code", unique = true, nullable = false)
    public Integer getCode() {
        return this.code;
    }
    
    public void setCode(final Integer newCode) {
        this.code = newCode;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    public User getUser() {
        return this.user;
    }
    
    public void setUser(final User newUser) {
        this.user = newUser;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processId")
    public Process getProcess() {
        return this.process;
    }
    
    public void setProcess(final Process newProcess) {
        this.process = newProcess;
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
    
    @Column(name="last_update", length=35)
    public String getLastUpdate() {
        return this.lastUpdate;
    }
    
    public void setLastUpdate(final String newLastUpdate) {
        this.lastUpdate = newLastUpdate;
    }

}


