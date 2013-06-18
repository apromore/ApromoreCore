package org.apromore.service.model;

/**
 * Simple Java Bean to hold the name and value pair.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class NameValuePair {

    private String name;
    private String value;
    private Double version;

    /**
     * Default Constructor.
     */
    public NameValuePair() {
    }

    /**
     * Public Constructor.
     * @param name  the name
     * @param value the value
     */
    public NameValuePair(final String name, final String value) {
        this.name = name;
        this.value = value;
    }


    /**
     * Public Constructor.
     * @param name  the name
     * @param value the value
     * @param version the version
     */
    public NameValuePair(final String name, final String value, final Double version) {
        this.name = name;
        this.value = value;
        this.version = version;
    }

    /**
     * return the name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * set the name.
     * @param newName the name
     */
    public void setName(final String newName) {
        this.name = newName;
    }

    /**
     * return the value.
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * set the value.
     * @param newValue the value
     */
    public void setValue(final String newValue) {
        this.value = newValue;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }
}
