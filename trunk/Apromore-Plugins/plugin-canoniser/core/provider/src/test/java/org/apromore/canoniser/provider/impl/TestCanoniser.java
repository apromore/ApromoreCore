package org.apromore.canoniser.provider.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.property.PropertyType;

public class TestCanoniser implements Canoniser {

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
    public String getNativeType() {
        return "testType";
    }

    @Override
    public PluginResult canonise(final InputStream nativeInput, final List<AnnotationsType> annotationFormat, final List<CanonicalProcessType> canonicalFormat,
            final PluginRequest request) throws CanoniserException {
        return null;
    }

    @Override
    public PluginResult deCanonise(final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat, final OutputStream nativeOutput,
            final PluginRequest request) throws CanoniserException {
        return null;
    }

    @Override
    public Set<PropertyType<?>> getAvailableProperties() {
        return null;
    }

    @Override
    public Set<PropertyType<?>> getMandatoryProperties() {
        return null;
    }

    @Override
    public Set<PropertyType<?>> getOptionalProperties() {
        return null;
    }

    @Override
    public PluginResult createInitialNativeFormat(OutputStream nativeOutput, String processName, String processVersion, String processAuthor,
            Date processCreated, PluginRequest request) {
        return null;
    }

    @Override
    public CanoniserMetadataResult readMetaData(InputStream nativeInput, PluginRequest request) {
        return null;
    }

}