package com.neaterbits.displayserver.framebuffer.common;

import com.neaterbits.displayserver.buffers.OffscreenBuffer;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.types.Size;

public interface OffscreenBufferProvider {

    OffscreenBuffer allocateOffscreenBuffer(Size size, PixelFormat pixelFormat);
    
    void freeOffscreenBuffer(OffscreenBuffer buffer);

}
