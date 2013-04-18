package org.apromore.service.model;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.message.PluginMessage;

import java.io.InputStream;
import java.util.List;

/**
 * Stores the Canonical Format and the Annotation.
 * NOTE: This isn't persisted to the DB, it is used as a value object to pass objects around.
 * Also, should we just have one, Format or this one????
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class CanonisedProcess {

    private InputStream original;

    private InputStream cpf;
    private CanonicalProcessType cpt;

    private InputStream anf;
    private AnnotationsType ant;

    private List<PluginMessage> messages;


    public CanonisedProcess() {
    }


    public InputStream getOriginal() {
        return original;
    }

    public void setOriginal(final InputStream newOriginal) {
        this.original = newOriginal;
    }

    public InputStream getAnf() {
        return anf;
    }

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

    public InputStream getCpf() {
        return cpf;
    }

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
