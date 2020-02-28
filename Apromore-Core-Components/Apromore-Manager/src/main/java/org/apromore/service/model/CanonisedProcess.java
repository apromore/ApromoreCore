/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
