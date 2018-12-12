package com.neaterbits.displayserver.buffers;

import com.neaterbits.displayserver.types.Size;

public interface OffscreenBuffer extends BufferOperations {

    Size getSize();
    
    int getDepth();
    
    default int getWidth() {
        return getSize().getWidth();
    }
    
    default int getHeight() {
        return getSize().getHeight();
    }

}
