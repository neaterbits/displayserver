package com.neaterbits.displayserver.xwindows.core.util;

import com.neaterbits.displayserver.protocol.types.BITMASK;

public abstract class XAttributesBuilder {

    private int bitmask;
    
    final void addFlag(int flag) {
        
        if ((bitmask & flag) != 0) {
            throw new IllegalStateException("Flag already set");
        }
        
        this.bitmask |= flag;
    }
    
    final BITMASK getBitmask() {
        return new BITMASK(bitmask);
    }
}
