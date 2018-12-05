package com.neaterbits.displayserver.framebuffer.common;

import com.neaterbits.displayserver.buffers.ImageBuffer;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.types.Size;

public interface GraphicsScreen {

    Size getSize();
    
    Size getSizeInMillimeters();
    
    int getDepth();
    
    PixelFormat getPixelFormat();
    
    FrameBuffer getFrameBuffer();

    ImageBuffer allocateBuffer(int width, int height, PixelFormat pixelFormat);
    
    void freeBuffer(ImageBuffer buffer);

}
