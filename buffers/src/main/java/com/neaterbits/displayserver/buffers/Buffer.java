package com.neaterbits.displayserver.buffers;

import com.neaterbits.displayserver.types.Size;

public interface Buffer {

    Size getSize();
    
    int getDepth();
    
    default int getWidth() {
        return getSize().getWidth();
    }
    
    default int getHeight() {
        return getSize().getHeight();
    }

    void flush();
}
