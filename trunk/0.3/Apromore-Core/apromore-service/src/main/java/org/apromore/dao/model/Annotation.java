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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Stores the Annotation in apromore.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "annotation",
       uniqueConstraints = {
               @UniqueConstraint(columnNames = { "process_model_version_id", "name" }),
               @UniqueConstraint(columnNames = { "native" })
       }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("annotation")
public class Annotation implements Serializable {

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -2353376324638485548L;

    private int uri;
    private String name;
    private String content;

    private Native natve;
    private ProcessModelVersion processModelVersion;

    /**
     * Default Constructor.
     */
    public Annotation() { }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the uri.
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "uri", unique = true, nullable = false, precision = 11, scale = 0)
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
    @Lob
    @Column(name = "content", nullable = true)
    public String getContent() {
        return content;
    }

    /**
     * Set the contents for the Object.
     * @param newContent The contents to set.
     */
    public void setContent(final String newContent) {
        this.content = newContent;
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
    public void setNatve(final Native newNative) {
        this.natve = newNative;
    }

    /**
     * Get the process Model Version for the Object.
     * @return Returns the process Model Version.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_model_version_id")
    public ProcessModelVersion getProcessModelVersion() {
        return this.processModelVersion;
    }

    /**
     * Set the process Model Version for the Object.
     * @param newProcessModelVersion The process Model Version format to set.
     */
    public void setProcessModelVersion(ProcessModelVersion newProcessModelVersion) {
        this.processModelVersion = newProcessModelVersion;
    }

}
