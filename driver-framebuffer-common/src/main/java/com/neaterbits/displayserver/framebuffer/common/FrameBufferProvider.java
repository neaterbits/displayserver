package com.neaterbits.displayserver.framebuffer.common;

import com.neaterbits.displayserver.buffers.PixelFormat;

public interface FrameBufferProvider {

    PixelFormat getPixelFormat();
    
    FrameBuffer getFrameBuffer();

}
