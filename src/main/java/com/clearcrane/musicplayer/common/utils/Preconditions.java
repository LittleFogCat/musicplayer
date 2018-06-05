package com.clearcrane.musicplayer.common.utils;

import java.util.List;

/**
 * Created by jjy on 2018/5/10.
 *
 * Preconditions
 */

public class Preconditions {
    public static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    public static <T> T checkIndexInBounds(T[] array, int index) {
        if (array == null || array.length == 0 || index < 0 || index > array.length - 1) {
            throw new IndexOutOfBoundsException();
        }
        return array[index];
    }

    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }
}
