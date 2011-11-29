package org.apromore.service.model;

import javax.activation.DataSource;

/**
 * Stores the Canonical Format and the Annotation.
 * NOTE: This isn't persisted to the DB, it is used as a value object to pass objects around.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class Format {

    private DataSource cpf;
    private DataSource anf;

    /**
     * Public Constructor.
     */
    public Format() { }


    /**
     * Returns the Canonical process model.
     * @return the model in canonical format
     */
    public DataSource getCpf() {
        return cpf;
    }

    /**
     * Sets the Canonical format.
     * @param cpf the canonical format
     */
    public void setCpf(DataSource cpf) {
        this.cpf = cpf;
    }

    /**
     * Returns the Annotation.
     * @return the annotation
     */
    public DataSource getAnf() {
        return anf;
    }

    /**
     * Sets the Annotation.
     * @param anf the annotation
     */
    public void setAnf(DataSource anf) {
        this.anf = anf;
    }
}
