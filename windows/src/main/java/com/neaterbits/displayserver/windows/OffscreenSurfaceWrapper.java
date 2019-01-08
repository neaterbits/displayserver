package com.neaterbits.displayserver.windows;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.buffers.OffscreenBuffer;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.compositor.CoordinateTranslator;
import com.neaterbits.displayserver.windows.compositor.OffscreenSurface;
import com.neaterbits.displayserver.windows.compositor.SurfaceWrapper;

final class OffscreenSurfaceWrapper extends SurfaceWrapper implements OffscreenSurface {

    OffscreenSurfaceWrapper(BufferOperations bufferOperations, CoordinateTranslator coordinateTranslator,
            Size size, int depth) {
    
        super(bufferOperations, coordinateTranslator, size, depth);
    }
    
    final OffscreenBuffer getOffscreenBuffer() {
        return (OffscreenBuffer)getBuffer();
    }
}
