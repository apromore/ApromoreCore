package org.apromore.dao.model;

import org.springframework.beans.factory.annotation.Configurable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Stores the process in apromore.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "process",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Cacheable(true)
@Configurable("process")
public class Process implements Serializable {

    private Integer id;
    private String name;
    private String domain;

    private User user;
    private Folder folder;
    private NativeType nativeType;

    private Set<Net> nets = new HashSet<Net>(0);
    private Set<EditSession> editSessions = new HashSet<EditSession>(0);
    private Set<TempVersion> tempVersions = new HashSet<TempVersion>(0);
    private Set<ProcessBranch> processBranches = new HashSet<ProcessBranch>(0);
    private Set<ProcessUser> processUsers = new HashSet<ProcessUser>(0);
//    private Set<Folder> folders = new HashSet<Folder>(0);


    /**
     * Default constructor.
     */
    public Process() {
        super();
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



    /**
     * Get the Name for the Object.
     * @return Returns the name.
     */
    @Column(name = "name")
    public String getName() {
        return name;
    }

    /**
     * Set the Name for the Object.
     * @param newName The name to set.
     */
    public void setName(final String newName) {
        this.name = newName;
    }

    /**
     * Get the User for the Object.
     * @return Returns the domain.
     */
    @Column(name = "domain")
    public String getDomain() {
        return domain;
    }

    /**
     * Set the domain for the Object.
     * @param newDomain The domain to set.
     */
    public void setDomain(final String newDomain) {
        this.domain = newDomain;
    }

    /**
     * Get the nativeType for the Object.
     * @return Returns the nativeType.
     */
    @ManyToOne
    @JoinColumn(name = "folderId")
    public Folder getFolder() {
        return this.folder;
    }

    /**
     * Set the user for the Object.
     * @param newFolder The user to set.
     */
    public void setFolder(final Folder newFolder) {
        this.folder = newFolder;
    }

    /**
     * Get the nativeType for the Object.
     * @return Returns the nativeType.
     */
    @ManyToOne
    @JoinColumn(name = "original_type")
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
     * Get the nativeType for the Object.
     * @return Returns the nativeType.
     */
    @ManyToOne
    @JoinColumn(name = "owner")
    public User getUser() {
        return this.user;
    }

    /**
     * Set the user for the Object.
     * @param newUser The user to set.
     */
    public void setUser(final User newUser) {
        this.user = newUser;
    }


    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Net> getNets() {
        return this.nets;
    }

    public void setNets(Set<Net> nets) {
        this.nets = nets;
    }

    /**
     * Get the editSessions for the Object.
     * @return Returns the editSessions.
     */
    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<EditSession> getEditSessions() {
        return this.editSessions;
    }

    /**
     * Set the editSessions for the Object.
     * @param newEditSessions The editSessions to set.
     */
    public void setEditSessions(final Set<EditSession> newEditSessions) {
        this.editSessions = newEditSessions;
    }


    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<TempVersion> getTempVersions() {
        return this.tempVersions;
    }

    public void setTempVersions(final Set<TempVersion> tempVersions) {
        this.tempVersions = tempVersions;
    }

    /**
     * Get the process branches for the Object.
     * @return Returns the process branches.
     */
    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<ProcessBranch> getProcessBranches() {
        return this.processBranches;
    }

    /**
     * Set the process Branches for the Object.
     * @param newProcessBranches The process Branches to set.
     */
    public void setProcessBranches(final Set<ProcessBranch> newProcessBranches) {
        this.processBranches = newProcessBranches;
    }

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<ProcessUser> getProcessUsers() {
        return this.processUsers;
    }

    public void setProcessUsers(Set<ProcessUser> processUsers) {
        this.processUsers = processUsers;
    }

//
//    /**
//     * Getter for the role collection.
//     * @return Returns the roles.
//     */
//    @ManyToMany(mappedBy = "processes", cascade = {CascadeType.ALL, CascadeType.ALL})
//    public Set<Folder> getFolders() {
//        return folders;
//    }
//
//    /**
//     * Setter for the role Collection.
//     * @param newFolders The roles to set.
//     */
//    public void setFolders(final Set<Folder> newFolders) {
//        this.folders = newFolders;
//    }

}
