package com.neaterbits.displayserver.xwindows.fonts.model;

import java.nio.ByteOrder;

public enum StoreOrder {

    LEAST_SIGNIFICANT_FIRST,
    MOST_SIGNIFICANT_FIRST;
    
    public static StoreOrder getNativeOrder() {

        final StoreOrder nativeOrder;
        
        if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
            nativeOrder = StoreOrder.MOST_SIGNIFICANT_FIRST;
        }
        else {
            nativeOrder = StoreOrder.LEAST_SIGNIFICANT_FIRST;
        }

        return nativeOrder;
    }
}
