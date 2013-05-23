package org.apromore.annotation.provider.impl;

import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.annotation.AnnotationProcessor;
import org.apromore.annotation.exception.AnnotationProcessorException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.property.ParameterType;

public class TestAnnotationProcessor implements AnnotationProcessor {

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
    public String getProcessFormatProcessor() {
        return "testType";
    }

    @Override
    public PluginResult processAnnotation(CanonicalProcessType canonisedFormat, AnnotationsType annotationFormat)
            throws AnnotationProcessorException {
        return null;
    }

    @Override
    public Set<ParameterType<?>> getAvailableParameters() {
        return null;
    }

    @Override
    public Set<ParameterType<?>> getMandatoryParameters() {
        return null;
    }

    @Override
    public Set<ParameterType<?>> getOptionalParameters() {
        return null;
    }

    @Override
    public String getEMail() {
        return "";
    }

}