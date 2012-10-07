package org.apromore.service.model;

import java.io.InputStream;
import java.util.List;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.message.PluginMessage;

/**
 * Stores the Canonical Format and the Annotation.
 * NOTE: This isn't persisted to the DB, it is used as a value object to pass objects around.
 * Also, should we just have one, Format or this one????
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class CanonisedProcess {

    private InputStream cpf;
    private CanonicalProcessType cpt;

    private InputStream anf;
    private AnnotationsType ant;

    private List<PluginMessage> messages;

    /**
     * Public Constructor.
     */
    public CanonisedProcess() {
    }


    /**
     * Returns the Annotation.
     *
     * @return the annotation
     */
    public InputStream getAnf() {
        return anf;
    }

    /**
     * Sets the Annotation.
     *
     * @param anf the annotation
     */
    public void setAnf(final InputStream anf) {
        this.anf = anf;
    }

    public CanonicalProcessType getCpt() {
        return cpt;
    }

    public void setCpt(final CanonicalProcessType cpt) {
        this.cpt = cpt;
    }

    public AnnotationsType getAnt() {
        return ant;
    }

    public void setAnt(final AnnotationsType ant) {
        this.ant = ant;
    }

    /**
     * Returns the Canonical process model.
     *
     * @return the model in canonical format
     */
    public InputStream getCpf() {
        return cpf;
    }

    /**
     * Sets the Canonical format.
     *
     * @param cpf the canonical format
     */
    public void setCpf(final InputStream cpf) {
        this.cpf = cpf;
    }


    public List<PluginMessage> getMessages() {
        return messages;
    }


    public void setMessages(final List<PluginMessage> messages) {
        this.messages = messages;
    }
}
