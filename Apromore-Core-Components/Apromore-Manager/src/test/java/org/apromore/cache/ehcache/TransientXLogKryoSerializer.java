package org.apromore.cache.ehcache;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.javakaffee.kryoserializers.UUIDSerializer;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.impl.*;
import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.DeflaterOutputStream;

/**
 *
 */
// tag::thirdPartyTransientSerializer[]
public class TransientXLogKryoSerializer implements Serializer<XLog>, Closeable{

//    protected static final Kryo kryo = new Kryo( new ListReferenceResolver());
    protected static final Kryo kryo = new Kryo();



    protected Map<Class, Integer> objectHeaderMap = new HashMap<Class, Integer>();  // <1>

    public TransientXLogKryoSerializer() {
    }

    public TransientXLogKryoSerializer(ClassLoader loader) {

//        Reference setting doesn't work when put here
//        kryo.setReferences(false);


//        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
//
//        kryo.register( Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer() );

//        populateObjectHeadersMap(kryo.register(XLog.class));  // <2>


//        populateObjectHeadersMap(kryo.register(XLogImpl.class));  // <2>
//        populateObjectHeadersMap(kryo.register(XTraceImpl.class));  // <3>
//        populateObjectHeadersMap(kryo.register(XEventImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeBooleanImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeCollectionImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeContainerImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeContinuousImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeDiscreteImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeIDImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeListImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeLiteralImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeMapImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeTimestampImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeMapLazyImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XsDateTimeFormat.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XExtension.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XLifecycleExtension.class)); // <4>
//        populateObjectHeadersMap(kryo.register(org.eclipse.collections.impl.set.mutable.UnifiedSet.class)); // <4>
    }

    protected void populateObjectHeadersMap(Registration reg) {
        objectHeaderMap.put(reg.getType(), reg.getId());  // <5>
    }

