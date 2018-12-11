package com.neaterbits.displayserver.framebuffer.common;

import com.neaterbits.displayserver.buffers.ImageBuffer;
import com.neaterbits.displayserver.buffers.OffscreenBuffer;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.buffers.RGBBuffer;
import com.neaterbits.displayserver.types.Size;

abstract class BaseOffscreenBufferProvider implements OffscreenBufferProvider {

    @Override
    public OffscreenBuffer allocateOffscreenBuffer(Size size, PixelFormat pixelFormat) {

        final ImageBuffer buffer;
        
        switch (pixelFormat) {
        case RGB24:
            buffer = new RGBBuffer(size.getWidth(), size.getHeight());
            break;
            
        default:
            throw new UnsupportedOperationException();
        }
        
        return buffer;
    }

    @Override
    public void freeOffscreenBuffer(OffscreenBuffer buffer) {
        
    }

}
