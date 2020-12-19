/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import static java.lang.reflect.Modifier.isStatic;
import com.google.common.collect.Lists;
import net.sf.ipsedixit.core.FieldHandler;
import net.sf.ipsedixit.core.MutableField;
import net.sf.ipsedixit.core.impl.DefaultFieldHandlerFinder;
import net.sf.ipsedixit.core.impl.DefaultMutableField;
import net.sf.ipsedixit.core.impl.FieldHandlerRepository;
import net.sf.ipsedixit.core.impl.LoggingNullFieldHandler;


public final class EqualsChecker {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EqualsChecker.class);

    private static DefaultFieldHandlerFinder fieldHandlerFinder =
            new DefaultFieldHandlerFinder(FieldHandlerRepository.getStandardFieldHandlers(), new LoggingNullFieldHandler());

    private EqualsChecker() {
    }

    // this is a crappy way to do it, need better support in Ipsedixit.
    public static void registerAdditionalFieldHandlers(Iterable<FieldHandler> fieldHandlers) {
        List<FieldHandler> tmp = Lists.newArrayList(fieldHandlers);
        tmp.addAll(FieldHandlerRepository.getStandardFieldHandlers());
        fieldHandlerFinder = new DefaultFieldHandlerFinder(FieldHandlerRepository.getStandardFieldHandlers(), new LoggingNullFieldHandler());
    }

    public static void assertEqualsIsProperlyImplemented(Object o1, Object o2, String... ignores) {
        if (!o1.equals(o2)) {
            throw new AssertionError("Two objects must be equal in order to run this test");
        }
        List<String> fieldNamesToIgnore = Arrays.asList(ignores);
        Class clazz = o1.getClass();
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!fieldNamesToIgnore.contains(field.getName()) && !isStatic(field.getModifiers())) {
                changeFieldValueAndAssertNotEqual(o1, o2, field);
            }
        }
    }

    private static void changeFieldValueAndAssertNotEqual(final Object o1, final Object o2, final Field field) {
        try {
            field.setAccessible(true);
            Object previous = field.get(o1);
            checkDifferentValue(o1, o2, field, previous);
            field.set(o1, previous);
            if (!o1.equals(o2)) {
                throw new AssertionError("When we set field [" +
                        field.getName() +
                        "] back to it's previous value [" +
                        previous +
                        "], we expected it would be equal to object 2, but it was!");
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // CHECKSTYLE:OFF MethodLength
    private static void checkDifferentValue(final Object o1, final Object o2, final Field field, final Object previous)
            throws IllegalAccessException {

        for (int i = 0; i < 10; i++) {
            Object generatedVal = generateRandomValueForField(o1, field);

            if (generatedVal == null) {
                LOGGER.warn("Unable to check field [" +
                        field.getName() +
                        "] because we do not know how to generate an object of type [" +
                        field.getType().getName() +
                        "]");
                return;
            }

            field.set(o1, generatedVal);

            if (o1.equals(o2) && !generatedVal.equals(previous)) {
                throw new AssertionError("When we set field [" +
                        field.getName() +
                        "] from [" +
                        previous +
                        "] to [" +
                        generatedVal +
                        "], we expected it would no longer be equal to object 2, but it was!");
            } else if (!o1.equals(o2) && !generatedVal.equals(previous)) {
                // we're good, the objects are not equal and the values we're using are not equal
                return;
            }
            // otherwise we probably generated a value that was equal to the previos value - likely with enums! Let's try again
            // (up to the bound of the loop)
        }
        LOGGER.warn("We were unable to create an unequeal field value for field name " + field.getName());
    }
    // CHECKSTYLE:ON

    private static Object generateRandomValueForField(Object o, Field fieldToFiddleWith) {
        if (fieldToFiddleWith.getType().equals(boolean.class) || fieldToFiddleWith.getType().equals(Boolean.class)) {
            try {
                return !(Boolean) fieldToFiddleWith.get(o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        MutableField mutableField = new DefaultMutableField(fieldToFiddleWith, o);
        FieldHandler fieldHandler = fieldHandlerFinder.findFieldHandler(mutableField);
        return fieldHandler.getValueFor(mutableField);
    }
}

