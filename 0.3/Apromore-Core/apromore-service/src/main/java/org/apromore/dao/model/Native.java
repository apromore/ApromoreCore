package org.apromore.dao.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
               @UniqueConstraint(columnNames = { "canonical", "nat_type" })
       }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries( {
    @NamedQuery(name = Native.FIND_NATIVE_TYPES, query = "SELECT n FROM Native n, Canonical c WHERE n.canonical.uri = c.uri AND c.process.processId = :processId AND c.versionName = :versionName"),
    @NamedQuery(name = Native.GET_NATIVE, query = "SELECT n FROM Native n, Canonical c WHERE n.canonical.uri = c.uri AND c.process.processId = :processId AND c.versionName = :versionName AND n.nativeType.natType = :nativeType")
})
@Configurable("native")
public class Native implements Serializable {

    public static final String FIND_NATIVE_TYPES = "native.findNativeTypes";
    public static final String GET_NATIVE = "pr.getNative";

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -235332908738485548L;

	private Integer uri;
	private String content;

    private Canonical canonical;
	private NativeType nativeType;

	private Set<Annotation> annotations = new HashSet<Annotation>(0);


    /**
     * Default Constructor.
     */
    public Native() { }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
     * Get the canonical for the Object.
     * @return Returns the canonical.
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "canonical")
	public Canonical getCanonical() {
		return this.canonical;
	}

    /**
     * Set the canonical for the Object.
     * @param newCanonical The canonical to set.
     */
    public void setCanonical(final Canonical newCanonical) {
		this.canonical = newCanonical;
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
