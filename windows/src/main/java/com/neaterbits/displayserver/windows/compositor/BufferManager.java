package com.neaterbits.displayserver.windows.compositor;

import com.neaterbits.displayserver.windows.Window;

public interface BufferManager {
    
    Surface getSurfaceForRootWindow(Window window);

    Surface allocateSurfaceForClientWindow(Window window);
    
    void freeSurfaceForClientWindow(Window window);
    
}
