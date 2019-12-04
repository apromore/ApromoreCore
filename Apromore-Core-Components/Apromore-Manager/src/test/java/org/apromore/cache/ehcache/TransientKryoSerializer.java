package org.apromore.cache.ehcache;

import org.apromore.cache.ehcache.model.Description;
import org.apromore.cache.ehcache.model.Employee;
import org.apromore.cache.ehcache.model.Person;
import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
// tag::thirdPartyTransientSerializer[]
public class TransientKryoSerializer implements Serializer<Employee>, Closeable{

    protected static final Kryo kryo = new Kryo();

    protected Map<Class, Integer> objectHeaderMap = new HashMap<Class, Integer>();  // <1>

    public TransientKryoSerializer() {
    }

    public TransientKryoSerializer(ClassLoader loader) {
        populateObjectHeadersMap(kryo.register(Employee.class));  // <2>
        populateObjectHeadersMap(kryo.register(Person.class));  // <3>
        populateObjectHeadersMap(kryo.register(Description.class)); // <4>
    }

    protected void populateObjectHeadersMap(Registration reg) {
        objectHeaderMap.put(reg.getType(), reg.getId());  // <5>
    }

    @Override
    public ByteBuffer serialize(Employee object) throws SerializerException {
        Output output = new Output(new ByteArrayOutputStream());
        kryo.setReferences(false);
        kryo.writeObject(output, object);
        output.close();

        return ByteBuffer.wrap(output.getBuffer());
    }

    @Override
    public Employee read(final ByteBuffer binary) throws ClassNotFoundException, SerializerException {
        Input input =  new Input(new ByteBufferInputStream(binary)) ;
        kryo.setReferences(false);
        return kryo.readObject(input, Employee.class);
    }

    @Override
    public boolean equals(final Employee object, final ByteBuffer binary) throws ClassNotFoundException, SerializerException {
        return object.equals(read(binary));
    }

    @Override
    public void close() throws IOException {
        objectHeaderMap.clear();
    }

}
// end::thirdPartyTransientSerializer[]