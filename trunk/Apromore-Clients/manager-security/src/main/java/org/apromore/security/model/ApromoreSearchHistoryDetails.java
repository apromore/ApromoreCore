package org.apromore.security.model;

import java.io.Serializable;

/**
 * Just a shell class so Devs doesn't confused between the DAO Models and the Security Models.
 *
 * @author Cameron James
 */
public class ApromoreSearchHistoryDetails implements Serializable {

    private Integer id;
    private String searchString;

    /**
     * Construct the object. This only contains a String but could potentially contain more.
     * @param id the id and position of the search string.
     * @param searchString the search string the user used.
     */
    public ApromoreSearchHistoryDetails(Integer id, String searchString) {
        this.id = id;
        this.searchString = searchString;
    }

    /**
     * returns the Id and the search position.
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * sets the id and the search position.
     * @param id the id.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Return the search String.
     * @return the search string
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * Sets the search string the user used.
     * @param searchString the search string
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
