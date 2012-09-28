package org.apromore.canoniser.provider.impl;

import java.util.ArrayList;
import java.util.List;

import org.apromore.canoniser.Canoniser;
import org.apromore.plugin.provider.PluginProviderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SimpleSpringCanoniserProvider extends CanoniserProviderImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSpringCanoniserProvider.class);

    public SimpleSpringCanoniserProvider() {
        super();
        List<Canoniser> canoniserList = new ArrayList<Canoniser>();
        Class<?>[] classes = PluginProviderHelper.getAllClassesImplementingInterfaceUsingSpring(Canoniser.class);
        for (int i = 0; i < classes.length; i++) {
            Class<?> canoniserClass = classes[i];
            try {
                Object canoniser = canoniserClass.newInstance();
                if (canoniser instanceof Canoniser) {
                    canoniserList.add((Canoniser) canoniser);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.warn("Could not instantiate Canoniser: "+canoniserClass.getName());
            }
        }
        setInternalCanoniserList(canoniserList);
    }


}
