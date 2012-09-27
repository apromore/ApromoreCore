package org.apromore.canoniser.provider.impl;

import java.util.ArrayList;
import java.util.List;

import org.apromore.canoniser.Canoniser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Service;

@Service
public class SimpleSpringCanoniserProvider extends CanoniserProviderImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSpringCanoniserProvider.class);

    public SimpleSpringCanoniserProvider() {
        super();
        List<Canoniser> canoniserList = new ArrayList<Canoniser>();
        Class<?>[] classes = getAllClassesImplementingCanoniser();
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

    private Class<?>[] getAllClassesImplementingCanoniser() {
        BeanDefinitionRegistry beanRegistry = new SimpleBeanDefinitionRegistry();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanRegistry, false);

        TypeFilter typeFilter = new AssignableTypeFilter(Canoniser.class);
        scanner.addIncludeFilter(typeFilter);
        scanner.setIncludeAnnotationConfig(false);
        scanner.scan("org.apromore");
        String[] beans = beanRegistry.getBeanDefinitionNames();
        Class<?>[] classes = new Class<?>[beans.length];
        for (int i = 0; i < beans.length; i ++) {
            BeanDefinition def = beanRegistry.getBeanDefinition(beans[i]);
            try {
                classes[i] = Class.forName(def.getBeanClassName());
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Could not find class: "+beans[i]);
            }
        }
        return classes;
    }

}
