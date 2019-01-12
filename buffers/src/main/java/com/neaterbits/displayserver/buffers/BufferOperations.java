package com.neaterbits.displayserver.buffers;

import com.neaterbits.displayserver.render.cairo.CairoSurface;

public interface BufferOperations extends Buffer {
    
    void putImage(int x, int y, int width, int height, PixelFormat format, byte [] data);
 
    void getImage(int x, int y, int width, int height, PixelFormat format, GetImageListener listener);
    
    void copyArea(BufferOperations src, int srcX, int srcY, int dstX, int dstY, int width, int height);
    
    void writeTestImage(int x, int y, int width, int height);
    
    CairoSurface createCairoSurface();
}
