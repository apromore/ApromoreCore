package org.apromore.annotation.cpf2bpmn;

import org.apromore.anf.AnnotationsType;
import org.apromore.annotation.result.AnnotationPluginResult;
import org.apromore.annotation.DefaultAbstractAnnotationProcessor;
import org.apromore.annotation.exception.AnnotationProcessorException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.message.PluginMessageImpl;
import org.springframework.stereotype.Component;

/**
 * CPF to BPMN Pre Processor.
 * Used to manipulate the ANF of the BPMN output when the input process langauge was CPF.
 * Used to change the size of the shapes as each language has different sizes elements.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Component("cpf2bpmnPreAnnotationProcessor")
public class Cpf2BpmnPreProcessor extends DefaultAbstractAnnotationProcessor {

    @Override
    @SuppressWarnings("unchecked")
    public PluginResult processAnnotation(CanonicalProcessType canonisedFormat, AnnotationsType annotationFormat)
            throws AnnotationProcessorException {
        AnnotationPluginResult pluginResult = new AnnotationPluginResult();

        if (canonisedFormat == null) {
            pluginResult.getPluginMessage().add(new PluginMessageImpl("Canonised model passed into the Post Processor is Empty."));
        } else {
            try {
                annotationFormat = createEmptyAnnotationFormat(canonisedFormat, annotationFormat);
                pluginResult.setAnnotationsType(annotationFormat);
            } catch (Exception e) {
                throw new AnnotationProcessorException("Failed to execute the Post Processing.", e);
            }
        }

        return pluginResult;
    }

}
