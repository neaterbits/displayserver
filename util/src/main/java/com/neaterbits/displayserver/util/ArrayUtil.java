package com.neaterbits.displayserver.util;

import java.util.function.Function;

public class ArrayUtil {

    public static <T> T [] merge(T [] objs1, T [] objs2, Function<Integer, T[]> createArray) {
        
        final T [] merged = createArray.apply(objs1.length + objs2.length);
        
        System.arraycopy(objs1, 0, merged, 0, objs1.length);
        System.arraycopy(objs2, 0, merged, objs1.length, objs2.length);
        
        return merged;
    }
}
