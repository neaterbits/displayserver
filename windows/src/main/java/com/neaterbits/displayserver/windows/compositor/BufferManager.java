package com.neaterbits.displayserver.windows.compositor;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.windows.Window;

public interface BufferManager {

    BufferOperations getBufferForWindow(Window window);
    
}
