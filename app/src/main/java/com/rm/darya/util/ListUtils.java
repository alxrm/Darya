package com.rm.darya.util;

import java.util.ArrayList;

/**
 * Created by alex
 */
public class ListUtils {
    public interface Predicate<T> { boolean apply(T type); }

    public static <T> ArrayList<T> filter(ArrayList<T> col, Predicate<T> predicate) {
        ArrayList<T> result = new ArrayList<>();
        for (T element: col) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }
}
