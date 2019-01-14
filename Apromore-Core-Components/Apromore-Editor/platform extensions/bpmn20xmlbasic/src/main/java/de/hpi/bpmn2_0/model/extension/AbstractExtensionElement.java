/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

package de.hpi.bpmn2_0.model.extension;

import de.hpi.bpmn2_0.model.extension.signavio.SignavioLabel;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioMessageName;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioMetaData;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioType;
import de.hpi.bpmn2_0.model.extension.synergia.Configurable;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationMapping;
import de.hpi.bpmn2_0.model.extension.synergia.Variants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Abstract BPMN 2.0 extension element
 *
 * @author Sven Wagner-Boysen
 */
@XmlSeeAlso({
	Configurable.class,
        ConfigurationAnnotation.class,
        ConfigurationMapping.class,
        SignavioMetaData.class,
        SignavioType.class,
        SignavioLabel.class,
        SignavioMessageName.class,
	Variants.class
})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractExtensionElement {

}
