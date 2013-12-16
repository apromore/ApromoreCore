package org.apromore.portal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by cameron on 16/12/2013.
 */
public class CollectionUtil {

    private CollectionUtil() {}

    public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }

}
