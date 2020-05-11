/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import javax.xml.bind.annotation.XmlRegistry;

/**
 * Element factory for a CPF 1.0 object model with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
@XmlRegistry
public class ObjectFactory extends org.apromore.cpf.ObjectFactory {

    @Override public CpfANDJoinType          createANDJoinType()          { return new CpfANDJoinType(); }
    @Override public CpfANDSplitType         createANDSplitType()         { return new CpfANDSplitType(); }
    @Override public CpfCanonicalProcessType createCanonicalProcessType() { return new CpfCanonicalProcessType(); }
    @Override public CpfEdgeType             createEdgeType()             { return new CpfEdgeType(); }
    @Override public CpfEventTypeImpl        createEventType()            { return new CpfEventTypeImpl(); }
    @Override public CpfHardType             createHardType()             { return new CpfHardType(); }
    @Override public CpfHumanType            createHumanType()            { return new CpfHumanType(); }
    @Override public CpfMessageType          createMessageType()          { return new CpfMessageType(); }
    @Override public CpfNetType              createNetType()              { return new CpfNetType(); }
    @Override public CpfNonhumanType         createNonhumanType()         { return new CpfNonhumanType(); }
    @Override public CpfObjectTypeImpl       createObjectType()           { return new CpfObjectTypeImpl(); }
    @Override public CpfObjectRefType        createObjectRefType()        { return new CpfObjectRefType(); }
    @Override public CpfORJoinType           createORJoinType()           { return new CpfORJoinType(); }
    @Override public CpfORSplitType          createORSplitType()          { return new CpfORSplitType(); }
    @Override public CpfResourceTypeTypeImpl createResourceTypeType()     { return new CpfResourceTypeTypeImpl(); }
    @Override public CpfResourceTypeRefType  createResourceTypeRefType()  { return new CpfResourceTypeRefType(); }
    @Override public CpfSoftType             createSoftType()             { return new CpfSoftType(); }
    @Override public CpfStateType            createStateType()            { return new CpfStateType(); }
    @Override public CpfTaskType             createTaskType()             { return new CpfTaskType(); }
    @Override public CpfTimerType            createTimerType()            { return new CpfTimerType(); }
    @Override public CpfXORJoinType          createXORJoinType()          { return new CpfXORJoinType(); }
    @Override public CpfXORSplitType         createXORSplitType()         { return new CpfXORSplitType(); }
}
