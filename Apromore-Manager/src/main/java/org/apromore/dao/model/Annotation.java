package org.apromore.dao.model;

import org.springframework.beans.factory.annotation.Configurable;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Stores the Annotation in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "annotation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"processModelVersionId", "name"}),
                @UniqueConstraint(columnNames = {"native"})
        }
)
@Configurable("annotation")
public class Annotation implements Serializable {

    private Integer id;
    private String name;
    private String content;

    private Native natve;
    private ProcessModelVersion processModelVersion;

    /**
     * Default Constructor.
     */
    public Annotation() { }



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
    @Column(name = "content")
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
    @ManyToOne
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
    @ManyToOne
    @JoinColumn(name = "processModelVersionId")
    public ProcessModelVersion getProcessModelVersion() {
        return this.processModelVersion;
    }

    /**
     * Set the process Model Version for the Object.
     * @param newProcessModelVersion The process Model Version format to set.
     */
    public void setProcessModelVersion(final ProcessModelVersion newProcessModelVersion) {
        this.processModelVersion = newProcessModelVersion;
    }

}
