package com.neaterbits.displayserver.buffers;

public interface BufferOperations {
    
    void putImage(int x, int y, int width, int height, PixelFormat format, byte [] data);
 
    void getImage(int x, int y, int width, int height, PixelFormat format, GetImageListener listener);
    
    void copyArea(BufferOperations src, int srcX, int srcY, int dstX, int dstY, int width, int height);
    
}
