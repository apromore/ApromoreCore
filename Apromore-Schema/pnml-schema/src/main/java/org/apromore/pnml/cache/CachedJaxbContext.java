package org.apromore.pnml.cache;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.util.Hashtable;

/**
 * A Cache specially used for JAXB Contexts.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@SuppressWarnings("rawtypes")
public class CachedJaxbContext {

    private static volatile Hashtable<String, JAXBCachedEntry> jaxbInstanceCache = new Hashtable<>();

    public synchronized static JAXBContext getJaxbContext(String type, ClassLoader classLoader) throws JAXBException {
        JAXBCachedEntry cache = jaxbInstanceCache.get(type);
        if (cache == null) {
            cache = new JAXBCachedEntry(type, classLoader);
            jaxbInstanceCache.put(type, cache);
            return cache.getContext();
        }
        return cache.getContext();
    }


    // Important thing is that JAXBContext is itself thread safe so we should not worry.
    public static Unmarshaller createUnMarshaller(String type, ClassLoader classLoader) throws JAXBException {
        JAXBContext context = getJaxbContext(type, classLoader);
        return context.createUnmarshaller();
    }

    // Important thing is that JAXBContext is itself thread safe so we should not worry.
    public static Marshaller createMarshaller(String type, ClassLoader classLoader) throws JAXBException {
        JAXBContext context = getJaxbContext(type, classLoader);
        return context.createMarshaller();
    }

}