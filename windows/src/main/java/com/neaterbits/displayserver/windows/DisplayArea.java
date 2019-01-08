package com.neaterbits.displayserver.windows;

import java.util.List;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.compositor.OffscreenSurface;

/**
 * Corresponding to X Windows screen, which can span multiple display devices 
 *
 */
public interface DisplayArea {

    Size getSize();
    
    Size getSizeInMillimeters();
    
    int getDepth();

    PixelFormat getPixelFormat();
    
    List<ViewPort> getViewPorts();
    
    OffscreenSurface allocateOffscreenSurface(Size size, PixelFormat pixelFormat);
    
    void freeOffscreenSurface(OffscreenSurface surface);

    boolean sameAs(DisplayArea other);
}
