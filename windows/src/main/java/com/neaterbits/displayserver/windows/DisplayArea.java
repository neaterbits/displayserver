package com.neaterbits.displayserver.windows;

import java.util.List;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.framebuffer.common.OffscreenBufferProvider;
import com.neaterbits.displayserver.types.Size;

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
    
    OffscreenBufferProvider getOffscreenBufferProvider();

}
