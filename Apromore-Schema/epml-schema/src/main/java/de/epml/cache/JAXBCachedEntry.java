package de.epml.cache;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Stores all the JAXB Context entries for fast retrieval later.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@SuppressWarnings("rawtypes")
public class JAXBCachedEntry {

    private final String cachedClass;
    private final ClassLoader cachedClassloader;
    private final JAXBContext context;

    public JAXBCachedEntry(String type, ClassLoader classLoader) throws JAXBException{
        context = JAXBContext.newInstance(type, classLoader);
        cachedClass = type;
        cachedClassloader = classLoader;
    }

    public String getCachedClass() {
        return cachedClass;
    }

    public ClassLoader getCachedClassLoader() {
        return cachedClassloader;
    }

    public JAXBContext getContext() {
        return context;
    }

}
