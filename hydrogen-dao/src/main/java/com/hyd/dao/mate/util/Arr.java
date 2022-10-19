package com.hyd.dao.mate.util;

/**
 * @author yidin
 */
public class Arr {

    @SuppressWarnings("unchecked")
    public static <T> T[] subarray(T[] arr, int start, int end) {
        if (start < 0 || end < 0 || start >= end) {
            return (T[]) new Object[0];
        }

        var result = new Object[end - start];
        System.arraycopy(arr, start, result, 0, end - start);
        return (T[]) result;
    }
}
