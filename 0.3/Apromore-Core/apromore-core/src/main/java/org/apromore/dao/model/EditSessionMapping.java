package org.apromore.dao.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Stores all the edits to the session mappings in apromore.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "edit_session_mappings")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries( {
//        @NamedQuery(name = User.FIND_USER, query = "SELECT usr FROM User usr WHERE usr.username = :username"),
//        @NamedQuery(name = User.FIND_ALL_USERS, query = "SELECT usr FROM User usr")
})
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
    private Canonical canonical;
    private Process process;


    /**
     * Default Constructor.
     */
    public EditSessionMapping() { }


    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "code", unique = true, nullable = false)
    public Integer getCode() {
        return this.code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    public User getUser() {
        return this.user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processId")
    public Process getProcess() {
        return this.process;
    }
    
    public void setProcess(Process process) {
        this.process = process;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uri", nullable = false)
    public Canonical getCanonical() {
        return this.canonical;
    }

    public void setCanonical(final Canonical newCanonical) {
        this.canonical = newCanonical;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "recordTime", length = 19)
    public Date getRecordTime() {
        return this.recordTime;
    }
    
    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }
    
    @Column(name = "version_name", length = 40)
    public String getVersionName() {
        return this.versionName;
    }
    
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
    
    @Column(name = "nat_type", length = 20)
    public String getNatType() {
        return this.natType;
    }
    
    public void setNatType(String natType) {
        this.natType = natType;
    }
    
    @Column(name = "annotation", length = 40)
    public String getAnnotation() {
        return this.annotation;
    }
    
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
    
    @Column(name = "remove_fake_events")
    public Boolean getRemoveFakeEvents() {
        return this.removeFakeEvents;
    }
    
    public void setRemoveFakeEvents(Boolean removeFakeEvents) {
        this.removeFakeEvents = removeFakeEvents;
    }
    
    @Column(name = "creation_date", length = 35)
    public String getCreationDate() {
        return this.creationDate;
    }
    
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
    
    @Column(name="last_update", length=35)
    public String getLastUpdate() {
        return this.lastUpdate;
    }
    
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}


