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
    @Override public CpfEventType            createEventType()            { return new CpfEventType(); }
    @Override public CpfNetType              createNetType()              { return new CpfNetType(); }
    @Override public CpfObjectType           createObjectType()           { return new CpfObjectType(); }
    @Override public CpfObjectRefType        createObjectRefType()        { return new CpfObjectRefType(); }
    @Override public CpfORJoinType           createORJoinType()           { return new CpfORJoinType(); }
    @Override public CpfORSplitType          createORSplitType()          { return new CpfORSplitType(); }
    @Override public CpfResourceTypeType     createResourceTypeType()     { return new CpfResourceTypeType(); }
    @Override public CpfResourceTypeRefType  createResourceTypeRefType()  { return new CpfResourceTypeRefType(); }
    @Override public CpfTaskType             createTaskType()             { return new CpfTaskType(); }
    @Override public CpfXORJoinType          createXORJoinType()          { return new CpfXORJoinType(); }
    @Override public CpfXORSplitType         createXORSplitType()         { return new CpfXORSplitType(); }
}
