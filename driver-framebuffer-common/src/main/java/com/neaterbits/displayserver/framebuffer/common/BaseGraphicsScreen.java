package com.neaterbits.displayserver.framebuffer.common;

import com.neaterbits.displayserver.buffers.ImageBuffer;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.buffers.RGBBuffer;

public abstract class BaseGraphicsScreen implements GraphicsScreen {

    @Override
    public ImageBuffer allocateBuffer(int width, int height, PixelFormat pixelFormat) {

        final ImageBuffer buffer;
        
        switch (pixelFormat) {
        case RGB24:
            buffer = new RGBBuffer(width, height);
            break;
            
        default:
            throw new UnsupportedOperationException();
        }
        
        return buffer;
    }

    @Override
    public void freeBuffer(ImageBuffer buffer) {
        
    }
}
