package org.apromore.dao.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Configurable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Edge generated by hbm2java
 */
@Entity
@Table(name = "edge",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"uri"})
        }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("edge")
public class Edge implements Serializable {

    /**
     * Hard coded for interoperability.
     */
    private static final long serialVersionUID = -9072531214638485548L;

    private Integer id;
    private String uri;
    private String cond;
    private String originalId;
    private Boolean def = false;

    private Content content;
    private Node verticesBySourceVid;
    private Node verticesByTargetVid;
    private Set<EdgeAttribute> attributes = new HashSet<EdgeAttribute>(0);

    /**
     * Public Constructor.
     */
    public Edge() { }


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
     * The URI of this fragmentVersion.
     * @return the uri
     */
    @Column(name = "uri", length = 40)
    public String getUri() {
        return this.uri;
    }

    /**
     * The URI of this fragmentVersion.
     * @param newUri the new uri.
     */
    public void setUri(final String newUri) {
        this.uri = newUri;
    }

    @Column(name = "cond", length = 2000)
    public String getCond() {
        return this.cond;
    }

    public void setCond(final String newCond) {
        this.cond = newCond;
    }

    @Column(name = "originalId", length = 40)
    public String getOriginalId() {
        return this.originalId;
    }

    public void setOriginalId(final String newOriginalId) {
        this.originalId = newOriginalId;
    }

    @Type(type = "boolean")
    @Column(name = "def", length = 1)
    public Boolean getDef() {
        return this.def;
    }

    public void setDef(final Boolean newDef) {
        this.def = newDef;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contentId")
    public Content getContent() {
        return this.content;
    }

    public void setContent(final Content newContent) {
        this.content = newContent;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sourceNodeId")
    public Node getVerticesBySourceVid() {
        return this.verticesBySourceVid;
    }

    public void setVerticesBySourceVid(final Node newVerticesBySourceVid) {
        this.verticesBySourceVid = newVerticesBySourceVid;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "targetNodeId")
    public Node getVerticesByTargetVid() {
        return this.verticesByTargetVid;
    }

    public void setVerticesByTargetVid(final Node newVerticesByTargetVid) {
        this.verticesByTargetVid = newVerticesByTargetVid;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "edge")
    public Set<EdgeAttribute> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Set<EdgeAttribute> newAttributes) {
        this.attributes = newAttributes;
    }

}


