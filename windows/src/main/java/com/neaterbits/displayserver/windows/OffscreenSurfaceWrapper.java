package com.neaterbits.displayserver.windows;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.buffers.OffscreenBuffer;
import com.neaterbits.displayserver.windows.compositor.CoordinateTranslator;
import com.neaterbits.displayserver.windows.compositor.OffscreenSurface;
import com.neaterbits.displayserver.windows.compositor.SurfaceWrapper;

final class OffscreenSurfaceWrapper extends SurfaceWrapper implements OffscreenSurface {

    OffscreenSurfaceWrapper(BufferOperations bufferOperations, CoordinateTranslator coordinateTranslator) {
    
        super(bufferOperations, coordinateTranslator);
    }
    
    final OffscreenBuffer getOffscreenBuffer() {
        return (OffscreenBuffer)getBuffer();
    }
}
