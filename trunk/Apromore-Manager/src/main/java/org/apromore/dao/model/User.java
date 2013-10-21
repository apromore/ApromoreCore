package org.apromore.dao.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Stores the process in apromore.
 * @author Cameron James
 */
@Entity
@Table(name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id"}),
                @UniqueConstraint(columnNames = {"row_guid"}),
                @UniqueConstraint(columnNames = {"username"})
        }
)
@Configurable("user")
@Cache(expiry = 180000, size = 100, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class User implements Serializable {

    private Integer id;
    private String rowGuid = UUID.randomUUID().toString();
    private String username;
    private String firstName;
    private String lastName;
    private Date dateCreated;
    private Date lastActivityDate;

    private Membership membership = new Membership();

    private Set<Role> roles = new HashSet<>();
    private Set<ProcessUser> processUsers = new HashSet<>();
    private Set<Workspace> workspaces = new HashSet<>();
    private Set<FragmentUser> fragmentUsers = new HashSet<>();
    private Set<FolderUser> folderUsers = new HashSet<>();
    private Set<Folder> foldersForCreatorId = new HashSet<>();
    private Set<Folder> foldersForModifiedById = new HashSet<>();
    private Set<Process> processes = new HashSet<>();
    private List<SearchHistory> searchHistories = new ArrayList<>();


    /**
     * Default Constructor.
     */
    public User() {
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

    /**
     * Get the row unique identifier for the Object.
     * @return Returns the row unique identifier.
     */
    @Column(name = "row_guid", unique = true)
    public String getRowGuid() {
        return rowGuid;
    }

    /**
     * Set the row unique identifier for the Object.
     * @param newRowGuid The row unique identifier to set.
     */
    public void setRowGuid(final String newRowGuid) {
        this.rowGuid = newRowGuid;
    }

    /**
     * Get the username for the Object.
     * @return Returns the username.
     */
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    /**
     * Set the username for the Object.
     * @param newUsername The username to set.
     */
    public void setUsername(final String newUsername) {
        this.username = newUsername;
    }

    /**
     * Get the first name for the Object.
     * @return Returns the first name.
     */
    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the first name for the Object.
     * @param newFirstName The first name to set.
     */
    public void setFirstName(final String newFirstName) {
        this.firstName = newFirstName;
    }

    /**
     * Get the last name for the Object.
     * @return Returns the last name.
     */
    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the last name for the Object.
     * @param newLastName The last name to set.
     */
    public void setLastName(final String newLastName) {
        this.lastName = newLastName;
    }

    /**
     * Get the date created for the Object.
     * @return Returns the date created.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "date_created")
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * Set the date created for the Object.
     * @param newDateCreated The date created to set.
     */
    public void setDateCreated(final Date newDateCreated) {
        this.dateCreated = newDateCreated;
    }

    /**
     * Get the last activity date for the Object.
     * @return Returns the last activity date.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "last_activity_date")
    public Date getLastActivityDate() {
        return lastActivityDate;
    }

    /**
     * Set the last activity date for the Object.
     * @param newLastActivityDate The last activity date to set.
     */
    public void setLastActivityDate(final Date newLastActivityDate) {
        this.lastActivityDate = newLastActivityDate;
    }

    /**
     * Get the membership for the Object.
     * @return Returns the membership.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    public Membership getMembership() {
        return this.membership;
    }

    /**
     * Set the membership for the Object.
     * @param newMembership The membership to set.
     */
    public void setMembership(final Membership newMembership) {
        this.membership = newMembership;
    }


    /**
     * Getter for the role collection.
     * @return Returns the roles.
     */
    @ManyToMany(mappedBy = "users")
    public Set<Role> getRoles() {
        return this.roles;
    }

    /**
     * Setter for the role Collection.
     * @param newRoles The roles to set.
     */
    public void setRoles(final Set<Role> newRoles) {
        this.roles = newRoles;
    }


    @OneToMany(mappedBy = "user")
    public Set<ProcessUser> getProcessUsers() {
        return this.processUsers;
    }

    public void setProcessUsers(Set<ProcessUser> processUsers) {
        this.processUsers = processUsers;
    }

    @OneToMany(mappedBy = "createdBy")
    public Set<Workspace> getWorkspaces() {
        return this.workspaces;
    }

    public void setWorkspaces(Set<Workspace> workspaces) {
        this.workspaces = workspaces;
    }

    @OneToMany(mappedBy = "user")
    public Set<FragmentUser> getFragmentUsers() {
        return this.fragmentUsers;
    }

    public void setFragmentUsers(Set<FragmentUser> fragmentUsers) {
        this.fragmentUsers = fragmentUsers;
    }

    @OneToMany(mappedBy = "user")
    public Set<FolderUser> getFolderUsers() {
        return this.folderUsers;
    }

    public void setFolderUsers(Set<FolderUser> folderUsers) {
        this.folderUsers = folderUsers;
    }

    @OneToMany(mappedBy = "createdBy")
    public Set<Folder> getFoldersForCreatorId() {
        return this.foldersForCreatorId;
    }

    public void setFoldersForCreatorId(Set<Folder> foldersForCreatorId) {
        this.foldersForCreatorId = foldersForCreatorId;
    }

    @OneToMany(mappedBy = "modifiedBy")
    public Set<Folder> getFoldersForModifiedById() {
        return this.foldersForModifiedById;
    }

    public void setFoldersForModifiedById(Set<Folder> foldersForModifiedById) {
        this.foldersForModifiedById = foldersForModifiedById;
    }

    @OneToMany(mappedBy = "user")
    public Set<Process> getProcesses() {
        return this.processes;
    }

    public void setProcesses(Set<Process> processes) {
        this.processes = processes;
    }

    /**
     * Get the searchHistories for the Object.
     * @return Returns the searchHistories.
     */
    @OneToMany(mappedBy = "user")
    @OrderBy("index ASC")
    public List<SearchHistory> getSearchHistories() {
        return this.searchHistories;
    }

    /**
     * Set the searchHistories for the Object.
     * @param newSearchHistories The searchHistories to set.
     */
    public void setSearchHistories(final List<SearchHistory> newSearchHistories) {
        this.searchHistories = newSearchHistories;
    }
}
