/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
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

package org.apromore.test.heuristic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ipsedixit.core.FieldHandler;
import net.sf.ipsedixit.core.FieldHandlerFinder;
import net.sf.ipsedixit.core.MutableField;
import net.sf.ipsedixit.core.impl.DefaultFieldHandlerFinder;
import net.sf.ipsedixit.core.impl.DefaultMutableField;
import net.sf.ipsedixit.core.impl.FieldHandlerRepository;
import net.sf.ipsedixit.core.impl.LoggingNullFieldHandler;
import org.apache.commons.lang.ObjectUtils;

public class JavaBeanHeuristic {

    private final FieldHandlerFinder fieldHandlerFinder;

    public JavaBeanHeuristic() {
        List<FieldHandler> fieldHandlers = new ArrayList<FieldHandler>();
        fieldHandlers.add(new MapFieldHandler());
        fieldHandlers.add(new SetFieldHandler());
        fieldHandlers.add(new ListOrSuperTypeFieldHandler());
        fieldHandlers.addAll(FieldHandlerRepository.getStandardFieldHandlers());
        fieldHandlerFinder = new DefaultFieldHandlerFinder(fieldHandlers, new LoggingNullFieldHandler());
    }

    public JavaBeanHeuristic(FieldHandlerFinder fieldHandlerFinder) {
        this.fieldHandlerFinder = fieldHandlerFinder;
    }

    /**
     * Checks that a Javabean matches a set of rules in terms of its structure and behaviour. Given a class representing a Javabean, run the following
     * checks. <ol> <li>The class has a public no-arg constructor</li> <li>For each field declared in the class, a public getter and setter
     * exists</li> <li>Set the value using the setter, then ensure that the getter returns the same object</li> </ol>
     *
     * @param clazz the class to check.
     * @param excludes any field names that should be excluded from the check.
     */
    public void checkJavaBeanProperties(Class clazz, String... excludes) {
        Field[] fields = clazz.getDeclaredFields();
        Object bean = newInstance(clazz);
        List<String> excludeList = Arrays.asList(excludes);
        for (Field field : fields) {
            if (excludeList.contains(field.getName())) {
                continue;
            }

            int modifiers = field.getModifiers();
            if (!(Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers))) {
                processField(field, bean);
            }
        }
    }

    /**
     * Static convienience method.
     *
     * @param clazz the class to check.
     * @param exclusions any field names to ignore when checking properties.
     */
    public static void assertLooksLikeJavaBean(Class clazz, String... exclusions) {
        new JavaBeanHeuristic().checkJavaBeanProperties(clazz, exclusions);
    }

    private Object newInstance(Class clazz) {
        try {
            return tryToInstantiate(clazz);
        } catch (InstantiationException e) {
            throw new AssertionError("Class " + clazz.getName() + " could not be instantiated, does it have a non-private no-arg constructor?");
        } catch (IllegalAccessException e) {
            throw new AssertionError("Class " + clazz.getName() + " could not be instantiated, does it have a non-private no-arg constructor?");
        } catch (InvocationTargetException e) {
            throw new AssertionError("Exception thrown when constructing object of type " + clazz.getName() + " [" + e.getMessage() + "]");
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Unable to find a default constructor for class " + clazz.getName());
        }
    }

    // CHECKSTYLE:OFF ThrowsCount

    private Object tryToInstantiate(Class clazz)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Constructor constructor = clazz.getDeclaredConstructor();
        // Default constructor must not be private. It can be protected or package-private though.
        if (!Modifier.isPrivate(constructor.getModifiers())) {
            constructor.setAccessible(true);
        }
        return constructor.newInstance();
    }
    // CHECKSTYLE:ON

    private void processField(Field field, Object bean) {
        try {
            Object value = getValueForField(field, bean);
            Method readMethod = fetchGetter(field, bean.getClass());
            Method writeMethod = fetchSetter(field, bean.getClass());
            writeMethod.invoke(bean, value);
            Object returnedValue = readMethod.invoke(bean);
            if (!ObjectUtils.equals(returnedValue, value)) {
                throw new AssertionError("Property " +
                        field.getName() +
                        " setter/getter mismatch.  Set " +
                        value +
                        " (" +
                        value.getClass().getName() +
                        ") but got " +
                        returnedValue +
                        " (" +
                        returnedValue.getClass().getName() +
                        ")");
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception while processing field " + field.getName(), e);
        }
    }

    private Object getValueForField(Field field, Object bean) {
        DefaultMutableField mutableField = new DefaultMutableField(field, bean);
        FieldHandler fieldHandler = fieldHandlerFinder.findFieldHandler(mutableField);
        return fieldHandler.getValueFor(mutableField);
    }

    private Method fetchGetter(Field field, Class clazz) {
        try {
            if (boolean.class.equals(field.getType())) {
                Method method = clazz.getDeclaredMethod("is" + capitalize(field.getName()));
                method.setAccessible(true);
                return method;
            }
            Method method = clazz.getDeclaredMethod("get" + capitalize(field.getName()));
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new AssertionError("No getter found for property " + field.getName());
        }
    }

    private Method fetchSetter(Field field, Class clazz) {
        try {
            Method method = clazz.getDeclaredMethod("set" + capitalize(field.getName()), field.getType());
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new AssertionError("No setter found for property " + field.getName());
        }
    }

    private static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static class MapFieldHandler implements FieldHandler {
        public Object getValueFor(final MutableField mutableField) {
            return new HashMap();
        }

        public boolean supports(final MutableField mutableField) {
            return mutableField.getType().equals(Map.class);
        }
    }

    public static class SetFieldHandler implements FieldHandler {
        public Object getValueFor(final MutableField mutableField) {
            return new HashSet();
        }

        public boolean supports(final MutableField mutableField) {
            return mutableField.getType().equals(Set.class);
        }
    }

    public static class ListOrSuperTypeFieldHandler implements FieldHandler {
        public Object getValueFor(final MutableField mutableField) {
            return new ArrayList();
        }

        public boolean supports(final MutableField mutableField) {
            return mutableField.getType().equals(Collection.class) ||
                    mutableField.getType().equals(Iterable.class) ||
                    mutableField.getType().equals(List.class);
        }
    }
}

