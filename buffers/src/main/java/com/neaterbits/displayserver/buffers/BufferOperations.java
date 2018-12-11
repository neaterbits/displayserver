package com.neaterbits.displayserver.buffers;

public interface BufferOperations {

    void putImage(int x, int y, int width, int height, PixelFormat format, byte [] data);
 
    byte [] getImage(int x, int y, int width, int height, PixelFormat format);
    
}
