/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.CheckValidModelMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.InputConditionMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.MESEToSESEMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.MacroRewriter;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.OutputConditionMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.RewriteMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.RoutingNodeMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.SEMEToSESEMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.WSInvokerMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer.AutomaticTimerMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer.MiscTimerMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer.TimerOnEnablementMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer.TimerOnStartMacro;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.yawlfoundation.yawlschema.MetaDataType;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;

/**
 * Converts a CanonicalProcessType an all its children.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class CanonicalProcessHandler extends CanonicalElementHandler<CanonicalProcessType, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanonicalProcessHandler.class);

    private static final Pattern NCNAME_PATTERN = Pattern.compile("[\\p{Alpha}_][\\p{Alnum}-_\\x2E]*");
    
    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        rewriteMacros();

        final SpecificationSetFactsType specSet = getContext().getYAWLSpecificationSet();
        specSet.setVersion("2.2");

        final YAWLSpecificationFactsType spec = YAWL_FACTORY.createYAWLSpecificationFactsType();
        specSet.getSpecification().add(spec);
        spec.setName(convertName());
        spec.setUri(generateUUID(getObject().getUri()));
        spec.setMetaData(convertMetaData(getObject()));
        spec.setAny(createDataTypeElement());
        LOGGER.debug("Added Specification {}", spec.getName());

        new CanonicalProcessResourcingHelper(getContext()).convertOrganisationalData(getObject().getResourceType());

        for (final NetType n : getObject().getNet()) {
            getContext().createHandler(n, spec, getObject()).convert();
        }
    }

    private String convertName() {
        if (getObject().getName() == null || getObject().getName().isEmpty()) {
            return UUID.randomUUID().toString();
        } else {
            return getObject().getName();
        }
    }

    private void rewriteMacros() throws CanoniserException {
        final MacroRewriter patternRewriter = createPatternRewriter();
        // First rewrite any detected YAWL Macros (Timers, Messages, ...) and fix issues like MEME Nets in CPF
        final Collection<RewriteMacro> appliedPattern = patternRewriter.executeAllMacros(getObject());

        if (LOGGER.isDebugEnabled()) {
            for (final RewriteMacro p : appliedPattern) {
                LOGGER.debug("Rewrote and fixed pattern  {}", p.getClass().getSimpleName());
            }
        }
    }

    private Element createDataTypeElement() throws CanoniserException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        Document doc;
        try {
            doc = dbf.newDocumentBuilder().newDocument();
        } catch (final ParserConfigurationException e) {
            throw new CanoniserException("Could not build document while creating YAWL data type fragment. This should never happen!", e);
        }
        return doc.createElementNS("http://www.w3.org/2001/XMLSchema", "schema");
    }

    private MacroRewriter createPatternRewriter() {
        final MacroRewriter patternRewriter = new MacroRewriter();
        patternRewriter.addMacro(new CheckValidModelMacro(getContext()));
        patternRewriter.addMacro(new MESEToSESEMacro(getContext()));
        patternRewriter.addMacro(new SEMEToSESEMacro(getContext()));
        patternRewriter.addMacro(new TimerOnEnablementMacro(getContext()));
        //TODO implement
        //patternRewriter.addMacro(new TimerOnStartMacro(getContext()));
        patternRewriter.addMacro(new AutomaticTimerMacro(getContext()));
        patternRewriter.addMacro(new MiscTimerMacro(getContext()));
        patternRewriter.addMacro(new RoutingNodeMacro(getContext()));
        patternRewriter.addMacro(new InputConditionMacro(getContext()));
        patternRewriter.addMacro(new OutputConditionMacro(getContext()));
        patternRewriter.addMacro(new WSInvokerMacro(getContext()));
        return patternRewriter;
    }

    private MetaDataType convertMetaData(final CanonicalProcessType c) {
        // First try to get our own Extension
        MetaDataType metaData = getContext().getExtensionFromAnnotations(null, ExtensionUtils.METADATA, MetaDataType.class,
                YAWL_FACTORY.createMetaDataType());
        // Now override with changed values on CPF
        try {
            if (c.getCreationDate() != null) {
                DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
                metaData.setCreated(datatypeFactory.newXMLGregorianCalendar(c.getCreationDate()));
            }
        } catch (DatatypeConfigurationException | IllegalArgumentException e) {
            LOGGER.warn("Could not convert 'createdDate'!", e);
        }
        try {
            if (c.getVersion() != null) {
                metaData.setVersion(new BigDecimal(c.getVersion()));
            } else {
                metaData.setVersion(BigDecimal.ONE);
            }
        } catch (NumberFormatException e) {
            metaData.setVersion(BigDecimal.ONE);
        }
        if (metaData.getIdentifier() == null || !NCNAME_PATTERN.matcher(metaData.getIdentifier()).find()) {
            metaData.setIdentifier(generateUUID(c.getUri()));
        }
        if (c.getAuthor() != null && metaData.getContributor().isEmpty()) {
            metaData.getContributor().add(c.getAuthor());
        }
        return metaData;
    }

}
