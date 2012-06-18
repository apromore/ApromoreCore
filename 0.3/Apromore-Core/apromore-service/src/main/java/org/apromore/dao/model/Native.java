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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Stores the native something in apromore.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "native",
        uniqueConstraints = {
               @UniqueConstraint(columnNames = { "process_model_version_id", "nat_type" })
       }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("native")
public class Native implements Serializable {

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -235332908738485548L;

	private Integer uri;
	private String content;

	private NativeType nativeType;
    private ProcessModelVersion processModelVersion;
    private Set<Annotation> annotations = new HashSet<Annotation>(0);


    /**
     * Default Constructor.
     */
    public Native() { }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
	@Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "uri", unique = true, nullable = false)
	public Integer getUri() {
		return this.uri;
	}

    /**
     * Set the Primary Key for the Object.
     * @param newUri The Id to set.
     */
    public void setUri(final Integer newUri) {
		this.uri = newUri;
	}


    /**
     * Get the content for the Object.
     * @return Returns the content.
     */
	@Column(name = "content")
	public String getContent() {
		return this.content;
	}

    /**
     * Set the content for the Object.
     * @param newContent The content to set.
     */
    public void setContent(final String newContent) {
		this.content = newContent;
	}

    /**
     * Get the nativeType for the Object.
     * @return Returns the nativeType.
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nat_type")
	public NativeType getNativeType() {
		return this.nativeType;
	}

    /**
     * Set the nativeType for the Object.
     * @param newNativeType The nativeType to set.
     */
    public void setNativeType(final NativeType newNativeType) {
		this.nativeType = newNativeType;
	}

    /**
     * Get the process Model Version for the Object.
     * @return Returns the process Model Version.
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="process_model_version_id")
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

    /**
     * Get the annotations for the Object.
     * @return Returns the annotations.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "natve")
    public Set<Annotation> getAnnotations() {
        return this.annotations;
    }

    /**
     * Set the annotations for the Object.
     * @param newAnnotations The annotations to set.
     */
    public void setAnnotations(final Set<Annotation> newAnnotations) {
        this.annotations = newAnnotations;
    }

}
