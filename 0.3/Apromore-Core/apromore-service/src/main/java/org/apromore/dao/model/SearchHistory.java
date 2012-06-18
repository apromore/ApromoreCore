package org.apromore.dao.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * Stores the process in apromore.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "search_history",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"username", "search"})
        }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("searchHistory")
public class SearchHistory implements Serializable {

    /**
     * Hard coded for interoperability.
     */
    private static final long serialVersionUID = -2353312846108485548L;

    private Integer num;
    private String search;

    private User user;


    /**
     * Default Constructor.
     */
    public SearchHistory() { }


    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "num", unique = true, nullable = false)
    public Integer getNum() {
        return this.num;
    }

    public void setNum(Integer newNum) {
        this.num = newNum;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    public User getUser() {
        return this.user;
    }

    public void setUser(User newUser) {
        this.user = newUser;
    }

    @Column(name = "search", length = 200)
    public String getSearch() {
        return this.search;
    }

    public void setSearch(String newSearch) {
        this.search = newSearch;
    }

}
