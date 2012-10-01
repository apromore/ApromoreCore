package org.apromore.canoniser.provider.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.PropertyType;
import org.springframework.stereotype.Service;

public class TestCanoniser implements Canoniser {

    @Override
    public Set<PropertyType> getAvailableProperties() {
        return null;
    }

    @Override
    public Set<PropertyType> getMandatoryProperties() {
        return null;
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getAuthor() {
        return null;
    }

    @Override
    public List<PluginMessage> getPluginMessages() {
        return null;
    }

    @Override
    public String getNativeType() {
        return "testType";
    }

    @Override
    public void canonise(final InputStream nativeInput, final List<AnnotationsType> annotationFormat, final List<CanonicalProcessType> canonicalFormat)
            throws CanoniserException {

    }

    @Override
    public void deCanonise(final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat, final OutputStream nativeOutput)
            throws CanoniserException {

    }

}