    @Override
    public ByteBuffer serialize(XLog object) throws SerializerException {
//        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

//        kryo.setReferences(false);
//        kryo.setRegistrationRequired(false);

//        kryo.setRegistrationRequired(false);
        kryo.setWarnUnregisteredClasses(true);

//        kryo.register(XLogImpl.class);
//        kryo.register(XTraceImpl.class);
//        kryo.register(XEventImpl.class);
//        kryo.register(XAttributeBooleanImpl.class);
//        kryo.register(XAttributeCollectionImpl.class);
//        kryo.register(XAttributeContainerImpl.class);
//        kryo.register(XAttributeContinuousImpl.class);
//        kryo.register(XAttributeDiscreteImpl.class);
//        kryo.register(XAttributeIDImpl.class);
//        kryo.register(XAttributeListImpl.class);
//        kryo.register(XAttributeLiteralImpl.class);
//        kryo.register(XAttributeMapImpl.class);
//        kryo.register(XAttributeTimestampImpl.class);
//        kryo.register(XAttributeImpl.class);
//        kryo.register(XAttributeMapLazyImpl.class);
//        kryo.register(XsDateTimeFormat.class);
//
//        kryo.register(XExtension.class);
//        kryo.register(XLifecycleExtension.class);
//        kryo.register(org.eclipse.collections.impl.set.mutable.UnifiedSet.class);

//        populateObjectHeadersMap(kryo.register(XAttributeMapLazyImpl.class, new XAttributeMapSerializer())); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeMapImpl.class, new XAttributeMapSerializer())); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeMap.class, new XAttributeMapSerializer())); // <4>


        kryo.register(org.eclipse.collections.impl.set.mutable.UnifiedSet.class);
        kryo.register(UUID.class, new UUIDSerializer());
//        kryo.register(XAttributeMap.class);
        kryo.register(XAttributeMapImpl.class);
//        kryo.register(XAttributeMap.class, new XAttributeMapSerializer());
//        kryo.register(XAttributeMapImpl.class, new XAttributeMapSerializer());
//        kryo.register(XAttributeMapLazyImpl.class, new XAttributeMapSerializer());
        kryo.register(java.net.URI.class);
        kryo.register(java.util.Date.class);

        kryo.register(org.deckfour.xes.model.impl.XLogImpl.class);
        kryo.register(org.deckfour.xes.model.impl.XTraceImpl.class);
        kryo.register(org.deckfour.xes.model.impl.XEventImpl.class);
        kryo.register(org.deckfour.xes.model.impl.XAttributeContinuousImpl.class);
        kryo.register(org.deckfour.xes.model.impl.XAttributeLiteralImpl.class);
        kryo.register(org.deckfour.xes.extension.std.XOrganizationalExtension.class);
        kryo.register(org.eclipse.collections.impl.set.mutable.UnifiedSet.class);
        kryo.register(org.deckfour.xes.extension.std.XLifecycleExtension.class);
        kryo.register(org.deckfour.xes.id.XID.class);


        kryo.register(XLogImpl.class);
        kryo.register(XTraceImpl.class);
        kryo.register(XEventImpl.class);
        kryo.register(XAttributeBooleanImpl.class);
        kryo.register(XAttributeCollectionImpl.class);
        kryo.register(XAttributeContainerImpl.class);
        kryo.register(XAttributeDiscreteImpl.class);
        kryo.register(XAttributeIDImpl.class);
        kryo.register(XAttributeListImpl.class);
        kryo.register(XAttributeLiteralImpl.class);
        kryo.register(XAttributeTimestampImpl.class);
        kryo.register(XAttributeImpl.class);
        kryo.register(XsDateTimeFormat.class);
        kryo.register(XExtension.class);
        kryo.register(XLifecycleExtension.class);

        kryo.register(org.deckfour.xes.extension.std.XConceptExtension.class);
        kryo.register(org.deckfour.xes.extension.std.XTimeExtension.class);

//        kryo.addDefaultSerializer(XAttributeMap.class, new XAttributeMapSerializer());
//        populateObjectHeadersMap(kryo.register( UUID.class, new UUIDSerializer()));
//        populateObjectHeadersMap(kryo.register(XAttributeMap.class));
//        populateObjectHeadersMap(kryo.register(XAttributeMapImpl.class));
//        populateObjectHeadersMap(kryo.register(java.net.URI.class));
//        populateObjectHeadersMap(kryo.register(java.util.Date.class));
//
//        populateObjectHeadersMap(kryo.register(XLog.class));  // <2>
//        populateObjectHeadersMap(kryo.register(XLogImpl.class));  // <2>
//        populateObjectHeadersMap(kryo.register(XTraceImpl.class));  // <3>
//        populateObjectHeadersMap(kryo.register(XEventImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeBooleanImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeCollectionImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeContainerImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeContinuousImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeDiscreteImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeIDImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeListImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeLiteralImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeTimestampImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeImpl.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XsDateTimeFormat.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XExtension.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XLifecycleExtension.class)); // <4>
//        populateObjectHeadersMap(kryo.register(org.eclipse.collections.impl.set.mutable.UnifiedSet.class)); // <4>
//
//        populateObjectHeadersMap(kryo.register(XAttribute.class)); // <4>
//        populateObjectHeadersMap(kryo.register(XAttributeTimestamp.class));
//        populateObjectHeadersMap(kryo.register(XAttributeLiteral.class));
//        populateObjectHeadersMap(kryo.register(XAttributeList.class));
//        populateObjectHeadersMap(kryo.register(XAttributeID.class));
//        populateObjectHeadersMap(kryo.register(XAttributeDiscrete.class));
//        populateObjectHeadersMap(kryo.register(XAttributable.class));
//
//        populateObjectHeadersMap(kryo.register(XID.class));
//        populateObjectHeadersMap(kryo.register(XIDFactory.class));
//        populateObjectHeadersMap(kryo.register(Objects.class));
//        populateObjectHeadersMap(kryo.register(XVisitor.class));
//        populateObjectHeadersMap(kryo.register(XAttributeUtils.class));
//        populateObjectHeadersMap(kryo.register(XsDateTimeFormat.class));
//        populateObjectHeadersMap(kryo.register(XAttributeNameMapImpl.class));
//
//        // info
//        populateObjectHeadersMap(kryo.register(XAttributeInfoImpl.class));
//        populateObjectHeadersMap(kryo.register(XLogInfoImpl.class));
//        populateObjectHeadersMap(kryo.register(XGlobalAttributeNameMap.class));
//        populateObjectHeadersMap(kryo.register(XLogInfoFactory.class));
//        populateObjectHeadersMap(kryo.register(XFactoryNaiveImpl.class));
//        populateObjectHeadersMap(kryo.register(XFactoryRegistry.class));
//        populateObjectHeadersMap(kryo.register(XTimeBoundsImpl.class));
//
//        // extension
//        populateObjectHeadersMap(kryo.register(XExtension.class));
//        populateObjectHeadersMap(kryo.register(XExtendedEvent.class));
//        populateObjectHeadersMap(kryo.register(XExtensionManager.class));
//        populateObjectHeadersMap(kryo.register(XExtensionParser.class));
//        populateObjectHeadersMap(kryo.register(XExtensionConverter.class));
//        populateObjectHeadersMap(kryo.register(XAbstractNestedAttributeSupport.class));
//        populateObjectHeadersMap(kryo.register(XArtifactLifecycleExtension.class));
//        populateObjectHeadersMap(kryo.register(XConceptExtension.class));
//        populateObjectHeadersMap(kryo.register(XCostExtension.class));
//        populateObjectHeadersMap(kryo.register(XExtendedEvent.class));
//        populateObjectHeadersMap(kryo.register(XIdentityExtension.class));
//        populateObjectHeadersMap(kryo.register(XLifecycleExtension.class));
//        populateObjectHeadersMap(kryo.register(XMicroExtension.class));
//        populateObjectHeadersMap(kryo.register(XOrganizationalExtension.class));
//        populateObjectHeadersMap(kryo.register(XSemanticExtension.class));
//        populateObjectHeadersMap(kryo.register(XSoftwareCommunicationExtension.class));
//        populateObjectHeadersMap(kryo.register(XSoftwareEventExtension.class));
//        populateObjectHeadersMap(kryo.register(XSoftwareTelemetryExtension.class));
//        populateObjectHeadersMap(kryo.register(XTimeExtension.class));
//        populateObjectHeadersMap(kryo.register(XCostAmount.class));
//        populateObjectHeadersMap(kryo.register(XCostDriver.class));
//        populateObjectHeadersMap(kryo.register(XCostType.class));
//
//        populateObjectHeadersMap(kryo.register(XEventAndClassifier.class));
//        populateObjectHeadersMap(kryo.register(XEventAttributeClassifier.class));
//        populateObjectHeadersMap(kryo.register(XEventClass.class));
//        populateObjectHeadersMap(kryo.register(XEventClasses.class));
//        populateObjectHeadersMap(kryo.register(XEventLifeTransClassifier.class));
//        populateObjectHeadersMap(kryo.register(XEventNameClassifier.class));
//        populateObjectHeadersMap(kryo.register(XEventResourceClassifier.class));
//
//
//        // Not needed
//        kryo.register( UUID.class, new UUIDSerializer() );
//        kryo.register( Arrays.asList( "" ).getClass(), new ArraysAsListSerializer() );
//        kryo.register( Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer() );
//        kryo.register( Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer() );
//        kryo.register( Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer() );
//        kryo.register( Collections.singletonList( "" ).getClass(), new CollectionsSingletonListSerializer() );
//        kryo.register( Collections.singleton( "" ).getClass(), new CollectionsSingletonSetSerializer() );
//        kryo.register( Collections.singletonMap( "", "" ).getClass(), new CollectionsSingletonMapSerializer() );
//        UnmodifiableCollectionsSerializer.registerSerializers( kryo );
//        SynchronizedCollectionsSerializer.registerSerializers( kryo );


        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

//        Output output = new Output( new ByteArrayOutputStream(), 1024 * 1024 * 1024 * 1);

        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();
//        DeflaterOutputStream deflaterOutputStream =
//                new DeflaterOutputStream(byteArrayOutputStream);
        Output output = new Output(byteArrayOutputStream);

        kryo.writeObject(output, object);
        System.out.println("**************** Kryo serialisation size: " + output.toBytes().length / 1024 / 1024 + " " +
                "MB");
//        output.flush();
        output.close();


        byte[] bytes = byteArrayOutputStream.toByteArray();
        return ByteBuffer.wrap(bytes);
    }

