package org.apromore.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The access control details corresponding to a particular group and folder.
 *  
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Entity
@Table(name = "group_folder")
@Configurable("group_folder")
@Cache(expiry = 180000, size = 1000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class GroupFolder implements Serializable {

    private Integer id;
    private boolean hasRead;
    private boolean hasWrite;
    private boolean hasOwnership;

    private Group  group;
    private Folder folder;


    /**
     * Default Constructor.
     */
    public GroupFolder() {
    }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    /**
     * Set the id for the Object.
     * @param newId The role name to set.
     */
    public void setId(final Integer newId) {
        this.id = newId;
    }


    @ManyToOne
    @JoinColumn(name = "groupId", nullable = false)
    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @ManyToOne
    @JoinColumn(name = "folderId", nullable = true)
    public Folder getFolder() {
        return this.folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }


    @Column(name = "has_read")
    public boolean isHasRead() {
        return this.hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }


    @Column(name = "has_write")
    public boolean isHasWrite() {
        return this.hasWrite;
    }

    public void setHasWrite(boolean hasWrite) {
        this.hasWrite = hasWrite;
    }


    @Column(name = "has_ownership")
    public boolean isHasOwnership() {
        return this.hasOwnership;
    }

    public void setHasOwnership(boolean hasOwnership) {
        this.hasOwnership = hasOwnership;
    }
}
