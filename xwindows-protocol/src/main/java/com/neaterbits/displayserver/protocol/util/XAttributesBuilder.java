package com.neaterbits.displayserver.protocol.util;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.BITMASK;

public abstract class XAttributesBuilder {

    private int bitmask;
    
    final void addFlag(int flag) {
        
        if ((bitmask & flag) != 0) {
            throw new IllegalStateException("Flag already set");
        }
        
        this.bitmask |= flag;
    }
    
    final <T> T set(T value, int flag) {
        
        Objects.requireNonNull(value);
        
        addFlag(flag);
        
        return value;
    }

    final BITMASK getBitmask() {
        return new BITMASK(bitmask);
    }
}