    @Override
    public XLog read(final ByteBuffer binary) throws ClassNotFoundException, SerializerException {

//        kryo.setReferences(false);

//        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

//        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

//        ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());

//        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());

//        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

//        kryo.setReferences(false);

//        kryo.register(UUID.class, new UUIDSerializer());
//        kryo.register(XAttributeMap.class, new XAttributeMapSerializer());
//        kryo.register(XAttributeMapImpl.class, new XAttributeMapSerializer());
//        kryo.register(XAttributeMapLazyImpl.class, new XAttributeMapSerializer());
//        kryo.register(java.net.URI.class);
//        kryo.register(java.util.Date.class);
//
//        kryo.register(org.deckfour.xes.model.impl.XLogImpl.class);
//        kryo.register(org.deckfour.xes.model.impl.XTraceImpl.class);
//        kryo.register(org.deckfour.xes.model.impl.XEventImpl.class);
//        kryo.register(org.deckfour.xes.model.impl.XAttributeContinuousImpl.class);
//        kryo.register(org.deckfour.xes.model.impl.XAttributeLiteralImpl.class);
//        kryo.register(org.deckfour.xes.extension.std.XOrganizationalExtension.class);
//        kryo.register(org.eclipse.collections.impl.set.mutable.UnifiedSet.class);
//        kryo.register(org.deckfour.xes.extension.std.XLifecycleExtension.class);
//        kryo.register(org.deckfour.xes.id.XID.class);

        Input input =  new Input(new ByteBufferInputStream(binary)) ;
        return kryo.readObject(input, XLogImpl.class);
    }

    @Override
    public boolean equals(final XLog object, final ByteBuffer binary) throws ClassNotFoundException, SerializerException {
        return object.equals(read(binary));
    }

    @Override
    public void close() throws IOException {
        objectHeaderMap.clear();
    }

}
// end::thirdPartyTransientSerializer[]