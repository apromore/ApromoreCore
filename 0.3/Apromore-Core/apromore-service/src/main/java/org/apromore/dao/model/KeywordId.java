package org.apromore.dao.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * This is the Id for the Keyword view.
 *
 * @author Cameron James
 */
@Embeddable
public class KeywordId implements Serializable {

    /**
     * Hard coded for interoperability.
     */
    private static final long serialVersionUID = -2310988704638485548L;

    private Integer processId;
    private String word;


    /**
     * Default Constructor.
     */
    public KeywordId() {
    }

    public KeywordId(Integer processId, String word) {
        this.processId = processId;
        this.word = word;
    }


    @Column(name = "processId")
    public Integer getProcessId() {
        return this.processId;
    }

    public void setProcessId(final Integer newProcessId) {
        this.processId = newProcessId;
    }

    @Column(name = "word", length = 100)
    public String getWord() {
        return this.word;
    }

    public void setWord(final String newWord) {
        this.word = newWord;
    }


    /**
     * The equals standard method to test if the Processing Ranking entity is the same.
     * @param obj the other ID object
     * @return true if the same otherwise false
     */
    @Override
    public boolean equals(Object obj) {
        Boolean result = false;

        if (obj instanceof KeywordId) {
            KeywordId other = (KeywordId) obj;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(getProcessId(), other.getProcessId());
            builder.append(getWord(), other.getWord());
            result = builder.isEquals();
        }

        return result;
    }

    /**
     * Determines the hashcode of the object.
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getProcessId());
        builder.append(getWord());
        return builder.toHashCode();
    }


}


