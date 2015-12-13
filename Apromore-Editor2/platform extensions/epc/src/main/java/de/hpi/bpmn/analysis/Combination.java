/*\
 * Copyright  2006 Klaus Rogall, Hamburg, Germany (klaus.rogall@web.de). All rights reserved.
 * _____________________________________________________________________________________________________________________
 * 
 * This class is "Open Source" as defined by the Open Source Initiative (OSI). You can redistribute it and/or modify it
 * under the terms of the BSD License. The license text is appended to the end of this file.
\*/

package de.hpi.bpmn.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Combination {
    /**
     * Erzeugt eine Instanz dieser Klasse.
     * <p/>
     * Da verhindert werden soll, dass Instanzen dieser Klasse ausserhalb dieser
     * Klasse erzeugt werdem, ist dieser Konstruktor 'private' deklariert.
     */

    private Combination() {
        super();
    }

    /**
     * Liefert eine Liste aller Kombinationen (ohne Duplikate) der Elemente der
     * angegebenen Liste.
     * <p/>
     * Beispiel: Angenommen, die angegebene Liste ist "[a, b, c]", dann wre das
     * Ergebnis eine Liste aus Listen wie folgend: "[[], [a], [a, b], [a,
     * c],[b], [b, c], [c]]".
     *
     * @param <T>      Der Typ der ELemente
     * @param elements Die Elemente
     * @return Alle Kombinationen der Elemente
     */

    public static <T extends Comparable<? super T>> List<List<T>> findCombinations(
            Collection<T> elements) {
        List<List<T>> result = new ArrayList<List<T>>();

        for (int i = 0; i <= elements.size(); i++)
            result.addAll(findCombinations(elements, i));

        return result;
    }

    /**
     * Liefert eine Liste aller Kombinationen (ohne Duplikate) der Elemente der
     * angegebenen Liste, die eine bestimmte Anzahl umfasst.
     * <p/>
     * Beispiel 1: Angenommen, die angegebene Liste ist "[a, b, c, d]" und die
     * Anzahl ist 2, dann wre das Ergebnis eine Liste aus Listen wie folgend:
     * "[[a, b], [a, c], [a, d], [b, c], [b, d], [], [c, d]]".
     * <p/>
     * Beispiel 2: Angenommen, die angegebene Liste ist "[a, b, c]" und die
     * Anzahl ist 3, dann wre das Ergebnis eine
     *
     * @param <T>      Der Typ der ELemente
     * @param elements Die Elemente
     * @param n        Die Anzahl der Elemente
     * @return Alle Kombinationen der Elemente
     */

    public static <T extends Comparable<? super T>> List<List<T>> findCombinations(
            Collection<T> elements, int n) {
        List<List<T>> result = new ArrayList<List<T>>();

        if (n == 0) {
            result.add(new ArrayList<T>());

            return result;
        }

        List<List<T>> combinations = findCombinations(elements, n - 1);
        for (List<T> combination : combinations) {
            for (T element : elements) {
                if (combination.contains(element)) {
                    continue;
                }

                List<T> list = new ArrayList<T>();

                list.addAll(combination);

                if (list.contains(element))
                    continue;

                list.add(element);
                Collections.sort(list);

                if (result.contains(list))
                    continue;

                result.add(list);
            }
        }

        return result;
    }

}

