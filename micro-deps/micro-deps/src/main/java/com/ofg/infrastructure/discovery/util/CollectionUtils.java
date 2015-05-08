package com.ofg.infrastructure.discovery.util;

import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CollectionUtils {

    public static <T> T find(Collection<T> collection, Predicate<T> whatToFind) {
        for (Iterator<T> iter = collection.iterator(); iter.hasNext(); ) {
            T item = iter.next();
            if (whatToFind.apply(item)) {
                return item;
            }
        }
        return null;
    }

    public static <T> Set<T> toSet(Collection<T> collection) {
        Set<T> answer = new HashSet<T>(collection.size());
        answer.addAll(collection);
        return answer;
    }

    public static <T> List<T> flatten(Collection<?> list, Class<T> type) {
        List<Object> retVal = new ArrayList<Object>();
        flatten(list, retVal);
        return (List<T>) retVal;
    }

    private static void flatten(Collection<?> fromTreeList, Collection<Object> toFlatList) {
        for (Object item : fromTreeList) {
            if (item instanceof Collection<?>) {
                flatten((Collection<?>) item, toFlatList);
            } else {
                toFlatList.add(item);
            }
        }
    }
}