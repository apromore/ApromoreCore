package org.apromore.dao.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

import org.apromore.graph.canonical.ResourceTypeEnum;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * CPFResource generated by hbm2java
 */
@Entity
@Table(name = "resource")
@Configurable("resource")
@Cache(expiry = 180000, size = 1000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class Resource implements java.io.Serializable {

    private Integer id;
    private String uri;
    private String name;
    private String originalId;
    private String typeName;
    private Boolean configurable;
    private ResourceTypeEnum type;

    private ProcessModelVersion processModelVersion;

    private Set<Resource> specialisations = new HashSet<>(0);
    private Set<ResourceAttribute> resourceAttributes = new HashSet<>(0);
    private Set<ResourceRef> resourceRefs = new HashSet<>(0);


    /**
     * Public Constructor.
     */
    public Resource() {
    }



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



    @Column(name = "uri")
    public String getUri() {
        return this.uri;
    }

    public void setUri(final String newUri) {
        this.uri = newUri;
    }

    @Column(name = "name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "originalId", length = 40)
    public String getOriginalId() {
        return this.originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    @Column(name = "configurable", length = 1)
    public Boolean getConfigurable() {
        return this.configurable;
    }

    public void setConfigurable(Boolean configurable) {
        this.configurable = configurable;
    }

    @Column(name = "type", length = 30)
    @Enumerated(EnumType.STRING)
    public ResourceTypeEnum getType() {
        return this.type;
    }

    public void setType(final ResourceTypeEnum newType) {
        this.type = newType;
    }

    @Column(name = "typeName", length = 255)
    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(final String newType) {
        this.typeName = newType;
    }


    @ManyToOne
    @JoinColumn(name = "processModelVersionId")
    public ProcessModelVersion getProcessModelVersion() {
        return this.processModelVersion;
    }

    public void setProcessModelVersion(ProcessModelVersion processModelVersion) {
        this.processModelVersion = processModelVersion;
    }


    @ManyToMany
    @JoinTable(name = "resource_specialisations",
            joinColumns = {@JoinColumn(name = "resourceId")},
            inverseJoinColumns = {@JoinColumn(name = "specialisationId")})
    public Set<Resource> getSpecialisations() {
        return specialisations;
    }

    public void setSpecialisations(Set<Resource> newSpecialisations) {
        this.specialisations = newSpecialisations;
    }


    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<ResourceRef> getResourceRefs() {
        return this.resourceRefs;
    }

    public void setResourceRefs(Set<ResourceRef> resourceRefTypes) {
        this.resourceRefs = resourceRefTypes;
    }

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<ResourceAttribute> getResourceAttributes() {
        return this.resourceAttributes;
    }

    public void setResourceAttributes(Set<ResourceAttribute> resourceTypeAttributes) {
        this.resourceAttributes = resourceTypeAttributes;
    }
}


